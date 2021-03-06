package com.dianwoba.dispatch.sender.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.cache.DepInfoCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.dto.DepPlatformAppDTO;
import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoba.dispatch.sender.manager.AppDepManager;
import com.dianwoba.dispatch.sender.manager.DepInfoManager;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.dubbo.base.result.ResponseDTO;
import com.dianwoba.genius.domain.dto.DepartDTO;
import com.dianwoba.genius.domain.dto.StaffDTO;
import com.dianwoba.genius.provider.DepartProvider;
import com.dianwoba.genius.provider.StaffProvider;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import com.dianwoba.wireless.http.support.util.HttpUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class AppDepSynHandler extends AbstractJobExecuteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDepSynHandler.class);

    @Resource
    private AppDepManager appDepManager;

    @Resource
    private StaffProvider staffProvider;

    @Resource
    private DepartProvider departProvider;

    @Resource
    private DepInfoManager depInfoManager;

    @Resource
    private DepInfoCache depInfoCache;

    @Override
    public void doExecute(ExecuteContext executeContext) {
        //获取最新修改的时间
        long modifyTime = 0L;
        List<AppDep> appDep = appDepManager.queryLastModify();
        if (CollectionUtils.isNotEmpty(appDep)) {
            modifyTime = DateUtils.addHours(appDep.get(0).getDepPlatModifyTime(), -1).getTime();
        }
        //获取该时间开始的增量数据
        List<DepPlatformAppDTO> appLists = getAppDepFromDepPlatform(modifyTime);
        if (CollectionUtils.isEmpty(appLists)) {
            return;
        }
        //按照更新时间排序
        appLists.sort(Comparator.comparing(DepPlatformAppDTO::getModifyTime));

        //有了owner、dev的code、名字，下一步找员工信息
        Set<String> staffCode = filterStaffCode(appLists);
        Map<String, StaffDTO> staffMap = staffProvider.findByCodes(Lists.newArrayList(staffCode));
        List<String> emptyStaff = staffMap.entrySet().stream().filter(e -> e.getValue() == null)
                .map(Entry::getKey).collect(Collectors.toList());
        staffMap.keySet().removeAll(emptyStaff);

        //处理部门信息 步骤提前，计算应用默认部门使用
        List<Integer> depCode = staffMap.values().stream().map(StaffDTO::getDepartId).distinct()
                .collect(Collectors.toList());
        List<DepInfo> depInfoList = Lists
                .newArrayList(depInfoCache.queryAllFromClientCache().values());
        if (depInfoCache.totalCount() > 0) {
            //移除已有的
            List<Integer> depCodeList = depInfoList.stream().map(DepInfo::getId)
                    .collect(Collectors.toList());
            depCode = ListUtils.removeAll(depCode, depCodeList);
        }
        if (CollectionUtils.isNotEmpty(depCode)) {
            // TODO: 2020/3/17  改称batch 减少数据库io
            updateDepartInfo(depCode, depInfoList);
//            gainAndUpdateDepartInfo(depCode);
            depInfoCache.reload();
        }

        //appLists转为map方便查询
        Map<String, List<DepPlatformAppDTO>> appMap = appLists.stream()
                .collect(Collectors.groupingBy(DepPlatformAppDTO::getName));
        //数据库中是否已有数据
        List<String> appNames = appLists.stream().map(DepPlatformAppDTO::getName)
                .collect(Collectors.toList());
        List<AppDep> appDepExist = appDepManager.queryConfigExist(appNames);
        if (CollectionUtils.isNotEmpty(appDepExist)) {
            for (AppDep dep : appDepExist) {
                DepPlatformAppDTO depPlatformAppDTO = appMap.get(dep.getAppName()).get(0);
                AppDep update = buildAppDep(dep, depPlatformAppDTO, staffMap);
                appDepManager.update(update);
            }
            List<String> existAppNames = appDepExist.stream().map(AppDep::getAppName)
                    .collect(Collectors.toList());
            List<AppDep> depInsert = appLists.stream()
                    .filter(t -> !existAppNames.contains(t.getName()))
                    .map(t -> buildAppDep(null, t, staffMap)).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            LOGGER.info("appList insert :{}", JSONObject.toJSONString(depInsert));
            appDepManager.batchSave(depInsert);
        } else {
            List<AppDep> save = appLists.stream().map(t -> buildAppDep(null, t, staffMap))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            LOGGER.info("appList insert :{}", JSONObject.toJSONString(save));
            appDepManager.batchSave(save);
        }
    }

    private void updateDepartInfo(List<Integer> depCodeList, List<DepInfo> depInfoList) {
        List<DepInfo> save = Lists.newArrayList();
        List<Integer> exist = depInfoList.stream().map(DepInfo::getId).collect(Collectors.toList());
        //map  最新部门id， 同名部门id的list
        Map<Integer, List<Integer>> updateMap = Maps.newHashMap();
        for (Integer depCode : depCodeList) {
            DepartDTO departDTO = getDepartInfo(depCode);
            if (departDTO != null) {
                save.add(ConvertUtils.convert2DepInfo(departDTO));
                List<DepInfo> savedDepInfoList = depInfoManager.queryByAppName(departDTO.getName());
                if (CollectionUtils.isNotEmpty(savedDepInfoList)) {
                    List<Integer> ids = savedDepInfoList.stream().map(DepInfo::getId)
                            .collect(Collectors.toList());
                    updateMap.put(departDTO.getId(), ids);
                }
                if (departDTO.getPath() != null) {
                    String[] ancestors = departDTO.getPath().split(",");
                    for (int i = ancestors.length - 1; i >= 0; i--) {
                        int ancDep = Integer.parseInt(ancestors[i]);
                        if (exist.contains(ancDep)) {
                            break;
                        }
                        if (depCodeList.contains(ancDep)) {
                            break;
                        }
                        List<Integer> saveId = save.stream().map(DepInfo::getId)
                                .collect(Collectors.toList());
                        if (saveId.contains(ancDep)) {
                            break;
                        }
                        DepartDTO ancDepart = getDepartInfo(ancDep);
                        if (ancDepart != null) {
                            save.add(ConvertUtils.convert2DepInfo(ancDepart));
                            List<DepInfo> acfInfoList = depInfoManager
                                    .queryByAppName(ancDepart.getName());
                            if (CollectionUtils.isNotEmpty(acfInfoList)) {
                                List<Integer> ids = acfInfoList.stream().map(DepInfo::getId)
                                        .collect(Collectors.toList());
                                updateMap.put(ancDepart.getId(), ids);
                            }
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(save)) {
            depInfoManager.batchSave(save);
        }
        if (MapUtils.isNotEmpty(updateMap)) {
            updateMap.forEach((k, v) -> depInfoManager.update(k, v));
        }
    }

    private DepartDTO getDepartInfo(Integer depCode) {
        try {
            ResponseDTO<DepartDTO> response = departProvider.findById(depCode);
            if (response.isSuccess()) {
                DepartDTO departDTO = response.getData();
                if (departDTO != null) {
                    return departDTO;
                }
            }
        } catch (Exception e) {
            LOGGER.warn("查找部门信息时异常，", e);
        }
        return null;
    }

    private void gainAndUpdateDepartInfo(List<Integer> depCode) {
        for (Integer dep : depCode) {
            try {
                DepartDTO departDTO = updateDepInfo(dep);
                while (departDTO != null && departDTO.getId() != 0) {
                    if (departDTO.getParent() != null && !depCode.contains(departDTO.getParent())) {
                        if (depInfoManager.queryById(departDTO.getParent()) == null) {
                            //父部门信息更新
                            departDTO = updateDepInfo(departDTO.getParent());
                            continue;
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("查找部门信息时异常，", e);
            }
        }
    }

    private DepartDTO updateDepInfo(Integer id) {
        ResponseDTO<DepartDTO> response = departProvider.findById(id);
        if (response.isSuccess()) {
            DepartDTO departDTO = response.getData();
            if (departDTO != null) {
                List<DepInfo> savedDepInfoList = depInfoManager.queryByAppName(departDTO.getName());
                if (CollectionUtils.isEmpty(savedDepInfoList)) {
                    depInfoManager.save(ConvertUtils.convert2DepInfo(departDTO));
                } else {
                    List<Integer> ids = savedDepInfoList.stream().map(DepInfo::getId)
                            .collect(Collectors.toList());
                    depInfoManager.saveAndUpdate(ConvertUtils.convert2DepInfo(departDTO), ids);
                }
            }
            return departDTO;
        }
        return null;
    }

    private List<DepPlatformAppDTO> getAppDepFromDepPlatform(Long modifyTime) {
        try {
            String link = Constant.PREFIX_LINK_DEPLOY_PLATFORM + modifyTime;
            String resultValue = HttpUtils.get(link);
            LOGGER.info("调发布平台接口，resultValue:{}", resultValue);
            if (StringUtils.isNotEmpty(resultValue)) {
                JSONObject json = JSON.parseObject(resultValue);
                String data = json.getString("data");
                if (StringUtils.isNotEmpty(data)) {
                    return JSON.parseArray(data, DepPlatformAppDTO.class);
                }
            }
        } catch (IOException e) {
            LOGGER.error("调发布平台接口异常", e);
        }
        return Lists.newArrayList();
    }

    private Set<String> filterStaffCode(List<DepPlatformAppDTO> lists) {
        Set<String> codes = Sets.newHashSet();
        lists.forEach(t -> {
            if (StringUtils.isNotEmpty(t.getDevelopersCode())) {
                codes.addAll(Arrays.asList(t.getDevelopersCode().split(",")));
            }
            if (StringUtils.isNotEmpty(t.getOwnersCode())) {
                codes.addAll(Arrays.asList(t.getOwnersCode().split(",")));
            }
        });
        return codes;
    }

    private AppDep buildAppDep(AppDep appDep, DepPlatformAppDTO depPlatformAppDTO,
            Map<String, StaffDTO> staffMap) {
        AppDep app = new AppDep();
        if (StringUtils.isNotEmpty(depPlatformAppDTO.getDevelopersCode())) {
            List<String> developers = Arrays
                    .asList(depPlatformAppDTO.getDevelopersCode().split(","));
            List<StaffDTO> developerDTO = developers.stream().map(staffMap::get)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(developerDTO)) {
                List<String> developersDepId = developerDTO.stream().map(StaffDTO::getDepartId)
                        .distinct().filter(Objects::nonNull).map(String::valueOf)
                        .collect(Collectors.toList());
                List<String> developersPhone = developerDTO.stream().map(StaffDTO::getMobile)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                List<String> developersMail = developerDTO.stream().map(StaffDTO::getEmail)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                app.setDevelopersDepId(String.join(",", developersDepId));
                app.setDevelopersPhone(String.join(",", developersPhone));
                app.setDevelopersMail(String.join(",", developersMail));
            }
        }
        if (StringUtils.isNotEmpty(depPlatformAppDTO.getOwnersCode())) {
            List<String> owners = Arrays.asList(depPlatformAppDTO.getOwnersCode().split(","));
            List<StaffDTO> ownersDTO = owners.stream().map(staffMap::get).filter(Objects::nonNull)
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(ownersDTO)) {
                List<String> ownersDepId = ownersDTO.stream().map(StaffDTO::getDepartId)
                        .filter(Objects::nonNull).distinct().map(String::valueOf)
                        .collect(Collectors.toList());
                List<String> ownersPhone = ownersDTO.stream().map(StaffDTO::getMobile)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                List<String> ownersMail = ownersDTO.stream().map(StaffDTO::getEmail)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                app.setOwnersDepId(String.join(",", ownersDepId));
                app.setOwnersPhone(String.join(",", ownersPhone));
                app.setOwnersMail(String.join(",", ownersMail));
            }
        }
        if (StringUtils.isEmpty(app.getDevelopersDepId()) && StringUtils
                .isEmpty(app.getOwnersDepId())) {
            return null;
        }
        if (appDep != null) {
            app.setId(appDep.getId());
            app.setCreateTime(appDep.getCreateTime());
            app.setCreator(appDep.getCreator());
            app.setModifer(Constant.DEFAULT_STAFF);
            app.setModifyTime(new Date());
        } else {
            app.setCreator(Constant.DEFAULT_STAFF);
            app.setCreateTime(new Date());
        }
        app.setAppName(depPlatformAppDTO.getName());
        app.setDepPlatModifyTime(depPlatformAppDTO.getModifyTime());
        app.setDepId(determineDepId(app.getDevelopersDepId(), app.getOwnersDepId()));
        return app;
    }

    private int determineDepId(String devDepIds, String ownDepIds) {
        List<Integer> depIds;
        if (StringUtils.isNotEmpty(devDepIds)) {
            depIds = Lists.newArrayList(devDepIds.split(",")).stream().distinct()
                    .map(Integer::parseInt).collect(Collectors.toList());
        } else {
            depIds = Lists.newArrayList(ownDepIds.split(",")).stream().distinct()
                    .map(Integer::parseInt).collect(Collectors.toList());
        }
        return findDepId(depIds);
    }

    private int findDepId(List<Integer> depIds) {
        if (depIds.size() == 1) {
            int depId = depIds.get(0);
            DepInfo depInfo = depInfoCache.queryFromClientCache(depId);
            return depInfo.getNewest();
        } else {
            Map<Integer, List<DepInfo>> depInfoMap = depIds.stream()
                    .map(depInfoCache::queryFromClientCache)
                    .collect(Collectors.groupingBy(DepInfo::getNewest));
            if (depInfoMap.keySet().size() == 1) {
                //部门都相同
                return Lists.newArrayList(depInfoMap.keySet()).get(0);
            } else {
                List<DepInfo> newestDepList = Lists.newArrayList(depInfoMap.keySet()).stream()
                        .map(depInfoCache::queryFromClientCache).collect(Collectors.toList());
                Map<Integer, List<DepInfo>> newestDepMap = newestDepList.stream()
                        .collect(Collectors.groupingBy(DepInfo::getId));
                //如果有parent 删除 1是3的父部门，留下3，删除1
                newestDepList.forEach(dep -> {
                    newestDepMap.remove(dep.getParent());
                    if (StringUtils.isNotEmpty(dep.getPath())) {
                        List<String> path = Arrays
                                .asList(dep.getPath().substring(0, dep.getPath().length() - 1)
                                        .split(","));
                        newestDepMap.keySet().removeAll(
                                path.stream().map(Integer::parseInt).collect(Collectors.toList()));
                    }
                });
                if (newestDepMap.keySet().size() == 1) {
                    return Lists.newArrayList(newestDepMap.keySet()).get(0);
                }
                List<List<String>> paths = newestDepMap.values().stream()
                        .map(v -> v.get(0).getPath()).distinct().filter(StringUtils::isNotEmpty)
                        .map(str -> Arrays.asList(str.substring(0, str.length() - 1).split(",")))
                        .collect(Collectors.toList());
                List<String> path = paths.get(0);
                for (int i = 1; i < paths.size() && path.size() > 0; i++) {
                    int len = Math.min(path.size(), paths.get(i).size());
                    int index = 0;
                    while (index < len && path.get(index).equals(paths.get(i).get(index))) {
                        index++;
                    }
                    path = path.subList(0, index);
                }
                if (CollectionUtils.isEmpty(path)) {
                    return -1;
                }
                return Integer.parseInt(path.get(path.size() - 1));
            }
        }
    }

}

package com.dianwoba.dispatch.sender.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.cache.DepInfoCache;
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
import com.dianwoba.wireless.monitor.constant.Constant;
import com.dianwoba.wireless.monitor.domain.dto.common.DepPlatformAppDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
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
        //有了owner、dev的code、名字，下一步找员工信息
        Set<String> staffCode = filterStaffCode(appLists);
        Map<String, StaffDTO> staffMap = staffProvider.findByCodes(Lists.newArrayList(staffCode));
        List<String> emptyStaff = staffMap.entrySet().stream().filter(e -> e.getValue() == null)
                .map(Entry::getKey).collect(Collectors.toList());
        staffMap.keySet().removeAll(emptyStaff);
        //appLists转为map方便查询
        Map<String, List<DepPlatformAppDTO>> appMap = appLists.stream()
                .collect(Collectors.groupingBy(DepPlatformAppDTO::getName));
        //数据库中是否已有数据
        List<String> appNames = appLists.stream().map(DepPlatformAppDTO::getName)
                .collect(Collectors.toList());
        List<AppDep> appDepExist = appDepManager.queryConfigExist(appNames);
        if(CollectionUtils.isNotEmpty(appDepExist)) {
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
            appDepManager.batchSave(depInsert);
        } else {
            appDepManager.batchSave(appLists.stream().map(t -> buildAppDep(null, t, staffMap))
                    .filter(Objects::nonNull).collect(Collectors.toList()));
        }

        //处理部门信息
        List<Integer> depCode = staffMap.values().stream().map(StaffDTO::getDepartId).distinct()
                .collect(Collectors.toList());
        List<DepInfo> depInfoList = Lists.newArrayList(depInfoCache.queryAllFromClientCache().values());
        Map<String, List<DepInfo>> depInfoMap = depInfoList.stream().collect(Collectors.groupingBy(DepInfo::getName));
        List<Integer> depCodeList = depInfoList.stream().map(DepInfo::getId).collect(Collectors.toList());
        CollectionUtils.removeAll(depCode, depCodeList);
        if (CollectionUtils.isNotEmpty(depCode)) {
            depCode.forEach(dep -> {
                try {
                    ResponseDTO<DepartDTO> response = departProvider.findById(dep);
                    if (response.isSuccess()) {
                        DepartDTO departDTO = response.getData();
                        List<DepInfo> savedDepInfoList = depInfoManager.queryByAppName(departDTO.getName());
                        if(CollectionUtils.isEmpty(savedDepInfoList)) {
                            depInfoManager.save(ConvertUtils.convert2DepInfo(departDTO));
                        } else {
                            List<Integer> ids = savedDepInfoList.stream().map(DepInfo::getId)
                                    .collect(Collectors.toList());
                            depInfoManager.saveAndUpdate(ConvertUtils.convert2DepInfo(departDTO), ids);
                        }
                    }
                } catch (Exception e){
                    LOGGER.error("查找部门信息时异常，", e);
                }
            });
        }
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
        if (appDep != null) {
            app.setId(appDep.getId());
            app.setCreateTime(appDep.getCreateTime());
        } else {
            app.setCreateTime(new Date());
        }
        app.setModifyTime(new Date());
        app.setAppName(depPlatformAppDTO.getName());
        app.setDepPlatModifyTime(depPlatformAppDTO.getModifyTime());
        if (StringUtils.isNotEmpty(depPlatformAppDTO.getDevelopersCode())) {
            List<String> developers = Arrays.asList(depPlatformAppDTO.getDevelopersCode().split(","));
            List<StaffDTO> developerDTO = developers.stream().map(staffMap::get)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(developerDTO)) {
                List<String> developersDepId = developerDTO.stream().map(StaffDTO::getDepartId)
                        .distinct().filter(Objects::nonNull).map(String::valueOf)
                        .collect(Collectors.toList());
                List<String> developersPhone = developerDTO.stream().map(StaffDTO::getMobile)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                app.setDevelopersDepId(String.join(",", developersDepId));
                app.setDevelopersPhone(String.join(",", developersPhone));
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
                app.setOwnersDepId(String.join(",", ownersDepId));
                app.setOwnersPhone(String.join(",", ownersPhone));
            }
        }
        if (app.getDevelopersDepId() == null && app.getOwnersDepId() == null) {
            return null;
        }
        return app;
    }
}

package com.dianwoba.dispatch.sender.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.manager.AppDepManager;
import com.dianwoba.genius.domain.dto.StaffDTO;
import com.dianwoba.genius.provider.DepartProvider;
import com.dianwoba.genius.provider.StaffProvider;
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
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppDepTest extends UnitTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDepTest.class);

    @Resource
    private AppDepManager appDepManager;

    @Resource
    private StaffProvider staffProvider;

    @Resource
    private DepartProvider departProvider;

    @Test
    public void test() {
//        synTest();
        departTest();
//        System.out.println(JSONObject.toJSONString(testStaff()));
//        long time = DateUtils.addDays(new Date(), -7).getTime();
//        System.out.println(new Date(time));
//        System.out.println(JSONObject.toJSONString(getAppDepFromDepPlatform(0L)));
    }

    public void departTest(){
//        System.out.println(JSONObject.toJSONString(departProvider.findById(446).getData()));
        System.out.println(JSONObject.toJSONString(departProvider.findById(371).getData()));
        System.out.println(JSONObject.toJSONString(departProvider.findById(458).getData()));
        System.out.println(JSONObject.toJSONString(departProvider.findById(459).getData()));
    }

    public List<DepPlatformAppDTO> getAppDepFromDepPlatform(Long modifyTime) {
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

    public Map<String, StaffDTO> testStaff() {
        return staffProvider.findByCodes(Lists.newArrayList("03595","04114"));
    }

    public void synTest() {
        //获取增量数据
        long modifyTime = 0L;
        List<AppDep> appDep = appDepManager.queryLastModify();
        if (CollectionUtils.isNotEmpty(appDep)) {
            modifyTime = DateUtils.addHours(appDep.get(0).getDepPlatModifyTime(), -1).getTime();
        }
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

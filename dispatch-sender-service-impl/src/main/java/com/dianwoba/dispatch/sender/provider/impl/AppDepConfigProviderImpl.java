package com.dianwoba.dispatch.sender.provider.impl;

import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.domain.dto.param.dep.AppDepManualDepUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.dep.AppDepQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.AppDepInfoDTO;
import com.dianwoba.dispatch.sender.exception.ConfigException;
import com.dianwoba.dispatch.sender.manager.AppDepManager;
import com.dianwoba.dispatch.sender.provider.AppDepConfigProvider;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.dispatch.sender.util.ValidUtils;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class AppDepConfigProviderImpl implements AppDepConfigProvider {

    @Resource
    private AppDepManager appDepManager;

    @Resource
    private AppDepCache appDepCache;

    @Override
    public Boolean updateManualDep(AppDepManualDepUpdateDTO updateDTO) {
        ValidUtils.validAppDepManualDepUpdateDTO(updateDTO);
        if (updateDTO.getManualDepId() != null) {
            if (updateDTO.getManualDepId().equals(updateDTO.getNewDepId())) {
                throw new ConfigException("部门未发生变更");
            }
        } else {
            if (updateDTO.getDeFaultDepId().equals(updateDTO.getNewDepId())) {
                throw new ConfigException("部门未发生变更");
            }
        }
        return appDepManager.updateManualDep(updateDTO) > 0;
    }

    @Override
    public List<AppDepInfoDTO> queryAppInfo(AppDepQueryDTO appDepQueryDTO) {
        Integer depId = appDepQueryDTO.getDepId();
        List<AppDepInfo> appDepList = Lists
                .newArrayList(appDepCache.queryAllFromClientCache().values());
        List<AppDepInfo> list = appDepList.stream().filter(appDepInfo -> {
            if (appDepInfo.getManualDepId() != null) {
                return appDepInfo.getManualDepId().equals(String.valueOf(depId));
            } else {
                return appDepInfo.getDepId().equals(String.valueOf(depId));
            }
        }).collect(Collectors.toList());
        return list.stream().map(ConvertUtils::convert2AppDepInfoDTO).collect(Collectors.toList());
    }
}

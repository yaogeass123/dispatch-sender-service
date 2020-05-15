package com.dianwoba.dispatch.sender.provider.impl;

import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenSaveDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.DingTokenInfoDTO;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.exception.ConfigException;
import com.dianwoba.dispatch.sender.manager.DingTokenConfigManager;
import com.dianwoba.dispatch.sender.provider.DingTokenConfigProvider;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

/**
 * @author Polaris
 */
@Component
public class DingTokenConfigProviderImpl implements DingTokenConfigProvider {

    @Resource
    private DingTokenConfigManager dingTokenConfigManager;

    @Override
    public void save(DingTokenSaveDTO saveDTO) {
        if (saveDTO.getAppDep() == null) {
            throw new ConfigException("部门不能为空");
        }
        if (saveDTO.getGroupId() == null) {
            throw new ConfigException("群组不能为空");
        }
        if (StringUtils.isEmpty(saveDTO.getToken())) {
            throw new ConfigException("token不能为空");
        }
        if (saveDTO.getCreator() == null) {
            throw new ConfigException("操作人员不能为空");
        }
        DingTokenConfig config = new DingTokenConfig();
        config.setAppDep(saveDTO.getAppDep());
        config.setGroupId(saveDTO.getGroupId());
        config.setToken(saveDTO.getToken());
        config.setCreator(saveDTO.getCreator());
        config.setCreateTime(new Date());
        config.setStatus((byte) 0);
        config.setIsActive(Boolean.TRUE);
        if (!StringUtils.isEmpty(saveDTO.getSecret())) {
            config.setSecret(saveDTO.getSecret());
        }
        if (!StringUtils.isEmpty(saveDTO.getKeyWords())) {
            config.setKeyWords(saveDTO.getKeyWords());
        }
        dingTokenConfigManager.save(config);
    }

    @Override
    public Boolean update(DingTokenUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new ConfigException("id不能为空");
        }
        if (updateDTO.getToken() == null) {
            throw new ConfigException("token不能为空");
        }
        if (updateDTO.getModifier() == null) {
            throw new ConfigException("操作人员不能为空");
        }
        return dingTokenConfigManager.update(updateDTO);
    }

    @Override
    public Boolean delete(DingTokenDeleteDTO deleteDTO) {
        if (deleteDTO.getId() == null) {
            throw new ConfigException("id不能为空");
        }
        if (deleteDTO.getModifier() == null) {
            throw new ConfigException("操作人员不能为空");
        }
        if (StringUtils.isEmpty(deleteDTO.getToken())) {
            throw new ConfigException("token不能为空");
        }
        return dingTokenConfigManager.delete(deleteDTO);
    }

    @Override
    public List<DingTokenInfoDTO> query(DingTokenQueryDTO queryDTO) {
        if (queryDTO.getAppDep() == null) {
            throw new ConfigException("部门不能为空");
        }
        if (queryDTO.getGroupId() == null) {
            throw new ConfigException("群id不能为空");
        }
        List<DingTokenConfig> list = dingTokenConfigManager.query(queryDTO);
        return list.stream().map(ConvertUtils::convert2DingTokenInfoDTO)
                .collect(Collectors.toList());
    }
}

package com.dianwoba.dispatch.sender.provider.impl;

import com.alibaba.fastjson.JSON;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupPagingQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupSaveDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.GroupInfoDTO;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.exception.ConfigException;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.provider.GroupConfigProvider;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.wireless.paging.Pagination;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class GroupConfigProviderImpl implements GroupConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupConfigProviderImpl.class);

    @Resource
    private GroupConfigManager groupConfigManager;

    @Override
    public void save(GroupSaveDTO saveDTO) {
        if (saveDTO.getGroupName() == null) {
            throw new ConfigException("群名称不能为空");
        }
        if (saveDTO.getCreator() == null) {
            throw new ConfigException("操作人不能为空");
        }
        DingGroupName groupName = new DingGroupName();
        groupName.setGroupName(saveDTO.getGroupName());
        groupName.setCreator(saveDTO.getCreator());
        groupName.setCreateTime(new Date());
        if (saveDTO.getAtWho() != null) {
            groupName.setAtWho(saveDTO.getAtWho());
        }
        if (saveDTO.getAtAll() != null) {
            groupName.setAtAll(saveDTO.getAtAll());
        }
        if (saveDTO.getMail() != null) {
            groupName.setMail(saveDTO.getMail());
        }
        groupConfigManager.save(groupName);
    }

    @Override
    public Boolean update(GroupUpdateDTO groupUpdateDTO) {
        if (groupUpdateDTO.getGroupId() == null) {
            throw new ConfigException("群id不能为空");
        }
        if (groupUpdateDTO.getModifier() == null) {
            throw new ConfigException("操作人不能为空");
        }
        DingGroupName groupName = new DingGroupName();
        groupName.setId(groupUpdateDTO.getGroupId());
        groupName.setModifier(groupUpdateDTO.getModifier());
        groupName.setModifyTime(new Date());
        if (groupUpdateDTO.getGroupName() != null) {
            groupName.setAtWho(groupUpdateDTO.getGroupName());
        }
        if (groupUpdateDTO.getAtWho() != null) {
            groupName.setAtWho(groupUpdateDTO.getAtWho());
        }
        if (groupUpdateDTO.getAtAll() != null) {
            groupName.setAtAll(groupUpdateDTO.getAtAll());
        }
        if (groupUpdateDTO.getMail() != null) {
            groupName.setMail(groupUpdateDTO.getMail());
        }
        return groupConfigManager.update(groupName);
    }

    @Override
    public Boolean delete(GroupDeleteDTO deleteDTO) {
        return groupConfigManager.delete(deleteDTO);
    }

    @Override
    public Pagination<GroupInfoDTO> query(GroupPagingQueryDTO queryDTO) {
        Pagination<GroupInfoDTO> pagination = new Pagination<>();
        pagination.setCurrentPage(queryDTO.getCurrentPage());
        pagination.setPageSize(queryDTO.getPageSize());
        try {
            List<DingGroupName> groupInfoList = groupConfigManager.queryPaging(queryDTO);
            long count = groupConfigManager.totalCount();
            List<GroupInfoDTO> resultList = Lists.newArrayList();
            groupInfoList.forEach(x -> resultList.add(ConvertUtils.convert2GroupInfoDTO(x)));
            pagination.setList(resultList);
            pagination.setTotalCount(count);
            return pagination;
        } catch (Exception e) {
            LOGGER.error("ParameterInfoProvider分页查询异常,parameterInfoQueryDTO:{}",
                    JSON.toJSONString(queryDTO), e);
            return pagination;
        }
    }

    @Override
    public Long totalCount() {
        return groupConfigManager.totalCount();
    }
}

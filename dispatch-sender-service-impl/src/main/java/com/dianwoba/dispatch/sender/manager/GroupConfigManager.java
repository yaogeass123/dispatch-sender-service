package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.cache.GroupConfigCache;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupPagingQueryDTO;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.entity.DingGroupName.Column;
import com.dianwoba.dispatch.sender.entity.DingGroupNameExample;
import com.dianwoba.dispatch.sender.entity.DingGroupNameExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DingGroupNameMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Polaris
 */

@Component
public class GroupConfigManager {

    @Resource
    private DingGroupNameMapper groupNameMapper;

    @Resource
    private GroupConfigCache groupConfigCache;

    @Resource
    private GroupMatchRulesManager groupMatchRulesManager;

    @Resource
    private DingTokenConfigManager dingTokenConfigManager;

    public Long totalCount() {
        DingGroupNameExample example = new DingGroupNameExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        return groupNameMapper.countByExample(example);
    }

    public List<DingGroupName> queryByPage(PagingSearchable pagingSearchable) {
        DingGroupNameExample example = new DingGroupNameExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return groupNameMapper.selectByExample(example);
    }

    public String findGroupNameByCache(Long id) {
        DingGroupName groupName = groupConfigCache.queryFromClientCache(id);
        if (groupName == null) {
            return String.valueOf(id);
        }
        return groupName.getGroupName();
    }

    public void save(DingGroupName groupName) {
        groupNameMapper.insertSelective(groupName);
    }

    public boolean update(DingGroupName groupName) {
        return groupNameMapper.updateByPrimaryKeySelective(groupName) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean delete(GroupDeleteDTO groupDeleteDTO) {
        DingGroupNameExample example = new DingGroupNameExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        criteria.andIdEqualTo(groupDeleteDTO.getGroupId());
        criteria.andGroupNameEqualTo(groupDeleteDTO.getGroupName());
        DingGroupName record = new DingGroupName();
        record.setIsActive(Boolean.FALSE);
        record.setModifier(groupDeleteDTO.getModifier());
        record.setModifyTime(new Date());
        boolean deleteGroup = groupNameMapper.updateByExampleSelective(record, example) > 1;
        boolean deleteRule = groupMatchRulesManager.deleteByGroup(groupDeleteDTO);
        boolean deleteToken = dingTokenConfigManager.deleteByGroup(groupDeleteDTO);
        return deleteGroup && deleteRule && deleteToken;
    }

    public List<DingGroupName> queryPaging(GroupPagingQueryDTO queryDTO) {
        DingGroupNameExample example = new DingGroupNameExample();
        Criteria criteria = example.page(queryDTO.getCurrentPage() - 1, queryDTO.getPageSize())
                .createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        example.orderBy(Column.id.asc());
        return groupNameMapper.selectByExample(example);
    }

}

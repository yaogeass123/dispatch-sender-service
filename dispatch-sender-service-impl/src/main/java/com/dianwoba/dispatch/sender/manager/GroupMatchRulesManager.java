package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleCountDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRulePagingQueryDTO;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.entity.GroupMatchRulesExample;
import com.dianwoba.dispatch.sender.entity.GroupMatchRulesExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.GroupMatchRulesMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class GroupMatchRulesManager {

    @Resource
    private GroupMatchRulesMapper groupMatchRulesMapper;

    public Long totalCount() {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        return groupMatchRulesMapper.countByExample(example);
    }

    public List<GroupMatchRules> queryByPage(PagingSearchable pagingSearchable) {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return groupMatchRulesMapper.selectByExample(example);
    }

    public boolean delete(Long groupId) {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        criteria.andGroupIdEqualTo(groupId);
        GroupMatchRules record = new GroupMatchRules();
        record.setIsActive(Boolean.FALSE);
        return groupMatchRulesMapper.updateByExampleSelective(record, example) > 1;
    }

    public void save(GroupMatchRules rule) {
        groupMatchRulesMapper.insertSelective(rule);
    }

    public boolean update(GroupMatchRules record) {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        return groupMatchRulesMapper.updateByExampleSelective(record, example) > 0;
    }

    public boolean delete(MatchRuleDeleteDTO deleteDTO) {
        GroupMatchRules rules = new GroupMatchRules();
        rules.setIsActive(Boolean.FALSE);
        rules.setModifier(deleteDTO.getModifier());
        rules.setModifyTime(new Date());
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(deleteDTO.getId());
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        return groupMatchRulesMapper.updateByExampleSelective(rules, example) > 0;
    }

    public List<GroupMatchRules> queryPaging(MatchRulePagingQueryDTO queryDTO) {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.page(queryDTO.getCurrentPage() - 1, queryDTO.getPageSize())
                .createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        if (queryDTO.getAppDep() != null) {
            criteria.andAppDepEqualTo(queryDTO.getAppDep());
        }
        if (queryDTO.getAppName() != null) {
            criteria.andAppNameEqualTo(queryDTO.getAppName());
        }
        if (queryDTO.getException() != null) {
            criteria.andExceptionEqualTo(queryDTO.getException());
        }
        if (queryDTO.getGroupId() != null) {
            criteria.andGroupIdEqualTo(queryDTO.getGroupId());
        }
        return groupMatchRulesMapper.selectByExample(example);
    }

    public Long count(MatchRuleCountDTO queryDTO) {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        if (queryDTO.getAppDep() != null) {
            criteria.andAppDepEqualTo(queryDTO.getAppDep());
        }
        if (queryDTO.getAppName() != null) {
            criteria.andAppNameEqualTo(queryDTO.getAppName());
        }
        if (queryDTO.getException() != null) {
            criteria.andExceptionEqualTo(queryDTO.getException());
        }
        if (queryDTO.getGroupId() != null) {
            criteria.andGroupIdEqualTo(queryDTO.getGroupId());
        }
        return groupMatchRulesMapper.countByExample(example);
    }

}

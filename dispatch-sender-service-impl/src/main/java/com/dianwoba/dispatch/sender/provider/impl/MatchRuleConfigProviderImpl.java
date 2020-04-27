package com.dianwoba.dispatch.sender.provider.impl;

import com.alibaba.fastjson.JSON;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleCountDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRulePagingQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleSaveDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.GroupInfoDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.MatchRuleInfoDTO;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.exception.ConfigException;
import com.dianwoba.dispatch.sender.manager.GroupMatchRulesManager;
import com.dianwoba.dispatch.sender.provider.MatchRuleConfigProvider;
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
public class MatchRuleConfigProviderImpl implements MatchRuleConfigProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatchRuleConfigProviderImpl.class);

    @Resource
    private GroupMatchRulesManager groupMatchRulesManager;

    @Override
    public void save(MatchRuleSaveDTO saveDTO) {
        if (saveDTO.getAppDep() == null) {
            throw new ConfigException("参数错误，未传入部门号");
        }
        if (saveDTO.getAppName() == null) {
            throw new ConfigException("参数错误，未传入应用名");
        }
        if (saveDTO.getException() == null) {
            throw new ConfigException("参数错误，未传入异常类型");
        }
        if (saveDTO.getGroupId() == null) {
            throw new ConfigException("参数错误，未传入群组号");
        }
        if (saveDTO.getCreator() == null) {
            throw new ConfigException("参数错误，未传入操作人员编号");
        }
        GroupMatchRules rules = new GroupMatchRules();
        rules.setAppDep(saveDTO.getAppDep());
        rules.setGroupId(saveDTO.getGroupId());
        rules.setCreator(saveDTO.getCreator());
        rules.setCreateTime(new Date());
        rules.setAppName(saveDTO.getAppName());
        rules.setException(saveDTO.getException());
        if (saveDTO.getKeyWords() != null) {
            rules.setKeyWords(saveDTO.getKeyWords());
        }
        if (saveDTO.getAtAll() != null) {
            rules.setAtAll(saveDTO.getAtAll());
        }
        if (saveDTO.getAtWho() != null) {
            rules.setAtWho(saveDTO.getAtWho());
        }
        if (saveDTO.getLevel() != null) {
            rules.setLevel(saveDTO.getLevel());
        }
        rules.setIsActive(Boolean.TRUE);
        groupMatchRulesManager.save(rules);
    }

    @Override
    public Boolean update(MatchRuleUpdateDTO updateDTO) {
        if (updateDTO.getId() == null) {
            throw new ConfigException("参数错误，未传入id");
        }
        if (updateDTO.getModifier() == null) {
            throw new ConfigException("参数错误，未传入操作人员编号");
        }
        GroupMatchRules rules = new GroupMatchRules();
        updateDTO.setId(updateDTO.getId());
        if (updateDTO.getGroupId() != null) {
            rules.setGroupId(updateDTO.getGroupId());
        }
        if (updateDTO.getLevel() != null) {
            rules.setLevel(updateDTO.getLevel());
        }
        if (updateDTO.getAtAll() != null) {
            rules.setAtAll(updateDTO.getAtAll());
        }
        if (updateDTO.getAtWho() != null) {
            rules.setAtWho(updateDTO.getAtWho());
        }
        rules.setModifier(updateDTO.getModifier());
        rules.setModifyTime(new Date());
        return groupMatchRulesManager.update(rules);
    }

    @Override
    public Boolean delete(MatchRuleDeleteDTO deleteDTO) {
        if (deleteDTO.getId() == null) {
            throw new ConfigException("参数错误，未传入id");
        }
        if (deleteDTO.getModifier() == null) {
            throw new ConfigException("参数错误，未传入修改者信息");
        }
        return groupMatchRulesManager.delete(deleteDTO);
    }

    @Override
    public Pagination<MatchRuleInfoDTO> query(MatchRulePagingQueryDTO queryDTO) {
        Pagination<MatchRuleInfoDTO> pagination = new Pagination<>();
        pagination.setCurrentPage(queryDTO.getCurrentPage());
        pagination.setPageSize(queryDTO.getPageSize());
        try {
            List<GroupMatchRules> groupInfoList = groupMatchRulesManager.queryPaging(queryDTO);
            long count = groupMatchRulesManager.totalCount();
            List<MatchRuleInfoDTO> resultList = Lists.newArrayList();
            groupInfoList.forEach(x -> resultList.add(ConvertUtils.convert2MatchRuleInfoDTO(x)));
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
    public Long totalCount(MatchRuleCountDTO countDTO) {
        return groupMatchRulesManager.count(countDTO);
    }
}

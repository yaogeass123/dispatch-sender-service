package com.dianwoba.dispatch.sender.provider;

import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleCountDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRulePagingQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleSaveDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleUpdateDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.MatchRuleInfoDTO;
import com.dianwoba.wireless.paging.Pagination;

/**
 * @author Polaris
 */
public interface MatchRuleConfigProvider {

    /**
     * 保存组匹配规则
     *
     * @param saveDTO MatchRulesSaveDTO
     */
    void save(MatchRuleSaveDTO saveDTO);

    /**
     * 更新匹配规则
     *
     * @param updateDTO MatchRuleUpdateDTO
     * @return Boolean
     */
    Boolean update(MatchRuleUpdateDTO updateDTO);

    /**
     * 删除匹配规则
     *
     * @param deleteDTO MatchRuleDeleteDTO
     * @return Boolean
     */
    Boolean delete(MatchRuleDeleteDTO deleteDTO);

    /**
     * 分页查询配置
     *
     * @param queryDTO MatchRulePagingQueryDTO
     * @return Pagination<MatchRuleInfoDTO>
     */
    Pagination<MatchRuleInfoDTO> query(MatchRulePagingQueryDTO queryDTO);


    /**
     * 根据条件查询总数
     *
     * @param countDTO MatchRuleCountDTO
     * @return Long
     */
    Long totalCount(MatchRuleCountDTO countDTO);
}

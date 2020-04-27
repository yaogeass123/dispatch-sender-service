package com.dianwoba.dispatch.sender.domain.dto.param.rule;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString(callSuper = true)
public class MatchRuleSaveDTO extends MatchRuleBaseDTO {

    private static final long serialVersionUID = -2654773958956201553L;

    private String keyWords;

    private Long groupId;

    private Byte level;

    private String atWho;

    private Boolean atAll;

    private String creator;

}

package com.dianwoba.dispatch.sender.domain.dto.result;

import com.dianwoba.dispatch.sender.domain.dto.param.rule.MatchRuleBaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Setter
@Getter
@ToString(callSuper = true)
public class MatchRuleInfoDTO extends MatchRuleBaseDTO {

    private static final long serialVersionUID = -6200550512322049558L;

    private String keyWords;

    private Long groupId;

    private Byte level;

    private String atWho;

    private Boolean atAll;

}

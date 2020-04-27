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
public class MatchRuleUpdateDTO extends MatchRuleBaseDTO {

    private static final long serialVersionUID = -7596083852396788488L;

    private Long id;

    private Long groupId;

    private Byte level;

    private String atWho;

    private Boolean atAll;

    private String modifier;

}

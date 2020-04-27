package com.dianwoba.dispatch.sender.domain.dto.param.rule;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Setter
@Getter
@ToString
public class MatchRuleCountDTO implements Serializable {

    private static final long serialVersionUID = 6590885716821423157L;

    private String appDep;

    private String exception;

    private String appName;

    private Long groupId;

}

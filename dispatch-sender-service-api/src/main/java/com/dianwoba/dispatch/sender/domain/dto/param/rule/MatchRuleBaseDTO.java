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
public class MatchRuleBaseDTO implements Serializable {

    private static final long serialVersionUID = 8619482600831141427L;

    private String appDep;

    private String exception;

    private String appName;

}

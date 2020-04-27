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
public class MatchRuleDeleteDTO implements Serializable {

    private static final long serialVersionUID = -282939798272126431L;

    private Long id;

    private String modifier;
}

package com.dianwoba.dispatch.sender.domain.dto.param.token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString
public class DingTokenDeleteDTO extends DingTokenBaseDTO{

    private static final long serialVersionUID = 3883950675631300743L;

    private Long id;

    private String token;

    private String modifier;
}

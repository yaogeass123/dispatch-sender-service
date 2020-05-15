package com.dianwoba.dispatch.sender.domain.dto.param.token;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString(callSuper = true)
public class DingTokenUpdateDTO extends DingTokenBaseDTO{

    private static final long serialVersionUID = -7799454858451369906L;

    private Long id;

    private String token;

    private String keyWords;

    private String secret;

    private String modifier;

}

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
public class DingTokenQueryDTO extends DingTokenBaseDTO {

    private static final long serialVersionUID = 5666244045894671002L;

    private Long id;

    private String token;

}

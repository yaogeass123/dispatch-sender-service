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
public class DingTokenSaveDTO extends DingTokenBaseDTO {

    private static final long serialVersionUID = 5666244045894671002L;

    private String token;

    private String keyWords;

    private String secret;

    private String creator;

}

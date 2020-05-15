package com.dianwoba.dispatch.sender.domain.dto.result;

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
public class DingTokenInfoDTO implements Serializable {

    private static final long serialVersionUID = -6470219824954404029L;

    private Long id;

    private String appDep;

    private Long groupId;

    private String token;

    private String keyWords;

    private String secret;

    private Byte status;
}

package com.dianwoba.dispatch.sender.domain.dto.param.token;

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
public class DingTokenBaseDTO implements Serializable {

    private static final long serialVersionUID = -5997638396929277036L;

    private String appDep;

    private Long groupId;

}

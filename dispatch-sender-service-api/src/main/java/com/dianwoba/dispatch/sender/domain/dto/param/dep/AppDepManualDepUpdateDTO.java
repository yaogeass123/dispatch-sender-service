package com.dianwoba.dispatch.sender.domain.dto.param.dep;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString(callSuper = true)
public class AppDepManualDepUpdateDTO extends AppDepBaseDTO{

    private static final long serialVersionUID = -9135408960690096217L;

    private String appName;

    private Integer manualDepId;

    private Integer deFaultDepId;

    private Integer newDepId;

    private String modifier;
}

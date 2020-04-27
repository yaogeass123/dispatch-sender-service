package com.dianwoba.dispatch.sender.domain.dto.param.dep;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Administrator
 */
@Setter
@Getter
@ToString(callSuper = true)
public class AppDepQueryDTO extends AppDepBaseDTO {

    private static final long serialVersionUID = -5249336029081690321L;

    private Integer depId;

}

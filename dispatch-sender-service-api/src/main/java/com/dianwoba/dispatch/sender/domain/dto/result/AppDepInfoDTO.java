package com.dianwoba.dispatch.sender.domain.dto.result;

import com.dianwoba.dispatch.sender.domain.dto.param.dep.AppDepBaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString
public class AppDepInfoDTO extends AppDepBaseDTO {

    private static final long serialVersionUID = 7124765644714781528L;

    private Long id;

    private String appName;

    private Integer defaultDepId;

    private Integer manualDepId;

}

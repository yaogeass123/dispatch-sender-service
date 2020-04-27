package com.dianwoba.dispatch.sender.domain.dto.param.group;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString(callSuper = true)
public class GroupUpdateDTO extends GroupBaseDTO {

    private static final long serialVersionUID = 7032908767552858390L;

    private Long groupId;

    private String modifier;

}

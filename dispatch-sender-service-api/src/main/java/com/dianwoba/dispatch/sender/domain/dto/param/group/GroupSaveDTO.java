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
public class GroupSaveDTO extends GroupBaseDTO {

    private static final long serialVersionUID = 7655354567229622309L;

    private String creator;
}

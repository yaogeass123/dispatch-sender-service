package com.dianwoba.dispatch.sender.domain.dto.param.group;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString(callSuper = true)
public class GroupDeleteDTO extends GroupBaseDTO {

    private static final long serialVersionUID = -1712965435129363478L;

    private Long groupId;

    private String modifier;
}

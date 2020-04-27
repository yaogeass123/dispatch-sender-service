package com.dianwoba.dispatch.sender.domain.dto.result;

import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupBaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString(callSuper = true)
public class GroupInfoDTO extends GroupBaseDTO {

    private static final long serialVersionUID = -37796549339325869L;

    private Long id;
}

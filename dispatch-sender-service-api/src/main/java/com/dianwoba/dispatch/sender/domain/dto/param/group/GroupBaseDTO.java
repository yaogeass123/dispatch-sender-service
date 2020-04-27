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
@ToString
public class GroupBaseDTO implements Serializable {

    private static final long serialVersionUID = 1963692170457306474L;

    private String groupName;

    private String atWho;

    private Boolean atAll;

    private String mail;

}

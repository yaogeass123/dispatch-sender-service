package com.dianwoba.dispatch.sender.domain.dto.param.group;

import com.dianwoba.wireless.paging.PagingSearchable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Setter
@Getter
@ToString(callSuper = true)
public class GroupPagingQueryDTO extends PagingSearchable {

    private static final long serialVersionUID = -4540653064380365808L;

}

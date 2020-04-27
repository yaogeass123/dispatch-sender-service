package com.dianwoba.dispatch.sender.domain.dto.param.rule;

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
public class MatchRulePagingQueryDTO extends PagingSearchable {

    private static final long serialVersionUID = 36369433937469937L;

    private String appDep;

    private String exception;

    private String appName;

    private Long groupId;

}

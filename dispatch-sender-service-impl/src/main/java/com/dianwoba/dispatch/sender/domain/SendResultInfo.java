package com.dianwoba.dispatch.sender.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Administrator
 */
@Setter
@Getter
@ToString
public class SendResultInfo {

    private Boolean isSuccess;

    private List<Long> ids;

    private String msg;

    private Long tokenId;

    private Integer errorCode;

    private String errorMsg;

    private String appDep;

    private String appName;
}

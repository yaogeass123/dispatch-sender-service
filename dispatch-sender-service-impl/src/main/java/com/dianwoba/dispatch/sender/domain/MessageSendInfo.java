package com.dianwoba.dispatch.sender.domain;

import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Setter
@Getter
@ToString
public class MessageSendInfo {

    private List<Long> ids;

    private String clusterId;

    private Long groupId;

    private String appName;

    private String ips;

    private String exceptionType;

    private String digest;

    private String msg;

    private Byte level;

    private Date startTm;

    private Date endTm;

    private Integer count;

    private String atWho;

    private Boolean atAll;

    private Date insertTm;

    private Byte status;

    private Date sendTm;

    private String robotErrorCode;

    private String robotErrorMsg;
}

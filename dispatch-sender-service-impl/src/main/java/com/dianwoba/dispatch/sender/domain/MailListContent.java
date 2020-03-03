package com.dianwoba.dispatch.sender.domain;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Setter
@Getter
@ToString
public class MailListContent {

    private String message;

    private String ips;

    private Integer count;

    private Date insert;

    private Date start;

    private Date end;
}

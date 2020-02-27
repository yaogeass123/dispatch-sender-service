package com.dianwoba.dispatch.sender.domain;

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

}

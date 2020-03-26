package com.dianwoba.dispatch.sender.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */

@Setter
@Getter
@ToString
public class MessageSendCountPO {

    private Long groupId;

    private Integer count;
}

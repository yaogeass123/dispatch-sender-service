package com.dianwoba.dispatch.sender.en;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polatis
 */

@Getter
public enum StatusEn {


    INIT(0, "初始化"),

    ERROR(8, "消息发送异常"),

    IGNORE(9, "消息被忽略"),

    SUCCESS(10, "消息发送成功");


    private final Byte statusCode;

    private final String statusMsg;

    StatusEn(int statusCode, String statusMsg) {
        this.statusCode = (byte)statusCode;
        this.statusMsg = statusMsg;
    }

}

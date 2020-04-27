package com.dianwoba.dispatch.sender.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 9045901179459116302L;

    private String msg;

    public ConfigException(String msg) {
        super(msg);
        this.msg = msg;
    }
}

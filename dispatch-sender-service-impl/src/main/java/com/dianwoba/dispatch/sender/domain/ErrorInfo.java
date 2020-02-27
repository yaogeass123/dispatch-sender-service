package com.dianwoba.dispatch.sender.domain;

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
public class ErrorInfo {

    private List<Long> ids;

    private String errorCode;

    private String errorMsg;
}

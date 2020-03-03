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
public class StatisticContent {

    private Integer ignore;

    private String errorCode;

    private Integer errorTimes;

}

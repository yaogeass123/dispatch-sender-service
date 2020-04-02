package com.dianwoba.dispatch.sender.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class SwitchConfigUtils {

    @Value("${threadMultiple:2}")
    private String threadMultiple;

    @Value("${sendThreadSleepTime:1000}")
    private String sendThreadSleepTime;

    @Value("${futureTimeOut:2000}")
    private String futureTimeOut;

    public String getThreadMultiple() {
        return threadMultiple;
    }

    public String getSendThreadSleepTime() {
        return sendThreadSleepTime;
    }

    public String getFutureTimeOut() {
        return futureTimeOut;
    }
}

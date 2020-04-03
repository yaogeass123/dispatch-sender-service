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

    @Value("${time2:5}")
    private String time2;

    @Value("${time2:6}")
    private String time3;

    @Value("${time2:7}")
    private String time4;

    public String getThreadMultiple() {
        return threadMultiple;
    }

    public String getSendThreadSleepTime() {
        return sendThreadSleepTime;
    }

    public String getFutureTimeOut() {
        return futureTimeOut;
    }

    public int getTime2() {return Integer.parseInt(time2);}

    public int getTime3() {return Integer.parseInt(time3);}

    public int getTime4() {return Integer.parseInt(time4);}

}

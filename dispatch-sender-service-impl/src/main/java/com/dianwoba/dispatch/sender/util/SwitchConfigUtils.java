package com.dianwoba.dispatch.sender.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class SwitchConfigUtils {

    @Value("${switches-threadMultiple:2}")
    private String threadMultiple;

    public String getThreadMultiple() {
        return threadMultiple;
    }

}

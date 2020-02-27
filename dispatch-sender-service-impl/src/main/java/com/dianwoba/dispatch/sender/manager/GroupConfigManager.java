package com.dianwoba.dispatch.sender.manager;

import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */

@Component
public class GroupConfigManager {

    public String queryGroupName(long groupId) {

        return "name";
    }
}

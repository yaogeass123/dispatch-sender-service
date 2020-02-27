package com.dianwoba.dispatch.sender.domain;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString
public class AppDepInfo implements Serializable {

    private static final long serialVersionUID = -4550876479892476765L;

    private String appCode;

    private String ownersDepId;

    private String ownersPhone;

    private String developersDepId;

    private String developersPhone;

    private String groupMail;

}

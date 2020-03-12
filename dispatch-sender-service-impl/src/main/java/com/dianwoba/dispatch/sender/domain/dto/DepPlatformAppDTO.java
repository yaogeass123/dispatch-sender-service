package com.dianwoba.dispatch.sender.domain.dto;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Setter
@Getter
@ToString
public class DepPlatformAppDTO implements Serializable {

    private static final long serialVersionUID = -987566468716438366L;

    private String name;
    private String appCode;
    private String ownersCode;
    private String ownersName;
    private String developersCode;
    private String developersName;
    private String svn;
    private Integer port;
    private String mailGroup;
    private String description;
    private Date createTime;
    private Date modifyTime;
    private String lastDeployStaffCode;
}

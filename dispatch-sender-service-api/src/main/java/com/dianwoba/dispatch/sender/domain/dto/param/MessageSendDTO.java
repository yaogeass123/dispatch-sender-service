package com.dianwoba.dispatch.sender.domain.dto.param;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Polaris
 */
@Getter
@Setter
@ToString
public class MessageSendDTO implements Serializable {

   private String appName;

   private String ip;

   private String digest;

   private String exceptionType;

   private String msg;

   private Long time;

   private Byte level;

}

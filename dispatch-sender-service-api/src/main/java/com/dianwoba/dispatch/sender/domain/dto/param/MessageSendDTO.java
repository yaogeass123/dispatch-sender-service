package com.dianwoba.dispatch.sender.domain.dto.param;

import com.dianwoba.dispatch.sender.en.LevelEn;
import java.io.Serializable;
import java.util.Date;
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

   private static final long serialVersionUID = 4866332110429072469L;

   private String appName;

   private String ip;

   private String digest;

   private String exceptionType;

   private String msg;

   private Date time;

   private LevelEn level;

}

package com.dianwoba.dispatch.sender;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.provider.MessageSendProvider;
import com.dianwoba.wireless.fundamental.util.AppUtils;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class SenderService {

    private static MessageSendProvider messageSendProvider;

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderService.class);

    public static void sendDingMsg(String msg, LevelEn level) {
        doSendDingMsg(msg, level);
    }

    public static void sendDingMsg(String msg) {
        doSendDingMsg(msg, LevelEn.MEDIUM);
    }

    private static void doSendDingMsg(String msg, LevelEn level) {
        if (messageSendProvider == null) {
            messageSendProvider = SpringUtils.getBean(MessageSendProvider.class);
            if (messageSendProvider == null) {
                LOGGER.error("MessageSendProvider is null");
                return;
            }
        }
        MessageSendDTO messageSendDTO = new MessageSendDTO();
        messageSendDTO.setAppName(AppUtils.PROJECT_NAME);
        messageSendDTO.setIp(AppUtils.IP);
        messageSendDTO.setMsg(msg);
        messageSendDTO.setLevel(level);
        messageSendDTO.setTime(new Date());
        messageSendDTO.setExceptionType(Constant.DING_MESSAGE);
        StackTraceElement[] current = Thread.currentThread().getStackTrace();
        messageSendDTO.setDigest(current[3].getClassName() + ":" + current[3].getLineNumber());
        LOGGER.info("MessageSendDto: {}", JSONObject.toJSONString(messageSendDTO));
        messageSendProvider.send(messageSendDTO);
    }

}

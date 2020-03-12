package com.dianwoba.dispatch.sender;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.provider.MessageSendProvider;
import com.dianwoba.wireless.fundamental.util.AppUtils;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Polaris
 */

public class SenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SenderService.class);

    private static MessageSendProvider messageSendProvider;

    @PostConstruct
    public void init() {
        messageSendProvider = SpringUtils.getBean(MessageSendProvider.class);
    }

    public static void sendDingMsg(String msg, LevelEn level) {
        if (messageSendProvider == null) {
            LOGGER.error("MessageSendProvider is null");
            return;
        }
        MessageSendDTO messageSendDTO = new MessageSendDTO();
        messageSendDTO.setAppName(AppUtils.PROJECT_NAME);
        messageSendDTO.setIp(AppUtils.IP);
        messageSendDTO.setMsg(msg);
        messageSendDTO.setLevel(level);
        messageSendDTO.setTime(new Date());
        messageSendDTO.setExceptionType(Constant.DING_MESSAGE);
        StackTraceElement[] current = Thread.currentThread().getStackTrace();
        messageSendDTO.setDigest(current[2].getClassName() + current[2].getLineNumber());
        messageSendProvider.send(messageSendDTO);
    }

    public static void sendDingMsg(String msg) {
        sendDingMsg(msg, LevelEn.MEDIUM);
    }
}

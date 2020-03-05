package com.dianwoba.dispatch.sender.provider.impl;

import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.manager.MessageLogManager;
import com.dianwoba.dispatch.sender.provider.MessageSendProvider;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class MessageSendProviderImpl implements MessageSendProvider {

    @Resource
    private MessageLogManager messageLogManager;

    @Override
    public void send(MessageSendDTO messageSendDTO) {
        MessageLog log = ConvertUtils.convert2MessageLog(messageSendDTO);
        messageLogManager.save(log);
    }
}

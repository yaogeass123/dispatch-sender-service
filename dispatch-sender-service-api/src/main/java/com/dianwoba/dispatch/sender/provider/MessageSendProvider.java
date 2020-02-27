package com.dianwoba.dispatch.sender.provider;

import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;

/**
 * @author Polaris
 */
public interface MessageSendProvider {

    /**
     * 普通发送接口
     * @param messageSendDTO 入参信息
     */
    void send(MessageSendDTO messageSendDTO);

}

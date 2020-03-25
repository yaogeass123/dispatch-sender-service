package com.dianwoba.dispatch.sender.job;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.runnable.MessageSender;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import com.dianwoba.wireless.threadpool.MonitoringThreadPool;
import com.dianwoba.wireless.threadpool.MonitoringThreadPoolMaintainer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class MessageSendHandler extends AbstractJobExecuteService {

    private static MonitoringThreadPool messageSendThreadPool = MonitoringThreadPoolMaintainer
            .newFixedThreadPool("message-send", 500);

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSendHandler.class);

    @Resource
    private MessageSenderManager messageSenderManager;

    @Override
    public void doExecute(ExecuteContext executeContext) {
        List<MessageSend> messageSends = messageSenderManager.queryMessageToBeSent();
        List<MessageSendInfo> infoList = messageSends.stream()
                .map(ConvertUtils::convert2MessageSendInfo).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(infoList)) {
            LOGGER.info("infoList:{}", JSONObject.toJSONString(infoList));
            Map<Long, List<MessageSendInfo>> group = infoList.stream()
                    .collect(Collectors.groupingBy(MessageSendInfo::getGroupId));
            group.values().forEach(v -> messageSendThreadPool.submit(new MessageSender(v)));
        } else {
            LOGGER.info("无消息发送");
        }

    }
}

package com.dianwoba.dispatch.sender.job;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.MessageSendCountPO;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.runnable.MessageSender;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import com.dianwoba.wireless.threadpool.MonitoringThreadPool;
import com.dianwoba.wireless.threadpool.MonitoringThreadPoolMaintainer;
import java.util.List;
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
        List<MessageSendCountPO> countList = messageSenderManager
                .countByGroupId(StatusEn.INIT.getStatusCode());
        if (CollectionUtils.isNotEmpty(countList)) {
            LOGGER.info("infoList:{}", JSONObject.toJSONString(countList));
            countList.forEach(v -> messageSendThreadPool.submit(new MessageSender(v.getGroupId())));
        } else {
            LOGGER.info("无消息发送");
        }
    }
}

package com.dianwoba.dispatch.sender.job;

import com.dianwoba.dispatch.sender.cache.GroupConfigCache;
import com.dianwoba.dispatch.sender.runnable.MessageSender;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import com.dianwoba.wireless.threadpool.MonitoringThreadPool;
import com.dianwoba.wireless.threadpool.MonitoringThreadPoolMaintainer;
import java.util.Set;
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
    private GroupConfigCache groupConfigCache;

    @Override
    public void doExecute(ExecuteContext executeContext) {
        //需要遍历每个群，因为要更新群可发送次数
        Set<Long> groupIdList = groupConfigCache.queryAllFromClientCache().keySet();
        if (CollectionUtils.isNotEmpty(groupIdList)) {
            groupIdList.forEach(v -> messageSendThreadPool.submit(new MessageSender(v)));
        } else {
            LOGGER.info("无消息发送");
        }
    }
}

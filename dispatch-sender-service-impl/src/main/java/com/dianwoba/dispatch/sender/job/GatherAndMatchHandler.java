package com.dianwoba.dispatch.sender.job;


import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.manager.MessageLogManager;
import com.dianwoba.dispatch.sender.runnable.GroupMatcher;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import com.dianwoba.wireless.threadpool.MonitoringThreadPool;
import com.dianwoba.wireless.threadpool.MonitoringThreadPoolMaintainer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */

@Component
public class GatherAndMatchHandler extends AbstractJobExecuteService {

    private static MonitoringThreadPool gatherMatchThreadPool = MonitoringThreadPoolMaintainer
            .newFixedThreadPool("group-match", 100);

    @Resource
    private MessageLogManager messageLogManager;

    @Override
    public void doExecute(ExecuteContext executeContext) {

        //1、读待处理数据
        List<MessageLog> unhandledMessage = messageLogManager.queryAllUnhandled();
        if(CollectionUtils.isEmpty(unhandledMessage)) {
            return;
        }
        //2. 分组聚合
        Map<String, List<MessageLog>> unhandledGroup = unhandledMessage.stream().collect(Collectors
                .groupingBy(message -> String.format(Constant.GROUP_COMMON_FORMAT, message.getAppName(),
                        message.getExceptionType(), message.getDigest())));
        //3、分组进行匹配落库
        unhandledGroup.values()
                .forEach(list -> gatherMatchThreadPool.submit(new GroupMatcher(list)));

    }
}

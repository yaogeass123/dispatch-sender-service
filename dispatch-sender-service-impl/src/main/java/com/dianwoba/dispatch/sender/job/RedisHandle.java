package com.dianwoba.dispatch.sender.job;

import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;

/**
 * 每分钟定时任务执行
 * 查token缓存，以群维度拼装bucket，放入redis
 *
 * @author Polaris
 */
public class RedisHandle extends AbstractJobExecuteService {

    @Override
    public void doExecute(ExecuteContext executeContext) {

    }
}

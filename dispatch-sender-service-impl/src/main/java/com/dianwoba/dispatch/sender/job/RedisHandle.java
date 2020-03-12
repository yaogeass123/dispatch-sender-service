package com.dianwoba.dispatch.sender.job;

import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.util.BucketUtils;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 每分钟定时任务执行
 * 查token缓存，以群维度拼装bucket，放入redis
 *
 * @author Polaris
 */
@Component
public class RedisHandle extends AbstractJobExecuteService {

    @Resource
    private DingTokenConfigCache dingTokenConfigCache;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doExecute(ExecuteContext executeContext) {
        Map<String, List<DingTokenConfig>> map = dingTokenConfigCache.queryAllFromClientCache();
        map.forEach((k, v) -> stringRedisTemplate.opsForValue().set("redis_" + k,
                "0:" + BucketUtils.buildBucketString(v.stream().map(DingTokenConfig::getId).collect(Collectors.toSet())),
                15, TimeUnit.SECONDS));
    }
}

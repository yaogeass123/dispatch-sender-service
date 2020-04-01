package com.dianwoba.dispatch.sender.job;

import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.util.BucketUtils;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 每分钟定时任务执行
 * 查token缓存，以群维度拼装bucket，放入redis
 *
 * @author Polaris
 */
@Component
public class RedisHandler extends AbstractJobExecuteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHandler.class);

    @Resource
    private DingTokenConfigCache dingTokenConfigCache;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void doExecute(ExecuteContext executeContext) {
        Map<String, List<DingTokenConfig>> map = dingTokenConfigCache.queryAllFromClientCache();
        map.forEach((k, v) -> {
            try {
                stringRedisTemplate.opsForValue()
                        .set(String.format(Constant.REDIS_SEND_STR, v.get(0).getGroupId()),
                        "0:" + BucketUtils.buildBucketString(v.stream().map(DingTokenConfig::getId).collect(Collectors.toSet())),
                        60, TimeUnit.SECONDS);
                LOGGER.info("组{}次数更新成功", k);
            } catch (Exception e) {
                LOGGER.error("更新组{}次数失败", k, e);
            }
        });
    }
}

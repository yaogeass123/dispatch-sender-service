package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.util.BucketUtils;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

public class QuickTest extends UnitTestBase {

    @Resource
    private DingTokenConfigCache dingTokenConfigCache;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${switches-redisGroupKeyTimeOut:15}")
    private String redisGroupKeyTimeOut;


    private static final Logger LOGGER = LoggerFactory.getLogger(QuickTest.class);

    @Test
    public void test() {
        doTest();
    }

    private void doTest() {
        Map<String, List<DingTokenConfig>> map = dingTokenConfigCache.queryAllFromClientCache();
        map.forEach((k, v) -> {
            try {
                stringRedisTemplate.opsForValue().set("redis_" + k,
                        "0:" + BucketUtils.buildBucketString(v.stream().map(DingTokenConfig::getId).collect(
                                Collectors.toSet())),
                        Integer.parseInt(redisGroupKeyTimeOut), TimeUnit.SECONDS);
                LOGGER.info("组{}次数更新成功", k);
            } catch (Exception e) {
                LOGGER.error("更新组{}次数失败", k, e);
            }
        });
    }
}

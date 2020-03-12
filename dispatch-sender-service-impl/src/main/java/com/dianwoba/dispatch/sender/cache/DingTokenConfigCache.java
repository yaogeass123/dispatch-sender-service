package com.dianwoba.dispatch.sender.cache;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.manager.DingTokenConfigManager;
import com.dianwoba.wireless.clientcache.spring.boot.autoconfigure.api.AbstractFullyClientCache;
import com.dianwoba.wireless.paging.PagingSearchable;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class DingTokenConfigCache extends
        AbstractFullyClientCache<String, List<DingTokenConfig>, DingTokenConfig> {


    @Resource
    private DingTokenConfigManager dingTokenConfigManager;

    @Override
    public String cronExpression() {
        //每分钟缓存一次
        return "0 0/1 * * * ?";
    }

    @Override
    public Long totalCount() {
        return dingTokenConfigManager.totalCount();
    }

    @Override
    public Cache<String, List<DingTokenConfig>> buildCache(List<DingTokenConfig> list) {
        Cache<String, List<DingTokenConfig>> cache = Caffeine.newBuilder().build();
        Map<String, List<DingTokenConfig>> map = list.stream().collect(Collectors.groupingBy(
                dingTokenConfig -> String
                        .format(Constant.GROUP_TOKEN_PREFIX, dingTokenConfig.getGroupId())));
        cache.putAll(map);
        return cache;
    }

    @Override
    public List<DingTokenConfig> queryFromDb4OnePage(PagingSearchable pagingSearchable) {
        return dingTokenConfigManager.queryByPage(pagingSearchable);
    }
}

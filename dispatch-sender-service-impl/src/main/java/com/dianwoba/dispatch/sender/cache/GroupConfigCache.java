package com.dianwoba.dispatch.sender.cache;

import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.wireless.clientcache.spring.boot.autoconfigure.api.AbstractFullyClientCache;
import com.dianwoba.wireless.paging.PagingSearchable;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class GroupConfigCache extends AbstractFullyClientCache<Long, DingGroupName, DingGroupName> {

    @Resource
    private GroupConfigManager groupConfigManager;

    @Override
    public Long totalCount() {
        return groupConfigManager.totalCount();
    }

    @Override
    public Cache buildCache(List<DingGroupName> list) {
        Cache<Long, DingGroupName> cache = Caffeine.newBuilder().build();
        list.forEach( v -> cache.put(v.getId(), v));
        return cache;
    }

    @Override
    public List<DingGroupName> queryFromDb4OnePage(PagingSearchable pagingSearchable) {
        return groupConfigManager.queryByPage(pagingSearchable);
    }
}

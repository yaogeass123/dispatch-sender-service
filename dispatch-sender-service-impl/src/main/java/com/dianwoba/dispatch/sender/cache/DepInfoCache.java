package com.dianwoba.dispatch.sender.cache;

import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoba.dispatch.sender.manager.DepInfoManager;
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
public class DepInfoCache extends AbstractFullyClientCache<Integer, DepInfo, DepInfo> {

    @Resource
    private DepInfoManager depInfoManager;

    @Override
    public Long totalCount() {
        return depInfoManager.totalCount();
    }

    @Override
    public Cache<Integer, DepInfo> buildCache(List<DepInfo> list) {
        Cache<Integer, DepInfo> cache = Caffeine.newBuilder().build();
        list.forEach(t -> cache.put(t.getId(), t));
        return cache;
    }

    @Override
    public List<DepInfo> queryFromDb4OnePage(PagingSearchable pagingSearchable) {
        return depInfoManager.queryByPage(pagingSearchable);
    }
}

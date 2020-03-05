package com.dianwoba.dispatch.sender.cache;

import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.manager.AppDepManager;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
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
public class AppDepCache extends AbstractFullyClientCache<String, AppDepInfo, AppDep> {

    @Resource
    private AppDepManager appDepManager;

    @Override
    public Long totalCount() {
        return appDepManager.totalCount();
    }

    @Override
    public Cache<String, AppDepInfo> buildCache(List<AppDep> list) {
        Cache<String, AppDepInfo> cache = Caffeine.newBuilder().build();
        list.forEach(
                appDep -> cache.put(appDep.getAppName(), ConvertUtils.convert2AppDepInfo(appDep)));
        return cache;
    }

    @Override
    public List<AppDep> queryFromDb4OnePage(PagingSearchable pagingSearchable) {
        return appDepManager.queryByPage(pagingSearchable);
    }
}

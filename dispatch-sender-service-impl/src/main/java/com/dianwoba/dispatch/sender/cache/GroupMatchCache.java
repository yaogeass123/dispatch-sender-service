package com.dianwoba.dispatch.sender.cache;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.manager.GroupMatchRulesManager;
import com.dianwoba.wireless.clientcache.spring.boot.autoconfigure.api.AbstractFullyClientCache;
import com.dianwoba.wireless.paging.PagingSearchable;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class GroupMatchCache extends
        AbstractFullyClientCache<String, List<GroupMatchRules>, GroupMatchRules> {

    @Resource
    private GroupMatchRulesManager groupMatchRulesManager;

    @Override
    public Long totalCount() {
        return groupMatchRulesManager.totalCount();
    }

    @Override
    public Cache<String, List<GroupMatchRules>> buildCache(List<GroupMatchRules> list) {
        Cache<String, List<GroupMatchRules>> cache = Caffeine.newBuilder().build();
        Map<String, List<GroupMatchRules>> map = list.stream().collect(Collectors.groupingBy(rule -> {
                String exception = rule.getException();
                String name = rule.getAppName();
                return String.format(Constant.GROUP_COMMON_FORMAT, rule.getAppDep(), exception, name);
            }));
        cache.putAll(map);
        return cache;
    }

    @Override
    public List<GroupMatchRules> queryFromDb4OnePage(PagingSearchable pagingSearchable) {
        return groupMatchRulesManager.queryByPage(pagingSearchable);
    }
}

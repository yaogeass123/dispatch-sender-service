package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.cache.GroupConfigCache;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.entity.DingGroupNameExample;
import com.dianwoba.dispatch.sender.entity.DingGroupNameExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DingGroupNameMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */

@Component
public class GroupConfigManager {

    @Resource
    private DingGroupNameMapper groupNameMapper;

    @Resource
    private GroupConfigCache groupConfigCache;

    public Long totalCount() {
        DingGroupNameExample example = new DingGroupNameExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        return groupNameMapper.countByExample(example);
    }

    public List<DingGroupName> queryByPage(PagingSearchable pagingSearchable) {
        DingGroupNameExample example = new DingGroupNameExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return groupNameMapper.selectByExample(example);
    }

    public String findGroupNameByCache(Long id) {
        DingGroupName groupName = groupConfigCache.queryFromClientCache(id);
        if (groupName == null) {
            return String.valueOf(id);
        }
        return groupName.getGroupName();
    }


}

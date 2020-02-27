package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.entity.AppDepExample;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.entity.GroupMatchRulesExample;
import com.dianwoba.dispatch.sender.entity.GroupMatchRulesExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.GroupMatchRulesMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
public class GroupMatchRulesManager {

    @Resource
    private GroupMatchRulesMapper groupMatchRulesMapper;

    public Long totalCount() {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        return groupMatchRulesMapper.countByExample(example);
    }

    public List<GroupMatchRules> queryByPage(PagingSearchable pagingSearchable) {
        GroupMatchRulesExample example = new GroupMatchRulesExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return groupMatchRulesMapper.selectByExample(example);
    }

}

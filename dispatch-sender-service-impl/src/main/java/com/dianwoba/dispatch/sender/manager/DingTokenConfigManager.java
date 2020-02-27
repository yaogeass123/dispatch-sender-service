package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.DingTokenConfigExample;
import com.dianwoba.dispatch.sender.entity.DingTokenConfigExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DingTokenConfigMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class DingTokenConfigManager {

    @Resource
    private DingTokenConfigMapper dingTokenConfigMapper;

    public Long totalCount() {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        return dingTokenConfigMapper.countByExample(example);
    }

    public List<DingTokenConfig> queryByPage(PagingSearchable pagingSearchable) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return dingTokenConfigMapper.selectByExample(example);
    }

}

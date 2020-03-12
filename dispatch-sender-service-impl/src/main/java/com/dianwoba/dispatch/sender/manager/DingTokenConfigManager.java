package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.DingTokenConfigExample;
import com.dianwoba.dispatch.sender.entity.DingTokenConfigExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DingTokenConfigMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.Date;
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
        criteria.andStatusEqualTo(Constant.TOKEN_NORMAL);
        return dingTokenConfigMapper.countByExample(example);
    }

    public List<DingTokenConfig> queryByPage(PagingSearchable pagingSearchable) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(true);
        criteria.andStatusEqualTo(Constant.TOKEN_NORMAL);
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return dingTokenConfigMapper.selectByExample(example);
    }

    public int setTokenError(List<Long> ids) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        criteria.andStatusEqualTo(Constant.TOKEN_NORMAL);
        DingTokenConfig record = new DingTokenConfig();
        record.setModifier(Constant.DEFAULT_STAFF);
        record.setModifyTime(new Date());
        record.setStatus(Constant.TOKEN_ERROR);
        return dingTokenConfigMapper.updateByExampleSelective(record, example);
    }

}

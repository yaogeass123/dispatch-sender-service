package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.entity.AppDepExample;
import com.dianwoba.dispatch.sender.entity.AppDepExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.AppDepMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class AppDepManager {

    @Resource
    private AppDepMapper appDepMapper;

    public Long totalCount() {
        return appDepMapper.countByExample(null);
    }

    public List<AppDep> queryByPage(PagingSearchable pagingSearchable) {
        AppDepExample example = new AppDepExample();
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return appDepMapper.selectByExample(example);
    }


}

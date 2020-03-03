package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.entity.AppDep.Column;
import com.dianwoba.dispatch.sender.entity.AppDepExample;
import com.dianwoba.dispatch.sender.entity.AppDepExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.AppDepMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    public List<AppDep> queryLastModify(){
        AppDepExample example = new AppDepExample();
        example.setOrderByClause("dep_plat_modify_time desc");
        example.limit(1);
        return appDepMapper.selectByExample(example);
    }

    public List<AppDep> queryConfigExist(List<String> appNames){
        AppDepExample example = new AppDepExample();
        Criteria criteria = example.createCriteria();
        criteria.andAppNameIn(appNames);
        return appDepMapper.selectByExample(example);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<AppDep> lists) {
        int max = Constant.BATCH_INSERT_MAX_SIZE;
        while (lists.size() > max) {
            appDepMapper.batchInsertSelective(lists.subList(0, max), Column.appName,
                    Column.ownersDepId, Column.ownersPhone, Column.developersDepId,
                    Column.developersPhone, Column.depPlatModifyTime, Column.createTime, Column.modifyTime);
            lists = lists.subList(max, lists.size());
        }
        if (lists.size() > 0) {
            appDepMapper.batchInsertSelective(lists, Column.appName, Column.ownersDepId,
                    Column.ownersPhone, Column.developersDepId, Column.developersPhone,
                    Column.depPlatModifyTime, Column.createTime, Column.modifyTime);
        }
    }

    public int update(AppDep appDep) {
        return appDepMapper.updateByPrimaryKey(appDep);
    }

}

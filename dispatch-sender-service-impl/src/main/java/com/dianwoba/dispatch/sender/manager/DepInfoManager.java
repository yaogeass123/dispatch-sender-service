package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.entity.AppDepExample;
import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoba.dispatch.sender.entity.DepInfoExample;
import com.dianwoba.dispatch.sender.entity.DepInfoExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DepInfoMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Polaris
 */

@Component
public class DepInfoManager {

    @Resource
    private DepInfoMapper depInfoMapper;

    public Long totalCount() {
        return depInfoMapper.countByExample(null);
    }

    public List<DepInfo> queryByPage(PagingSearchable pagingSearchable) {
        DepInfoExample example = new DepInfoExample();
        if (pagingSearchable.isPaging()) {
            example.page(pagingSearchable.getCurrentPage() - 1, pagingSearchable.getPageSize());
        }
        return depInfoMapper.selectByExample(example);
    }

    public List<DepInfo> queryByAppName(String appName) {
        DepInfoExample example = new DepInfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(appName);
        return depInfoMapper.selectByExample(example);
    }

    public DepInfo queryById(int id) {
        return depInfoMapper.selectByPrimaryKey(id);
    }

    public void save(DepInfo depInfo) {
        depInfo.setCreateTime(new Date());
        depInfoMapper.insertSelective(depInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAndUpdate(DepInfo depInfo, List<Integer> ids) {
        DepInfoExample example = new DepInfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        DepInfo record = new DepInfo();
        record.setNewest(depInfo.getId());
        record.setModifyTime(new Date());
        save(depInfo);
        depInfoMapper.updateByExampleSelective(record, example);
    }


}

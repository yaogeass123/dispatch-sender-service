package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoba.dispatch.sender.entity.DepInfo.Column;
import com.dianwoba.dispatch.sender.entity.DepInfoExample;
import com.dianwoba.dispatch.sender.entity.DepInfoExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DepInfoMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
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
        depInfo.setCreator(Constant.DEFAULT_STAFF);
        depInfo.setCreateTime(new Date());
        depInfo.setNewest(depInfo.getId());
        depInfoMapper.insertSelective(depInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveAndUpdate(DepInfo depInfo, List<Integer> ids) {
        DepInfoExample example = new DepInfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        DepInfo record = new DepInfo();
        record.setNewest(depInfo.getId());
        record.setModifier(Constant.DEFAULT_STAFF);
        record.setModifyTime(new Date());
        save(depInfo);
        depInfoMapper.updateByExampleSelective(record, example);
    }

    public void update(Integer newId, List<Integer> ids) {
        DepInfoExample example = new DepInfoExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        DepInfo record = new DepInfo();
        record.setNewest(newId);
        record.setModifier(Constant.DEFAULT_STAFF);
        record.setModifyTime(new Date());
        depInfoMapper.updateByExampleSelective(record, example);
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<DepInfo> lists) {
        int max = Constant.BATCH_INSERT_MAX_SIZE;
        while (lists.size() > max) {
            depInfoMapper
                    .batchInsertSelective(lists, Column.id, Column.name, Column.parent, Column.path,
                            Column.newest, Column.creator, Column.createTime);
            lists = lists.subList(max, lists.size());
        }
        if (lists.size() > 0) {
            depInfoMapper
                    .batchInsertSelective(lists, Column.id, Column.name, Column.parent, Column.path,
                            Column.newest, Column.creator, Column.createTime);
        }
    }
}

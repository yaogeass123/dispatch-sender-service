package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.dto.param.group.GroupDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenDeleteDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenQueryDTO;
import com.dianwoba.dispatch.sender.domain.dto.param.token.DingTokenUpdateDTO;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.DingTokenConfigExample;
import com.dianwoba.dispatch.sender.entity.DingTokenConfigExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.DingTokenConfigMapper;
import com.dianwoba.wireless.paging.PagingSearchable;
import com.dianwoba.wireless.treasure.util.DateUtil;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.data.keyvalue.core.CriteriaAccessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

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
        criteria.andAvailableTimeLessThan(new Date());
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

    public int setTokenBlock(List<Long> ids) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        DingTokenConfig record = new DingTokenConfig();
        record.setModifier(Constant.DEFAULT_STAFF);
        record.setModifyTime(new Date());
        record.setAvailableTime(DateUtil.add(new Date(), Calendar.MINUTE, 3));
        return dingTokenConfigMapper.updateByExampleSelective(record, example);
    }

    public boolean deleteByGroup(GroupDeleteDTO deleteDTO) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        criteria.andGroupIdEqualTo(deleteDTO.getGroupId());
        DingTokenConfig record = new DingTokenConfig();
        record.setIsActive(Boolean.FALSE);
        record.setModifier(deleteDTO.getModifier());
        record.setModifyTime(new Date());
        return dingTokenConfigMapper.updateByExampleSelective(record, example) > 0;
    }

    public void save(DingTokenConfig config) {
        dingTokenConfigMapper.insertSelective(config);
    }

    public Boolean update(DingTokenUpdateDTO updateDTO) {
        DingTokenConfig record = new DingTokenConfig();
        record.setModifier(updateDTO.getModifier());
        record.setModifyTime(new Date());
        if (!StringUtils.isEmpty(updateDTO.getSecret())) {
            record.setSecret(updateDTO.getSecret());
        }
        if (!StringUtils.isEmpty(updateDTO.getKeyWords())) {
            record.setKeyWords(updateDTO.getKeyWords());
        }
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        criteria.andIdEqualTo(record.getId());
        criteria.andTokenEqualTo(updateDTO.getToken());
        return dingTokenConfigMapper.updateByExampleSelective(record, example) > 0;
    }

    public Boolean delete(DingTokenDeleteDTO deleteDTO) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsActiveEqualTo(Boolean.TRUE);
        criteria.andIdEqualTo(deleteDTO.getId());
        criteria.andTokenEqualTo(deleteDTO.getToken());
        DingTokenConfig record = new DingTokenConfig();
        record.setIsActive(Boolean.FALSE);
        record.setModifier(deleteDTO.getModifier());
        record.setModifyTime(new Date());
        return dingTokenConfigMapper.updateByExampleSelective(record, example) > 0;
    }

    public List<DingTokenConfig> query(DingTokenQueryDTO queryDTO) {
        DingTokenConfigExample example = new DingTokenConfigExample();
        Criteria criteria = example.createCriteria();
        criteria.andGroupIdEqualTo(queryDTO.getGroupId());
        criteria.andAppDepEqualTo(queryDTO.getAppDep());
        if (queryDTO.getId() != null) {
            criteria.andIdEqualTo(queryDTO.getId());
        }
        if(!StringUtils.isEmpty(queryDTO.getToken())) {
            criteria.andTokenEqualTo(queryDTO.getToken());
        }
        return dingTokenConfigMapper.selectByExample(example);
    }


}

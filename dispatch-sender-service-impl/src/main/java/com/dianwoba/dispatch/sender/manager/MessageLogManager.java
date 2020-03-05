package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.entity.MessageLogExample;
import com.dianwoba.dispatch.sender.entity.MessageLogExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.MessageLogMapper;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */

@Component
public class MessageLogManager {

    @Resource
    private MessageLogMapper messageLogMapper;

    public void save(MessageLog messageLog) {
        messageLogMapper.insertSelective(messageLog);
    }

    public List<MessageLog> queryAllUnhandled() {
        MessageLogExample example = new MessageLogExample();
        Criteria criteria = example.createCriteria();
        criteria.andHandledEqualTo(false);
        //改分页查询
        return messageLogMapper.selectByExampleSelective(example);
    }

    public void batchUpdateStatus(List<Long> ids) {
        MessageLogExample example = new MessageLogExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        criteria.andHandledEqualTo(false);
        MessageLog record = new MessageLog();
        record.setHandled(true);
        record.setHandleTm(new Date());
        messageLogMapper.updateByExampleSelective(record, example);
    }
}

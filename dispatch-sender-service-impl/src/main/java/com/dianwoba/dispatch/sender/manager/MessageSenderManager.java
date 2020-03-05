package com.dianwoba.dispatch.sender.manager;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.ErrorInfo;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.entity.MessageSend.Column;
import com.dianwoba.dispatch.sender.entity.MessageSendExample;
import com.dianwoba.dispatch.sender.entity.MessageSendExample.Criteria;
import com.dianwoba.dispatch.sender.mapper.MessageSendMapper;
import com.dianwoba.wireless.treasure.util.DateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Polaris
 */

@Component
public class MessageSenderManager {

    @Resource
    private MessageSendMapper messageSendMapper;

    public List<String> hasSent(List<MessageSend> messageSend) {
        //n秒内已发送过该消息,判断标注：app一致，群一致，digest一致，
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andAppNameEqualTo(messageSend.get(0).getAppName());
        criteria.andGroupIdEqualTo(messageSend.get(0).getGroupId());
        criteria.andDigestEqualTo(messageSend.get(0).getDigest());
        criteria.andMsgIn(messageSend.stream().map(MessageSend::getMsg).distinct()
                .collect(Collectors.toList()));
        //一分钟内发过的数据
        criteria.andSendTmGreaterThan(DateUtil.add(new Date(), Calendar.SECOND, -60));
        List<MessageSend> hasSent = messageSendMapper.selectByExampleSelective(example);
        if (CollectionUtils.isEmpty(hasSent)) {
            return null;
        }
        return hasSent.stream().map(MessageSend::getMsg).distinct().collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<MessageSend> messageSend) {
        int max = Constant.BATCH_INSERT_MAX_SIZE;
        while (messageSend.size() > max) {
            messageSendMapper.batchInsertSelective(messageSend.subList(0, max), Column.clusterId,
                    Column.groupId, Column.appName, Column.ips, Column.exceptionType, Column.digest,
                    Column.msg, Column.level, Column.startTm, Column.endTm, Column.count,
                    Column.atWho, Column.atAll, Column.insertTm, Column.status);
            messageSend = messageSend.subList(max, messageSend.size());
        }
        if (messageSend.size() > 0) {
            messageSendMapper.batchInsertSelective(messageSend, Column.clusterId, Column.groupId,
                    Column.appName, Column.ips, Column.exceptionType, Column.digest, Column.msg,
                    Column.level, Column.startTm, Column.endTm, Column.count, Column.atWho,
                    Column.atAll, Column.insertTm, Column.status);
        }
    }

    /**
     * 本轮此待发送消息 包括十秒内所有消息与十秒前High的消息
     *
     * @return 消息
     */
    public List<MessageSend> queryMessageToBeSent() {
        List<MessageSend> lists = Lists.newArrayList();
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(StatusEn.INIT.getStatusCode());
        criteria.andInsertTmGreaterThanOrEqualTo(DateUtil.add(new Date(), Calendar.SECOND, -10));
        lists.addAll(messageSendMapper.selectByExample(example));
        MessageSendExample example2 = new MessageSendExample();
        Criteria criteria2 = example2.createCriteria();
        criteria2.andStatusEqualTo(StatusEn.INIT.getStatusCode());
        criteria2.andLevelEqualTo(LevelEn.HIGH.getLevelCode());
        criteria2.andInsertTmLessThan(DateUtil.add(new Date(), Calendar.SECOND, -10));
        lists.addAll(messageSendMapper.selectByExample(example2));
        return lists;
    }

    public List<MessageSend> queryUnSentMessage4Retry(long groupId) {
        //测试时使用 所以先注释掉，其它的时间信息也同样
        return new ArrayList<>();
//        MessageSendExample example = new MessageSendExample();
//        Criteria criteria = example.createCriteria();
//        criteria.andGroupIdEqualTo(groupId);
//        criteria.andStatusEqualTo(StatusEn.INIT.getStatusCode());
//        criteria.andLevelLessThan(LevelEn.HIGH.getLevelCode());
//        criteria.andInsertTmLessThan(DateUtil.add(new Date(), Calendar.SECOND, -10));
//        return messageSendMapper.selectByExample(example);
    }

    public List<MessageSend> queryUnsentMessageWithMinute(int minute) {
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(StatusEn.INIT.getStatusCode());
        criteria.andInsertTmLessThan(DateUtil.add(new Date(), Calendar.MINUTE, -minute));
        return messageSendMapper.selectByExample(example);
    }

    public void batchUpdateSuccess(List<Long> ids) {
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        criteria.andStatusEqualTo(StatusEn.INIT.getStatusCode());
        MessageSend record = new MessageSend();
        record.setSendTm(new Date());
        record.setStatus(StatusEn.SUCCESS.getStatusCode());
        messageSendMapper.updateByExampleSelective(record, example);
    }

    public void batchUpdateError(ErrorInfo info) {
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(info.getIds());
        criteria.andStatusEqualTo(StatusEn.INIT.getStatusCode());
        MessageSend record = new MessageSend();
        record.setStatus(StatusEn.ERROR.getStatusCode());
        record.setRobotErrorCode(info.getErrorCode());
        record.setRobotErrorMsg(info.getErrorMsg());
        messageSendMapper.updateByExampleSelective(record, example);
    }

    public void batchUpdateIgnore(List<Long> ids) {
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);
        criteria.andStatusEqualTo(StatusEn.INIT.getStatusCode());
        MessageSend record = new MessageSend();
        record.setStatus(StatusEn.IGNORE.getStatusCode());
        messageSendMapper.updateByExampleSelective(record, example);
    }

    public List<MessageSend> statisticMessage() {
        MessageSendExample example = new MessageSendExample();
        Criteria criteria = example.createCriteria();
        criteria.andStatusIn(Lists.newArrayList(StatusEn.IGNORE.getStatusCode(),
                StatusEn.ERROR.getStatusCode()));
//        criteria.andInsertTmGreaterThanOrEqualTo(DateUtil.add(new Date(), Calendar.HOUR, -24));
        return messageSendMapper.selectByExample(example);
    }
}

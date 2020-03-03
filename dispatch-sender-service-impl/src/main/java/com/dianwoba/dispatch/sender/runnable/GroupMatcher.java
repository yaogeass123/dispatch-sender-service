package com.dianwoba.dispatch.sender.runnable;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.GroupMatchCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.MessageLogManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author polaris
 */
public class GroupMatcher implements Runnable {

    public static final Logger LOGGER = LoggerFactory.getLogger(GroupMatcher.class);

    private MessageLogManager messageLogManager;

    private MessageSenderManager messageSenderManager;

    private AppDepCache appDepCache;

    private GroupMatchCache groupMatchCache;

    private List<MessageLog> messageLogs;

    public GroupMatcher(List<MessageLog> messageLogs) {
        this.messageLogs = messageLogs;
        messageLogManager = SpringUtils.getBean(MessageLogManager.class);
        messageSenderManager = SpringUtils.getBean(MessageSenderManager.class);
        appDepCache = SpringUtils.getBean(AppDepCache.class);
        groupMatchCache = SpringUtils.getBean(GroupMatchCache.class);
    }

    @Override
    public void run() {
        //1、app-digest-msg暂时按照一摸一样聚合，后期考虑计算相似度
        Map<String, List<MessageLog>> lists = messageLogs.stream()
                .collect(Collectors.groupingBy( log -> String.format(Constant.GROUP_COMMON_FORMAT,
                        log.getAppName(), log.getDigest(), log.getMsg())));
        //2、转换
        List<MessageSend> messageSend = Lists.newArrayList();
        lists.values().forEach(list -> messageSend.add(ConvertUtils.convert2MessageSend(list)));
        //3、匹配群组
        matchCluster(messageSend);
        //4、匹配群并获取规则
        GroupMatchRules rule = matchGroup(messageSend.get(0));
        setGroup(messageSend, rule);
        //5、去掉1min内已发送的消息（以群、app、digest和msg的维度，最终保证的是一摸一样的消息不重复发送）
        List<String> hasSent = messageSenderManager.hasSent(messageSend);
        if (CollectionUtils.isNotEmpty(hasSent)) {
            LOGGER.info("已发送消息内容:{}", hasSent);
            messageSenderManager.batchSave(
                    messageSend.stream().filter(t -> !hasSent.contains(t.getMsg()))
                            .collect(Collectors.toList()));
        } else {
            messageSenderManager.batchSave(messageSend);
        }
        //6、messageLog数据库中相应入库数据置位成已处理状态
        List<Long> ids = messageLogs.stream().map(MessageLog::getId).collect(Collectors.toList());
        messageLogManager.batchUpdateStatus(ids);
    }

    private void matchCluster(List<MessageSend> messageSends) {
        String appName = messageSends.get(0).getAppName();
        AppDepInfo appDepInfo = appDepCache.queryFromClientCache(appName);
        String clusterId = determineClusterId(appDepInfo);
        messageSends.forEach( messageSend -> messageSend.setClusterId(clusterId));
    }

    private GroupMatchRules matchGroup(MessageSend messageSend) {
        String appName = messageSend.getAppName();
        String clusterId = messageSend.getClusterId();
        String exceptionType = messageSend.getExceptionType();
        List<String> keys = buildKeys(clusterId, exceptionType, appName);
        GroupMatchRules matchInfo = doMatch(messageSend, keys);
        if (matchInfo == null) {
            matchInfo = backMatch(messageSend);
        }
        return matchInfo;
    }

    private List<String> buildKeys(String clusterId, String exceptionType, String appName) {
        List<String> keys = Lists.newArrayList();
        keys.add(String.format(Constant.GROUP_COMMON_FORMAT, clusterId, exceptionType, appName));
        keys.add(String.format(Constant.GROUP_COMMON_FORMAT, clusterId, exceptionType,
                Constant.BACK));
        return keys;
    }

    private GroupMatchRules doMatch(MessageSend messageSend, List<String> keys) {
        for (String key : keys) {
            List<GroupMatchRules> rules = groupMatchCache.queryFromClientCache(key);
            if (CollectionUtils.isEmpty(rules)) {
                continue;
            }
            rules.sort((o1, o2) -> {
                if (StringUtils.isEmpty(o1.getKeyWords()) && StringUtils
                        .isNotEmpty(o2.getKeyWords())) {
                    return 1;
                } else if (StringUtils.isEmpty(o2.getKeyWords()) && StringUtils
                        .isNotEmpty(o1.getKeyWords())) {
                    return -1;
                }
                return 0;
            });
            //todo 思考一句话可能被多个关键词命中
            for (GroupMatchRules rule : rules) {
                if (StringUtils.isEmpty(rule.getKeyWords()) ||
                        messageSend.getMsg().contains(rule.getKeyWords())) {
                    return rule;
                }
            }
        }
        // TODO: 2020/2/17 兜底
        return null;
    }

    private GroupMatchRules backMatch(MessageSend messageSend) {
        String key = String
                .format(Constant.GROUP_COMMON_FORMAT, messageSend.getClusterId(), Constant.BACK,
                        Constant.BACK);
        List<GroupMatchRules> rules = groupMatchCache.queryFromClientCache(key);
        return rules.get(0);
    }
    
    private String determineClusterId(AppDepInfo appDepInfo) {
        // TODO: 2020/2/17 后期再修改，先测试流程使用


        return appDepInfo.getDevelopersDepId();
    }

    private void setGroup(List<MessageSend> messageSends, GroupMatchRules rule) {
        for (MessageSend messageSend : messageSends) {
            messageSend.setGroupId(rule.getGroupId());
            if (rule.getAtWho() != null) {
                messageSend.setAtWho(rule.getAtWho());
            }
            if (rule.getLevel() != null) {
                messageSend.setLevel(rule.getLevel());
            }
        }
    }

}

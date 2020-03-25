package com.dianwoba.dispatch.sender.runnable;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.DepInfoCache;
import com.dianwoba.dispatch.sender.cache.GroupConfigCache;
import com.dianwoba.dispatch.sender.cache.GroupMatchCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.MessageLogManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.dispatch.sender.wrapper.MailSendWrapper;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import org.assertj.core.util.Sets;
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

    private GroupConfigCache groupConfigCache;

    private List<MessageLog> messageLogs;

    private MailSendWrapper mailSendWrapper;

    private DepInfoCache depInfoCache;

    public GroupMatcher(List<MessageLog> messageLogs) {
        this.messageLogs = messageLogs;
        messageLogManager = SpringUtils.getBean(MessageLogManager.class);
        messageSenderManager = SpringUtils.getBean(MessageSenderManager.class);
        appDepCache = SpringUtils.getBean(AppDepCache.class);
        groupMatchCache = SpringUtils.getBean(GroupMatchCache.class);
        groupConfigCache = SpringUtils.getBean(GroupConfigCache.class);
        mailSendWrapper = SpringUtils.getBean(MailSendWrapper.class);
        depInfoCache = SpringUtils.getBean(DepInfoCache.class);
    }

    @Override
    public void run() {
        try {
            Long start = System.currentTimeMillis();
            //1、app-digest-msg暂时按照一摸一样聚合，后期考虑计算相似度
            Map<String, List<MessageLog>> lists = messageLogs.stream().collect(Collectors.groupingBy(
                    log -> String.format(Constant.GROUP_COMMON_FORMAT, log.getAppName(), log.getDigest(), log.getMsg())));
            //2、转换
            List<MessageSend> messageSend = Lists.newArrayList();
            lists.values().forEach(list -> messageSend.add(ConvertUtils.convert2MessageSend(list)));
            LOGGER.info("信息内容:{} ", JSONObject.toJSONString(messageSend));
            //3、匹配群组
            AppDepInfo appDepInfo = matchAppDep(messageSend);
            LOGGER.info("匹配到群组信息:{} ", JSONObject.toJSONString(appDepInfo));
            //4、匹配群并获取规则
            GroupMatchRules rule = matchGroup(messageSend.get(0));
            if (rule == null) {
                String content = String.format("未配置兜底群, 部门id：%s", messageSend.get(0).getId());
                LOGGER.warn(content);
                String mailAddress = mailSendWrapper.getMailAddress(messageSend.get(0).getAppDep(),
                        messageSend.get(0).getAppName());
                mailSendWrapper.sendMail(content, mailAddress, Constant.MAIL_SUBJECT_NOT_MATCH);
                return;
            }
            LOGGER.info("匹配到规则信息:{}", JSONObject.toJSONString(rule));
            String atWho = matchAtWho(rule, appDepInfo);
            setGroup(messageSend, rule, atWho);
            //5、去掉1min内已发送的消息（以群、app、digest和msg的维度，最终保证的是一摸一样的消息不重复发送）
            List<String> hasSent = messageSenderManager.hasSent(messageSend);
            if (CollectionUtils.isNotEmpty(hasSent)) {
                LOGGER.info("已发送消息内容:{}", hasSent);
                messageSenderManager.batchSave(messageSend.stream().filter(t -> !hasSent
                        .contains(t.getMsg())).collect(Collectors.toList()));
            } else {
                messageSenderManager.batchSave(messageSend);
            }
            LOGGER.info("插入完成");
            //6、messageLog数据库中相应入库数据置位成已处理状态
            List<Long> ids = messageLogs.stream().map(MessageLog::getId).collect(Collectors.toList());
            messageLogManager.batchUpdateStatus(ids);
            LOGGER.info("更新完成");
            Long end = System.currentTimeMillis();
            LOGGER.info("总共耗时：{}", end - start);
        } catch (Exception e) {
            LOGGER.warn("匹配处理异常", e);
        }

    }

    private AppDepInfo matchAppDep(List<MessageSend> messageSends) {
        String appName = messageSends.get(0).getAppName();
        AppDepInfo appDepInfo = appDepCache.queryFromClientCache(appName);
        String appDepId = determineAppDepId(appDepInfo);
        messageSends.forEach(messageSend -> messageSend.setAppDep(appDepId));
        return appDepInfo;
    }

    private GroupMatchRules matchGroup(MessageSend messageSend) {
        String appName = messageSend.getAppName();
        String appDep = messageSend.getAppDep();
        String exceptionType = messageSend.getExceptionType();
        List<String> keys = buildKeys(appDep, exceptionType, appName);
        GroupMatchRules matchInfo = doMatch(messageSend, keys);
        if (matchInfo == null) {
            matchInfo = backMatch(messageSend);
        }
        return matchInfo;
    }

    private String matchAtWho(GroupMatchRules rules, AppDepInfo appDepInfo) {
        if (rules.getAtAll()) {
            return Constant.AT_ALL;
        }
        if (StringUtils.isNotEmpty(rules.getAtWho())) {
            return rules.getAtWho();
        }
        DingGroupName groupName = groupConfigCache.queryFromClientCache(rules.getGroupId());
        if (groupName.getAtAll()) {
            return Constant.AT_ALL;
        }
        if (StringUtils.isNotEmpty(groupName.getAtWho())) {
            return groupName.getAtWho();
        }
        Set<String> staffs = Sets.newHashSet();
        if (StringUtils.isNotEmpty(appDepInfo.getDevelopersPhone())) {
            staffs.addAll(Arrays.asList(appDepInfo.getDevelopersPhone().split(",")));
        }
        if (StringUtils.isNotEmpty(appDepInfo.getOwnersPhone())) {
            staffs.addAll(Arrays.asList(appDepInfo.getOwnersPhone().split(",")));
        }
        return String.join(",", staffs);
    }

    private List<String> buildKeys(String appDep, String exceptionType, String appName) {
        List<String> keys = Lists.newArrayList();
        keys.add(String.format(Constant.GROUP_COMMON_FORMAT, appDep, exceptionType, appName));
        keys.add(String.format(Constant.GROUP_COMMON_FORMAT, appDep, exceptionType,
                Constant.BACK));
        return keys;
    }

    private GroupMatchRules doMatch(MessageSend messageSend, List<String> keys) {
        for (String key : keys) {
            // 1-1-1 keyWord:1
            // 1-1-1 keyWord:2
            // 1-1-1 无keyWord
            // 会命中这三条规则，按照有无关键字排序，当关键字命中时返回。
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
                if (StringUtils.isEmpty(rule.getKeyWords()) || messageSend.getMsg()
                        .contains(rule.getKeyWords())) {
                    return rule;
                }
            }
        }
        return null;
    }

    private GroupMatchRules backMatch(MessageSend messageSend) {
        String key = String.format(Constant.GROUP_COMMON_FORMAT, messageSend.getAppDep(),
                Constant.BACK, Constant.BACK);
        List<GroupMatchRules> rules = groupMatchCache.queryFromClientCache(key);
        if(CollectionUtils.isEmpty(rules)) {
            return null;
        }
        return rules.get(0);
    }

    private String determineAppDepId(AppDepInfo appDepInfo) {
        if (StringUtils.isNotEmpty(appDepInfo.getManualDepId())) {
            return appDepInfo.getManualDepId();
        }
        return appDepInfo.getDevelopersDepId();
    }

    private void setGroup(List<MessageSend> messageSends, GroupMatchRules rule, String atWho) {
        for (MessageSend messageSend : messageSends) {
            messageSend.setGroupId(rule.getGroupId());
            if (Constant.AT_ALL.equals(atWho)) {
                messageSend.setAtAll(true);
            } else {
                messageSend.setAtAll(false);
                messageSend.setAtWho(atWho);
            }
            if (rule.getLevel() != null) {
                messageSend.setLevel(rule.getLevel());
            }
        }
    }

}

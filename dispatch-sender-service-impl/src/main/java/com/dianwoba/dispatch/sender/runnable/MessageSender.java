package com.dianwoba.dispatch.sender.runnable;

import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.ErrorInfo;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.domain.SendResultInfo;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.DingTokenConfigManager;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.manager.TokenBucketBackupManager;
import com.dianwoba.dispatch.sender.util.BucketUtils;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.dispatch.sender.wrapper.MailSendWrapper;
import com.dianwoba.dispatch.utils.HttpClientUtils;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import com.dianwoba.wireless.threadpool.MonitoringThreadPool;
import com.dianwoba.wireless.threadpool.MonitoringThreadPoolMaintainer;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.thymeleaf.util.StringUtils;

/**
 * @author Polaris
 */
public class MessageSender implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    private static MonitoringThreadPool threadPool;

    private List<MessageSendInfo> messageSendList;

    private long groupId;

    private MessageSenderManager messageSenderManager;

    private StringRedisTemplate stringRedisTemplate;

    private Map<Long, List<DingTokenConfig>> tokenMap;

    private HttpClientUtils client;

    private List<Long> tokenQueue;

    private List<Long> success;
    private List<ErrorInfo> error;

    private Integer residualSentAbleTimes;

    private MailSendWrapper mailSendWrapper;

    private DingTokenConfigManager dingTokenConfigManager;

    private GroupConfigManager groupConfigManager;

    private DingTokenConfigCache dingTokenConfigCache;

    private TokenBucketBackupManager sendBucketBackupManager;

    private int second;

    public MessageSender(List<MessageSendInfo> list) {
        messageSendList = list;
        groupId = list.get(0).getGroupId();
        messageSenderManager = SpringUtils.getBean(MessageSenderManager.class);
        stringRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        dingTokenConfigManager = SpringUtils.getBean(DingTokenConfigManager.class);
        mailSendWrapper = SpringUtils.getBean(MailSendWrapper.class);
        groupConfigManager = SpringUtils.getBean(GroupConfigManager.class);
        dingTokenConfigCache = SpringUtils.getBean(DingTokenConfigCache.class);
        sendBucketBackupManager = SpringUtils.getBean(TokenBucketBackupManager.class);
        tokenQueue = null;
        success = Lists.newArrayList();
        error = Lists.newArrayList();
        client = new HttpClientUtils();
        second = Calendar.getInstance().get(Calendar.SECOND);
        threadPool = MonitoringThreadPoolMaintainer
                .newFixedThreadPool(String.format(Constant.EXECUTOR_SEND_FORMAT, groupId), 120);
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            List<DingTokenConfig> tokenList = dingTokenConfigCache
                    .queryFromClientCache(String.format(Constant.GROUP_TOKEN_PREFIX, groupId));
            if (CollectionUtils.isEmpty(tokenList)) {
                String group = findGroupName();
                LOGGER.warn("群{}无正确机器人配置", group);
                String appDep = messageSendList.get(0).getAppDep();
                String content =
                        "群编号: " + groupId + "\n" + "群名称：" + group + "\n" + "无正确机器人配置，请及时处理";
                String mailAddress = mailSendWrapper.getMailAddress(appDep);
                mailSendWrapper.sendMail(content, mailAddress, Constant.MAIL_SUBJECT_NOT_EXIST);
                return;
            }
            tokenMap = tokenList.stream()
                    .collect(Collectors.groupingBy(DingTokenConfig::getId));
            queryRedis();
            //1、消息再聚合（当前时段的high可能与10s前未发送的high消息重复）
            List<MessageSendInfo> messages = dealHighLevelMessage();
            //2、消息排序
            messages.sort(MessageSender::compare);
            //3、计算本时段可发送消息
            int times = calSendAbleTimes();
            LOGGER.info("times : {} ", times);
            //4、循环发送
            times = sendProcess(times, messages);
            LOGGER.info("times now : {} ", times);
            //5、更新数据库
            updateSql();
            //6、本时段有剩余可发送次数，尝试发送堆积的消息
            if (times > 0) {
                times = retry(times);
                updateSql();
            }
            //7、更新redis
            residualSentAbleTimes = times;
            updateRedis();
            long end = System.currentTimeMillis();
            LOGGER.info("耗时:{}", end - start);
        } catch (Exception e) {
            LOGGER.error("发送流程异常", e);
        }
    }

    private void queryRedis() {
        try {
            String redisStr = stringRedisTemplate.opsForValue()
                    .get(String.format(Constant.REDIS_SEND_STR, groupId));
            if (StringUtils.isEmpty(redisStr)) {
                if (second == 0) {
                    tokenQueue = BucketUtils.buildTokenQueue(tokenMap.keySet());
                    residualSentAbleTimes = 0;
                    return;
                } else {
                    throw new RuntimeException("redis数据不存在！");
                }
            }
            String[] spit = redisStr.split(":");
            residualSentAbleTimes = Integer.parseInt(spit[0]);
            tokenQueue = BucketUtils.buildTokenQueue(spit[1]);
        } catch (Exception e) {
            LOGGER.error("查询redis出错", e);
            if (second == 0) {
                tokenQueue = BucketUtils.buildTokenQueue(tokenMap.keySet());
                residualSentAbleTimes = 0;
            } else {
                throw e;
            }
        }
    }
//
//    private void buildTokenQueue() {
//        if (tokenQueue == null) {
//            //数据库查找
//            TokenBucketBackup backup = sendBucketBackupManager.queryByGroupId(groupId);
//            //数据库也查找不到
//            if (backup != null && backup.getBucket() != null) {
//
//            } else {
//                //如果是第一次，计算bucket，放入数据库与redis
//                if(second == 0) {
//                    Set<String> keys = tokenMap.keySet();
//                    tokenQueue = BucketUtils.buildTokenQueue(keys);
//                } else {
//                    throw new RuntimeException("bucket获取失败，本轮此不发送消息");
//                }
//            }
//        }
//
//    }

    private int retry(int times) {
        List<MessageSendInfo> retryMessages = getUnSentMessage();
        if (CollectionUtils.isEmpty(retryMessages)) {
            return times;
        }
        retryMessages.sort(MessageSender::compare);
        times = sendProcess(times, retryMessages);
        return times;
    }

    private void updateRedis() {
        String bucket = BucketUtils.buildBucketString(tokenQueue);
        stringRedisTemplate.opsForValue().set(String.format(Constant.REDIS_SEND_STR, groupId),
                String.format("%d,%s", residualSentAbleTimes, bucket));
    }

    private void updateSql() {
        if (CollectionUtils.isNotEmpty(success)) {
            try {
                messageSenderManager.batchUpdateSuccess(success);
                success.clear();
            } catch (Exception e) {
                LOGGER.error("更新数据库出错, ", e);
            }
        }
        if (CollectionUtils.isNotEmpty(error)) {
            try {
                error.forEach(errorInfo -> messageSenderManager.batchUpdateError(errorInfo));
                error.clear();
            } catch (Exception e) {
                LOGGER.error("更新数据库出错, ", e);
            }
        }
    }

    private Long poll() {
        Long ret = tokenQueue.get(0);
        tokenQueue.remove(0);
        return ret;
    }

    /**
     * 发送流程
     */
    private int sendProcess(int times, List<MessageSendInfo> messages) {
        //todo 改成异步发送
        //可发送次数与消息数量的最小值
        int count = Math.min(times, messages.size());
        List<Future<SendResultInfo>> futures = Lists.newArrayList();
        List<SendResultInfo> results = Lists.newArrayList();

        for (int i = 0; i < count; i++) {
            DingTokenConfig token = tokenMap.get(poll()).get(0);
            futures.add(threadPool.submit(new SendProcessor(messages.get(i), token, client)));
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            LOGGER.error("中断", e);
        }
        for (Future<SendResultInfo> future : futures) {
            try {
                SendResultInfo resultInfo = future.get(2000, TimeUnit.MILLISECONDS);
                if (resultInfo != null) {
                    results.add(resultInfo);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.warn("多线程发送获取结果失败", e);
            }
        }
        List<SendResultInfo> successResults = results.stream().filter(SendResultInfo::getIsSuccess)
                .collect(Collectors.toList());
        successResults.forEach(t -> success.addAll(t.getIds()));
        //本时段成功次数 - 成功次数
        times -= successResults.size();

        List<SendResultInfo> errorResults = results.stream().filter(t -> !t.getIsSuccess())
                .collect(Collectors.toList());
        Map<Integer, List<SendResultInfo>> errorMap = errorResults.stream()
                .collect(Collectors.groupingBy(SendResultInfo::getErrorCode));
        for (Entry<Integer, List<SendResultInfo>> entry : errorMap.entrySet()) {
            Integer k = entry.getKey();
            List<SendResultInfo> v = entry.getValue();
            List<Long> tokenIds = v.stream().map(SendResultInfo::getTokenId).distinct()
                    .collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            if (k.equals(Constant.DING_PARAM_ERROR) || k.equals(Constant.DING_VALID_ERROR)) {
                sb.append("群组编号: ").append(groupId).append("\n");
                sb.append("群名称：").append(findGroupName()).append("\n");
                sb.append(tokenIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                        .append("号机器人配置错误，请及时检查修改");
                sb.append("\n").append("错误代码: ").append(k).append("\n");
                for (SendResultInfo result : v) {
                    sb.append(result.getTokenId()).append("号机器人:").append(result.getErrorMsg())
                            .append("\n");
                }
                //数据库置位
                dingTokenConfigManager.setTokenError(tokenIds);
                //缓存表删除
                tokenIds.forEach(id -> tokenMap.remove(id));
                //可发送次数更新
                times -= v.size();
                int n = times;
                for (int i = 0; i < n; i++) {
                    if (tokenIds.contains(tokenQueue.get(i))) {
                        times--;
                    }
                }
                //tokenQueue删除
                tokenQueue.removeAll(tokenIds);

                if (tokenMap.size() == 0) {
                    //remove后没有机器人了
                    sb.append("此群已无可用机器人,请及时配置");
                    // TODO: 2020/2/25  特殊处理
                }
            } else {
                //系统错误 内容不合法 邮件告警，并且STATUS置位ERROR，不再重试
                //A B C A B C A B C  B失败两次，后面加上B B
                sb.append("钉钉消息发送失败, 群编号: ").append(groupId).append("\n");
                sb.append("群名称：").append(findGroupName()).append("\n");
                sb.append("错误代码: ").append(k).append("\n").append("\n");
                for (SendResultInfo result : v) {
                    sb.append("应用名: ").append(result.getAppName()).append("\n");
                    sb.append("错误详情").append(result.getErrorMsg()).append("\n");
                    sb.append("消息内容").append(result.getMsg()).append("\n").append("\n");
                    ErrorInfo errorInfo = ConvertUtils.convert2ErrorInfo(result);
                    error.add(errorInfo);
                    //对于这种错误，实际上钉钉机器人配置没有问题，且没有发送，不占用发送次数
                    //目前设计对于这些错误的次数暂时不去找本时段的消息进行发送，后期可以考虑优化
                    //将机器人token的id放回发送队列中
                    tokenQueue.add(result.getTokenId());
                }
            }
            // TODO: 2020/3/11  这里设计有问题，之前串行通知给app相关人员处理，这里应该给群设置个邮件群组或者负责人，进行通知
            String mailAddress = mailSendWrapper.getMailAddress(v.get(0).getAppDep());
            mailSendWrapper.sendMail(sb.toString(), mailAddress, Constant.MAIL_SUBJECT_SEND_ERROR);
        }

        return times;
    }

    private List<MessageSendInfo> getUnSentMessage() {
        List<MessageSend> unSentMessage = messageSenderManager.queryUnSentMessage4Retry(groupId);
        List<MessageSendInfo> infoList = unSentMessage.stream()
                .map(ConvertUtils::convert2MessageSendInfo).collect(Collectors.toList());
        Map<String, List<MessageSendInfo>> unSent = infoList.stream().collect(Collectors.groupingBy(
                t -> String.format(Constant.GROUP_COMMON_FORMAT, t.getAppName(), t.getDigest(),
                        t.getMsg())));
        return gatherMessageMap(unSent);
    }

    private List<MessageSendInfo> dealHighLevelMessage() {
        //拿出high等级消极进行聚合
        Map<String, List<MessageSendInfo>> highLevel = messageSendList.stream()
                .filter(info -> info.getLevel().equals(LevelEn.HIGH)).collect(Collectors.groupingBy(
                        t -> String
                                .format(Constant.GROUP_COMMON_FORMAT, t.getAppName(), t.getDigest(),
                                        t.getMsg())));
        List<MessageSendInfo> gatherList = gatherMessageMap(highLevel);
        //加上其它的消息
        gatherList.addAll(messageSendList.stream()
                .filter(messageSend -> !messageSend.getLevel().equals(LevelEn.HIGH))
                .collect(Collectors.toList()));
        return gatherList;
    }

    private List<MessageSendInfo> gatherMessageMap(Map<String, List<MessageSendInfo>> map) {
        List<MessageSendInfo> gatherList = Lists.newArrayList();
        map.forEach((k, v) -> {
            LOGGER.info("v.size :{}", v.size());
            if (v.size() > 1) {
                gatherList.add(gatherMessage(v));
            } else {
                LOGGER.info("12312312312312 :{}", v.get(0).getCount());
                gatherList.add(v.get(0));
            }
        });
        return gatherList;
    }

    private MessageSendInfo gatherMessage(List<MessageSendInfo> list) {
        MessageSendInfo messageSend = list.get(0);
        //主键
        messageSend.setIds(gatherId(
                list.stream().map(MessageSendInfo::getIds).collect(Collectors.toList())));
        //ip
        messageSend.setIps(gatherIp(
                list.stream().map(MessageSendInfo::getIps).collect(Collectors.toList())));
        //count
        messageSend.setCount(gatherCount(
                list.stream().map(MessageSendInfo::getCount).collect(Collectors.toList())));
        //start end time
        messageSend.setStartTm(list.stream().map(MessageSendInfo::getStartTm).min(Date::compareTo)
                .orElse(list.get(0).getStartTm()));
        messageSend.setEndTm(list.stream().map(MessageSendInfo::getEndTm).max(Date::compareTo)
                .orElse(new Date()));
        //insert time
        messageSend.setInsertTm(list.stream().map(MessageSendInfo::getInsertTm).max(Date::compareTo)
                .orElse(new Date()));
        return messageSend;
    }

    private int gatherCount(List<Integer> counts) {
        int count = 0;
        for (int i : counts) {
            count += i;
        }
        return count;
    }

    private List<Long> gatherId(List<List<Long>> ids) {
        List<Long> idList = Lists.newArrayList();
        ids.forEach(idList::addAll);
        return idList.stream().distinct().collect(Collectors.toList());
    }

    private String gatherIp(List<String> ips) {
        Set<String> ipSet = new HashSet<>();
        ips.forEach(t -> Collections.addAll(ipSet, t.split(",")));
        return String.join(",", ipSet);
    }

    private static int compare(MessageSendInfo a, MessageSendInfo b) {
        if (a.getLevel().getLevelCode() > b.getLevel().getLevelCode()) {
            return -1;
        } else if (a.getLevel().getLevelCode() < b.getLevel().getLevelCode()) {
            return 1;
        } else if (a.getEndTm().compareTo(b.getEndTm()) < 0) {
            return 1;
        } else {
            return -1;
        }
    }

    private int calSendAbleTimes() {
        int tokenNum = tokenMap.size();
        //本批次
        if (second < Constant.FIFTY) {
            return  tokenNum * 3 + residualSentAbleTimes;
        }
        //对于单个机器人，最多可发送20，冗余1条，故期望19条。对于前5论，理想中包和发送3*5个
        //故最后一轮 4 = 19 - 3 * 5
        return 4 * tokenNum + residualSentAbleTimes;
    }


    private String findGroupName() {
        return groupConfigManager.findGroupNameByCache(groupId);
    }
}

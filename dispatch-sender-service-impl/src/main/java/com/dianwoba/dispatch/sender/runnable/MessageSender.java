package com.dianwoba.dispatch.sender.runnable;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.ErrorInfo;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.DingTokenConfigManager;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.dispatch.sender.wrapper.MailSendWrapper;
import com.dianwoba.dispatch.utils.HttpClientUtils;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import com.dianwoda.delibird.dingtalk.chatbot.SendResult;
import com.dianwoda.delibird.dingtalk.chatbot.message.TextMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Polaris
 */
public class MessageSender implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageSender.class);

    private List<MessageSendInfo> messageSendList;

    private long groupId;

    private MessageSenderManager messageSenderManager;

    private StringRedisTemplate stringRedisTemplate;

    private List<DingTokenConfig> tokens;

    private HttpClientUtils client;

    private int nextToken;

    private List<Long> success;
    private List<ErrorInfo> error;

    private int sentTimes;

    private MailSendWrapper mailSendWrapper;

    private DingTokenConfigManager dingTokenConfigManager;

    private GroupConfigManager groupConfigManager;

    private int second;

    public MessageSender(List<MessageSendInfo> list) {
        messageSendList = list;
        groupId = list.get(0).getGroupId();
        messageSenderManager = SpringUtils.getBean(MessageSenderManager.class);
        stringRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        dingTokenConfigManager = SpringUtils.getBean(DingTokenConfigManager.class);
        mailSendWrapper = SpringUtils.getBean(MailSendWrapper.class);
        groupConfigManager = SpringUtils.getBean(GroupConfigManager.class);
        DingTokenConfigCache dingTokenConfigCache = SpringUtils.getBean(DingTokenConfigCache.class);
        tokens = dingTokenConfigCache.queryFromClientCache(String.format(Constant.GROUP_TOKEN_PREFIX, groupId));
        String nextTokenStr = stringRedisTemplate.opsForValue().get(String.format(Constant.GROUP_NEXT_TOKEN, groupId));
        nextToken = nextTokenStr == null ? 0 : Integer.parseInt(nextTokenStr);
        String redisTimes = stringRedisTemplate.opsForValue().get(String.format(Constant.REDIS_SEND_TIMES, groupId));
        sentTimes = redisTimes == null ? 0 : Integer.parseInt(redisTimes);
        success = Lists.newArrayList();
        error = Lists.newArrayList();
        client = new HttpClientUtils();
        second = Calendar.getInstance().get(Calendar.SECOND);
    }

    @Override
    public void run() {
        try {
            if (CollectionUtils.isEmpty(tokens)) {
                String clusterId = messageSendList.get(0).getClusterId();
                String content = "群编号: " + groupId + "\n" + "群名称：" + findGroupName() + "\n"
                        + "无正确机器人配置，请及时处理";
                String mailAddress = mailSendWrapper.getMailAddress(clusterId);
                mailSendWrapper.sendMail(content, mailAddress, Constant.MAIL_SUBJECT_NOT_EXIST);
                return;
            }
            //1、消息再聚合（当前时段的high可能与10s前未发送的high消息重复）
            List<MessageSendInfo> messages = dealHighLevelMessage();
            //2、消息排序
            messages.sort(MessageSender::compare);
            //3、计算本时段可发送消息
            int times = calSendAbleTimes();
            int back = times;
            LOGGER.info("times : {} ", times);
            //4、循环发送
            times = sendProcess(times, messages);
            //5、更新数据库
            sentTimes += back - times;
            updateSql();
            //6、尝试重试
            if (times > 0) {
                times = retry(times);
                updateSql();
            }
            //7、更新redis
            sentTimes += back - times;
            updateRedis();
        } catch (Exception e) {
            LOGGER.error("发送流程异常", e);
        }
    }

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
        stringRedisTemplate.opsForValue()
                .set(String.format(Constant.GROUP_NEXT_TOKEN, groupId), String.valueOf(nextToken));
        stringRedisTemplate.opsForValue()
                .set(String.format(Constant.REDIS_SEND_TIMES, groupId), String.valueOf(sentTimes));
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

    /**
     * 发送流程
     */
    private int sendProcess(int times, List<MessageSendInfo> messages) {
        int index = 0;
        //times可能发生变化，当机器人配置错误时会修改相应的可发送次数
        int count = 0;
        while (index < messages.size() && count < times) {
            String msg = msgAppend(messages.get(index));
            DingTokenConfig token = tokens.get(nextToken);
            SendResult res = sendMessage(messages.get(index), msg, token);
            if (res == null) {
                LOGGER.warn("请求错误，msg等待重试，信息：{}", JSONObject.toJSONString(messages.get(index)));
                index++;
                continue;
            }
            if (res.isSuccess()) {
                success.addAll(messages.get(index).getIds());
                nextToken = findNext(nextToken);
                count++;
                index++;
            } else {
                int errorCode = res.getErrorCode();
                StringBuilder sb = new StringBuilder();
                if (errorCode == Constant.DING_PARAM_ERROR
                        || errorCode == Constant.DING_VALID_ERROR) {
                    sb.append("群组编号: ").append(groupId).append("\n");
                    sb.append("群名称：").append(findGroupName()).append("\n");
                    sb.append(tokens.get(nextToken).getId()).append("号机器人配置错误，请及时检查修改");
                    sb.append("\n").append("错误代码: ").append(errorCode).append("\n");
                    sb.append("详情：").append("\n").append(res.getErrorMsg()).append("\n");
                    //参数错误与配置错误。内容没问题，要对机器人进行处理
                    // 1 2 3 4 5   remove(3) --> tokens[3] = 5;
                    //count / tokens.size() 可以表示改机器人已发送几条消息
                    count = count - count / tokens.size();
                    dingTokenConfigManager.setTokenError(token.getId());
                    tokens.remove(nextToken);
                    times = calSendAbleTimes();
                    if (tokens.size() == 0) {
                        //remove后没有机器人了
                        sb.append("此群已无可用机器人,请及时配置");
                        // TODO: 2020/2/25  特殊处理
                        break;
                    }
                } else {
                    //系统错误 内容不合法 邮件告警，并且STATUS置位ERROR，不再重试
                    sb.append("钉钉消息发送失败, 群编号: ").append(groupId).append("\n");
                    sb.append("群名称：").append(findGroupName()).append("\n");
                    sb.append("消息内容: ").append("\n").append(msg);
                    sb.append("错误代码: ").append(errorCode).append("\n");
                    sb.append("错误详情：").append(res.getErrorMsg()).append("\n");
                    index++;
                    ErrorInfo errorInfo = ConvertUtils
                            .convert2ErrorInfo(res, messages.get(index).getIds());
                    error.add(errorInfo);
                }
                String mailAddress = mailSendWrapper.getMailAddress(messages.get(index).getClusterId(),
                        messages.get(index).getAppName());
                mailSendWrapper.sendMail(sb.toString(), mailAddress, Constant.MAIL_SUBJECT_SEND_ERROR);
            }
        } return Math.max(times - count, 0);
    }

    private SendResult sendMessage(MessageSendInfo messageSend, String msg, DingTokenConfig token) {
        try {
            TextMessage textMessage = new TextMessage(msg);
            if (messageSend.getAtAll()) {
                textMessage.setIsAtAll(true);
            } else {
                textMessage.setAtMobiles(Lists.newArrayList(messageSend.getAtWho().split(",")));
            }
            String url = Constant.DING_URL_PRE + token.getToken();
            if (StringUtils.isNotEmpty(token.getSecret())) {
                Long timeStamp = System.currentTimeMillis();
                String sign = calSecret(token.getSecret(), timeStamp);
                url = url + "&timestamp=" + timeStamp.toString() + "&sign=" + sign;
            }
            SendResult sendResult = new SendResult();
            String result = client.post(url, textMessage.toJsonString());
            if (StringUtils.isNotEmpty(result)) {
                JSONObject obj = JSONObject.parseObject(result);
                Integer errCode = obj.getInteger("errcode");
                sendResult.setErrorCode(errCode);
                sendResult.setErrorMsg(obj.getString("errmsg"));
                sendResult.setIsSuccess(errCode.equals(0));
            }
            return sendResult;
        } catch (Exception e) {
            LOGGER.error("消息发送失败，", e);
            return null;
        }
    }

    private int findNext(int next) {
        return (next + 1) % tokens.size();
    }

    private String calSecret(String secret, Long timestamp) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
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
                .filter(info -> info.getLevel().equals(LevelEn.HIGH)).collect(Collectors
                        .groupingBy(t -> String
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
        messageSend.setIds(gatherId(list.stream().map(MessageSendInfo::getIds).collect(Collectors.toList())));
        //ip
        messageSend.setIps(gatherIp(list.stream().map(MessageSendInfo::getIps).collect(Collectors.toList())));
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
        int tokenNum = tokens.size();
        //本批次
        if (second < Constant.FIFTY) {
            return (second / 10 + 1) * tokenNum * 3 - sentTimes;
        }
        return 19 * tokenNum - sentTimes;
    }

    private String msgAppend(MessageSendInfo messageSendInfo) {
        if (!Constant.DING_MESSAGE.equals(messageSendInfo.getExceptionType())) {
            return messageSendInfo.getMsg();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("应用名: ").append(messageSendInfo.getAppName()).append("\n");
        sb.append("IP: ").append(messageSendInfo.getIps()).append("\n");
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT);
        sb.append("时间: ").append(sdf.format(messageSendInfo.getStartTm())).append(" - ")
                .append(sdf.format(messageSendInfo.getEndTm())).append("\n");
        sb.append("数量: ").append(messageSendInfo.getCount()).append("\n");
        sb.append("堆栈: ").append(messageSendInfo.getDigest()).append("\n");
        sb.append("消息等级: ").append(messageSendInfo.getLevel().getLevelMsg()).append("\n");
        sb.append("内容: ").append(messageSendInfo.getMsg()).append("\n");
        return sb.toString();
    }

    private String findGroupName() {
        return groupConfigManager.findGroupNameByCache(groupId);
    }
}

package com.dianwoba.dispatch.sender.runnable;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.domain.ErrorInfo;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoba.dispatch.sender.util.MailUtils;
import com.dianwoba.dispatch.utils.HttpClientUtils;
import com.dianwoba.wireless.fundamental.util.AppUtils;
import com.dianwoba.wireless.fundamental.util.SpringUtils;
import com.dianwoda.delibird.dingtalk.chatbot.SendResult;
import com.dianwoda.delibird.dingtalk.chatbot.message.TextMessage;
import com.dianwoda.delibird.mail.dto.MailBody;
import com.dianwoda.delibird.mail.dto.MailHead;
import com.dianwoda.delibird.mail.dto.MailReceiver;
import com.dianwoda.delibird.mail.dto.MailRequest;
import com.dianwoda.delibird.provider.DeliMailProvider;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    private DeliMailProvider deliMailProvider;

    private AppDepCache appDepCache;

    public MessageSender(List<MessageSendInfo> list) {
        messageSendList = list;
        groupId = list.get(0).getGroupId();
        messageSenderManager = SpringUtils.getBean(MessageSenderManager.class);
        DingTokenConfigCache dingTokenConfigCache = SpringUtils.getBean(DingTokenConfigCache.class);
        stringRedisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        tokens = dingTokenConfigCache
                .queryFromClientCache(String.format(Constant.GROUP_TOKEN_PREFIX, groupId));
        String nextTokenStr = stringRedisTemplate.opsForValue()
                .get(String.format(Constant.GROUP_NEXT_TOKEN, groupId));
        nextToken = nextTokenStr == null ? 0 : Integer.parseInt(nextTokenStr);
        String redisTimes = stringRedisTemplate.opsForValue()
                .get(String.format(Constant.REDIS_SEND_TIMES, groupId));
        sentTimes = redisTimes == null ? 0 : Integer.parseInt(redisTimes);
        success = Lists.newArrayList();
        error = Lists.newArrayList();
        client = new HttpClientUtils();
        deliMailProvider = SpringUtils.getBean(DeliMailProvider.class);
        appDepCache = SpringUtils.getBean(AppDepCache.class);
    }

    @Override
    public void run() {
        try {
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
            //5、尝试重试
            if (times > 0) {
                times = retry(times);
            }
            //6、更新数据库与redis
            sentTimes += back - times;
            updateRedisAndSql();
        } catch (Exception e) {
            e.printStackTrace();
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


    private void updateRedisAndSql() {
        stringRedisTemplate.opsForValue()
                .set(String.format(Constant.GROUP_NEXT_TOKEN, groupId), String.valueOf(nextToken));
        stringRedisTemplate.opsForValue()
                .set(String.format(Constant.REDIS_SEND_TIMES, groupId), String.valueOf(sentTimes));
        if (CollectionUtils.isNotEmpty(success)) {
            messageSenderManager.batchUpdateSuccess(success);
        }
        if (CollectionUtils.isNotEmpty(error)) {
            error.forEach(errorInfo -> messageSenderManager.batchUpdateError(errorInfo));
        }
    }

    /**
     * 发送流程
     */
    private int sendProcess(int times, List<MessageSendInfo> messages) {
        int index = 0;
        //times可能发生变化，当机器人配置错误时会修改相应的可发送次数
        while (index == messages.size() || times == 0) {
            String msg = msgAppend(messages.get(index));
            SendResult res = sendMessage(messages.get(index), msg, nextToken);
            if (res == null) {
                LOGGER.warn("请求错误，msg等待重试，信息：{}", JSONObject.toJSONString(messages.get(index)));
                index++;
                continue;
            }
            if (res.isSuccess()) {
                success.addAll(messages.get(index).getIds());
                nextToken = findNext(nextToken);
                times--;
                index++;
            } else {
                int errorCode = res.getErrorCode();
                StringBuilder sb = new StringBuilder();
                if (errorCode == Constant.DING_PARAM_ERROR
                        || errorCode == Constant.DING_VALID_ERROR) {
                    sb.append("群组编号: ").append(messages.get(index).getGroupId()).append("\n");
                    sb.append(tokens.get(nextToken).getId()).append("号机器人配置错误，请及时检查修改");
                    sb.append("\n").append("错误代码: ").append(errorCode).append("\n");
                    sb.append("详情：").append("\n").append(res.getErrorMsg()).append("\n");
                    //参数错误与配置错误。内容没问题，要对机器人进行处理
                    // 1 2 3 4 5   remove(3) --> tokens[3] = 4;
                    // TODO: 2020/2/25 思考机器人配置错误时的对应time等的处理
                    tokens.remove(nextToken);
                    if (tokens.size() == 0) {
                        //remove后没有机器人了
                        sb.append("此群已无可用机器人,请及时配置");
                        // TODO: 2020/2/25  特殊处理
                        break;
                    }
                    sendMail(sb.toString(), messages.get(index).getClusterId());
                } else {
                    //系统错误 内容不合法 邮件告警，并且STATUS置位ERROR，不再重试
                    sb.append("钉钉消息发送失败, 群组编号: ").append(messages.get(index).getGroupId()).append("\n");
                    sb.append("消息内容: ").append("\n").append(msg);
                    sb.append("错误代码: ").append(errorCode).append("\n");
                    sb.append("错误详情：").append(res.getErrorMsg()).append("\n");
                    sendMail(sb.toString(), messages.get(index).getClusterId());
                    index++;
                    ErrorInfo errorInfo = ConvertUtils
                            .convert2ErrorInfo(res, messages.get(index).getIds());
                    error.add(errorInfo);
                }
            }
        }
        return times;
    }

    private SendResult sendMessage(MessageSendInfo messageSend, String msg, int next) {
        try {
            DingTokenConfig token = tokens.get(next);
            TextMessage textMessage = new TextMessage(msg);
            if (Constant.ALL.equals(messageSend.getAtWho())) {
                textMessage.setIsAtAll(true);
            } else {
                textMessage.setAtMobiles(Lists.newArrayList(messageSend.getAtWho().split(",")));
            }
            String url = Constant.DING_URL_PRE + token.getToken();
            if (StringUtils.isNotEmpty(token.getSecret())) {
                url = url + calSecret(token.getSecret());
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

    private String calSecret(String secret) {
        return "";
    }

    private List<MessageSendInfo> getUnSentMessage() {
        List<MessageSend> unSentMessage = messageSenderManager.queryUnSentMessage(groupId);
        List<MessageSendInfo> infoList = unSentMessage.stream()
                .map(ConvertUtils::convert2MessageSendInfo).collect(Collectors.toList());
        Map<String, List<MessageSendInfo>> unSent = infoList.stream().collect(Collectors.groupingBy(
                t -> String.format(Constant.GROUP_COMMON_FORMAT, t.getAppCode(), t.getDigest(),
                        t.getMsg())));
        return gatherMessageMap(unSent);
    }

    private List<MessageSendInfo> dealHighLevelMessage() {
        //拿出high等级消极进行聚合
        Map<String, List<MessageSendInfo>> highLevel = messageSendList.stream()
                .filter(info -> info.getLevel().equals(Constant.HIGH)).collect(Collectors
                        .groupingBy(t -> String
                                .format(Constant.GROUP_COMMON_FORMAT, t.getAppCode(), t.getDigest(),
                                        t.getMsg())));
        List<MessageSendInfo> gatherList = gatherMessageMap(highLevel);
        //加上其它的消息
        gatherList.addAll(messageSendList.stream()
                .filter(messageSend -> !messageSend.getLevel().equals(Constant.HIGH))
                .collect(Collectors.toList()));
        return gatherList;
    }

    private List<MessageSendInfo> gatherMessageMap(Map<String, List<MessageSendInfo>> map) {
        List<MessageSendInfo> gatherList = Lists.newArrayList();
        map.forEach((k, v) -> {
            if (v.size() > 1) {
                gatherList.add(gatherMessage(v));
            } else {
                gatherList.add(v.get(0));
            }
        });
        return gatherList;
    }

    private MessageSendInfo gatherMessage(List<MessageSendInfo> list) {
        MessageSendInfo messageSend = list.get(0);
        //主键
        messageSend.setIds(list.stream().map(t -> t.getIds().get(0)).collect(Collectors.toList()));
        //ip
        messageSend.setIps(gatherIp(
                list.stream().map(MessageSendInfo::getIps).collect(Collectors.toList())));
        //count
        messageSend.setCount(
                list.stream().map(MessageSendInfo::getCount).reduce(Integer::sum).orElse(0));
        //start end time
        messageSend.setStartTm(list.stream().map(MessageSendInfo::getStartTm).min(Date::compareTo)
                .orElse(list.get(0).getStartTm()));
        messageSend.setEndTm(list.stream().map(MessageSendInfo::getEndTm).max(Date::compareTo)
                .orElse(new Date()));
        //insert time
        messageSend.setInsertTm(list.stream().map(MessageSendInfo::getInsertTm).max(Date::compareTo)
                .orElse(new Date()));
        LOGGER.info("gather message result: {}", JSONObject.toJSONString(messageSend));
        LOGGER.info("gather message result : {} ", JSONObject.toJSONString(messageSend));
        return messageSend;
    }

    private String gatherIp(List<String> ips) {
        Set<String> ipSet = new HashSet<>();
        ips.forEach(t -> Collections.addAll(ipSet, t.split(",")));
        StringBuilder sb = new StringBuilder();
        ipSet.forEach(s -> sb.append(s).append(","));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private static int compare(MessageSendInfo a, MessageSendInfo b) {
        if (a.getLevel() > b.getLevel()) {
            return -1;
        } else if (a.getLevel() < b.getLevel()) {
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
        Calendar calendar = Calendar.getInstance();
        int sec = calendar.get(Calendar.SECOND);
        if (sec < Constant.FIFTY) {
            return (sec / 10 + 1) * tokenNum * 3 - sentTimes;
        }
        return 19 * tokenNum - sentTimes;
    }

    private String msgAppend(MessageSendInfo messageSendInfo) {
        if (!Constant.DING_MESSAGE.equals(messageSendInfo.getExceptionType())) {
            return messageSendInfo.getMsg();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("应用名:").append(messageSendInfo.getAppCode()).append("\n");
        sb.append("IP:").append(Arrays.toString(messageSendInfo.getIds().toArray())).append("\n");
        sb.append("时间:").append(messageSendInfo.getStatus()).append(" - ")
                .append(messageSendInfo.getEndTm()).append("\n");
        sb.append("数量:").append(messageSendInfo.getCount()).append("\n");
        sb.append("内容:").append(messageSendInfo.getMsg()).append("\n");
        return sb.toString();
    }

    private void sendMail(String content, String clusterId) {
        String mailAddress = MailUtils.getMailAddress(clusterId);
        MailHead mailHead = MailHead.create();
        MailRequest mailRequest = MailRequest.builder()
                .receivers(MailReceiver.create(mailAddress.split(",")))
                .body(MailBody.create().setSubject(Constant.MAIL_SUBJECT_SEND_ERROR)
                .setContent(content)).head(mailHead).build();
        deliMailProvider.send(mailRequest);
    }
}

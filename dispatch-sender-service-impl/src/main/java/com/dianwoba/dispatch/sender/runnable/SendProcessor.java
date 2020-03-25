package com.dianwoba.dispatch.sender.runnable;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.domain.SendResultInfo;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.utils.HttpClientUtils;
import com.dianwoda.delibird.dingtalk.chatbot.message.TextMessage;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Polaris
 */
public class SendProcessor implements Callable<SendResultInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendProcessor.class);

    private MessageSendInfo messageSend;

    private DingTokenConfig token;

    private HttpClientUtils client;

    public SendProcessor(MessageSendInfo messageSend, DingTokenConfig token,
            HttpClientUtils client) {
        this.client = client;
        this.messageSend = messageSend;
        this.token = token;
    }

    @Override
    public SendResultInfo call() {
        try {
            String msg = msgAppend(messageSend);
            SendResultInfo result = sendMessage(messageSend, msg, token);
            if (result == null) {
                LOGGER.warn("请求错误，msg等待重试，信息：{}", JSONObject.toJSONString(messageSend));
            }
            return result;
        } catch (Exception e) {
            LOGGER.warn("发送消息异常", e);
        }
        return null;
    }

    private SendResultInfo sendMessage(MessageSendInfo messageSend, String msg,
            DingTokenConfig token) {
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
            SendResultInfo sendResult = new SendResultInfo();
            String result = client.post(url, textMessage.toJsonString());
            if (StringUtils.isNotEmpty(result)) {
                JSONObject obj = JSONObject.parseObject(result);
                Integer errCode = obj.getInteger("errcode");
                sendResult.setIsSuccess(errCode.equals(0));
                sendResult.setIds(messageSend.getIds());
                sendResult.setAppDep(messageSend.getAppDep());
                sendResult.setAppName(messageSend.getAppName());
                sendResult.setTokenId(token.getId());
                if (!sendResult.getIsSuccess()) {
                    sendResult.setMsg(msg);
                    sendResult.setErrorCode(errCode);
                    sendResult.setErrorMsg(obj.getString("errmsg"));
                }
            }
            return sendResult;
        } catch (Exception e) {
            LOGGER.error("消息发送失败，", e);
            return null;
        }
    }

    private String calSecret(String secret, Long timestamp) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
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
}

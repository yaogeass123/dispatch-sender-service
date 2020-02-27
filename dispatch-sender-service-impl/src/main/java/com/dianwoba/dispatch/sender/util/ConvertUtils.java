package com.dianwoba.dispatch.sender.util;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.domain.ErrorInfo;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoda.delibird.dingtalk.chatbot.SendResult;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;

/**
 * @author Polaris
 */
public class ConvertUtils {

    public static MessageLog convert2MessageLog(MessageSendDTO messageSendDTO) {
        MessageLog messageLog = new MessageLog();
        messageLog.setAppCode(messageSendDTO.getAppCode());
        messageLog.setDigest(messageSendDTO.getDigest());
        messageLog.setMsg(messageSendDTO.getMsg());
        messageLog.setLevel(messageSendDTO.getLevel());
        messageLog.setTime(new Date(messageSendDTO.getTime()));
        messageLog.setIp(messageSendDTO.getIp());
        if (StringUtils.isEmpty(messageSendDTO.getExceptionType())) {
            messageLog.setExceptionType("OthersException");
        } else {
            messageLog.setExceptionType(messageSendDTO.getExceptionType());
        }
        messageLog.setInsTm(new Date());
        return messageLog;
    }

    public static MessageSend convert2MessageSend(List<MessageLog> messageLogs) {
        MessageSend messageSend = new MessageSend();
        MessageLog messageLog = messageLogs.get(0);
        messageSend.setAppCode(messageLog.getAppCode());
        messageSend.setExceptionType(messageLog.getExceptionType());
        messageSend.setDigest(messageLog.getDigest());
        messageSend.setLevel(messageLog.getLevel());
        messageSend.setMsg(messageLog.getMsg());
        messageSend.setCount(messageLogs.size());
        messageSend.setStatus(StatusEn.INIT.getStatusCode());
        messageSend.setInsertTm(new Date());
        if (messageLogs.size() > 1) {
            List<String> ipList = messageLogs.stream().map(MessageLog::getIp)
                    .collect(Collectors.toList());
            if (ipList.size() > 1) {
                StringBuilder ips = new StringBuilder();
                for (String ip : ipList) {
                    ips.append(ip).append(",");
                }
                ips.deleteCharAt(ips.length() - 1);
                messageSend.setIps(ips.toString());
            } else {
                messageSend.setIps(ipList.get(0));
            }
            messageSend.setStartTm(messageLogs.get(0).getTime());
            messageSend.setEndTm(messageLogs.get(messageLogs.size() - 1).getTime());
        } else {
            messageSend.setIps(messageLog.getIp());
            messageSend.setStartTm(messageLog.getTime());
            messageSend.setEndTm(messageLog.getTime());
        }
        return messageSend;
    }

    public static AppDepInfo convert2AppDepInfo(AppDep appDep) {
        AppDepInfo info = new AppDepInfo();
        info.setAppCode(appDep.getAppCode());
        info.setDevelopersDepId(appDep.getDevelopersDepId());
        info.setDevelopersPhone(appDep.getDevelopersPhone());
        info.setOwnersDepId(appDep.getOwnersDepId());
        info.setOwnersPhone(appDep.getOwnersPhone());
        info.setGroupMail(appDep.getMailGroup());
        return info;
    }

    public static ErrorInfo convert2ErrorInfo(SendResult result, List<Long> ids) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setIds(ids);
        errorInfo.setErrorCode(String.valueOf(result.getErrorCode()));
        errorInfo.setErrorMsg(result.getErrorMsg());
        return errorInfo;
    }

    public static MessageSendInfo convert2MessageSendInfo(MessageSend messageSend) {
        MessageSendInfo info = new MessageSendInfo();
        info.setIds(Lists.newArrayList(messageSend.getId()));
        info.setClusterId(messageSend.getClusterId());
        info.setGroupId(messageSend.getGroupId());
        info.setAppCode(messageSend.getAppCode());
        info.setIps(messageSend.getIps());
        info.setExceptionType(messageSend.getExceptionType());
        info.setDigest(messageSend.getDigest());
        info.setMsg(messageSend.getMsg());
        info.setLevel(messageSend.getLevel());
        info.setStartTm(messageSend.getStartTm());
        info.setEndTm(messageSend.getEndTm());
        info.setCount(messageSend.getCount());
        info.setAtWho(messageSend.getAtWho());
        info.setInsertTm(messageSend.getInsertTm());
        info.setStatus(messageSend.getStatus());
        return info;
    }
}

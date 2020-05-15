package com.dianwoba.dispatch.sender.util;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.domain.ErrorInfo;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.domain.SendResultInfo;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.AppDepInfoDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.DingTokenInfoDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.GroupInfoDTO;
import com.dianwoba.dispatch.sender.domain.dto.result.MatchRuleInfoDTO;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.AppDep;
import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.GroupMatchRules;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.genius.domain.dto.DepartDTO;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;


/**
 * @author Polaris
 */
public class ConvertUtils {

    public static MessageLog convert2MessageLog(MessageSendDTO messageSendDTO) {
        MessageLog messageLog = new MessageLog();
        messageLog.setAppName(messageSendDTO.getAppName());
        messageLog.setDigest(messageSendDTO.getDigest());
        if (messageSendDTO.getMsg().length() > Constant.MAX_MSG_LEN) {
            messageLog.setMsg(messageSendDTO.getMsg().substring(0, Constant.MAX_MSG_LEN) + "\n...");
        } else {
            messageLog.setMsg(messageSendDTO.getMsg());
        }
        messageLog.setLevel(messageSendDTO.getLevel().getLevelCode());
        messageLog.setTime(messageSendDTO.getTime());
        messageLog.setIp(messageSendDTO.getIp());
        if (StringUtils.isEmpty(messageSendDTO.getExceptionType())) {
            messageLog.setExceptionType(Constant.DING_MESSAGE);
        } else {
            messageLog.setExceptionType(messageSendDTO.getExceptionType());
        }
        messageLog.setInsTm(new Date());
        return messageLog;
    }

    public static MessageSend convert2MessageSend(List<MessageLog> messageLogs) {
        MessageSend messageSend = new MessageSend();
        MessageLog messageLog = messageLogs.get(0);
        messageSend.setAppName(messageLog.getAppName());
        messageSend.setExceptionType(messageLog.getExceptionType());
        messageSend.setDigest(messageLog.getDigest());
        messageSend.setLevel(messageLog.getLevel());
        messageSend.setMsg(messageLog.getMsg());
        messageSend.setCount(messageLogs.size());
        messageSend.setStatus(StatusEn.INIT.getStatusCode());
        messageSend.setInsertTm(new Date());
        if (messageLogs.size() > 1) {
            List<String> ipList = messageLogs.stream().map(MessageLog::getIp).distinct()
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
        info.setId(appDep.getId());
        info.setAppName(appDep.getAppName());
        info.setDevelopersDepId(appDep.getDevelopersDepId());
        info.setDevelopersPhone(appDep.getDevelopersPhone());
        info.setDevelopersMail(appDep.getDevelopersMail());
        info.setOwnersDepId(appDep.getOwnersDepId());
        info.setOwnersPhone(appDep.getOwnersPhone());
        info.setOwnersMail(appDep.getOwnersMail());
        info.setDepId(String.valueOf(appDep.getDepId()));
        if (appDep.getManualDepId() != null) {
            info.setManualDepId(String.valueOf(appDep.getManualDepId()));
        }
        return info;
    }

    public static ErrorInfo convert2ErrorInfo(SendResultInfo result) {
        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setIds(result.getIds());
        errorInfo.setErrorCode(String.valueOf(result.getErrorCode()));
        errorInfo.setErrorMsg(result.getErrorMsg());
        return errorInfo;
    }

    public static MessageSendInfo convert2MessageSendInfo(MessageSend messageSend) {
        MessageSendInfo info = new MessageSendInfo();
        info.setIds(Lists.newArrayList(messageSend.getId()));
        info.setAppDep(messageSend.getAppDep());
        info.setGroupId(messageSend.getGroupId());
        info.setAppName(messageSend.getAppName());
        info.setIps(messageSend.getIps());
        info.setExceptionType(messageSend.getExceptionType());
        info.setDigest(messageSend.getDigest());
        info.setMsg(messageSend.getMsg());
        info.setLevel(LevelEn.get(messageSend.getLevel()));
        info.setStartTm(messageSend.getStartTm());
        info.setEndTm(messageSend.getEndTm());
        info.setCount(messageSend.getCount());
        info.setAtWho(messageSend.getAtWho());
        info.setAtAll(messageSend.getAtAll());
        info.setInsertTm(messageSend.getInsertTm());
        info.setStatus(messageSend.getStatus());
        return info;
    }

    public static DepInfo convert2DepInfo(DepartDTO departDTO) {
        DepInfo depInfo = new DepInfo();
        depInfo.setId(departDTO.getId());
        depInfo.setName(departDTO.getName());
        depInfo.setPath(departDTO.getPath());
        depInfo.setParent(departDTO.getParent());
        depInfo.setCreator(Constant.DEFAULT_STAFF);
        depInfo.setCreateTime(new Date());
        depInfo.setNewest(depInfo.getId());
        return depInfo;
    }

    public static AppDepInfoDTO convert2AppDepInfoDTO(AppDepInfo appDepInfo) {
        AppDepInfoDTO infoDTO = new AppDepInfoDTO();
        infoDTO.setAppName(appDepInfo.getAppName());
        infoDTO.setDefaultDepId(Integer.parseInt(appDepInfo.getDepId()));
        infoDTO.setId(appDepInfo.getId());
        infoDTO.setManualDepId(StringUtils.isEmpty(appDepInfo.getManualDepId()) ? null
                : Integer.parseInt(appDepInfo.getManualDepId()));
        return infoDTO;
    }

    public static GroupInfoDTO convert2GroupInfoDTO(DingGroupName groupName) {
        GroupInfoDTO infoDTO = new GroupInfoDTO();
        infoDTO.setId(groupName.getId());
        infoDTO.setGroupName(groupName.getGroupName());
        infoDTO.setAtWho(groupName.getAtWho());
        infoDTO.setAtAll(groupName.getAtAll());
        infoDTO.setMail(groupName.getMail());
        return infoDTO;
    }

    public static MatchRuleInfoDTO convert2MatchRuleInfoDTO(GroupMatchRules rules) {
        MatchRuleInfoDTO matchRuleInfoDTO = new MatchRuleInfoDTO();
        matchRuleInfoDTO.setGroupId(rules.getGroupId());
        matchRuleInfoDTO.setAppName(rules.getAppName());
        matchRuleInfoDTO.setAppDep(rules.getAppDep());
        matchRuleInfoDTO.setException(rules.getException());
        matchRuleInfoDTO.setAtAll(rules.getAtAll());
        matchRuleInfoDTO.setKeyWords(rules.getKeyWords());
        matchRuleInfoDTO.setLevel(rules.getLevel());
        matchRuleInfoDTO.setAtWho(rules.getAtWho());
        return matchRuleInfoDTO;
    }

    public static DingTokenInfoDTO convert2DingTokenInfoDTO(DingTokenConfig dingTokenConfig) {
        DingTokenInfoDTO dingTokenInfoDTO = new DingTokenInfoDTO();
        dingTokenInfoDTO.setAppDep(dingTokenConfig.getAppDep());
        dingTokenInfoDTO.setGroupId(dingTokenConfig.getGroupId());
        dingTokenInfoDTO.setId(dingTokenConfig.getId());
        dingTokenInfoDTO.setKeyWords(dingTokenConfig.getKeyWords());
        dingTokenInfoDTO.setSecret(dingTokenConfig.getSecret());
        dingTokenInfoDTO.setToken(dingTokenConfig.getToken());
        dingTokenInfoDTO.setStatus(dingTokenConfig.getStatus());
        return dingTokenInfoDTO;
    }
}

package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.GroupMatchCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.manager.MessageLogManager;
import com.dianwoba.dispatch.sender.provider.impl.MessageSendProviderImpl;
import com.dianwoba.dispatch.sender.runnable.GroupMatcher;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;

import org.junit.Test;

public class MatchTest extends UnitTestBase {

    @Resource
    private AppDepCache appDepCache;

    @Resource
    private GroupMatchCache groupMatchCache;

    @Resource
    private MessageSendProviderImpl messageSendProvider;

    @Resource
    private MessageLogManager messageLogManager;

    @Test
    public void doTest(){
//        query();
        matchTest();
//        log();
//        logTest();
    }

    private void query(){
        System.out.println(messageLogManager.queryAllUnhandled().get(0).getMsg().length());
    }


    private void matchTest() {
        List<MessageLog> unhandledMessage = messageLogManager.queryAllUnhandled();
        if (CollectionUtils.isEmpty(unhandledMessage)) {
            return;
        }
        Map<String, List<MessageLog>> unhandledGroup = unhandledMessage.stream().collect(Collectors
                .groupingBy(message -> String
                        .format(Constant.GROUP_COMMON_FORMAT, message.getAppName(),
                                message.getExceptionType(), message.getDigest())));
        ExecutorService es = Executors.newFixedThreadPool(10);
        unhandledGroup.values()
                .forEach(list ->es.submit(new GroupMatcher(list)));
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logTest() {
        MessageLog messageLog = new MessageLog();
        messageLog.setMsg("grab-dispatch match test 1");
        messageLog.setIp("1.1.1.1");
        messageLog.setLevel((byte) 2);
        messageLog.setExceptionType("OthersException");
        messageLog.setAppName("grab-dispatch");
        messageLog.setDigest("digest2");
        messageLog.setTime(new Date());
        List<MessageLog> list = Lists.newArrayList(messageLog);
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(new GroupMatcher(list));
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void log() {
        MessageSendDTO messageSendDTO = new MessageSendDTO();
        messageSendDTO.setAppName("grab-dispatch");
        messageSendDTO.setDigest("digest 2");
        messageSendDTO.setExceptionType(Constant.DING_MESSAGE);
        messageSendDTO.setIp("1.1.1.21");
        messageSendDTO.setLevel(LevelEn.MEDIUM);
        messageSendDTO.setMsg("grab-dispatch match test 123");
        messageSendDTO.setTime(new Date());
        messageSendProvider.send(messageSendDTO);
    }

}

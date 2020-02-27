package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.GroupMatchCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.entity.MessageLog;
import com.dianwoba.dispatch.sender.runnable.GroupMatcher;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Resource;
import org.assertj.core.util.Lists;
import org.junit.Test;

public class MatchTest extends UnitTestBase {

    @Resource
    private AppDepCache appDepCache;

    @Resource
    private GroupMatchCache groupMatchCache;

    @Test
    public void doTest(){
        matchTest();
    }

    private void matchTest() {
        MessageLog messageLog = new MessageLog();
        messageLog.setMsg("1234");
        messageLog.setIp("1.1.1.1");
        messageLog.setLevel((byte) 2);
        messageLog.setExceptionType("OthersException");
        messageLog.setAppCode("test");
        messageLog.setDigest("12");
        messageLog.setTime(new Date());
        List<MessageLog> list = Lists.newArrayList(messageLog);
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.submit(new GroupMatcher(list));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

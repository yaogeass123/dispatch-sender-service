package com.dianwoba.dispatch.sender.test;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.cache.DingTokenConfigCache;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.MailListContent;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.domain.dto.param.MessageSendDTO;
import com.dianwoba.dispatch.sender.en.LevelEn;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.DingTokenConfig;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.entity.MessageSendCountPO;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.provider.impl.MessageSendProviderImpl;
import com.dianwoba.dispatch.sender.runnable.MessageSender;
import com.dianwoba.dispatch.sender.util.BucketUtils;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoda.delibird.provider.DeliMailProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;

import org.junit.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class SendTest extends UnitTestBase {

    @Resource
    private MessageSenderManager messageSenderManager;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private DeliMailProvider deliMailProvider;

    @Resource
    private GroupConfigManager groupConfigManager;

    @Resource
    private MessageSendProviderImpl messageSendProviderImpl;

    @Resource
    private DingTokenConfigCache dingTokenConfigCache;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void doTest(){
//        send();
        sendTest();
//        test();
    }

    public void sendTest() {
        init();
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<MessageSendCountPO> countList = messageSenderManager
                .countByGroupId(StatusEn.INIT.getStatusCode());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(countList)) {
            countList.forEach(v -> es.submit(new MessageSender(v.getGroupId())));
        }
        try {
            Thread.sleep(300000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void init() {
        //测试初始化redis
        Map<String, List<DingTokenConfig>> map = dingTokenConfigCache.queryAllFromClientCache();
        map.forEach((k, v) -> stringRedisTemplate.opsForValue().set("redis_" + k,
                "0:" + BucketUtils.buildBucketString(
                        v.stream().map(DingTokenConfig::getId).collect(Collectors.toSet())), 15,
                TimeUnit.SECONDS));

    }

    private void send(){
        MessageSendDTO dto = new MessageSendDTO();
        dto.setAppName("1");
        dto.setIp("1");
        dto.setDigest("1");
        dto.setLevel(LevelEn.MEDIUM);
        dto.setExceptionType(Constant.DING_MESSAGE);
        for (int i = 0; i < 120; i++) {
            dto.setMsg(String.format("test %d",i));
            dto.setTime(new Date());
            messageSendProviderImpl.send(dto);
        }

    }


    public void test(){

        List<MessageSend> unreportedMessage = messageSenderManager.queryUnsentMessageWithMinute(3);
        if (CollectionUtils.isEmpty(unreportedMessage)) {
            return;
        }
        Map<String, List<MessageSend>> groupBy = unreportedMessage.stream().collect(
                Collectors.groupingBy(MessageSend::getAppDep));

        groupBy.forEach((k,v) ->{
            List<Long> ids = v.stream().map(MessageSend::getId).collect(Collectors.toList());
//            messageSenderManager.batchUpdateIgnore(ids);
            System.out.println(buildContent(v));
//            sendMail(buildContent(v),k);
        });

    }
    private String buildContent(List<MessageSend> list){
        Context context = new Context();
        context.setVariable("content", "Test");

        Map<Long, List<MessageSend>> map = list.stream().collect(Collectors.groupingBy(MessageSend::getGroupId));
        Map<String, List<MailListContent>> listMap = Maps.newHashMap();
        map.forEach((k,v) ->{
            String groupName = groupConfigManager.findGroupNameByCache(k);
            List<MailListContent> mailList = Lists.newArrayList();
            v.forEach(x -> {
                MailListContent content = new MailListContent();
                content.setMessage(x.getMsg());
                content.setIps(x.getIps());
                content.setCount(x.getCount());
                mailList.add(content);
            });
            listMap.put(groupName, mailList);
        });
        context.setVariable("map", listMap);
        return templateEngine.process("mailTemplate", context);
    }
}

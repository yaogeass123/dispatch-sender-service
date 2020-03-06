package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.domain.MailListContent;
import com.dianwoba.dispatch.sender.domain.MessageSendInfo;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.runnable.MessageSender;
import com.dianwoba.dispatch.sender.util.ConvertUtils;
import com.dianwoda.delibird.provider.DeliMailProvider;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
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


    @Test
    public void doTest(){
        sendTest();
//        test();
    }

    public void sendTest(){
        ExecutorService es = Executors.newFixedThreadPool(10);
        List<MessageSend> messageSends = messageSenderManager.queryMessageToBeSent();
        List<MessageSendInfo> infoList = messageSends.stream()
                .map(ConvertUtils::convert2MessageSendInfo).collect(Collectors.toList());
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(infoList)) {
            Map<Long, List<MessageSendInfo>> group = infoList.stream()
                    .collect(Collectors.groupingBy(MessageSendInfo::getGroupId));
            group.values().forEach(v -> es.submit(new MessageSender(v)));
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

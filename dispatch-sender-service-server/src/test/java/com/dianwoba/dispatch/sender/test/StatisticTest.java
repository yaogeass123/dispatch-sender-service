package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.StatisticContent;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.util.MailUtils;
import com.dianwoda.delibird.mail.dto.MailBody;
import com.dianwoda.delibird.mail.dto.MailHead;
import com.dianwoda.delibird.mail.dto.MailReceiver;
import com.dianwoda.delibird.mail.dto.MailRequest;
import com.dianwoda.delibird.provider.DeliMailProvider;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class StatisticTest extends UnitTestBase {

    @Resource
    private MessageSenderManager messageSenderManager;

    @Resource
    private DeliMailProvider deliMailProvider;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private GroupConfigManager groupConfigManager;

    @Test
    public void test() {
        doTest();
    }


    private void doTest() {

        List<MessageSend> messageSendList = messageSenderManager.statisticMessage();
        Map<String, List<MessageSend>> divideByAppDep = messageSendList.stream()
                .collect(Collectors.groupingBy(MessageSend::getAppDep));
        divideByAppDep.forEach((k, v) -> {
            String content = buildContent(v);
            System.out.println(content);
//            sendMail(content, k);
        });

    }

    private String buildContent(List<MessageSend> list) {
        Map<Long, List<MessageSend>> group = list.stream()
                .collect(Collectors.groupingBy(MessageSend::getGroupId));
        Map<String, List<StatisticContent>> map = Maps.newHashMap();
        group.forEach((k, v) -> {
            List<StatisticContent> contents = Lists.newArrayList();
            List<MessageSend> ignore = v.stream()
                    .filter(x -> x.getStatus().equals(StatusEn.IGNORE.getStatusCode()))
                    .collect(Collectors.toList());
            List<MessageSend> error = v.stream()
                    .filter(x -> x.getStatus().equals(StatusEn.ERROR.getStatusCode()))
                    .collect(Collectors.toList());
            int ignoreNum = ignore.stream().mapToInt(MessageSend::getCount).sum();
            if (CollectionUtils.isEmpty(error)) {
                StatisticContent content = new StatisticContent();
                content.setIgnore(ignoreNum);
                content.setErrorTimes(0);
                content.setErrorCode("null");
                contents.add(content);
            } else {
                Map<String, List<MessageSend>> errorMap = error.stream()
                        .collect(Collectors.groupingBy(MessageSend::getRobotErrorCode));
                errorMap.forEach((key, value) -> {
                    StatisticContent content = new StatisticContent();
                    content.setIgnore(ignoreNum);
                    content.setErrorCode(value.get(0).getRobotErrorCode());
                    content.setErrorTimes(value.stream().mapToInt(MessageSend::getCount).sum());
                    contents.add(content);
                });
            }
            map.put(groupConfigManager.findGroupNameByCache(k), contents);
        });
        Context context = new Context();
        context.setVariable("content", "统计");
        context.setVariable("map", map);
        return templateEngine.process("statisticTemplate", context);
    }

}

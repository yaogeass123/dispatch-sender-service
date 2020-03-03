package com.dianwoba.dispatch.sender.job;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.StatisticContent;
import com.dianwoba.dispatch.sender.en.StatusEn;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.util.MailUtils;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
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
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * @author Polaris
 */

@Component
public class StatisticHandler extends AbstractJobExecuteService {

    @Resource
    private MessageSenderManager messageSenderManager;

    @Resource
    private DeliMailProvider deliMailProvider;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private GroupConfigManager groupConfigManager;

    @Override
    public void doExecute(ExecuteContext executeContext) {
        List<MessageSend> messageSendList = messageSenderManager.statisticMessage();
        Map<String, List<MessageSend>> divideByCluster = messageSendList.stream()
                .collect(Collectors.groupingBy(MessageSend::getClusterId));
        divideByCluster.forEach((k, v) -> {
            String content = buildClusterContent(v);
            sendMail(content, k);
        });
    }

    private String buildClusterContent(List<MessageSend> list) {
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

    private void sendMail(String content, String clusterId) {
        String mailAddress = MailUtils.getMailAddress(clusterId);
        MailHead mailHead = MailHead.create();
        MailRequest mailRequest = MailRequest.builder()
                .receivers(MailReceiver.create(mailAddress))
                .body(MailBody.create().setSubject(Constant.MAIL_SUBJECT_STATISTIC)
                        .setContent(content)).head(mailHead).build();
        deliMailProvider.send(mailRequest);
    }
}

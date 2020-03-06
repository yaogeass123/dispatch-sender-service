package com.dianwoba.dispatch.sender.job;

import com.dianwoba.dispatch.sender.constant.Constant;
import com.dianwoba.dispatch.sender.domain.MailListContent;
import com.dianwoba.dispatch.sender.entity.MessageSend;
import com.dianwoba.dispatch.sender.manager.GroupConfigManager;
import com.dianwoba.dispatch.sender.manager.MessageSenderManager;
import com.dianwoba.dispatch.sender.wrapper.MailSendWrapper;
import com.dianwoba.pt.goodjob.node.bean.ExecuteContext;
import com.dianwoba.pt.goodjob.node.service.impl.AbstractJobExecuteService;
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
 * @author Polatis
 */
@Component
public class UnreportedMessageHandler extends AbstractJobExecuteService {

    @Resource
    private MessageSenderManager messageSenderManager;

    @Resource
    private DeliMailProvider deliMailProvider;

    @Resource
    private TemplateEngine templateEngine;

    @Resource
    private GroupConfigManager groupConfigManager;

    @Resource
    private MailSendWrapper mailSendWrapper;

    @Override
    public void doExecute(ExecuteContext executeContext) {

        List<MessageSend> unreportedMessage = messageSenderManager.queryUnsentMessageWithMinute(3);
        if (CollectionUtils.isEmpty(unreportedMessage)) {
            return;
        }
        Map<String, List<MessageSend>> groupByAppDep = unreportedMessage.stream()
                .collect(Collectors.groupingBy(MessageSend::getAppDep));

        groupByAppDep.forEach((k, v) -> {
            List<Long> ids = v.stream().map(MessageSend::getId).collect(Collectors.toList());
            messageSenderManager.batchUpdateIgnore(ids);
            String mailAddress = mailSendWrapper.getMailAddress(k);
            mailSendWrapper.sendMail(buildContent(v), mailAddress, Constant.MAIL_SUBJECT_IGNORE);
        });
    }

    private String buildContent(List<MessageSend> list) {
        Context context = new Context();
        context.setVariable("content", "Test");
        Map<Long, List<MessageSend>> map = list.stream()
                .collect(Collectors.groupingBy(MessageSend::getGroupId));
        Map<String, List<MailListContent>> listMap = Maps.newHashMap();
        map.forEach((k, v) -> {
            String groupName = groupConfigManager.findGroupNameByCache(k);
            List<MailListContent> mailList = Lists.newArrayList();
            v.forEach(x -> {
                MailListContent content = new MailListContent();
                content.setMessage(x.getMsg());
                content.setIps(x.getIps());
                content.setCount(x.getCount());
                content.setInsert(x.getInsertTm());
                content.setStart(x.getStartTm());
                content.setEnd(x.getEndTm());
                mailList.add(content);
            });
            listMap.put(groupName, mailList);
        });
        context.setVariable("map", listMap);
        return templateEngine.process("mailTemplate", context);
    }


}

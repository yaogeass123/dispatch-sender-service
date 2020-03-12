package com.dianwoba.dispatch.sender.wrapper;

import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.DepInfoCache;
import com.dianwoba.dispatch.sender.cache.GroupConfigCache;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoba.dispatch.sender.entity.DingGroupName;
import com.dianwoda.delibird.provider.DeliMailProvider;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class MailSendWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailSendWrapper.class);

    @Value("${defaultMailAddress:yaochenguang@dianwoda.com}")
    private String defaultMailAddress;

    @Resource
    private AppDepCache appDepCache;

    @Resource
    private DepInfoCache depInfoCache;

    @Resource
    private GroupConfigCache groupConfigCache;

    @Resource
    private DeliMailProvider deliMailProvider;

    public String getMailAddress(String appDep, String appCode) {
        DepInfo info = depInfoCache.queryFromClientCache(Integer.parseInt(appDep));
        if (info != null && StringUtils.isNotEmpty(info.getMail())) {
            return info.getMail();
        }
        AppDepInfo appDepInfo = appDepCache.queryFromClientCache(appCode);
        if (StringUtils.isNotEmpty(appDepInfo.getDevelopersMail())) {
            return appDepInfo.getDevelopersMail();
        }
        if (StringUtils.isNotEmpty(appDepInfo.getOwnersMail())) {
            return appDepInfo.getOwnersMail();
        }
        return defaultMailAddress;
    }

    public String getMailAddress(String appDep, long groupId) {
        DingGroupName groupName = groupConfigCache.queryFromClientCache(groupId);
        if (groupName != null && StringUtils.isNotEmpty(groupName.getMail())) {
            return groupName.getMail();
        }
        DepInfo info = depInfoCache.queryFromClientCache(Integer.parseInt(appDep));
        if (info != null && StringUtils.isNotEmpty(info.getMail())) {
            return info.getMail();
        }
        return defaultMailAddress;
    }

    public String getMailAddress(String appDep) {
        DepInfo info = depInfoCache.queryFromClientCache(Integer.parseInt(appDep));
        if (info != null && StringUtils.isNotEmpty(info.getMail())) {
            return info.getMail();
        }
        return defaultMailAddress;
    }

    public void sendMail(String content, String mailAddress, String subject) {
        LOGGER.info(content);
//        MailHead mailHead = MailHead.create();
//        MailRequest mailRequest = MailRequest.builder()
//                .receivers(MailReceiver.create(mailAddress.split(",")))
//                .body(MailBody.create().setSubject(subject).setContent(content)).head(mailHead)
//                .build();
//        deliMailProvider.send(mailRequest);
    }

}

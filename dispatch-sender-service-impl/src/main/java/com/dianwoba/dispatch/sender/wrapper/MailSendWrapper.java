package com.dianwoba.dispatch.sender.wrapper;

import com.dianwoba.dispatch.sender.cache.AppDepCache;
import com.dianwoba.dispatch.sender.cache.DepInfoCache;
import com.dianwoba.dispatch.sender.domain.AppDepInfo;
import com.dianwoba.dispatch.sender.entity.DepInfo;
import com.dianwoda.delibird.mail.dto.MailBody;
import com.dianwoda.delibird.mail.dto.MailHead;
import com.dianwoda.delibird.mail.dto.MailReceiver;
import com.dianwoda.delibird.mail.dto.MailRequest;
import com.dianwoda.delibird.provider.DeliMailProvider;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Polaris
 */
@Component
public class MailSendWrapper {

    @Value("${defaultMailAddress:yaochenguang@dianwoda.com}")
    private String defaultMailAddress;

    @Resource
    private AppDepCache appDepCache;

    @Resource
    private DepInfoCache depInfoCache;

    @Resource
    private DeliMailProvider deliMailProvider;

    public String getMailAddress(String clusterId, String appCode) {
        DepInfo info = depInfoCache.queryFromClientCache(Integer.parseInt(clusterId));
        if (info != null && info.getMail() != null) {
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

    public String getMailAddress(String clusterId) {
        DepInfo info = depInfoCache.queryFromClientCache(Integer.parseInt(clusterId));
        if (info != null && info.getMail() != null) {
            return info.getMail();
        }
        return defaultMailAddress;
    }

    public void sendMail(String content, String mailAddress, String subject) {
        MailHead mailHead = MailHead.create();
        MailRequest mailRequest = MailRequest.builder()
                .receivers(MailReceiver.create(mailAddress.split(",")))
                .body(MailBody.create().setSubject(subject).setContent(content)).head(mailHead)
                .build();
        deliMailProvider.send(mailRequest);
    }

}

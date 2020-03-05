package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.dispatch.sender.wrapper.MailSendWrapper;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.stereotype.Component;

@Component
public class MailAddressTest extends UnitTestBase {

    @Resource
    private MailSendWrapper mailSendWrapper;

    @Test
    public void test() {
        System.out.println(mailMatch());
    }

    private String mailMatch(){
        return mailSendWrapper.getMailAddress("445", "system-dispatch");
    }



}

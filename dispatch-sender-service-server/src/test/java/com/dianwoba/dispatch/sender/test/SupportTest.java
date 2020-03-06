package com.dianwoba.dispatch.sender.test;

import com.dianwoba.dispatch.sender.SenderService;
import com.dianwoba.dispatch.sender.UnitTestBase;
import org.junit.Test;


public class SupportTest extends UnitTestBase {

    @Test
    public void test(){
        SenderService.sendDingMsg("11234");
    }
}

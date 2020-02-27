package com.dianwoba.dispatch.sender.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.UnitTestBase;
import com.dianwoba.wireless.http.support.util.HttpUtils;
import com.dianwoba.wireless.monitor.constant.Constant;
import com.dianwoba.wireless.monitor.domain.dto.common.DepPlatformAppDTO;
import com.google.common.collect.Lists;
import com.google.gson.annotations.JsonAdapter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppDepTest extends UnitTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppDepTest.class);

    @Test
    public void test(){
        System.out.println(JSONObject.toJSONString(getAppDepFromDepPlatform(0L)));
    }

    public List<DepPlatformAppDTO> getAppDepFromDepPlatform(Long modifyTime) {
        try {
            String link = Constant.PREFIX_LINK_DEPLOY_PLATFORM + modifyTime;
            String resultValue = HttpUtils.get(link);
            LOGGER.info("调发布平台接口，resultValue:{}", resultValue);
            if (StringUtils.isNotEmpty(resultValue)) {
                JSONObject json = JSON.parseObject(resultValue);
                String data = json.getString("data");
                if (StringUtils.isNotEmpty(data)) {
                    return JSON.parseArray(data, DepPlatformAppDTO.class);
                }
            }
            LOGGER.info("调发布平台接口，resultValue:{}", resultValue);
            if (StringUtils.isNotEmpty(resultValue)) {
                JSONObject json = JSON.parseObject(resultValue);
                String data = json.getString("data");
                if (StringUtils.isNotEmpty(data)) {
                    return JSON.parseArray(data, DepPlatformAppDTO.class);
                }
            }
        } catch (IOException e) {
            LOGGER.error("调发布平台接口异常", e);
        }

        return Lists.newArrayList();
    }
}

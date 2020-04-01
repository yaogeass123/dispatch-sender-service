package com.dianwoba.dispatch.sender.util;

import com.alibaba.fastjson.JSONObject;
import com.dianwoba.dispatch.sender.constant.Constant;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dispatch-utils工具包中HttpClientUtils 魔改
 *
 * @author Polaris
 */
public class HttpClientUtils {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(com.dianwoba.dispatch.utils.HttpClientUtils.class);

    private int maxConnTotal = 200;
    private int maxConnPerRoute = 100;
    private RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000)
            .setConnectionRequestTimeout(3000).setSocketTimeout(3000).build();

    private HttpClient httpclient = null;

    public HttpClient getHttpclient() {
        return this.httpclient;
    }

    public void setHttpclient(HttpClient httpclient) {
        this.httpclient = httpclient;
    }

    public void setMaxConnPerRoute(int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
    }

    public void setMaxConnTotal(int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
    }

    public void setTimeout(int connTimeout, int requestTimeout, int soTimeout) {
        this.requestConfig = RequestConfig.custom().setConnectTimeout(connTimeout)
                .setConnectionRequestTimeout(requestTimeout).setSocketTimeout(soTimeout).build();
    }

    public void buildHttpClient() {
        if (this.httpclient == null) {
            this.httpclient = HttpClients.custom().setDefaultRequestConfig(this.requestConfig)
                    .setMaxConnTotal(this.maxConnTotal).setMaxConnPerRoute(this.maxConnPerRoute)
                    .build();
        }
    }


    public String post(String webhook, String json) throws IOException {
        long start = System.currentTimeMillis();

        this.buildHttpClient();
        HttpPost httpPost = new HttpPost(webhook);
        httpPost.setConfig(this.requestConfig);
        httpPost.addHeader("Content-Type", "application/json; charset=utf-8");
        StringEntity se = new StringEntity(json, "utf-8");
        httpPost.setEntity(se);
        HttpResponse response = null;
        try {
            response = this.httpclient.execute(httpPost);
            LOGGER.info("http response :{}", JSONObject.toJSONString(response));
            if (response.getStatusLine().getStatusCode() == Constant.HTTP_OK) {
                return EntityUtils.toString(response.getEntity());
            }
            if (Constant.HTTP_MOVED_TEMPORARILY
                    .equals(String.valueOf(response.getStatusLine().getStatusCode()))) {
                return Constant.HTTP_MOVED_TEMPORARILY;
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("execute occurs error", e);
            return null;
        } finally {
            if (response != null) {
                EntityUtils.consume(response.getEntity());
            }
            LOGGER.info("HttpClientUtils send cost time: {}", System.currentTimeMillis() - start);
        }
    }
}

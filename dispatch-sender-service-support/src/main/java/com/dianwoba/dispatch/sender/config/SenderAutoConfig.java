package com.dianwoba.dispatch.sender.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.dianwoba.dispatch.sender.SenderService;
import com.dianwoba.dispatch.sender.provider.MessageSendProvider;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Polaris
 */
@Configuration
public class SenderAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public MessageSendProvider messageSendProvider(ApplicationConfig application,
            @Autowired List<RegistryConfig> registries) {
        ReferenceConfig<MessageSendProvider> reference = new ReferenceConfig();
        reference.setApplication(application);
        reference.setRegistries(registries);
        reference.setInterface(MessageSendProvider.class);
        reference.setVersion("1.0.0");
        reference.setCheck(Boolean.FALSE);
        reference.setTimeout(500);
        reference.setRetries(0);
        return reference.get();
    }

    @Bean
    @ConditionalOnMissingBean
    public SenderService senderService() {
        return new SenderService();
    }

}

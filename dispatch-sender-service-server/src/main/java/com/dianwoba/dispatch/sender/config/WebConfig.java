package com.dianwoba.dispatch.sender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class WebConfig {


        /**
         * 配置 Thymeleaf 视图解析器 —— 将逻辑视图名称解析为 Thymeleaf 模板视图
         *
         * @param springTemplateEngine 模板引擎
         * @return
         */
        @Bean
        public ViewResolver viewResolver(SpringTemplateEngine springTemplateEngine){
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine(springTemplateEngine);
            return resolver;
        }

        /**
         * 模板引擎 —— 处理模板并渲染结果
         *
         * @param templateResolver 模板解析器
         * @return
         */
        @Bean
        public SpringTemplateEngine springTemplateEngine(ITemplateResolver templateResolver) {
            SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
            springTemplateEngine.setTemplateResolver(templateResolver);
            return springTemplateEngine;
        }

        /**
         * 模板解析器 —— 加载 Thymeleaf 模板
         *
         * @return
         */
        @Bean
        public ITemplateResolver templateResolver() {
            SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
            templateResolver.setPrefix("classpath:/template/");
            templateResolver.setSuffix(".htm");
            templateResolver.setCacheable(false);
            templateResolver.setTemplateMode("HTML5");
            return templateResolver;
        }
}

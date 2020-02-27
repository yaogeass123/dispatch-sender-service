package com.dianwoba.dispatch.sender.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.dianwoba.dispatch.sender.mapper")
public class AppContext {

}

package com.dianwoba.dispatch.sender;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.dianwoba.wireless.fundamental.boot.WirelessSpringApplicationBuilder;
import com.dianwoba.wireless.tomcat.datasource.spring.boot.autoconfigure.TomcatDsMonitorAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo(multipleConfig = true)
public class Application {

	static {
//		System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new WirelessSpringApplicationBuilder(Application.class).profiles("default")
				.build(args).run(args);
	}
}

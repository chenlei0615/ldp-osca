package com.meicloud.paas.osca.console;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author liangkd
 */
@Configuration
@EnableFeignClients
@ComponentScan(basePackages = {"com.meicloud.paas.osca.console.client", "com.meicloud.paas.osca.console.config"})
public class OscaAutoConfiguration {

}

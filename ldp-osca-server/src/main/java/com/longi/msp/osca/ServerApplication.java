package com.longi.msp.osca;

import com.longi.msp.osca.properties.OfficeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 上传下载服务
 *
 * @author liangkd
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableConfigurationProperties(OfficeProperties.class)
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}

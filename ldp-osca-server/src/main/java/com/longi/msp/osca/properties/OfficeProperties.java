package com.longi.msp.osca.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * office配置
 *
 * @author chenlei140
 * @date 2022/10/17 10:09
 **/
@ConfigurationProperties(prefix = "converter")
@Configuration
@Data
public class OfficeProperties {
    private boolean enabled;
    private String officeHome;
    private String portNumbers;

    // min
    private Long taskExecutionTimeout;
    //hour
    private Long taskQueueTimeout;
    // 最大任务线程
    private Integer maxTaskPerProcess;

    private String workDir;
}

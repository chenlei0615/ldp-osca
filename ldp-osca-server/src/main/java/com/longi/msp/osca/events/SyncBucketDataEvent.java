package com.longi.msp.osca.events;

import com.longi.msp.osca.model.entity.ServerConfigEntity;
import com.meicloud.paas.core.client.ObjectClient;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author chenlei140
 * @className SyncBucketDataEvent
 * @description 同步桶数据事件模型
 * @date 2022/7/8 18:17
 */
@Getter
public class SyncBucketDataEvent extends ApplicationEvent {

    private static final long serialVersionUID = 3039313222160544111L;

    private ServerConfigEntity ossConfig;
    private transient ObjectClient objectClient;

    public SyncBucketDataEvent(ObjectClient objectClient, ServerConfigEntity ossConfig) {
        super(objectClient);
        this.ossConfig = ossConfig;
        this.objectClient = objectClient;
    }

}

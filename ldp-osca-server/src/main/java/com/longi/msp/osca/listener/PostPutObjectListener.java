package com.longi.msp.osca.listener;

import com.longi.msp.osca.events.PutObjectEvent;
import com.longi.msp.osca.model.request.InsertRecordRequest;
import com.longi.msp.osca.service.ObjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 上报上传记录监听器
 *
 * @author chenlei140
 * @date 2022/10/28 10:55
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class PostPutObjectListener {

    private final ObjectService objectService;

    @EventListener
    public void onApplicationEvent(PutObjectEvent event) {
        InsertRecordRequest recordRequest = event.getRecordRequest();
        objectService.insertAndSelect(recordRequest);
    }

}

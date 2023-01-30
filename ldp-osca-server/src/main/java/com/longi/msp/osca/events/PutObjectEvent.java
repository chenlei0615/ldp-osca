package com.longi.msp.osca.events;

import com.longi.msp.osca.model.request.InsertRecordRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 上传事件
 *
 * @author chenlei140
 * @date 2022/10/28 10:57
 **/
@Getter
public class PutObjectEvent extends ApplicationEvent {
    private static final long serialVersionUID = 3039313222160544111L;

    private InsertRecordRequest recordRequest;

    /**
     * 上传
     *
     * @param recordRequest
     */
    public PutObjectEvent(InsertRecordRequest recordRequest) {
        super(recordRequest);
        this.recordRequest = recordRequest;
    }
}

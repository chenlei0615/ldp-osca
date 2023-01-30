package com.longi.msp.osca.service.impl;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.entity.ObjectRecordEntity;
import com.longi.msp.osca.events.PutObjectEvent;
import com.longi.msp.osca.mapper.RecordMapper;
import com.longi.msp.osca.service.RecordService;
import com.meicloud.paas.common.constants.Constants;
import com.longi.msp.osca.model.request.InsertRecordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 记录业务实现层
 *
 * @author chenlei140
 * @date 2022/10/26 15:22
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final RecordMapper recordMapper;
    private final ApplicationContext applicationContext;

    /**
     * 记录分页列表查询
     *
     * @param oid
     * @return
     */
    @Override
    public Page<ObjectRecordEntity> list(String oid) {
        return recordMapper.list(oid);
    }

    /**
     * 上报
     *
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insert(InsertRecordRequest request) {
        request.setSource(Constants.SOURCE_SDK);
        applicationContext.publishEvent(new PutObjectEvent(request));
    }


}

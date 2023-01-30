package com.longi.msp.osca.service;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.request.InsertRecordRequest;
import com.longi.msp.osca.model.entity.ObjectRecordEntity;

/**
 * 记录业务层
 *
 * @author chenlei140
 * @date 2022/10/26 15:22
 **/
public interface RecordService {
    /**
     * 记录分页列表查询
     *
     * @param oid
     * @return
     */
    Page<ObjectRecordEntity> list(String oid);

    /**
     * 上报
     *
     * @param request
     */
    void insert(InsertRecordRequest request);
}

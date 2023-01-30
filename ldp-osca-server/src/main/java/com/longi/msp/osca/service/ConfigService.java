package com.longi.msp.osca.service;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.request.InsertConfigRequest;
import com.longi.msp.osca.model.request.UpdateConfigRequest;
import com.longi.msp.osca.model.entity.ServerConfigEntity;

import java.util.List;

public interface ConfigService {
    Page<ServerConfigEntity> list(String type);

    int insert(InsertConfigRequest request);

    List<ServerConfigEntity> getAll(String type);

    ServerConfigEntity checkUserAlias(String userAlias, String type);

    ServerConfigEntity checkAccessKey(String accessKey, String type);

    int update(UpdateConfigRequest request);

    int delete(String id);

    ServerConfigEntity getOne(String id);

    /**
     * test config is available or not
     * @param ossConfig
     */
    void connect(ServerConfigEntity ossConfig);

    /**
     *授权
     * @param id server id
     * @param policy
     * @return
     */
    ServerConfigEntity grant(String id, String policy);
}

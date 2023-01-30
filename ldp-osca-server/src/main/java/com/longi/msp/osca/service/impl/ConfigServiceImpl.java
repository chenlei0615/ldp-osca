package com.longi.msp.osca.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.Page;
import com.longi.msp.common.security.DetailsHelper;
import com.longi.msp.common.security.LmspUser;
import com.longi.msp.osca.mapper.BucketMapper;
import com.longi.msp.osca.mapper.ServerConfigMapper;
import com.longi.msp.osca.model.entity.BaseEntity;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.entity.ServerConfigEntity;
import com.longi.msp.osca.model.request.InsertConfigRequest;
import com.longi.msp.osca.model.request.UpdateConfigRequest;
import com.longi.msp.osca.service.AbstractObjectClient;
import com.longi.msp.osca.service.ConfigService;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.utils.AssertUtils;
import com.meicloud.paas.common.utils.Sm4Util;
import com.meicloud.paas.core.client.ObjectClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务商配置业务层
 *
 * @author chenlei140
 * @date 2022/10/13 15:09
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends AbstractObjectClient implements ConfigService {

    private final ServerConfigMapper serverConfigMapper;
    private final BucketMapper bucketMapper;

    @Override
    public Page<ServerConfigEntity> list(String mode) {
        return serverConfigMapper.list(mode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insert(InsertConfigRequest request) {
        //校验别名是否存在
        if (!ObjectUtils.isEmpty(serverConfigMapper.checkUserAlias(request.getUserAlias(), request.getType()))) {
            throw new ErrorCodeException(ErrorCode.USER_ALIAS_EXISTS);
        }
        request.setAccessKey(Sm4Util.decrypt(request.getAccessKey()));
        request.setSecretKey(Sm4Util.decrypt(request.getSecretKey()));
        if (!ObjectUtils.isEmpty(serverConfigMapper.checkAccessKey(request.getAccessKey(), request.getType()))) {
            throw new ErrorCodeException(ErrorCode.ACCESS_KEY_EXISTS);
        }
        ServerConfigEntity ossConfig = new ServerConfigEntity();
        BeanUtil.copyProperties(request, ossConfig);
        this.connect(ossConfig);
        //记录创建账号的用户信息
        LmspUser lmspUser = DetailsHelper.getUserDetails();
        ossConfig.setPolicy(lmspUser.getUsername());
        BaseEntity.initUser(ossConfig, lmspUser);
        return serverConfigMapper.insert(ossConfig);
    }

    @Override
    public List<ServerConfigEntity> getAll(String type) {
        List<ServerConfigEntity> result = serverConfigMapper.getAll(type);
        return CollUtil.isEmpty(result) ? new ArrayList<>() : result;
    }

    @Override
    public ServerConfigEntity checkUserAlias(String userAlias, String mode) {
        return serverConfigMapper.checkUserAlias(userAlias, mode);
    }

    @Override
    public ServerConfigEntity checkAccessKey(String accessKey, String mode) {
        return serverConfigMapper.checkAccessKey(accessKey, mode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateConfigRequest request) {
        //使用id查询数据是否存在
        ServerConfigEntity ossConfig = serverConfigMapper.getOne(request.getId());
        if (ObjectUtils.isEmpty(ossConfig)) {
            throw new ErrorCodeException(ErrorCode.CONFIG_NOT_FOUND);
        }
        //校验别名是否修改，如果修改则查询数据库校验是否已存在
        String userAlias = request.getUserAlias();
        if (!userAlias.equals(ossConfig.getUserAlias()) && !ObjectUtils.isEmpty(serverConfigMapper.checkUserAlias(userAlias, ossConfig.getType()))) {
            throw new ErrorCodeException(ErrorCode.USER_ALIAS_EXISTS);
        }
        if (CharSequenceUtil.isNotBlank(request.getAccessKey()) && CharSequenceUtil.isNotBlank(request.getSecretKey())) {
            String accessKey = Sm4Util.decrypt(request.getAccessKey());
            String secretKey = Sm4Util.decrypt(request.getSecretKey());
            if (!accessKey.equals(ossConfig.getAccessKey()) && !ObjectUtils.isEmpty(serverConfigMapper.checkAccessKey(accessKey, ossConfig.getType()))) {
                throw new ErrorCodeException(ErrorCode.ACCESS_KEY_EXISTS);
            }
            request.setAccessKey(accessKey);
            request.setSecretKey(secretKey);
            BeanUtil.copyProperties(request, ossConfig);
            this.connect(ossConfig);
            LmspUser lmspUser = DetailsHelper.getUserDetails();
            BaseEntity.updateUser(ossConfig, lmspUser);
            return serverConfigMapper.update(ossConfig);
        }
        throw new ErrorCodeException(ErrorCode.CONFIG_UPDATE_ERROR);
    }

    @Override
    public int delete(String id) {
        List<BucketEntity> nodes = bucketMapper.findByType(id);
        if (CollUtil.isNotEmpty(nodes)) {
            bucketMapper.batchDeleteByCid(id);
        }
        return serverConfigMapper.delete(id);
    }

    @Override
    public ServerConfigEntity getOne(String id) {
        return serverConfigMapper.getOne(id);
    }

    @Override
    public void connect(ServerConfigEntity ossConfig) {
        try {
            log.info("正在校验配置连接信息...");
            ObjectClient objectClient = getObjectClient(ossConfig);
            objectClient.opsForBucket().listBuckets();
            log.info("连接信息校验通过");
        } catch (Exception e) {
            log.error("校验配置连接信息失败：{}", e.getMessage());
            throw new ErrorCodeException(ErrorCode.OTHER_ERROR, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerConfigEntity grant(String id, String policy) {
        ServerConfigEntity serverConfigEntity = serverConfigMapper.getOne(id);
        AssertUtils.isTrue(ObjectUtil.isNotNull(serverConfigEntity), ErrorCode.CONFIG_NOT_FOUND);
        LmspUser lmspUser = DetailsHelper.getUserDetails();
        if (CharSequenceUtil.isNotBlank(policy)) {
            serverConfigEntity.setPolicy(policy.concat(StrPool.COMMA).concat(lmspUser.getUsername()));
        } else {
            serverConfigEntity.setPolicy(lmspUser.getUsername());
        }
        serverConfigMapper.update(serverConfigEntity);
        return serverConfigEntity;
    }
}

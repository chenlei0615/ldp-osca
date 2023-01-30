package com.longi.msp.osca.service;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.entity.ServerConfigEntity;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.dto.CidBidInfo;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.hepler.LocalConfigHelper;
import com.meicloud.paas.core.client.ObjectClient;
import com.meicloud.paas.core.client.ObjectClientBuilder;
import com.meicloud.paas.core.client.ObjectClientConfig;
import com.meicloud.paas.core.client.ObjectClientType;
import com.meicloud.paas.core.operations.interceptor.ObjectArgumentsInterceptor;
import com.meicloud.paas.core.operations.interceptor.PutObjectDecryptInterceptor;
import org.springframework.util.ObjectUtils;

/**
 * @author chenlei140
 * @date 2022/10/14 14:07
 **/
public abstract class AbstractObjectClient {

    protected ObjectClient getObjectClient(String bid) {
        BucketService bucketService = SpringUtil.getBean(BucketService.class);
        BucketEntity bucketEntity = bucketService.getOne(bid);
        if (ObjectUtils.isEmpty(bucketEntity)) {
            throw new ErrorCodeException(ErrorCode.BUCKET_NOT_FOUND);
        }
        CidBidInfo cidBidInfo = CidBidInfo.builder()
                .bid(bucketEntity.getId())
                .bucketName(bucketEntity.getBucketName())
                .encType(bucketEntity.getEncryptedType())
                .type(bucketEntity.getType()).build();
        LocalConfigHelper.set(cidBidInfo);
        return buildObjectClient(bucketEntity);
    }


    protected ObjectClient getObjectClient(String cid, String bid) {
        ConfigService configService = SpringUtil.getBean(ConfigService.class);
        ServerConfigEntity ossConfig = configService.getOne(cid);
        if (!ObjectUtils.isEmpty(ossConfig)) {
            BucketService bucketService = SpringUtil.getBean(BucketService.class);
            BucketEntity bucketEntity = bucketService.getOne(bid);
            if (ObjectUtils.isEmpty(bucketEntity)) {
                throw new ErrorCodeException(ErrorCode.BUCKET_NOT_FOUND);
            }
            CidBidInfo cidBidInfo = CidBidInfo.builder().cid(ossConfig.getId())
                    .bid(bucketEntity.getId())
                    .bucketName(bucketEntity.getBucketName())
                    .encType(bucketEntity.getEncryptedType())
                    .type(ossConfig.getType()).build();
            LocalConfigHelper.set(cidBidInfo);
            return buildObjectClient(ossConfig, bucketEntity);
        }
        throw new ErrorCodeException(ErrorCode.CONFIG_NOT_FOUND);
    }

    /**
     * 构建一个默认的objectClient
     * 从配置页面进入bucket列表 无法拿到准确的endpoint 需要构造一个默认的client来获取bucket列表
     * 同一个region下面有很多bucket 一个region对应一个endpoint
     *
     * @param ossConfig
     * @return
     */
    protected ObjectClient getObjectClient(ServerConfigEntity ossConfig) {
        if (!ObjectUtils.isEmpty(ossConfig)) {
            return buildObjectClient(ossConfig);
        }
        throw new ErrorCodeException(ErrorCode.BUCKET_NOT_FOUND);
    }

    private ObjectClient buildObjectClient(ServerConfigEntity ossConfig) {
        ObjectClientBuilder objectClientBuilder = new ObjectClientBuilder();
        ObjectClientConfig objectClientConfig = new ObjectClientConfig(
                CharSequenceUtil.isBlank(ossConfig.getEndPoint()) ? getDefaultEndPoint(ossConfig.getType().toLowerCase()) : ossConfig.getEndPoint(),
                ossConfig.getAccessKey(), ossConfig.getSecretKey(), "");
        ObjectClientType objectClientType = ObjectClientType.of(ossConfig.getType());
        return objectClientBuilder.build(objectClientType, objectClientConfig);
    }

    protected ObjectClient buildObjectClient(BucketEntity bucketEntity) {
        ObjectClientBuilder objectClientBuilder = new ObjectClientBuilder();
        ObjectClientConfig objectClientConfig = new ObjectClientConfig(bucketEntity.getEndPoint(),
                bucketEntity.getAccessKeyId(), bucketEntity.getAccessKeySecret(), bucketEntity.getRegion());
        ObjectClientType objectClientType = ObjectClientType.of(bucketEntity.getType());
        if (BucketEntity.SWITCH_OPEN.equals(bucketEntity.getWhiteSwitch())) {
            objectClientConfig.setWhiteList(bucketEntity.getWhiteList());
        }
        if (BucketEntity.SWITCH_OPEN.equals(bucketEntity.getGlobalSwitch())) {
            objectClientConfig.setLimitJson(bucketEntity.getLimitJson());
            objectClientConfig.setGlobalSizeLimit(bucketEntity.getGlobalSizeLimit());
            objectClientConfig.setGlobalSizeUnit(bucketEntity.getGlobalSizeUnit());
        }
        return objectClientBuilder
                .build(objectClientType, objectClientConfig)
                .addInterceptor(new ObjectArgumentsInterceptor())
                .addInterceptor(new PutObjectDecryptInterceptor());
    }

    public String getDefaultEndPoint(String type) {
        switch (type) {
            case "oss":
                return "oss-cn-hangzhou.aliyuncs.com";
            case "obs":
                return "obs.cn-north-4.myhuaweicloud.com";
            default:
                return "";
        }
    }

    private ObjectClient buildObjectClient(ServerConfigEntity ossConfig, BucketEntity bucketEntity) {
        ObjectClientBuilder objectClientBuilder = new ObjectClientBuilder();
        ObjectClientConfig objectClientConfig = new ObjectClientConfig(bucketEntity.getEndPoint(),
                ossConfig.getAccessKey(), ossConfig.getSecretKey(), bucketEntity.getRegion());
        ObjectClientType objectClientType = ObjectClientType.of(ossConfig.getType());
        if (BucketEntity.SWITCH_OPEN.equals(bucketEntity.getWhiteSwitch())) {
            objectClientConfig.setWhiteList(bucketEntity.getWhiteList());
        }
        if (BucketEntity.SWITCH_OPEN.equals(bucketEntity.getGlobalSwitch())) {
            objectClientConfig.setLimitJson(bucketEntity.getLimitJson());
            objectClientConfig.setGlobalSizeLimit(bucketEntity.getGlobalSizeLimit());
            objectClientConfig.setGlobalSizeUnit(bucketEntity.getGlobalSizeUnit());
        }

        return objectClientBuilder
                .build(objectClientType, objectClientConfig)
                .addInterceptor(new ObjectArgumentsInterceptor())
                .addInterceptor(new PutObjectDecryptInterceptor());
    }

}

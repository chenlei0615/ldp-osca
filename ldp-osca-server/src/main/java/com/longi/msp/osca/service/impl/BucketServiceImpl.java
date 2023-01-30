package com.longi.msp.osca.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.longi.msp.common.security.DetailsHelper;
import com.longi.msp.osca.events.SyncBucketDataEvent;
import com.longi.msp.osca.mapper.BucketMapper;
import com.longi.msp.osca.model.entity.BaseEntity;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.entity.ServerConfigEntity;
import com.longi.msp.osca.model.request.BucketConfigRequest;
import com.longi.msp.osca.model.request.BucketEditRequest;
import com.longi.msp.osca.model.request.BucketEnterRequest;
import com.longi.msp.osca.model.response.BucketVO;
import com.longi.msp.osca.service.AbstractObjectClient;
import com.longi.msp.osca.service.BucketService;
import com.longi.msp.osca.service.ConfigService;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.utils.DataValidatorUtil;
import com.meicloud.paas.core.client.ObjectClient;
import com.meicloud.paas.core.model.bucket.CreateBucketRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BucketServiceImpl extends AbstractObjectClient implements BucketService {
    public final BucketMapper bucketMapper;
    public final ConfigService configService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private ObjectClient objectClient;

    @Value("${lmsp.osca.max-enc-file-size}")
    private Integer limitSize;
    @Value("${lmsp.osca.max-file-size}")
    private Integer maxSize;

    /**
     * list bucket
     *
     * @param type
     * @param bucketName
     * @return
     */
    @Override
    public Page<BucketVO> listBucket(String type, String bucketName) {
        return bucketMapper.listBucket(type,bucketName);
    }

    /**
     * create bucket
     *
     * @param cid        server config id
     * @param bucketName
     * @param desc
     * @param acl
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(String cid, String bucketName, String desc, String acl) {
        ServerConfigEntity ossConfig = configService.getOne(cid);
        objectClient = getObjectClient(ossConfig);
        CreateBucketRequest request = new CreateBucketRequest();
        request.setBucketName(bucketName);
        request.setAcl(acl);
        objectClient.opsForBucket().createBucket(request);
        BucketEntity fromDB = bucketMapper.findByNameAndType(ossConfig.getType(), bucketName);
        if (ObjectUtil.isNotNull(fromDB)) {
            throw new ErrorCodeException(ErrorCode.BUCKET_ALREADY_EXISTS);
        }
        fromDB = new BucketEntity();
        fromDB.setBucketName(bucketName);
        fromDB.setType(ossConfig.getType());
        fromDB.setEndPoint(ossConfig.getEndPoint());
        fromDB.setCid(cid);
        fromDB.setAcl(acl);
        fromDB.setDescription(desc);
        BaseEntity.initUser(fromDB, DetailsHelper.getUserDetails());
        bucketMapper.insert(fromDB);
    }

    /**
     * update bucket config
     *
     * @param request
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBucket(BucketConfigRequest request) {
        checkSize(request);
        BucketEntity bucketEntity = bucketMapper.getOne(request.getId());
        if (ObjectUtils.isEmpty(bucketEntity)) {
            throw new ErrorCodeException(ErrorCode.BUCKET_NOT_FOUND);
        }
        bucketEntity.setEncryptedType(request.getEncryptedType());
        bucketEntity.setGlobalSizeLimit(request.getGlobalSizeLimit());
        bucketEntity.setGlobalSizeUnit(request.getGlobalSizeUnit());
        if (ObjectUtil.isNull(request.getGlobalSizeLimit())) {
            bucketEntity.setGlobalSizeUnit(null);
        }
        bucketEntity.setLimitJson(JSONUtil.toJsonStr(request.getLimitJson()));
        bucketEntity.setWhiteList(request.getWhiteList());
        if (CharSequenceUtil.isNotBlank(request.getAcl())) {
            // 先更新存存储的权限
            objectClient = getObjectClient(bucketEntity.getId());
            objectClient.opsForBucket().setBucketPolicy(bucketEntity.getBucketName(), request.getAcl());
            bucketEntity.setAcl(request.getAcl());
        }
        bucketEntity.setGlobalSwitch(request.getGlobalSwitch());
        bucketEntity.setWhiteSwitch(request.getWhiteSwitch());
        bucketMapper.update(bucketEntity);
    }


    private void checkSize(BucketConfigRequest request) {
        if (CollUtil.isEmpty(request.getLimitJson())) {
            return;
        }
        if (!BucketEntity.SWITCH_OPEN.equals(request.getGlobalSwitch())) {
            request.setGlobalSizeLimit(null);
            request.setGlobalSizeUnit(CharSequenceUtil.EMPTY);
            request.setLimitJson(new ArrayList<>());
            return;
        }
        //判断用户是否设置了全局大小
        BigDecimal globalSizeLimit = request.getGlobalSizeLimit();
        String globalSizeUnit = request.getGlobalSizeUnit();
        if (globalSizeLimit == null || globalSizeUnit == null) {
            return;
        }
        BigDecimal targetGlobalSize = DataValidatorUtil.getSize(globalSizeLimit, globalSizeUnit);
        if (CharSequenceUtil.isBlank(request.getEncryptedType()) &&
                targetGlobalSize.compareTo(new BigDecimal(maxSize * 1024 * 1024)) > 0) {
            throw new ErrorCodeException(ErrorCode.PARAM_EXCEPTION, "全局配置文件大小超过 【" + maxSize + "M】 上传限制");
        }
        if (CharSequenceUtil.isNotBlank(request.getEncryptedType()) &&
                targetGlobalSize.compareTo(new BigDecimal(limitSize * 1024 * 1024)) > 0) {
            throw new ErrorCodeException(ErrorCode.PARAM_EXCEPTION, "全局配置文件大小超过 【" + limitSize + "M】 加密限制");
        }
        for (Map<String, Object> map : request.getLimitJson()) {
            // 全局必须大于局部
            String type = String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_TYPE));
            if (Boolean.TRUE.equals(DataValidatorUtil.compare(targetGlobalSize,
                    new BigDecimal(String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_SIZE))),
                    String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_UNIT))))) {
                throw new ErrorCodeException(ErrorCode.PARAM_EXCEPTION, "文件类型为【" + type + "】的大小不能大于全局设置的值");
            }
            if (CharSequenceUtil.isBlank(request.getEncryptedType()) &&
                    Boolean.TRUE.equals(DataValidatorUtil.compare(new BigDecimal(maxSize * 1024 * 1024),
                            new BigDecimal(String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_SIZE))),
                            String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_UNIT))))) {
                throw new ErrorCodeException(ErrorCode.PARAM_EXCEPTION, "文件类型为【" + type + "】的大小超过 【" + maxSize + "M】 上传限制");
            }
            if (CharSequenceUtil.isNotBlank(request.getEncryptedType()) &&
                    Boolean.TRUE.equals(DataValidatorUtil.compare(new BigDecimal(limitSize * 1024 * 1024),
                            new BigDecimal(String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_SIZE))),
                            String.valueOf(map.get(DataValidatorUtil.LIMIT_FILED_UNIT))))) {
                throw new ErrorCodeException(ErrorCode.PARAM_EXCEPTION, "文件类型为【" + type + "】的大小超过 【" + limitSize + "M】 加密限制");
            }
        }
    }

    /**
     * detail of the bucket
     *
     * @param id
     * @return
     */
    @Override
    public BucketEntity getOne(String id) {
        BucketEntity bucketEntity = bucketMapper.getOne(id);
        if (ObjectUtils.isEmpty(bucketEntity)) {
            throw new ErrorCodeException(ErrorCode.BUCKET_NOT_FOUND);
        }
        return bucketEntity;
    }


    @Override
    public List<BucketEntity> findAllByType(String type) {
        return bucketMapper.findByType(type);
    }

    /**
     * sync bucket from server
     */
    @Override
    public void sync() {
        List<ServerConfigEntity> list = configService.list(CharSequenceUtil.EMPTY);
        if (CollUtil.isEmpty(list)) {
            return;
        }
        for (ServerConfigEntity serverConfigEntity : list) {
            objectClient = getObjectClient(serverConfigEntity);
            applicationEventPublisher.publishEvent(new SyncBucketDataEvent(objectClient, serverConfigEntity));
        }
    }

    @Override
    public BucketEntity enter(BucketEnterRequest bucketEnterRequest) {
        BucketEntity bucketEntity = bucketMapper.findByNameAndType(bucketEnterRequest.getType(), bucketEnterRequest.getBucketName());
        if (ObjectUtil.isNotNull(bucketEntity)) {
            throw new ErrorCodeException(ErrorCode.BUCKET_ALREADY_EXISTS);
        }
        bucketEntity = new BucketEntity();
        BeanUtil.copyProperties(bucketEnterRequest, bucketEntity);
        if (CharSequenceUtil.isNotBlank(bucketEnterRequest.getEndPoint()) && bucketEnterRequest.getEndPoint().endsWith(StrPool.SLASH)) {
            String endpoint = bucketEnterRequest.getEndPoint();
            bucketEntity.setEndPoint(endpoint.substring(0, endpoint.length() - 1));
        }
        BaseEntity.initUser(bucketEntity, DetailsHelper.getUserDetails());
        checkConfigValidity(bucketEntity);
        bucketMapper.insert(bucketEntity);
        return bucketEntity;
    }

    private void checkConfigValidity(BucketEntity bucketEntity) {
        objectClient = buildObjectClient(bucketEntity);
        if (Boolean.FALSE.equals(objectClient.opsForBucket().doesBucketExist(bucketEntity.getBucketName()))) {
            throw new ErrorCodeException(ErrorCode.OTHER_ERROR, "远程存储桶空间不存在");
        }
    }

    @Override
    public Integer delete(String id) {
        return bucketMapper.delete(id);
    }

    @Override
    public BucketEntity editBucket(BucketEditRequest request) {
        BucketEntity bucketEntity = getOne(request.getId());
        String endpoint = request.getEndPoint();
        if (CharSequenceUtil.isNotBlank(endpoint) && endpoint.endsWith(StrPool.SLASH)) {
            endpoint = endpoint.substring(0, endpoint.length() - 1);
        }
        bucketEntity.setEndPoint(endpoint);
        bucketEntity.setAccessKeyId(request.getAccessKeyId());
        bucketEntity.setAccessKeySecret(request.getAccessKeySecret());
        bucketEntity.setDescription(request.getDescription());
        checkConfigValidity(bucketEntity);
        bucketMapper.update(bucketEntity);
        return bucketEntity;
    }
}

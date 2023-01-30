package com.longi.msp.osca.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.Page;
import com.longi.msp.common.security.DetailsHelper;
import com.longi.msp.common.security.LmspUser;
import com.longi.msp.osca.events.PutObjectEvent;
import com.longi.msp.osca.mapper.ObjectEntityMapper;
import com.longi.msp.osca.mapper.RecordMapper;
import com.longi.msp.osca.model.dto.PutObjectVo;
import com.longi.msp.osca.model.dto.UploaderInfoDTO;
import com.longi.msp.osca.model.entity.BaseEntity;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.entity.ObjectEntity;
import com.longi.msp.osca.model.entity.ObjectRecordEntity;
import com.longi.msp.osca.model.request.*;
import com.longi.msp.osca.service.AbstractObjectClient;
import com.longi.msp.osca.service.BucketService;
import com.longi.msp.osca.service.ObjectService;
import com.meicloud.paas.common.constants.Constants;
import com.meicloud.paas.common.constants.ErrorCode;
import com.meicloud.paas.common.error.ErrorCodeException;
import com.meicloud.paas.common.hepler.LocalConfigHelper;
import com.meicloud.paas.common.utils.FilePathUtils;
import com.meicloud.paas.common.utils.InputStreamUtil;
import com.meicloud.paas.core.client.ObjectClient;
import com.meicloud.paas.core.model.ObjectMetadata;
import com.meicloud.paas.core.model.objects.DeleteObjectsRequest;
import com.meicloud.paas.core.model.objects.GetObjectResult;
import com.meicloud.paas.core.model.objects.PutObjectRequest;
import com.meicloud.paas.core.model.objects.PutObjectResult;
import com.meicloud.paas.core.watermark.WatermarkOperations;
import com.meicloud.paas.core.watermark.WatermarkType;
import com.meicloud.paas.osca.console.model.response.ErrorObjectDTO;
import com.meicloud.paas.osca.console.model.response.PutObjectDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObjectServiceImpl extends AbstractObjectClient implements ObjectService {

    private final ApplicationContext applicationContext;
    private ObjectClient objectClient;
    @Value("${lmsp.osca.max-enc-file-size}")
    private Integer limitSize;
    @Value("${lmsp.osca.max-file-size}")
    private Integer maxSize;
    private final ObjectEntityMapper objectEntityMapper;
    private final BucketService bucketService;

    private final RecordMapper recordMapper;

    /**
     * list object data
     *
     * @param request
     * @return
     */
    @Override
    public Page<ObjectEntity> listObjects(ObjectListRequest request) {
        return objectEntityMapper.list(request);
    }

    /**
     * upload file
     *
     * @param uploadObjectRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PutObjectDTO putObject(UploadObjectRequest uploadObjectRequest) {
        if (!checkPath(new MultipartFile[]{uploadObjectRequest.getFile()}, uploadObjectRequest.getCustomPath())) {
            throw new ErrorCodeException(ErrorCode.PARAMETER_ERROR, "文件名或路径不合法");
        }
        StopWatch stopWatch = new StopWatch();
        BucketEntity bucket = bucketService.getOne(uploadObjectRequest.getBid());
        if (CharSequenceUtil.isNotBlank(bucket.getEncryptedType()) && uploadObjectRequest.getFile().getSize() > limitSize * 1024 * 1024) {
            throw new ErrorCodeException(ErrorCode.PUT_OBJECT_ERROR, "文件大小超过加密限制" + limitSize + "M");
        }
        objectClient = getObjectClient(bucket.getId());
        String bucketName = bucket.getBucketName();
        String folderName = uploadObjectRequest.getCustomPath();
        PutObjectResult putObjectResult;
        // 直接创建文件夹
        if (uploadObjectRequest.getFile() == null && CharSequenceUtil.isNotBlank(folderName)) {
            if (!CharSequenceUtil.endWithIgnoreCase(folderName, StrPool.SLASH)) {
                folderName += StrPool.SLASH;
            }
            putObjectResult = objectClient.opsForObject().mkdir(bucketName, folderName);
            return BeanUtil.copyProperties(putObjectResult, PutObjectDTO.class);
        }
        PutObjectRequest putObjectRequest = buildPutObjectRequest(uploadObjectRequest, uploadObjectRequest.getFile(), bucketName);
        stopWatch.start("上传开始");
        putObjectResult = objectClient.opsForObject().putObject(putObjectRequest);
        stopWatch.stop();
        log.info("上传文件 耗时: {} s", stopWatch.getTotalTimeSeconds());
        stopWatch.start("数据更新");
        ObjectEntity objectEntity = insertAndSelect(buildRecordRequestByUpload(uploadObjectRequest.getCustomPath(),
                uploadObjectRequest.getFile(), bucket, true, CharSequenceUtil.EMPTY));
        PutObjectDTO putObjectDto = new PutObjectDTO();
        BeanUtil.copyProperties(putObjectResult, putObjectDto);
        putObjectDto.setId(objectEntity.getId());
        putObjectDto.setObjectSize(objectEntity.getObjectSize());
        putObjectDto.setFileName(objectEntity.getObjectName());
        stopWatch.stop();
        log.info("上传任务结束 耗时: {} s", stopWatch.getTotalTimeSeconds());
        return putObjectDto;
    }

    /**
     * batch upload file
     *
     * @param batchUploadRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PutObjectVo putObjects(BatchUploadRequest batchUploadRequest) {
        if (!checkPath(batchUploadRequest.getFiles(), batchUploadRequest.getCustomPath())) {
            throw new ErrorCodeException(ErrorCode.PARAM_EXCEPTION, "文件名或路径不合法");
        }
        long totalSize = getTotalSize(batchUploadRequest.getFiles());
        BucketEntity bucket = bucketService.getOne(batchUploadRequest.getBid());
        if (batchUploadRequest.getFiles() != null &&
                totalSize > maxSize * 1024 * 1024) {
            throw new ErrorCodeException(ErrorCode.PUT_OBJECT_ERROR, "文件大小超过 【" + maxSize + "M】 限制");
        }
        if (CharSequenceUtil.isNotBlank(bucket.getEncryptedType()) &&
                totalSize > limitSize * 1024 * 1024) {
            throw new ErrorCodeException(ErrorCode.PUT_OBJECT_ERROR, "文件大小超过 【" + limitSize + "M】 加密限制");
        }
        objectClient = getObjectClient(bucket.getId());
        String bucketName = bucket.getBucketName();
        String folderName = batchUploadRequest.getCustomPath();
        List<PutObjectDTO> result = new ArrayList<>(12);
        List<ErrorObjectDTO> errors = new ArrayList<>(12);
        // 直接创建文件夹
        if (batchUploadRequest.getFiles() == null && CharSequenceUtil.isNotBlank(folderName)) {
            if (!CharSequenceUtil.endWithIgnoreCase(folderName, StrPool.SLASH)) {
                folderName += StrPool.SLASH;
            }
            PutObjectResult putObjectResult = objectClient.opsForObject().mkdir(bucketName, folderName);
            result.add(BeanUtil.copyProperties(putObjectResult, PutObjectDTO.class));
            return PutObjectVo.builder().objects(result).build();
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("上传开始");
        for (MultipartFile file : batchUploadRequest.getFiles()) {
            PutObjectResult putObjectResult = null;
            String desc = CharSequenceUtil.EMPTY;
            try {
                PutObjectRequest putObjectRequest = buildPutObjectRequest(batchUploadRequest, file, bucketName);
                putObjectResult = objectClient.opsForObject().putObject(putObjectRequest);
                log.info(" \n 上传成功，结果：{}", putObjectResult);
            } catch (Exception e) {
                log.info(" \n 当前文件：{} 上传失败 ，\n 异常信息：{} ", file.getOriginalFilename(), e.getMessage());
                ErrorObjectDTO errorObjectDTO = ErrorObjectDTO.builder().description(e.getMessage()).fileName(file.getOriginalFilename()).build();
                errors.add(errorObjectDTO);
                desc = e.getMessage();
            }
            ObjectEntity objectEntity = insertAndSelect(buildRecordRequestByUpload(batchUploadRequest.getCustomPath(), file, bucket, putObjectResult != null, desc));
            if (ObjectUtil.isNotNull(putObjectResult)) {
                PutObjectDTO putObjectDto = new PutObjectDTO();
                BeanUtil.copyProperties(putObjectResult, putObjectDto);
                putObjectDto.setId(objectEntity.getId());
                putObjectDto.setObjectSize(objectEntity.getObjectSize());
                putObjectDto.setFileName(objectEntity.getObjectName());
                result.add(putObjectDto);
            }
        }
        stopWatch.stop();
        log.info("上传文件 总耗时: {} s", stopWatch.getTotalTimeSeconds());
        return PutObjectVo.builder().objects(result).errors(errors).build();
    }

    private long getTotalSize(MultipartFile[] files) {
        long size = 0;
        for (MultipartFile file : files) {
            if (file != null) {
                if (file.isEmpty()) {
                    throw new ErrorCodeException(ErrorCode.PUT_OBJECT_ERROR, "上传文件内容为空");
                }
                size += file.getSize();
            }
        }
        log.info("文件总大小为：{} B", size);
        return size;
    }

    @Override
    public ObjectEntity insertAndSelect(InsertRecordRequest recordRequest) {
        LmspUser lmspUser = DetailsHelper.getUserDetails();
        ObjectEntity objectEntity = objectEntityMapper.findOne(recordRequest.getBucketName(), recordRequest.getObjectName(), recordRequest.getObjectPath());
        // 第一次上传 直插object_info
        if (ObjectUtil.isNull(objectEntity)) {
            objectEntity = objectEntityBuild(recordRequest);
            objectEntityMapper.insert(objectEntity);
            return objectEntity;
        }
        // 检测第二次上传 插入object_record
        ObjectRecordEntity objectRecordEntity = buildRecord();
        objectRecordEntity.setObjectId(objectEntity.getId());
        objectRecordEntity.setObjectName(objectEntity.getObjectName());
        objectRecordEntity.setObjectSize(objectEntity.getObjectSize());
        objectRecordEntity.setCreationDate(objectEntity.getLastUpdateDate());
        BaseEntity.initUser(objectRecordEntity, lmspUser);
        recordMapper.insert(objectRecordEntity);
        // 大小不一样 需要更新大小
        if (objectEntity.getObjectSize() != recordRequest.getObjectSize()) {
            objectEntity.setObjectSize(recordRequest.getObjectSize());
            objectEntity.setLastUpdateDate(new Date());
            BaseEntity.updateUser(objectEntity, lmspUser);
            objectEntityMapper.update(objectEntity);
        }
        return objectEntity;
    }

    public ObjectRecordEntity buildRecord() {
        ObjectRecordEntity objectRecordEntity = new ObjectRecordEntity();
        LmspUser lmspUser = DetailsHelper.getUserDetails();
        BaseEntity.initUser(objectRecordEntity, lmspUser);
        return objectRecordEntity;
    }

    private ObjectEntity objectEntityBuild(InsertRecordRequest recordRequest) {
        ObjectEntity objectEntity = new ObjectEntity();
        BeanUtil.copyProperties(recordRequest, objectEntity);
        LmspUser lmspUser = DetailsHelper.getUserDetails();
        BaseEntity.initUser(objectEntity, lmspUser);
        return objectEntity;
    }

    @NotNull
    private static InsertRecordRequest buildRecordRequestByUpload(String customPath, MultipartFile file, BucketEntity bucket, boolean isSuccess, String desc) {
        InsertRecordRequest recordRequest = new InsertRecordRequest();
        recordRequest.setBid(bucket.getId());
        recordRequest.setBucketName(bucket.getBucketName());
        recordRequest.setObjectName(file.getOriginalFilename());
        recordRequest.setDescription(desc);
        String objectPath = customPath;
        if (CharSequenceUtil.isNotBlank(customPath) && CharSequenceUtil.endWithIgnoreCase(customPath, StrPool.SLASH)) {
            objectPath = objectPath.substring(0, objectPath.length() - 1);
            if (CharSequenceUtil.startWithIgnoreCase(customPath, StrPool.SLASH)) {
                objectPath = objectPath.substring(1);
            }
        }
        recordRequest.setObjectPath(objectPath);
        recordRequest.setObjectSize(file.getSize());
        recordRequest.setType(LocalConfigHelper.getType());
        recordRequest.setSource(Constants.SOURCE_SERVER);
        if (Boolean.FALSE.equals(isSuccess)) {
            recordRequest.setObjectStatus(ObjectEntity.STATUS_FAILED);
        }
        return recordRequest;
    }

    @NotNull
    private static PutObjectRequest buildPutObjectRequest(BaseUploadRequest uploadRequest, MultipartFile file, String bucketName) {
        String uploadFileName = FilePathUtils.buildUploadFilePath(uploadRequest.getCustomPath(),
                file.getOriginalFilename(), true, false);
        PutObjectRequest putObjectRequest = new PutObjectRequest();
        putObjectRequest.setBucketName(bucketName);
        putObjectRequest.setObjectKey(uploadFileName);
        putObjectRequest.setPath(uploadRequest.getCustomPath());
        putObjectRequest.setSize(file.getSize());
        if (CharSequenceUtil.isNotBlank(uploadRequest.getMetaMap())) {
            ObjectMetadata objectMetadata = putObjectRequest.getObjectMetadata();
            objectMetadata.setUserMetadata(JSONUtil.toBean(uploadRequest.getMetaMap(), Map.class));
            putObjectRequest.setObjectMetadata(objectMetadata);
        }
        try {
            putObjectRequest.setInputStream(file.getInputStream());
        } catch (IOException e) {
            throw new ErrorCodeException(ErrorCode.PUT_OBJECT_ERROR, e.getMessage());
        }
        return putObjectRequest;
    }

    /**
     * download file with watermark
     *
     * @param outputStream
     * @param objectEntity
     * @param watermarkContent
     * @param watermarkPicUrl
     * @return
     */
    @Override
    public InputStream getObject(OutputStream outputStream, ObjectEntity objectEntity, String
            watermarkContent, String watermarkPicUrl) {
        String objectKey = objectEntity.getObjectKey();
        InputStream imgIo = null;
        InputStream inputStream;
        try {
            imgIo = CharSequenceUtil.isBlank(watermarkPicUrl) ? null : InputStreamUtil.getInputStreamByUrl(watermarkPicUrl);
            objectClient = getObjectClient(objectEntity.getBid());
            String bucketName = LocalConfigHelper.getBucket();
            StopWatch stopWatch = new StopWatch();
            stopWatch.start("下载任务");
            GetObjectResult result = objectClient.opsForObject().getObject(bucketName, objectKey);
            log.info("result :{} {}", result, result.getObjectContent() == null);
            inputStream = result.getObjectContent();
            stopWatch.stop();
            log.info("获取数据流耗时 :{} ", stopWatch.getTotalTimeSeconds());
            if (CharSequenceUtil.isNotBlank(watermarkPicUrl) || CharSequenceUtil.isNotBlank(watermarkContent)) {
                WatermarkOperations watermarkOperations = WatermarkType.of(FileNameUtil.getSuffix(objectKey)).instance();
                inputStream = watermarkOperations.addWatermark(FileNameUtil.getSuffix(objectKey), inputStream, outputStream, imgIo, watermarkContent);
            }
        } catch (Exception e) {
            throw new ErrorCodeException(ErrorCode.GET_OBJECT_ERROR, e.getMessage());
        } finally {
            IoUtil.close(imgIo);
        }
        return inputStream;
    }

    /**
     * delete object
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        ObjectEntity objectEntity = getOne(id);
        objectClient = getObjectClient(objectEntity.getBid());
        objectClient.opsForObject().deleteObject(objectEntity.getBucketName(), objectEntity.getObjectKey());
        objectEntityMapper.delete(objectEntity);
    }

    /**
     * batch delete object
     *
     * @param bid
     * @param objectNames
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteObjects(String bid, List<String> objectNames) {
        objectClient = getObjectClient(bid);
        DeleteObjectsRequest request = new DeleteObjectsRequest();
        request.setObjectKeys(objectNames);
        request.setBucketName(LocalConfigHelper.getBucket());
        objectClient.opsForObject().deleteObject(request);
    }

    /**
     * copy object
     *
     * @param copyRequest
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copy(CopyRequest copyRequest) {
        if (!checkPath(null, copyRequest.getTargetFolder())) {
            throw new ErrorCodeException(ErrorCode.PARAMETER_ERROR, "路径填写不合法");
        }
        ObjectEntity objectEntity = getOne(copyRequest.getOid());
        ObjectEntity targetObjectEntity = objectEntityMapper.findOne(objectEntity.getBucketName(), objectEntity.getObjectName(), copyRequest.getTargetFolder());
        if (ObjectUtil.isNotNull(targetObjectEntity)) {
            throw new ErrorCodeException(ErrorCode.OBJECT_ALREADY_EXIST);
        }
        objectClient = getObjectClient(objectEntity.getBid());
        String bucketName = objectEntity.getBucketName();
        String targetName = FilePathUtils.buildUploadFilePath(copyRequest.getTargetFolder(), objectEntity.getObjectName(), true, false);
        String sourceObjectKey = objectEntity.getObjectKey();
        objectClient.opsForObject().copyObject(bucketName, sourceObjectKey, bucketName, targetName);
        applicationContext.publishEvent(new PutObjectEvent(buildRecordRequestByCopy(copyRequest, objectEntity)));

    }

    private InsertRecordRequest buildRecordRequestByCopy(CopyRequest copyRequest, ObjectEntity objectEntity) {
        InsertRecordRequest recordRequest = new InsertRecordRequest();
        recordRequest.setBid(objectEntity.getBid());
        recordRequest.setBucketName(objectEntity.getBucketName());
        recordRequest.setObjectName(objectEntity.getObjectName());
        String objectPath = copyRequest.getTargetFolder() == null ? CharSequenceUtil.EMPTY : copyRequest.getTargetFolder();
        recordRequest.setObjectPath(objectPath);
        recordRequest.setObjectSize(objectEntity.getObjectSize());
        recordRequest.setType(objectEntity.getType());
        recordRequest.setSource(Constants.SOURCE_SERVER);
        return recordRequest;
    }

    /**
     * batch copy objects
     *
     * @param bid
     * @param objectNames
     * @param targetFolder
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyObjects(String bid, List<String> objectNames, String targetFolder) {
        if (CollUtil.isEmpty(objectNames)) {
            return;
        }
        objectClient = getObjectClient(bid);
        String bucketName = LocalConfigHelper.getBucket();
        for (String objectKey : objectNames) {
            String targetName = targetFolder.concat(FileNameUtil.getName(objectKey));
            objectClient.opsForObject().copyObject(bucketName, objectKey, bucketName, targetName);
        }
    }

    /**
     * rename object
     *
     * @param id
     * @param targetObjectName
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rename(String id, String targetObjectName) {
        ObjectEntity objectEntity = this.getOne(id);
        if (CharSequenceUtil.isEmpty(targetObjectName) || objectEntity.getObjectName().equals(targetObjectName)) {
            return;
        }
        objectClient = getObjectClient(objectEntity.getBid());
        String bucketName = LocalConfigHelper.getBucket();
        objectClient.opsForObject().rename(bucketName, objectEntity.getObjectKey(), ObjectEntity.assembleObjectKey(targetObjectName, objectEntity.getObjectPath()));
        objectEntity.setObjectName(targetObjectName);
        objectEntityMapper.update(objectEntity);

    }

    /**
     * object detail info
     *
     * @param id
     * @return
     */
    @Override
    public ObjectEntity getOne(String id) {
        ObjectEntity objectEntity = objectEntityMapper.getOne(id);
        if (ObjectUtil.isNull(objectEntity)) {
            throw new ErrorCodeException(ErrorCode.OBJECT_NOT_FOUND_ERROR);
        }
        objectClient = getObjectClient(objectEntity.getBid());
        if (Boolean.FALSE.equals(objectClient.opsForObject().doesObjectExist(objectEntity.getBucketName(), objectEntity.getObjectKey()))) {
            objectEntityMapper.delete(objectEntity);
            throw new ErrorCodeException(ErrorCode.OBJECT_NOT_FOUND_ERROR);
        }
        return objectEntity;
    }

    /**
     * object config
     *
     * @param bid
     * @param objectName
     * @param storageClass
     * @param metaMap
     */
    @Override
    public void config(String bid, String objectName, String storageClass, String metaMap) {
        objectClient = getObjectClient(bid);
        String bucketName = LocalConfigHelper.getBucket();
        objectClient.opsForObject().update(bucketName, objectName, storageClass, metaMap);
    }


    @Override
    public InputStream getSourceInputStream(String bid, String objectKey) {
        objectClient = getObjectClient(bid);
        String bucketName = LocalConfigHelper.getBucket();
        GetObjectResult result = objectClient.opsForObject().getObject(bucketName, objectKey);
        return result.getObjectContent();
    }

    @Override
    public List<UploaderInfoDTO> getUploader() {
        return objectEntityMapper.findAllCreatedBy();
    }

    private boolean checkPath(MultipartFile[] files, String path) {
        if (CharSequenceUtil.isNotBlank(path) && (path.startsWith(StrPool.SLASH) || path.endsWith(StrPool.SLASH))) {
            return false;
        }
        if (ArrayUtil.isNotEmpty(files)) {
            for (MultipartFile file : files) {
                if (CharSequenceUtil.isNotBlank(file.getOriginalFilename()) && CharSequenceUtil.contains(file.getOriginalFilename(), StrPool.SLASH)) {
                    return false;
                }
            }
        }
        return true;
    }

}

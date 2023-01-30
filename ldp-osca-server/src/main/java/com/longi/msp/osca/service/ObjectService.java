package com.longi.msp.osca.service;


import com.github.pagehelper.Page;
import com.longi.msp.osca.model.dto.PutObjectVo;
import com.longi.msp.osca.model.dto.UploaderInfoDTO;
import com.longi.msp.osca.model.entity.ObjectEntity;
import com.longi.msp.osca.model.request.*;
import com.meicloud.paas.osca.console.model.response.PutObjectDTO;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface ObjectService {
    /**
     * list object data
     *
     * @param model
     * @return
     */
    Page<ObjectEntity> listObjects(ObjectListRequest model);

    /**
     * upload file
     *
     * @param uploadObjectRequest
     * @return
     */
    PutObjectDTO putObject(UploadObjectRequest uploadObjectRequest);

    /**
     * batch upload file
     *
     * @param batchUploadRequest
     * @return
     */
    PutObjectVo putObjects(BatchUploadRequest batchUploadRequest);

    /**
     * download file
     *
     * @param outputStream
     * @param objectEntity
     * @param watermarkContent
     * @param watermarkPicUrl
     * @return
     */
    InputStream getObject(OutputStream outputStream, ObjectEntity objectEntity, String watermarkContent, String watermarkPicUrl);


    /**
     * delete object
     *
     * @param objectName
     */
    void delete(String objectName);

    /**
     * batch delete object
     *
     * @param bid
     * @param objectNames
     */
    void deleteObjects(String bid, List<String> objectNames);

    /**
     * copy object
     *
     * @param copyRequest
     */
    void copy(CopyRequest copyRequest);

    /**
     * batch copy objects
     *
     * @param bid
     * @param objectNames
     * @param targetFolder
     */
    void copyObjects(String bid, List<String> objectNames, String targetFolder);

    /**
     * rename object
     *
     * @param id
     * @param targetObjectName
     */
    void rename(String id, String targetObjectName);


    /**
     * object detail info
     *
     * @param id
     * @return
     */
    ObjectEntity getOne(String id);

    /**
     * object config
     *
     * @param bid
     * @param objectName
     * @param storageClass
     * @param metaMap
     */
    void config(String bid, String objectName, String storageClass, String metaMap);

    /**
     * get object inputstream
     *
     * @param bid
     * @param objectKey
     * @return
     */
    InputStream getSourceInputStream(String bid, String objectKey);

    /**
     * 获取上传人列表
     *
     * @return
     */

    List<UploaderInfoDTO> getUploader();

    ObjectEntity insertAndSelect(InsertRecordRequest recordRequest);

}

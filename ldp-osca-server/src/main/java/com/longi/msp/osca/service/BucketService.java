package com.longi.msp.osca.service;


import com.github.pagehelper.Page;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.request.BucketConfigRequest;
import com.longi.msp.osca.model.request.BucketEditRequest;
import com.longi.msp.osca.model.request.BucketEnterRequest;
import com.longi.msp.osca.model.response.BucketVO;

import java.util.List;

public interface BucketService {
    /**
     * list bucket
     *
     * @param type       server type
     * @param bucketName
     * @return
     */
    Page<BucketVO> listBucket(String type, String bucketName);

    /**
     * create bucket
     *
     * @param cid        server config id
     * @param bucketName
     * @param desc
     * @param acl
     */
    void create(String cid, String bucketName, String desc, String acl);

    /**
     * update bucket config
     *
     * @param request
     */
    void updateBucket(BucketConfigRequest request);

    /**
     * detail of the bucket
     *
     * @param id
     * @return
     */
    BucketEntity getOne(String id);

    /**
     * find all bucket by type
     *
     * @param type
     * @return
     */

    List<BucketEntity> findAllByType(String type);

    /**
     * sync bucket from server
     */
    void sync();

    /**
     * enter bucket info
     *
     * @param bucketEnterRequest
     * @return
     */
    BucketEntity enter(BucketEnterRequest bucketEnterRequest);

    Integer delete(String id);

    BucketEntity editBucket(BucketEditRequest request);
}

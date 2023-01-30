package com.longi.msp.osca.listener;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.longi.msp.common.security.DetailsHelper;
import com.longi.msp.common.security.LmspUser;
import com.longi.msp.osca.events.SyncBucketDataEvent;
import com.longi.msp.osca.mapper.BucketMapper;
import com.longi.msp.osca.model.entity.BaseEntity;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.entity.ServerConfigEntity;
import com.meicloud.paas.core.client.ObjectClient;
import com.meicloud.paas.core.model.bucket.Bucket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenlei140
 * @className SyncListener
 * @description 同步桶数据监听器
 * @date 2022/7/8 18:22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BucketSyncListener {

    private final BucketMapper bucketMapper;

    @EventListener
    public void onApplicationEvent(SyncBucketDataEvent sequenceConfigPushEvent) {
        ServerConfigEntity config = sequenceConfigPushEvent.getOssConfig();
        ObjectClient objectClient = sequenceConfigPushEvent.getObjectClient();
        List<Bucket> buckets = objectClient.opsForBucket().listBuckets();
        if (CollUtil.isEmpty(buckets)) {
            bucketMapper.batchDeleteByCid(config.getId());
            return;
        }
        // 数据库已存在的桶的数据
        List<BucketEntity> dbList = bucketMapper.findByType(config.getId());
        // 待删除的桶的id集合
        List<String> delList = new ArrayList<>();
        if (CollUtil.isNotEmpty(dbList)) {
            delList = dbList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        }
        LmspUser lmspUser = DetailsHelper.getUserDetails();
        log.info("Sync bucket listener event \n :user {} config {}", lmspUser, config);
        for (Bucket bucket : buckets) {
            BucketEntity fromDB = bucketMapper.findByNameAndType(config.getType(), bucket.getName());
            if (ObjectUtil.isNull(fromDB)) {
                fromDB = buildBucketEntity(config, lmspUser, bucket);
                bucketMapper.insert(fromDB);
            } else {
                // 存在就从待删列表移除
                delList.remove(fromDB.getId());
                fromDB.setEndPoint(CharSequenceUtil.isBlank(bucket.getEndPoint()) ? config.getEndPoint() : bucket.getEndPoint());
                fromDB.setRegion(bucket.getRegion());
                fromDB.setAcl(bucket.getAcl());
                bucketMapper.update(fromDB);
            }
        }

        // 同步远程服务商删除的桶数据
        if (CollUtil.isNotEmpty(delList)) {
            log.info("sync server delete  bucket : \n  {}", delList);
            for (String id : delList) {
                bucketMapper.delete(id);
            }
        }
    }

    @NotNull
    private static BucketEntity buildBucketEntity(ServerConfigEntity config, LmspUser lmspUser, Bucket bucket) {
        BucketEntity fromDB = new BucketEntity();
        fromDB.setBucketName(bucket.getName());
        fromDB.setType(config.getType());
        fromDB.setRegion(bucket.getRegion());
        fromDB.setEndPoint(CharSequenceUtil.isBlank(bucket.getEndPoint()) ? config.getEndPoint() : bucket.getEndPoint());
        fromDB.setCid(config.getId());
        fromDB.setAcl(bucket.getAcl() == null ? CharSequenceUtil.EMPTY : bucket.getAcl());
        fromDB.setCreationDate(bucket.getCreationDate());
        BaseEntity.initUser(fromDB, lmspUser);
        return fromDB;
    }

}

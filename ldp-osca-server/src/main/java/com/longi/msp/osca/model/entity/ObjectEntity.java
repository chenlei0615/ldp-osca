package com.longi.msp.osca.model.entity;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrPool;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文件对象实体类
 *
 * @author chenlei140
 * @date 2022/10/26 14:56
 **/
@Data
@Schema(description = "文件对象实体类")
@EqualsAndHashCode(callSuper = false)
public class ObjectEntity extends BaseEntity {

    public static final Integer STATUS_SUCCESS = 1;
    public static final Integer STATUS_FAILED = 0;

    @Schema(description = "服务商配置id")
    private String cid;
    @Schema(description = "桶id")
    private String bid;
    @Schema(description = "桶空间名")
    private String bucketName;
    @Schema(description = "对象名")
    private String objectName;
    @Schema(description = "对象存储路径")
    private String objectPath;
    @Schema(description = "对象大小 b")
    private long objectSize;
    @Schema(description = "存储源")
    private String type;
    @Schema(description = "调用源 SDK/SERVER")
    private String source;
    @Schema(description = "文件状态 0：失败  1：成功")
    private Integer objectStatus = STATUS_SUCCESS;
    @Schema(description = "文件上传返回描述")
    private String description;

    public String getObjectKey() {
        return CharSequenceUtil.isBlank(this.getObjectPath()) ?
                this.getObjectName() :
                this.getObjectPath().concat(StrPool.SLASH).concat(this.getObjectName());
    }

    public static String assembleObjectKey(String sourceKey, String sourcePath) {
        return CharSequenceUtil.isBlank(sourcePath) ?
                sourceKey :
                sourcePath.concat(StrPool.SLASH).concat(sourceKey);
    }
}

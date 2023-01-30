package com.meicloud.paas.osca.console.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 对象详情
 *
 * @author chenlei140
 * @date 2023/01/13 10:21
 **/
@Data
@Schema(description = "文件对象dto")
@EqualsAndHashCode(callSuper = false)
public class ObjectDTO {

    private String id;

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
    private Integer objectStatus;
    @Schema(description = "文件上传返回描述")
    private String description;
    @Schema(description = "创建人id")
    private String createdBy;
    @Schema(description = "创建人")
    private String createdByName;
    @Schema(description = "创建时间")
    private Date creationDate;
    @Schema(description = "最近更新人id")
    private String lastUpdatedBy;
    @Schema(description = "最近更新人")
    private String lastUpdatedByName;
    @Schema(description = "最近更新时间")
    private Date lastUpdateDate;
}

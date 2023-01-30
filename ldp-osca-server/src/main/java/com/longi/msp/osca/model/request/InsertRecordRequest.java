package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chenlei140
 * @className InsertConfigRequest
 * @description 新增操作记录
 * @date 2022/6/24 15:31
 */
@Data
@Schema(description= "新增操作模型")
public class InsertRecordRequest implements Serializable {
    private static final long serialVersionUID = 2405172041950251807L;

    @Schema(description= "服务配置id")
    private String cid;
    @Schema(description= "桶id")
    private String bid;
    @Schema(description= "桶名")
    private String bucketName;
    @Schema(description= "对象名")
    private String objectName;
    @Schema(description= "对象存储路径")
    private String objectPath;
    @Schema(description= "对象大小 b")
    private long objectSize;
    @Schema(description= "对象来源")
    private String type;
    @Schema(description= "调用来源")
    private String source;
    @Schema(description= "文件状态 0：失败  1：成功")
    private Integer objectStatus = 1;
    @Schema(description = "文件上传返回描述")
    private String description;
}

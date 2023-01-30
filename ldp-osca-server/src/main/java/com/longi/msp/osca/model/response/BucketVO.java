package com.longi.msp.osca.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chenlei140
 * @date 2022/11/25 16:50
 **/
@Data
public class BucketVO {
    private String id;
    @Schema(description= "创建人id")
    private String createdBy = "";
    @Schema(description= "创建人")
    private String createdByName = "";
    @Schema(description= "创建时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate = new Date();
    @Schema(description= "最近更新人id")
    private String lastUpdatedBy = "";
    @Schema(description= "最近更新人")
    private String lastUpdatedByName = "";
    @Schema(description= "最近更新时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate = new Date();

    @Schema(description= "桶名")
    private String bucketName;

    @Schema(description= "存储服务配置id")
    private String cid;

    /**
     * 位置
     */
    @Schema(description= "位置")
    private String region;

    @Schema(description= "访问网点")
    private String endPoint;
    /**
     * 来源
     */
    @Schema(description= "来源： OSS COS OBS MINIO")
    private String type;

    @Schema(description= "桶权限")
    private String acl;

    @Schema(description= "加密类型 目前固定SM4")
    private String encryptedType;

    @Schema(description= "桶内上传最大的文件大小")
    private BigDecimal globalSizeLimit;

    @Schema(description= "桶内上传最大的文件大小单位")
    private String globalSizeUnit;

    @Schema(description= "限制json")
    private String limitJson;

    /**
     * 类型白名单
     */
    @Schema(description= "类型白名单 以英文逗号分隔")
    private String whiteList;

    @Schema(description= "白名单开关，0关闭1开启（默认关闭）")
    private Integer whiteSwitch;

    @Schema(description= "全局文件大小限制开关，0关闭1开启（默认关闭）")
    private Integer globalSwitch;

    @Schema(description= "描述")
    private String description;

    @Schema(description = "标识用户的key")
    private String accessKeyId;

    @Schema(description = "加密密钥")
    private String accessKeySecret;

}

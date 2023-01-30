package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 桶信息录入请求模型
 *
 * @author chenlei140
 * @date 2022/12/30 13:42
 **/
@Data
@Schema(description = "桶信息录入请求模型")
public class BucketEnterRequest {

    @NotBlank(message = "桶空间名不能为空")
    @Schema(description = "桶空间名")
    private String bucketName;

    @NotBlank(message = "类型不能为空")
    @Schema(description = "类型： OSS MINIO")
    private String type;

    @Schema(description = "访问网络端点")
    private String endPoint;

    @NotBlank(message = "密钥不能为空")
    @Schema(description = "密钥")
    private String accessKeyId;

    @NotBlank(message = "访问密钥不能为空")
    @Schema(description = "访问密钥")
    private String accessKeySecret;

    @Schema(description = "描述")
    private String description;

}


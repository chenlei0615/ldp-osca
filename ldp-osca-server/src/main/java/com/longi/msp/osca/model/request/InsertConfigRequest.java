package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenlei140
 * @className InsertConfigRequest
 * @description 新增模型
 * @date 2022/6/24 15:31
 */
@Data
@Schema(description= "新增配置模型")
public class InsertConfigRequest {

    /**
     * 用户别名
     */
    @NotBlank(message = "用户别名不能为空")
    @Schema(description= "用户别名")
    private String userAlias;

    /**
     * 模式： cos、oss、obs、fdfs、minio
     */
    @NotBlank(message = "模式不能为空")
    @Schema(description= "模式： cos、oss、obs、fdfs、minio")
    private String type;

    /**
     * 密钥
     */
    @NotBlank(message = "密钥不能为空")
    @Schema(description= "密钥")
    private String secretKey;

    /**
     * 访问密钥
     */
    @NotBlank(message = "访问密钥不能为空")
    @Schema(description= "访问密钥")
    private String accessKey;

    /**
     * 访问网点
     */
    @Schema(description= "访问网点")
    private String endPoint;

    @Schema(description= "描述")
    private String description;


}

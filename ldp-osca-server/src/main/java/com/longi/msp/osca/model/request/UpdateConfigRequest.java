package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author chenlei140
 * @className UpdateConfigRequest
 * @description 更新模型
 * @date 2022/6/24 15:33
 */
@Data
@Schema(description= "配置更新模型")
public class UpdateConfigRequest {

    /**
     * id
     */
    @NotBlank(message = "配置id不能为空")
    @Schema(description= "id")
    private String id;

    /**
     * 用户别名
     */
    @NotBlank(message = "用户别名不能为空")
    @Schema(description= "用户别名")
    private String userAlias;

    /**
     * 模式： cos、oss、obs、fdfs、minio
     */
    @Schema(description= "模式： cos、oss、obs、fdfs、minio")
    private String mode;

    /**
     * 密钥
     */
    @Schema(description= "密钥")
    private String secretKey;

    /**
     * 访问密钥
     */
    @Schema(description= "访问密钥")
    private String accessKey;

    /**
     * 访问网点
     */
    @Schema(description= "访问网点")
    private String endPoint;


    /**
     * 描述
     */
    @Schema(description= "描述")
    private String description;


}

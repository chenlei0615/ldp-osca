package com.longi.msp.osca.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * @author chenlei140
 * @className OssConfigEntity
 * @description oss配置实体
 * @date 2022/6/22 13:58
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class ServerConfigEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户别名
     */
    @Schema(description= "用户别名")
    private String userAlias;

    /**
     * 模式： cos、oss、obs、fdfs、minio
     */
    @Schema(description= "模式： cos、oss、obs、fdfs、minio")
    private String type;

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

    @Schema(description= "权限配置")
    private String policy;
}

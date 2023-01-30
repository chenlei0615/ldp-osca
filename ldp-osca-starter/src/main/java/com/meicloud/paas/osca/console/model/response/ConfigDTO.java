package com.meicloud.paas.osca.console.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * @author chenlei140
 * @className OssConfigEntity
 * @description oss配置实体
 * @date 2022/6/22 13:58
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
public class ConfigDTO{

    @Schema(description= "id")
    private String id;

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
    @Schema(description= "创建人id")
    private String createdBy = "";
    @Schema(description= "创建人")
    private String createdByName = "";
    @Schema(description= "创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate = new Date();
    @Schema(description= "最近更新人id")
    private String lastUpdatedBy = "";
    @Schema(description= "最近更新人")
    private String lastUpdatedByName = "";
    @Schema(description= "最近更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate = new Date();

    /**
     * 删除标志
     */
    @Schema(description= "删除")
    private Boolean deleted = false;

}

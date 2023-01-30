package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chenlei140
 * @className BucketConfigRequest
 * @description 桶编辑请求模型
 * @date 2022/6/27 9:36
 */
@Data
@Schema(description = "桶配置请求模型")
public class BucketEditRequest {

    @Schema(description = "桶id")
    private String id;

    @Schema(description = "访问网点")
    private String endPoint;

    @Schema(description = "标识用户的key")
    private String accessKeyId;

    @Schema(description = "加密密钥")
    private String accessKeySecret;

    @Schema(description = "描述")
    private String description;
}

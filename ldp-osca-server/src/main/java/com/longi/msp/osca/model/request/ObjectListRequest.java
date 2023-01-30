package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author chenlei140
 * @className ObjectsRequestEntity
 * @description 对象列表请求模型
 * @date 2022/6/21 10:09
 */
@Data
@Schema(description= "对象列表请求模型")
public class ObjectListRequest {

    @Schema(description= "存储商配置id")
    private String cid;

    @Schema(description= "桶id")
    private String bid;

    @Schema(description= "服务商类型")
    private String type;

    @Schema(description= "桶名称")
    private String bucketName;

    @Schema(description= "用户别名")
    private String userAlias;

    @Schema(description= "对象id")
    private String objectKey;

    @Schema(description= "每页数量")
    @NotNull(message = "每页数量")
    private Integer pageSize;

    @Schema(description= "页码")
    @NotNull(message = "页码")
    private Integer pageNo;

}

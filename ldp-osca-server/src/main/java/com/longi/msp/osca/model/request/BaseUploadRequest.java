package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * base upload model
 *
 * @author chenlei140
 * @date 2022/12/21 15:51
 **/
@Data
@Schema(description = "基础请求模型")
public class BaseUploadRequest {

    @Schema(description = "桶Id")
    private String bid;
    @Schema(description = "自定义存储路径")
    private String customPath;
    @Schema(description = "自定义元数据")
    private String metaMap;
}

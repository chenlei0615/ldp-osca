package com.meicloud.paas.osca.console.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author chenlei140
 * @date 2022/11/21 10:33
 **/
@Data
@Schema(description = "对象信息")
public class PutObjectDTO {
    @Schema(description = "对象id")
    private String id;
    @Schema(description = "桶")
    private String bucketName;
    @Schema(description = "对象存储地址")
    private String objectKey;
    @Schema(description = "对象真实地址")
    private String objectUrl;
    @Schema(description = "对象大小 B")
    private long objectSize;
    @Schema(description = "文件名字")
    private String fileName;
}

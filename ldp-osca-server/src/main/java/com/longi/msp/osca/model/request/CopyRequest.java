package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author chenlei140
 * @className BatchCopyRequest
 * @description 批量拷贝请求模型
 * @date 2022/7/7 11:36
 */
@Data
@Schema(description= "批量拷贝请求模型")
public class CopyRequest {

    @NotEmpty(message = "文件id不能为空")
    @Schema(description= "文件id路径")
    private String oid;
    @Schema(description= "目标目录")
    private String targetFolder;
}

package com.meicloud.paas.osca.console.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件请求模型
 *
 * @author chenlei140
 * @date 2022/12/22 14:34
 **/
@Data
@Schema
public class UploadFileRequest {
    @Schema(description = "上传文件")
    private MultipartFile file;
    @Schema(description = "桶Id")
    private String bid;
    @Schema(description = "自定义存储路径")
    private String customPath;
    @Schema(description = "自定义元数据")
    private String metaMap;
}

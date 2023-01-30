package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传请求模型
 *
 * @author chenlei140
 * @date 2022/10/14 18:28
 **/
@Data
@Schema(description = "上传请求模型")
public class UploadObjectRequest extends BaseUploadRequest {
    @Schema(description = "上传文件")
    private MultipartFile file;
}

package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * 水印下载请求模型
 *
 * @author chenlei140
 * @date 2022/10/14 18:51
 **/
@Schema(description= "下载请求模型")
@Data
public class DownloadRequest {
    @NotNull(message = "对象id")
    @Schema(description= "对象id")
    private String id;
    @Schema(description= "自定义下载路径")
    private String customPath;
    @Schema(description= "水印文字内容")
    private String watermarkContent;
    @Schema(description= "水印图片文件")
    private MultipartFile watermarkPicFile;
}

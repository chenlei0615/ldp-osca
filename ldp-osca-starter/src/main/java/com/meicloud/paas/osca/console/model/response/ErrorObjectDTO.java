package com.meicloud.paas.osca.console.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误文件信息
 *
 * @author chenlei140
 * @date 2022/12/26 11:55
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "错误文件信息")
public class ErrorObjectDTO {
    @Schema(description = "文件名字")
    private String fileName;
    @Schema(description = "错误描述")
    private String description;
}

package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author chenlei140
 * @className BatchCopyRequest
 * @description 批量拷贝请求模型
 * @date 2022/7/7 11:36
 */
@Data
@Schema(description= "批量拷贝请求模型")
public class BatchCopyRequest {

    @Schema(description= "存储商配置id")
    @NotNull(message = "存储商配置id不能为空")
    private String cid;
    @Schema(description= "桶id")
    @NotNull(message = "桶id不能为空")
    private String bid;
    @NotEmpty(message = "集合不能为空")
    @Schema(description= "源文件路径集合")
    private List<String> objectKeys;
    @Schema(description= "目标目录")
    private String targetFolder;
}

package com.longi.msp.osca.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author chenlei140
 * @className BucketConfigRequest
 * @description 桶配置请求模型
 * @date 2022/6/27 9:36
 */
@Data
@Schema(description = "桶配置请求模型")
public class BucketConfigRequest {

    @Schema(description = "桶id")
    private String id;


    @Schema(description = "桶名")
    private String bucketName;
    /**
     * 目前仅支持
     */
    @Schema(description = "目前仅支持 SM4")
    private String encryptedType;


    @Schema(description = "最大上传值")
    private BigDecimal globalSizeLimit;

    @Schema(description = "单位")
    private String globalSizeUnit;
    /**
     * 限制json
     * <p>
     * [
     * {
     * "type":"doc",
     * "size":10000,
     * "unit":"MB"
     * },
     * {
     * "type":"ppt",
     * "size":10000,
     * "unit":"MB"
     * }
     * ]
     */
    @Schema(description = "限制json文本")
    private List<Map<String, Object>> limitJson;

    /**
     * 类型白名单
     */
    @Schema(description = "类型白名单 以英文逗号分隔")
    private String whiteList;

    @Schema(description = "白名单开关，0关闭1开启（默认关闭）")
    private Integer whiteSwitch;

    @Schema(description = "全局文件大小限制开关，0关闭1开启（默认关闭）")
    private Integer globalSwitch;

    @Schema(description = "权限 PUBLIC_READ_WRITE:公有读写,PUBLIC_READ:公有读,PRIVATE:私有读写 PRIVATE_READ:私有读")
    private String acl;
}

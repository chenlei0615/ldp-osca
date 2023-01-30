package com.meicloud.paas.osca.console.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chenlei140
 * @date 2022/10/31 17:38
 **/
@Data
public class BucketInfoDTO {
    /**
     * 桶名
     */
    private String id;

    /**
     * 桶名
     */
    private String name;
    /**
     * 创建时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date creationDate;
    /**
     * 位置
     */
    private String location;

    /**
     * 位置id
     */
    private String region;

    /**
     * 访问网点
     */
    private String endpoint;
    /**
     * 桶类型
     */
    private String bucketType;

    /**
     * 桶存储类型 标准存储（Standard） 低频访问（Infrequent Access） 归档存储（Archive） 冷归档存储（Cold Archive）
     */
    private String type;
    /**
     * 桶权限
     */
    private String acl;

    /**
     * 加密类型
     */
    private String encryptedType;

    /**
     * 桶内上传最大的文件大小
     */
    private BigDecimal globalSizeLimit;
    /**
     * 桶内上传最大的文件大小单位
     */
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
    private String limitJson;

    /**
     * 类型白名单
     */
    private String whiteList;

    /**
     * 白名单开关，0关闭1开启（默认关闭）
     */
    private Integer whiteSwitch;

    /**
     * 全局文件大小限制开关，0关闭1开启（默认关闭）
     */
    private Integer globalSwitch;
}

package com.longi.msp.osca.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对象操作记录表
 *
 * @author chenlei140
 * @date 2022/10/26 14:56
 **/
@Data
@Schema(description= "对象操作记录表")
@EqualsAndHashCode(callSuper = false)
public class ObjectRecordEntity extends BaseEntity {

    @Schema(description= "对象id")
    private String objectId;
    @Schema(description= "对象名")
    private String objectName;
    @Schema(description= "对象大小 b")
    private long objectSize;

}

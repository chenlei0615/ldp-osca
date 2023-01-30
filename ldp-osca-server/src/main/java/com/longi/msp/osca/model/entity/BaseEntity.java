package com.longi.msp.osca.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.longi.msp.common.security.LmspUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 基础实体类信息
 *
 * @author chenlei140
 * @date 2022/10/26 14:57
 **/
@Data
@Schema(description= "基础实体类信息")
public class BaseEntity {

    private String id;

    @Schema(description= "创建人id")
    private String createdBy = "";
    @Schema(description= "创建人")
    private String createdByName = "";
    @Schema(description= "创建时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate = new Date();
    @Schema(description= "最近更新人id")
    private String lastUpdatedBy = "";
    @Schema(description= "最近更新人")
    private String lastUpdatedByName = "";
    @Schema(description= "最近更新时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdateDate = new Date();

    /**
     * 删除标志
     */
    @Schema(description= "删除")
    private Boolean deleted = false;


    public static <T extends BaseEntity> T initUser(T entity, LmspUser user) {
        entity.setCreatedBy(user.getLoginName());
        entity.setCreatedByName(user.getRealName());
        entity.setLastUpdatedBy(user.getLoginName());
        entity.setLastUpdatedByName(user.getRealName());
        return entity;
    }

    public static <T extends BaseEntity> T updateUser(T entity, LmspUser user) {
        entity.setLastUpdatedBy(user.getLoginName());
        entity.setLastUpdatedByName(user.getRealName());
        return entity;
    }
}

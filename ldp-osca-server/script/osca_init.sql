-- osca.msp_osca_server_config definition
-- 服务商配置表
CREATE TABLE `msp_osca_server_config`
(
    `id`                   varchar(64)  NOT NULL COMMENT '主键id',
    `user_alias`           varchar(100)          DEFAULT NULL COMMENT '用户别名',
    `type`                 varchar(100) NOT NULL DEFAULT '' COMMENT '模式',
    `end_point`            varchar(100)          DEFAULT NULL COMMENT '访问域名',
    `secret_key`           varchar(100)          DEFAULT NULL COMMENT '验证用户的密钥',
    `access_key`           varchar(100)          DEFAULT NULL COMMENT '访问密钥',
    `policy`               varchar(256)          DEFAULT '' COMMENT '权限配置',
    `description`          varchar(100)          DEFAULT '' COMMENT '描述',
    `created_by`           varchar(48)           DEFAULT '' COMMENT '创建人id',
    `created_by_name`      varchar(64)           DEFAULT '' COMMENT '创建人',
    `creation_date`        datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_updated_by`      varchar(48)           DEFAULT '' COMMENT '最近更新人id',
    `last_updated_by_name` varchar(64)           DEFAULT '' COMMENT '最近更新人',
    `last_update_date`     datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近更新时间',
    `deleted`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标志',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务商配置表';

-- 桶空间表
-- osca.msp_osca_bucket definition

CREATE TABLE `msp_osca_bucket`
(
    `id`                   varchar(64)  NOT NULL COMMENT '主键id',
    `bucket_name`          varchar(100)          DEFAULT NULL COMMENT '桶名',
    `cid`                  varchar(64)  NOT NULL COMMENT '服务商配置id',
    `type`                 varchar(100) NOT NULL DEFAULT '' COMMENT '存储源 oss/minio',
    `acl`                  varchar(32)           DEFAULT '' COMMENT '桶权限',
    `region`               varchar(100)          DEFAULT NULL COMMENT '访问地域',
    `end_point`            varchar(100)          DEFAULT '' COMMENT '访问站点',
    `encrypted_type`       varchar(100)          DEFAULT NULL COMMENT '加密类型',
    `global_size_limit`    decimal(19, 4)        DEFAULT NULL COMMENT '全局对象大小限制值',
    `global_size_unit`     varchar(100)          DEFAULT NULL COMMENT '全局对象大小限制单位',
    `limit_json`           varchar(256)          DEFAULT '' COMMENT '限制json配置',
    `white_list`           varchar(256)          DEFAULT '' COMMENT '白名单配置',
    `description`           varchar(256)          DEFAULT '' COMMENT '描述',
    `white_switch`         tinyint(1) DEFAULT '0' COMMENT '白名单开关(0关闭1开启,默认0)',
    `global_switch`        tinyint(1) DEFAULT '0' COMMENT '全局文件大小限制开关(0关闭1开启,默认0）',
    `created_by`           varchar(48)           DEFAULT '' COMMENT '创建人id',
    `created_by_name`      varchar(64)           DEFAULT '' COMMENT '创建人',
    `creation_date`        datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_updated_by`      varchar(48)           DEFAULT '' COMMENT '最近更新人id',
    `last_updated_by_name` varchar(64)           DEFAULT '' COMMENT '最近更新人',
    `last_update_date`     datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近更新时间',
    `deleted`              tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标志',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='桶配置表';

-- longi.msp_object definition
-- 对象表
-- osca.msp_osca_object definition

CREATE TABLE `msp_osca_object`
(
    `id`                   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '主键',
    `object_name`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对象名',
    `object_size`          bigint                                                                 DEFAULT '0' COMMENT '对象大小',
    `object_path`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '对象存储路径',
    `bucket_name`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '桶名',
    `type`                 varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '存储源 oss/minio',
    `source`               varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '调用源 SDK/SERVER',
    `cid`                  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '服务商配置id',
    `bid`                  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '桶id',
    `created_by`           varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '创建人id',
    `created_by_name`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '创建人',
    `creation_date`        datetime                                                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_updated_by`      varchar(48) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '最近更新人id',
    `last_updated_by_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '最近更新人',
    `last_update_date`     datetime                                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近更新时间',
    `deleted`              tinyint                                                       NOT NULL DEFAULT '0' COMMENT '删除标志',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='对象信息';


-- longi.msp_object_record definition
-- 上传记录表
-- osca.msp_osca_object_record definition

CREATE TABLE `msp_osca_object_record`
(
    `id`                   varchar(64)  NOT NULL COMMENT '主键',
    `object_id`            varchar(100) NOT NULL COMMENT '对象id',
    `object_name`          varchar(100) DEFAULT '' COMMENT '对象名',
    `object_size`          bigint(20) DEFAULT '0' COMMENT '对象大小',
    `created_by`           varchar(48)  DEFAULT '' COMMENT '创建人id',
    `created_by_name`      varchar(64)  DEFAULT '' COMMENT '创建人',
    `creation_date`        datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `last_updated_by`      varchar(48)  DEFAULT '' COMMENT '最近更新人id',
    `last_updated_by_name` varchar(64)  DEFAULT '' COMMENT '最近更新人',
    `last_update_date`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最近更新时间',
    `deleted`              tinyint(4) DEFAULT '0' COMMENT '删除标志',
    PRIMARY KEY (`id`),
    KEY                    `msp_object_record_object_id_IDX` (`object_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='对象上传记录表';


CREATE INDEX msp_osca_object_record_object_id_IDX USING BTREE ON msp_osca_object_record (object_id);


ALTER TABLE msp_osca_bucket MODIFY COLUMN limit_json varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' NULL COMMENT '限制json配置';

-- 20221219
ALTER TABLE msp_osca_object ADD object_status TINYINT DEFAULT 1 NOT NULL COMMENT '文件状态 0：失败  1：成功';

-- 20221223
ALTER TABLE msp_osca_object ADD description varchar(256) DEFAULT '' NOT NULL COMMENT '上传结果描述';

-- 20230103
ALTER TABLE msp_osca_bucket ADD access_key_id varchar(100) NOT NULL COMMENT '标识用户的key';
ALTER TABLE msp_osca_bucket ADD access_key_secret varchar(100) NOT NULL COMMENT '加密密钥';
ALTER TABLE msp_osca_bucket MODIFY COLUMN cid varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '服务商配置id';
ALTER TABLE msp_osca_bucket MODIFY COLUMN acl varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' NULL COMMENT '桶权限';


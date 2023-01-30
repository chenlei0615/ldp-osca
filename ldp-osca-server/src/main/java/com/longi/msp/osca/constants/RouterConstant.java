package com.longi.msp.osca.constants;

public class RouterConstant {

    private RouterConstant() {
    }

    public static final String CONFIG_LIST_PATH = "config/list";
    public static final String CONFIG_INSERT_PATH = "config/insert";
    public static final String CONFIG_UPDATE_PATH = "config/update";
    public static final String CONFIG_DELETE_PATH = "config/delete/{id}";
    public static final String CONFIG_DETAIL_PATH = "config/detail/{id}";
    public static final String CONFIG_USERS_PATH = "config/users/{type}";
    public static final String CONFIG_AUTH_PATH = "config/grant/{id}";

    /**
     * 桶
     */
    public static final String BUCKET_LIST_PATH = "bucket/list";
    public static final String BUCKET_LIST_ALL = "bucket/all";
    public static final String BUCKET_DETAIL_PATH = "bucket/detail";
    public static final String BUCKET_INSERT_PATH = "bucket/insert";
    public static final String BUCKET_CONFIG_PATH = "bucket/config";

    public static final String BUCKET_EDIT_PATH = "bucket/edit";

    public static final String BUCKET_SYNC_PATH = "bucket/sync";
    public static final String BUCKET_ENTER_PATH = "bucket/enter";
    public static final String BUCKET_DELETE_PATH = "bucket/delete/{id}";
    /**
     * 对象
     */
    public static final String OBJECT_LIST_PATH = "object/list";
    public static final String OBJECT_UPLOAD_PATH = "object/upload";
    public static final String OBJECT_BATCH_UPLOAD_PATH = "object/uploads";
    public static final String OBJECT_DELETE_PATH = "object/delete/{id}";
    public static final String OBJECT_COPY_PATH = "object/copy";
    public static final String OBJECT_RENAME_PATH = "object/rename/{id}";
    public static final String OBJECT_DOWN_PATH = "object/down";
    public static final String OBJECT_DETAIL_PATH = "object/detail/{id}";
    public static final String OBJECT_STORAGE_LIST_PATH = "object/storage/list";
    public static final String OBJECT_UPLOADER_LIST_PATH = "object/uploader";
    /**
     * 记录
     */
    public static final String RECORD_INSERT_PATH = "record/insert";
    public static final String RECORD_LIST_PATH = "record/list";
    /**
     * 预览
     */
    public static final String PREVIEW_PATH = "preview/link";
    public static final String PREVIEW_OSS_PATH = "preview/object/{id}";


}

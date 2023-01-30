package com.longi.msp.osca.mapper;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.entity.ObjectRecordEntity;
import org.apache.ibatis.annotations.*;

@Mapper
public interface RecordMapper {
    @Select("<script>" +
            "   SELECT id,object_id,object_name,object_size, " +
            "   deleted,creation_date,last_update_date,created_by,created_by_name," +
            "   last_updated_by,last_updated_by_name " +
            "   FROM msp_osca_object_record WHERE deleted = false  " +
            "   <when test=' objectId!=null and objectId.length() > 0 '>" +
            "     AND object_id = #{objectId} " +
            "    </when> " +
            "    order by creation_date desc " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "objectId", column = "object_id"),
            @Result(property = "objectName", column = "object_name"),
            @Result(property = "objectSize", column = "object_size"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedBy", column = "last_updated_by"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
    })
    Page<ObjectRecordEntity> list(@Param("objectId") String objectId);

    @Insert(" insert into msp_osca_object_record(id,object_id,object_name,object_size," +
            " created_by,created_by_name,last_updated_by,last_updated_by_name,creation_date) values " +
            " (#{id},#{objectId},#{objectName},#{objectSize},#{createdBy},#{createdByName}," +
            " #{lastUpdatedBy},#{lastUpdatedByName},#{creationDate} )")
    @SelectKey(keyProperty = "id", resultType = String.class, before = true,
            statement = "select replace(uuid(),'-','') as id from dual ")
    void insert(ObjectRecordEntity objectRecordEntity);
}

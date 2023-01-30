package com.longi.msp.osca.mapper;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.dto.UploaderInfoDTO;
import com.longi.msp.osca.model.entity.ObjectEntity;
import com.longi.msp.osca.model.request.ObjectListRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ObjectEntityMapper {

    @Select("<script>" +
            "   SELECT o.id,o.object_name,o.object_size,o.object_path,o.bid,o.bucket_name,o.object_status,o.description,o.cid,o.type,o.source, " +
            "   o.deleted,o.created_by,o.created_by_name,o.last_updated_by,o.last_updated_by_name,o.creation_date," +
            "   o.last_update_date FROM msp_osca_object o left join msp_osca_bucket b on b.id = o.bid  " +
            "     WHERE o.deleted = false  and b.deleted = false " +
            "   <when test=' cid!=null and cid.length() > 0 '>" +
            "     AND o.cid = #{cid} " +
            "    </when> " +
            "   <when test=' bid!=null and bid.length() > 0 '>" +
            "     AND o.bid = #{bid} " +
            "    </when> " +
            "   <when test=' type!=null and type.length() > 0 '>" +
            "     AND o.type = #{type} " +
            "    </when> " +
            "   <when test=' bucketName!=null and bucketName.length() > 0 '>" +
            "     AND o.bucket_name like CONCAT('%', #{bucketName}, '%') " +
            "    </when> " +
            "   <when test=' objectKey!=null and objectKey.length() > 0 '>" +
            "     AND o.object_name like CONCAT('%', #{objectKey}, '%') " +
            "    </when> " +
            "   <when test=' userAlias!=null and userAlias.length() > 0 '>" +
            "     AND (o.created_by_name like CONCAT ('%', #{userAlias}, '%')  " +
            "           or  o.created_by = #{userAlias} )" +
            "    </when> " +
            "    order by o.last_update_date desc " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "objectName", column = "object_name"),
            @Result(property = "objectSize", column = "object_size"),
            @Result(property = "objectPath", column = "object_path"),
            @Result(property = "bid", column = "bid"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "objectStatus", column = "object_status"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "type", column = "type"),
            @Result(property = "source", column = "source"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "description", column = "description"),
    })
    Page<ObjectEntity> list(ObjectListRequest request);

    @Insert(" insert IGNORE into msp_osca_object(id,object_name,object_path,object_size,bid,bucket_name, object_status,description, " +
            " cid,type,source,created_by,created_by_name,last_updated_by,last_updated_by_name) values " +
            " (#{id},#{objectName},#{objectPath},#{objectSize},#{bid},#{bucketName},#{objectStatus},#{description}, " +
            " #{cid},#{type},#{source},#{createdBy},#{createdByName}," +
            " #{lastUpdatedBy},#{lastUpdatedByName} )")
    @SelectKey(keyProperty = "id", resultType = String.class, before = true,
            statement = "select replace(uuid(),'-','') as id from dual ")
    void insert(ObjectEntity objectEntity);

    @Select("<script>" +
            "   SELECT id,object_name,object_size,object_path,bid,bucket_name,cid,type," +
            "   deleted, creation_date,created_by_name,last_updated_by_name,last_update_date FROM msp_osca_object " +
            "     WHERE deleted = false  AND object_path = #{objectPath} " +
            "   <when test=' bucketName!=null and bucketName.length() > 0 '>" +
            "     AND bucket_name = #{bucketName} " +
            "    </when> " +
            "   <when test=' objectName!=null and objectName.length() > 0 '>" +
            "     AND object_name = #{objectName} " +
            "    </when> " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "objectName", column = "object_name"),
            @Result(property = "objectSize", column = "object_size"),
            @Result(property = "objectPath", column = "object_path"),
            @Result(property = "bid", column = "bid"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "type", column = "type"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
    })
    ObjectEntity findOne(@Param("bucketName") String bucketName, @Param("objectName") String objectName, @Param("objectPath") String objectPath);

    @Update("<script>" +
            " update msp_osca_object " +
            " <trim prefix='set' suffixOverrides=','> " +
            "  <if test = 'objectName!=null and objectName.length()>0' >" +
            "    object_name = #{objectName} , " +
            "  </if>" +
            "  <if test = ' objectSize>0 ' >" +
            "    object_size = #{objectSize}, " +
            "  </if>" +
            "  <if test = 'objectPath!=null and objectPath.length()>0' >" +
            "    object_path = #{objectPath} ," +
            "  </if>" +
            "  <if test = 'bucketName!=null and bucketName.length()>0' >" +
            "    bucket_name = #{bucketName} ," +
            "  </if>" +
            "  <if test = 'cid!=null and cid.length()>0' >" +
            "    cid = #{cid}, " +
            "  </if>" +
            "  <if test = 'bid!=null and bid.length()>0' >" +
            "    bid = #{bid} ," +
            "  </if>" +
            "  <if test = 'lastUpdatedBy!=null and lastUpdatedBy.length()>0' >" +
            "    last_updated_by = #{lastUpdatedBy} ," +
            "  </if>" +
            "  <if test = 'lastUpdatedByName!=null and lastUpdatedByName.length()>0' >" +
            "    last_updated_by_name = #{lastUpdatedByName} ," +
            "  </if>" +
            " </trim> " +
            " where id = #{id} " +
            "</script>")
    int update(ObjectEntity objectEntity);

    @Update("<script>" +
            " update msp_osca_object set deleted = 1 where id = #{id} " +
            "</script>")
    int delete(ObjectEntity objectEntity);

    /**
     * getOne
     *
     * @param id
     * @return
     */
    @Select("<script>" +
            "   SELECT id,object_name,object_size,object_path,bid,bucket_name,cid,type," +
            "   deleted, creation_date,created_by_name,last_updated_by_name,last_update_date FROM msp_osca_object " +
            "     WHERE deleted = false and id = #{id} " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "objectName", column = "object_name"),
            @Result(property = "objectSize", column = "object_size"),
            @Result(property = "objectPath", column = "object_path"),
            @Result(property = "bid", column = "bid"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "type", column = "type"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
    })
    ObjectEntity getOne(@Param("id") String id);

    @Select("<script>" +
            "   SELECT created_by,created_by_name FROM msp_osca_object  WHERE deleted = false" +
            "   GROUP by created_by ,created_by_name " +
            "</script>")
    @Results({
            @Result(property = "username", column = "created_by"),
            @Result(property = "realname", column = "created_by_name"),
    })
    List<UploaderInfoDTO> findAllCreatedBy();

}

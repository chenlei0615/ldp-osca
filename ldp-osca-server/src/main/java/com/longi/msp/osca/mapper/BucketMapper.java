package com.longi.msp.osca.mapper;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.entity.BucketEntity;
import com.longi.msp.osca.model.response.BucketVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author chenlei140
 * @className BucketConfigMapper
 * @description 桶配置
 * @date 2022/6/25 18:09
 */
@Mapper
public interface BucketMapper {

    @Select("<script>" +
            "   SELECT b.id,b.bucket_name,b.region,b.end_point,b.type,cid,b.acl,b.encrypted_type, " +
            "   b.global_size_limit,b.global_size_unit,b.limit_json,b.white_list, " +
            "   b.creation_date,b.last_update_date, b.created_by,  " +
            "   b.created_by_name,b.last_updated_by_name,b.last_updated_by,b.description, " +
            "   b.access_key_id,b.access_key_secret " +
            "FROM msp_osca_bucket b " +
            "     WHERE b.deleted = false  " +
            "   <when test=' type!=null and type.length() > 0 '>" +
            "     AND b.type = #{type} " +
            "    </when> " +
            "   <when test=' bucketName!=null and bucketName.length() > 0 '>" +
            "     AND b.bucket_name like CONCAT('%', #{bucketName}, '%') " +
            "    </when> " +
            "    order by b.last_update_date desc " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "region", column = "region"),
            @Result(property = "endPoint", column = "end_point"),
            @Result(property = "type", column = "type"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "acl", column = "acl"),
            @Result(property = "encryptedType", column = "encrypted_type"),
            @Result(property = "globalSizeLimit", column = "global_size_limit"),
            @Result(property = "globalSizeUnit", column = "global_size_unit"),
            @Result(property = "limitJson", column = "limit_json"),
            @Result(property = "whiteList", column = "white_list"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "lastUpdatedBy", column = "last_updated_by"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "description", column = "description"),
            @Result(property = "accessKeyId", column = "access_key_id"),
            @Result(property = "accessKeySecret", column = "access_key_secret")
    })
    Page<BucketVO> listBucket(@Param("type") String type, @Param("bucketName") String bucketName);

    @Insert(" insert into msp_osca_bucket(id,bucket_name,cid,region,end_point,type,access_key_id,access_key_secret," +
            " acl,encrypted_type, global_size_limit,global_size_unit,limit_json,white_list,creation_date," +
            " created_by,created_by_name,last_updated_by,last_updated_by_name,description) values " +
            " (#{id},#{bucketName},#{cid},#{region},#{endPoint},#{type},#{accessKeyId},#{accessKeySecret},#{acl}," +
            " #{encryptedType},#{globalSizeLimit}, #{globalSizeUnit},#{limitJson},#{whiteList}, " +
            " #{creationDate},#{createdBy},#{createdByName},#{lastUpdatedBy},#{lastUpdatedByName},#{description})")
    @SelectKey(keyProperty = "id", resultType = String.class, before = true,
            statement = "select replace(uuid(),'-','') as id from dual ")
    void insert(BucketEntity bucketEntity);

    @Select("<script>" +
            "   SELECT id,bucket_name,region,end_point,cid,type,acl,encrypted_type, " +
            "   access_key_id,access_key_secret,global_size_limit,global_size_unit,limit_json,white_list, " +
            "   deleted,DATE_FORMAT(creation_date,'%Y-%m-%d %H:%i:%s') creation_date," +
            "   DATE_FORMAT(last_update_date,'%Y-%m-%d %H:%i:%s') last_update_date,white_switch,global_switch,description " +
            "   FROM msp_osca_bucket " +
            "     WHERE deleted = false AND id = #{id} " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "region", column = "region"),
            @Result(property = "endPoint", column = "end_point"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "type", column = "type"),
            @Result(property = "acl", column = "acl"),
            @Result(property = "encryptedType", column = "encrypted_type"),
            @Result(property = "globalSizeLimit", column = "global_size_limit"),
            @Result(property = "globalSizeUnit", column = "global_size_unit"),
            @Result(property = "limitJson", column = "limit_json"),
            @Result(property = "whiteList", column = "white_list"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "whiteSwitch", column = "white_switch"),
            @Result(property = "globalSwitch", column = "global_switch"),
            @Result(property = "description", column = "description"),
            @Result(property = "accessKeyId", column = "access_key_id"),
            @Result(property = "accessKeySecret", column = "access_key_secret")
    })
    BucketEntity getOne(@Param("id") String id);

    @Update("<script>" +
            " update msp_osca_bucket " +
            " <trim prefix='set' suffixOverrides=','> " +
            "  <if test = 'limitJson!=null and limitJson.length()>0' >" +
            "    limit_json = #{limitJson}, " +
            "  </if>" +
            "  <if test = 'whiteList!=null and whiteList.length()>0' >" +
            "    white_list = #{whiteList} ," +
            "  </if>" +
            "  <if test = 'globalSwitch == 1 or globalSwitch == 0' >" +
            "    global_switch = #{globalSwitch} ," +
            "  </if>" +
            "  <if test = 'whiteSwitch == 1 or whiteSwitch == 0' >" +
            "    white_switch = #{whiteSwitch} ," +
            "  </if>" +
            "  <if test = 'endPoint!=null and endPoint.length()>0' >" +
            "    end_point = #{endPoint} ," +
            "  </if>" +
            "  <if test = 'accessKeySecret!=null and accessKeySecret.length()>0' >" +
            "    access_key_secret = #{accessKeySecret} ," +
            "  </if>" +
            "  <if test = 'accessKeyId!=null and accessKeyId.length()>0' >" +
            "    access_key_id = #{accessKeyId} ," +
            "  </if>" +
            "  <if test = 'acl!=null and acl.length()>0' >" +
            "    acl = #{acl} ," +
            "  </if>" +
            "  <if test = 'lastUpdatedBy!=null and lastUpdatedBy.length()>0' >" +
            "     last_updated_by = #{lastUpdatedBy}, " +
            "  </if>" +
            "  <if test = 'lastUpdatedByName!=null and lastUpdatedByName.length()>0' >" +
            "     last_updated_by_name = #{request.lastUpdatedByName}, " +
            "  </if>" +
            "   global_size_limit = #{globalSizeLimit}," +
            "   global_size_unit = #{globalSizeUnit} , " +
            "   encrypted_type = #{encryptedType}  ," +
            "   description = #{description},  " +
            " </trim> " +
            " where id = #{id}" +
            "</script>")
    void update(BucketEntity bucketEntity);


    @Select("<script>" +
            "   SELECT id,bucket_name,region,end_point,type,acl,encrypted_type, " +
            "   global_size_limit,global_size_unit,limit_json,white_list, " +
            "   deleted,DATE_FORMAT(creation_date,'%Y-%m-%d %H:%i:%s'), " +
            "   DATE_FORMAT(last_update_date,'%Y-%m-%d %H:%i:%s'),white_switch,global_switch FROM msp_osca_bucket " +
            "     WHERE deleted = false  " +
            "   <when test=' type!=null and type.length() > 0 '>" +
            "     AND type = #{type} " +
            "    </when> " +
            "   <when test=' bucketName!=null and bucketName.length() > 0 '>" +
            "     AND bucket_name = #{bucketName} " +
            "    </when> " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "region", column = "region"),
            @Result(property = "endPoint", column = "end_point"),
            @Result(property = "type", column = "type"),
            @Result(property = "acl", column = "acl"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "encryptedType", column = "encrypted_type"),
            @Result(property = "globalSizeLimit", column = "global_size_limit"),
            @Result(property = "globalSizeUnit", column = "global_size_unit"),
            @Result(property = "limitJson", column = "limit_json"),
            @Result(property = "whiteList", column = "white_list"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "whiteSwitch", column = "white_switch"),
            @Result(property = "globalSwitch", column = "global_switch"),
            @Result(property = "description", column = "description")
    })
    BucketEntity findByNameAndType(@Param("type") String type, @Param("bucketName") String bucketName);

    @Select("<script>" +
            "   SELECT id,bucket_name,region,end_point,type,cid,acl,encrypted_type, " +
            "   global_size_limit,global_size_unit,limit_json,white_list, " +
            "   deleted,creation_date,last_update_date,description FROM msp_osca_bucket " +
            "     WHERE deleted = false  " +
            "   <when test=' type!=null and type.length() > 0 '>" +
            "     AND type = #{type} " +
            "    </when> " +
            "    order by creation_date desc " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "bucketName", column = "bucket_name"),
            @Result(property = "region", column = "region"),
            @Result(property = "endPoint", column = "end_point"),
            @Result(property = "type", column = "type"),
            @Result(property = "cid", column = "cid"),
            @Result(property = "acl", column = "acl"),
            @Result(property = "encryptedType", column = "encrypted_type"),
            @Result(property = "globalSizeLimit", column = "global_size_limit"),
            @Result(property = "globalSizeUnit", column = "global_size_unit"),
            @Result(property = "limitJson", column = "limit_json"),
            @Result(property = "whiteList", column = "white_list"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
            @Result(property = "description", column = "description"),
    })
    List<BucketEntity> findByType(String type);

    @Update({"update msp_osca_bucket set deleted = 1 where cid = #{id} "})
    void batchDeleteByCid(@Param("id") String id);

    @Update("<script>" +
            " update msp_osca_bucket set deleted = 1 where id = #{id} " +
            "</script>")
    Integer delete(@Param("id") String id);
}

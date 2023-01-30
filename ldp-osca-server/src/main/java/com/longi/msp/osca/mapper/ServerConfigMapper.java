package com.longi.msp.osca.mapper;

import com.github.pagehelper.Page;
import com.longi.msp.osca.model.entity.ServerConfigEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author chenlei140
 * @className OssConfigMapper
 * @description 对象配置持久层
 * @date 2022/6/23 16:04
 */
@Mapper
public interface ServerConfigMapper {

    @Select("<script>" +
            "   SELECT id,user_alias,type,secret_key,access_key,end_point,deleted, " +
            "     description, creation_date,last_update_date FROM msp_osca_server_config " +
            "     WHERE id = #{id}  and deleted = 0 " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userAlias", column = "user_alias"),
            @Result(property = "type", column = "type"),
            @Result(property = "secretKey", column = "secret_key"),
            @Result(property = "accessKey", column = "access_key"),
            @Result(property = "endPoint", column = "end_point"),
            @Result(property = "description", column = "description"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
    })
    ServerConfigEntity getOne(@Param("id") String id);


    @Select("<script>" +
            "   SELECT id FROM msp_osca_server_config " +
            "     WHERE user_alias = #{userAlias} and type = #{type}  and deleted = 0 limit 1" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id")
    })
    ServerConfigEntity checkUserAlias(@Param("userAlias") String userAlias, @Param("type") String type);


    @Select("<script>" +
            "   SELECT id FROM msp_osca_server_config " +
            "     WHERE access_key = #{accessKey} and type = #{type}  and deleted = 0 limit 1" +
            "</script>")
    @Results({
            @Result(property = "id", column = "id")
    })
    ServerConfigEntity checkAccessKey(@Param("accessKey") String accessKey, @Param("type") String type);


    @Select("<script>" +
            "   SELECT id,user_alias,type,secret_key,access_key,end_point,description, " +
            "   deleted,created_by_name,created_by,last_updated_by_name,last_updated_by," +
            "   creation_date,last_update_date" +
            "   FROM msp_osca_server_config " +
            "     WHERE deleted = false " +
            "   <when test=' type!=null and type.length() > 0 '>" +
            "     AND type = #{type} " +
            "    </when> " +
            "    order by last_update_date desc " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userAlias", column = "user_alias"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "createdBy", column = "created_by"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "lastUpdatedBy", column = "last_updated_by"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
    })
    Page<ServerConfigEntity> list(@Param("type") String type);

    @Update("update msp_osca_server_config set deleted = true where id = #{id}")
    int delete(@Param("id") String id);

    @Insert("insert into msp_osca_server_config(id,user_alias,type,secret_key,access_key," +
            "end_point,description,policy,deleted,creation_date,last_update_date," +
            "created_by,created_by_name,last_updated_by,last_updated_by_name) values " +
            " (#{id},#{request.userAlias},#{request.type},#{request.secretKey},#{request.accessKey}, " +
            "#{request.endPoint},#{request.description},#{request.policy},#{request.deleted}," +
            "#{request.creationDate},#{request.lastUpdateDate},#{request.createdBy},#{request.createdByName}," +
            "#{request.lastUpdatedBy},#{request.lastUpdatedByName})")
    @SelectKey(keyProperty = "id", resultType = String.class, before = true,
            statement = "select replace(uuid(),'-','') as id from dual ")
    int insert(@Param("request") ServerConfigEntity request);

    @Update("<script>" +
            " update msp_osca_server_config " +
            " <trim prefix='set' suffixOverrides=','> " +
            " <if test = 'request.accessKey!=null and request.accessKey.length()>0' >" +
            " access_key = #{request.accessKey}, " +
            "  </if>" +
            " <if test = 'request.secretKey!=null and request.secretKey.length()>0' >" +
            " secret_key = #{request.secretKey}, " +
            "  </if>" +
            " <if test = 'request.endPoint!=null and request.endPoint.length()>0' >" +
            " end_point = #{request.endPoint}, " +
            "  </if>" +
            " <if test = 'request.userAlias!=null and request.userAlias.length()>0' >" +
            " user_alias = #{request.userAlias}, " +
            "  </if>" +
            "  <if test = 'request.lastUpdatedBy!=null and request.lastUpdatedBy.length()>0' >" +
            " last_updated_by = #{request.lastUpdatedBy}, " +
            "  </if>" +
            "  <if test = 'request.lastUpdatedByName!=null and request.lastUpdatedByName.length()>0' >" +
            " last_updated_by_name = #{request.lastUpdatedByName}, " +
            "  </if>" +
            "  description = #{request.description}, " +
            " </trim> " +
            " where id = #{request.id}" +
            "</script>")
    int update(@Param("request") ServerConfigEntity request);

    @Select("<script>" +
            "   SELECT id,user_alias,type,secret_key,access_key,end_point,description, " +
            "   deleted,creation_date,last_update_date FROM msp_osca_server_config " +
            "     WHERE deleted = false  " +
            "   <when test=' type!=null and type.length() > 0 '>" +
            "     AND type = #{type} " +
            "    </when> " +
            "</script>")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userAlias", column = "user_alias"),
            @Result(property = "type", column = "type"),
            @Result(property = "description", column = "description"),
            @Result(property = "deleted", column = "deleted"),
            @Result(property = "createdByName", column = "created_by_name"),
            @Result(property = "lastUpdatedByName", column = "last_updated_by_name"),
            @Result(property = "creationDate", column = "creation_date"),
            @Result(property = "lastUpdateDate", column = "last_update_date"),
    })
    List<ServerConfigEntity> getAll(@Param("type") String type);
}

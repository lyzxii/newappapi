package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.*;

/**
 * tb_push_device_data
 *
 * @author GJ
 * @create 2017-12-21 20:22
 **/
@Mapper
public interface PushDeviceDataMapper {
    @Select("select count(1) from tb_push_device_data where id = #{id}")
    int queryHistory(@Param("id") String id);

    @Update("update tb_push_device_data set channel = #{channel},tags = #{tag},updatedate = sysdate,username = #{uid} where id = #{id}")
    int updateFinalTime(@Param("channel") String channel, @Param("tag") String tag, @Param("uid") String uid, @Param("id") String id);

    @Insert("insert into tb_push_device_data(id,channel,tags,updatedate,username) values(#{id},#{channel},#{tag},sysdate,#{uid})")
    int insertTagRecord(@Param("id") String id, @Param("channel") String channel, @Param("tag") String tag, @Param("uid") String uid);

    @Update("update tb_push_device_data set updatedate = sysdate where id = #{id}")
    int updateTime(@Param("id") String id);

    @Insert("insert into tb_push_device_data(id,channel,tags,updatedate) values(#{id},#{channel},#{tag},sysdate)")
    int insertTag(@Param("id") String id, @Param("channel") String channel, @Param("tag") String tag);
}

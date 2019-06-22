package com.caiyi.lottery.tradesystem.ordercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by tiankun on 2018/1/2.
 */
@Mapper
public interface UserMapper {

    @Select("select cuserphoto from tb_user where cnickid = #{uid}")
    String queryUserPhoto(@Param("uid") String uid);
}

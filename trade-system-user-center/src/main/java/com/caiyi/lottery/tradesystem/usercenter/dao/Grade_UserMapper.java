package com.caiyi.lottery.tradesystem.usercenter.dao;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.Grade_UserPojo;

/**
 * 对应tb_user_grade,tb_user
 * @author A-0205
 *
 */

@Mapper
public interface Grade_UserMapper {
	
    //查询用户等级名称
    @Select("select t1.cgradename levelTitle,t1.ineedexper levelExper from tb_user_grade t1,tb_user t2 where t1.igradeid = t2.igradeid and t2.cnickid = #{uid}")
    Grade_UserPojo queryLevelTitle(@Param("uid") String uid);

    @Select("select ineedexper from tb_user_grade where igradeid = #{level}")
    String queryLevelExper(@Param("level") String level);
}

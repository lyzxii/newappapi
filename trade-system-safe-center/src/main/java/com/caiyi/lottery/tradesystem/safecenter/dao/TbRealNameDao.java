package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_id_card 表
 */
@Mapper
public interface TbRealNameDao {

    @Select("select rid as realnameId,realname from tb_realname where realname = #{realname} ")
    List<SafeBean> queryRealNameByNo(@Param("realname") String realname);

    @Select("select rid as realnameId,realname from tb_realname where rid = #{rid} ")
    List<SafeBean> queryRealNameByRid(@Param("rid") String rid);

    // seq_realname_rid.nextval
    @Modifying
    @Insert("insert into tb_realname(rid,realname) values(#{md5realname}, #{realname}) ")
    Integer addRealNameByNo(@Param("md5realname") String md5realname, @Param("realname") String realname);
}

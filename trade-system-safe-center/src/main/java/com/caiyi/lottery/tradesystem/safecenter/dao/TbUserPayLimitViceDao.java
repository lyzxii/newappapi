package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_user_pay_limit_vice 表
 */
@Mapper
public interface TbUserPayLimitViceDao {

    @Modifying
    @Insert("insert into tb_user_pay_limit_vice(lid,bankcard,usersource,cid) values(seq_user_pay_limit_vice.nextval,#{bankcard},#{usersource},#{cid}) ")
    Integer addUserPayLimitVice(@Param("bankcard") String bankcard, @Param("usersource") String usersource, @Param("cid") String cid);

    @Modifying
    @Update("update tb_user_pay_limit_vice set bankcard = #{bankcard} where cid = #{cid} and usersource = #{usersource} ")
    Integer updateUserPayLimitVice(@Param("bankcard") String bankcard, @Param("usersource") String usersource, @Param("cid") String cid);

    @Select("select count(cid) from tb_user_pay_limit_vice where cid = #{cid} and usersource = #{usersource} ")
    Integer getUserPayLimitCountByCid(@Param("cid") String cid, @Param("usersource") String usersource);

    @Select("select lid,bankcard,usersource,cid from tb_user_pay_limit_vice where cid = #{cid} and usersource = #{usersource}")
    List<SafeBean> queryUserPayLimitViceByCid(@Param("cid") String cid, @Param("usersource") String usersource);
}

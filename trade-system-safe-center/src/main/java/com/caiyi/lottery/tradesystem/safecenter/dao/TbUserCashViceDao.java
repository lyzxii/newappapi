package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_user_cash_vice 表
 */
@Mapper
public interface TbUserCashViceDao {

    @Modifying
    @Insert("insert into tb_user_cash_vice(cid,realname,bankcard,usersource,cashid) values(seq_user_cash_vice.nextval,#{realname},#{bankcard},#{usersource},#{cashid}) ")
    Integer addUserCashVice(@Param("realname") String realname, @Param("bankcard") String bankcard,@Param("usersource") String usersource, @Param("cashid") String cashid);

    @Modifying
    @Update("update tb_user_cash_vice set realname = #{realname}, bankcard = #{bankcard} where cashid = #{cashid} and usersource = #{usersource} ")
    Integer updateUserCashVice(@Param("realname") String realname, @Param("bankcard") String bankcard,@Param("usersource") String usersource, @Param("cashid") String cashid);

    @Select("select count(cashid) from tb_user_cash_vice where cashid = #{cashid} and usersource = #{usersource} ")
    Integer getUserViceCountByIcashid(@Param("cashid") String cashid, @Param("usersource") String usersource);

    @Select("select cid,realname,bankcard,usersource,cashid from tb_user_cash_vice where cashid = #{cashid} and usersource = #{usersource} ")
    List<SafeBean> queryUserCashViceByIcashid(@Param("cashid") String cashid,  @Param("usersource") String usersource);
}

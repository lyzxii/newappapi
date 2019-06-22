package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_umpay_protocol_vice 表
 */
@Mapper
public interface TbRechargeCardViceDao {

    // seq_umpay_protocol_vice.nextval
    @Modifying
    @Insert("insert into tb_recharge_card_vice(pid,mobileno,bankcard,usersource,nickid) values(#{md5bankcard},#{mobileno},#{bankcard},#{usersource},#{nickid}) ")
    Integer addRechargeCardVice(@Param("md5bankcard") String md5bankcard,@Param("mobileno") String mobileno, @Param("bankcard") String bankcard,@Param("usersource") String usersource, @Param("nickid") String nickid);

    @Modifying
    @Update("update tb_recharge_card_vice set mobileno = #{mobileno} where nickid = #{nickid} and usersource = #{usersource} and bankcard = #{bankcard}")
    Integer updateRechargeCardVice(@Param("mobileno") String mobileno, @Param("bankcard") String bankcard,@Param("usersource") String usersource, @Param("nickid") String nickid);

    @Select("select count(nickid) from tb_recharge_card_vice where nickid = #{nickid} and usersource = #{usersource} and bankcard = #{bankcard}")
    Integer getRechargeCardCountByCnickid(@Param("nickid") String nickid, @Param("usersource") String usersource, @Param("bankcard") String bankcard);

    @Select("select pid as rechargeCardId,mobileno,bankcard,usersource,adddate,nickid from tb_recharge_card_vice where pid = #{pid} and nickid=#{cnickid} and usersource=#{usersource}")
    List<SafeBean> queryRechargeCardViceByPid(@Param("pid") String pid, @Param("cnickid") String cnickid, @Param("usersource") String usersource);

    @Select("select pid as rechargeCardId,mobileno,bankcard,usersource,adddate,nickid from tb_recharge_card_vice where nickid = #{nickid} and usersource = #{usersource}")
    List<SafeBean> queryRechargeCardViceByNickid(@Param("nickid") String nickid, @Param("usersource") String usersource);

    @Select("select pid as rechargeCardId,mobileno,bankcard,usersource,adddate,nickid from tb_recharge_card_vice where nickid = #{nickid} and usersource = #{usersource} and bankcard = #{bankcard}")
    List<SafeBean> queryRechargeCardInfo(@Param("nickid") String nickid, @Param("usersource") String usersource, @Param("bankcard") String bankcard);

    @Select("select pid as rechargeCardId,mobileno,bankcard,usersource,adddate,nickid from tb_recharge_card_vice where nickid = #{nickid} and usersource = #{usersource} " +
            " and pid in (${rechargeIds} )")
    List<SafeBean> queryRechargeByRechargeId(@Param("nickid") String nickid, @Param("usersource") String usersource, @Param("rechargeIds") String rechargeIds);

    @Select("select pid as rechargeCardId from tb_recharge_card_vice where nickid = #{nickid} and usersource = #{usersource} and bankcard = #{bankcard}")
    List<SafeBean> getRechargeCardSequence(@Param("nickid") String nickid, @Param("usersource") String usersource, @Param("bankcard") String bankcard);
}

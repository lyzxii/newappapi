package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_user_vice 表
 */
@Mapper
public interface TbUserViceDao {

    @Modifying
    @Insert("insert into tb_user_vice(vid,realname,idcard,mobileno,bankcard,cardmobile,usersource,nickid) values(seq_user_vice.nextval,#{realname},#{idcard},#{mobileno},#{bankcard},#{cardmobile},#{usersource},#{nickid}) ")
    Integer addUserVice(@Param("realname") String realname, @Param("idcard") String idcard, @Param("mobileno") String mobileno, @Param("bankcard") String bankcard, @Param("cardmobile") String cardmobile, @Param("usersource") String usersource, @Param("nickid") String nickid);

    @Modifying
    Integer updateUserVice(@Param("realname") String realname, @Param("idcard") String idcard, @Param("mobileno") String mobileno, @Param("bankcard") String bankcard,@Param("cardmobile") String cardmobile, @Param("usersource") String usersource, @Param("nickid") String nickid);

    @Select("select count(nickid) from tb_user_vice where nickid = #{nickid} and usersource =#{usersource} ")
    Integer getUserViceCountByCnickid(@Param("nickid") String nickid, @Param("usersource") String usersource);

    @Select("select vid,realname,idcard,mobileno,bankcard,cardmobile,usersource,nickid from tb_user_vice where nickid = #{nickid} and usersource = #{usersource} ")
    List<SafeBean> queryUserViceByCnickid(@Param("nickid") String nickid, @Param("usersource") String usersource);

    @Select("select vid,realname,idcard,mobileno,bankcard,cardmobile,usersource,nickid from tb_user_vice where mobileno = #{mobileno} and usersource = #{usersource}")
    List<SafeBean> queryUserInfoByMobileno(@Param("mobileno") String mobileno, @Param("usersource") String usersource);

    @Select("select vid,realname,idcard,mobileno,bankcard,cardmobile,usersource,nickid from tb_user_vice where idcard = #{idcard} and usersource = #{usersource}")
    List<SafeBean> queryUserInfoByIdcard(@Param("idcard") String idcard, @Param("usersource") String usersource);
}

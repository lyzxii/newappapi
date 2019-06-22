package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_sms_vice 表
 */
@Mapper
public interface TbSmsViceDao {

    @Modifying
    @Insert("insert into tb_sms_vice(sid,mobileno,usersource,smsid) values(seq_sms_vice.nextval,#{mobileno},#{usersource},#{smsid}) ")
    Integer addSmsVice(@Param("mobileno") String mobileno,@Param("usersource") String usersource, @Param("smsid") String smsid);

    @Modifying
    @Update("update tb_sms_vice set mobileno = #{mobileno} where smsid = #{smsid} and usersource = #{usersource}")
    Integer updateSmsVice(@Param("mobileno") String mobileno,@Param("usersource") String usersource, @Param("smsid") String smsid);

    @Select("select count(smsid) from tb_sms_vice where smsid = #{smsid} and usersource = #{usersource} ")
    Integer getSmsCountByIsmsid(@Param("smsid") String smsid, @Param("usersource") String usersource);

    @Select("select sid,mobileno,usersource,smsid from tb_sms_vice where smsid = #{smsid} and usersource = #{usersource}")
    List<SafeBean> querySmsViceByIsmsid(@Param("smsid") String smsid, @Param("usersource") String usersource);
}

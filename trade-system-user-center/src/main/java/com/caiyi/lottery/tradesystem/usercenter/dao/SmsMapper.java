package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SmsMapper {
    @Select("select count(*) from tb_sms where ipaddr = #{ipAddr}  and to_char(sysdate,'yyyy-mm-dd') = to_char(cadddate,'yyyy-mm-dd')")
    int selectIpMsg(@Param("ipAddr") String ipAddr);

    @Insert("insert into tb_sms (ismsid,crecphone,ccontents,smstype,cagentid,ipaddr,cmobilenomd5) values (seq_sms.nextval, #{mobileNo}, #{smsContent},1, #{comeFrom}, #{ipAddr},#{mobilemd5})")
    int insertMsgByVoice(@Param("mobileNo") String mobileNo, @Param("smsContent") String smsContent, @Param("comeFrom") String comeFrom, @Param("ipAddr") String ipAddr, @Param("mobilemd5") String mobilemd5);

    @Insert("insert into tb_sms (ismsid,crecphone,ccontents,smstype,cagentid,ipaddr,cmobilenomd5) values (seq_sms.nextval,#{mobileNo}, #{smsContent},0,#{comeFrom}, #{ipAddr},#{mobilemd5})")
    int insertMsg(@Param("mobileNo") String mobileNo, @Param("smsContent") String smsContent, @Param("comeFrom") String comeFrom, @Param("ipAddr") String ipAddr, @Param("mobilemd5") String mobilemd5);

    /**
     * 查询短信验证码，临时使用
     * @param mobileNo
     * @return
     */
    @Select("select ccontents from tb_sms where cadddate > sysdate - 1 and crecphone = #{mobileNo} order by cadddate desc")
    List<String> selectSmsAutoCode(@Param("mobileNo")String mobileNo);
}

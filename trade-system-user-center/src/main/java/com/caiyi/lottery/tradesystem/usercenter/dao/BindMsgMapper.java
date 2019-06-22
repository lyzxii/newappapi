package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface BindMsgMapper {

    @Select("select count(*) from tb_bind_msg where itype = 1 and types = #{flag} and cmobilenomd5 = #{mobilemd5} and to_date(CADDDATE) = to_date(sysdate)")
    int selectMobileMsg(@Param("flag") int flag,@Param("mobilemd5") String mobilemd5);

    @Update("update tb_bind_msg set iverify=1 where cmobilenomd5=#{mobilemd5} and itype=1 and types=#{flag} and iverify=0")
    void updateOverDueMsg(@Param("mobilemd5") String mobilemd5, @Param("flag") int flag);

    @Insert("insert into tb_bind_msg (ibindid, itype, crec, cnickid, crandom, types, cmobilenomd5) values(SEQ_BIND_MSG.nextval, 1, #{mobileNo}, #{uid}, #{verycode}, #{flag}, #{mobilemd5})")
    int insertBindMsg(@Param("mobileNo") String mobileNo, @Param("verycode") String verycode, @Param("flag") int flag, @Param("uid") String uid, @Param("mobilemd5") String mobilemd5);

    @Update("update tb_bind_msg set counts=counts+1 where cmobilenomd5=#{mobilemd5} and crandom=#{yzm} and itype=1 and types=#{tid} and iverify=0 and cadddate>=sysdate-1")
    int updateBindMsgCount(@Param("mobilemd5") String mobilemd5, @Param("yzm") String yzm, @Param("tid") String tid);

    @Update("update tb_bind_msg set iverify=1 where cmobilenomd5=#{mobilemd5} and crandom=#{yzm} and itype=1 and types=#{tid} and iverify=0 and counts<=3 and cadddate>=sysdate-1")
    int updateBindMsg(@Param("mobilemd5") String mobilemd5, @Param("yzm") String yzm, @Param("tid") String tid);

    @Select(" select count(1) from tb_bind_msg where itype = 1 and types = 2 and iverify=1 and CMOBILENOMD5 = #{mobilenoMd5} and sysdate-CADDDATE < 20/60/24 ")
    int selectLt20MinMsg(@Param("mobilenoMd5")String mobilenomd5);

}

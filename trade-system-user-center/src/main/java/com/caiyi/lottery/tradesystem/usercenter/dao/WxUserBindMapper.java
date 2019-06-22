package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.WxUserBindPojo;

import java.util.List;

/**
 * tb_wx_user_bind
 */
@Mapper
public interface WxUserBindMapper {
    @Insert("insert into tb_wx_user_bind (COPENID,CUID,CUNIONID,CTYPE,CRETURNINFO) values (#{openid},#{nickid},#{unionid},1,#{returnInfo})")
    int insertWxBind(@Param("openid") String openid, @Param("nickid") String nickid, @Param("unionid") String unionid, @Param("returnInfo") String returnInfo);

    @Select("select cuid from tb_wx_user_bind where CUNIONID = #{unionid}")
    String queryNickidByUnionid(@Param("unionid") String unionid);

    @Select("select count(1) from tb_wx_user_bind where COPENID = #{openid} or CUID = #{uid} or CMOBILENOMD5 = #{mobileNoMD5}")
    int countByOpenidOrUid(@Param("openid") String openid, @Param("uid") String uid, @Param("mobileNoMD5") String mobileNoMD5);

    int countByUid(@Param("openid") String openid, @Param("uids")List<String> uids);

    @Insert("insert into tb_wx_user_bind (COPENID, CUID, CMOBILE, CUNIONID, CRETURNINFO, CMOBILENOMD5) values " +
                    "(#{pojo.openid,jdbcType=VARCHAR}, #{pojo.uid,jdbcType=VARCHAR}, #{pojo.mobileNo,jdbcType=VARCHAR}, " +
                    "#{pojo.unionid,jdbcType=VARCHAR}, #{pojo.returnInfo,jdbcType=VARCHAR}, #{pojo.mobilenoMD5,jdbcType=VARCHAR})")
    int insertBindUser(@Param("pojo") WxUserBindPojo pojo);
}

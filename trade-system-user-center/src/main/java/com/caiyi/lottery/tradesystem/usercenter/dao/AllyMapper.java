package com.caiyi.lottery.tradesystem.usercenter.dao;

import bean.AlipayLoginBean;
import org.apache.ibatis.annotations.*;

/**
 * 联合登入tb_ally
 *
 * @author GJ
 * @create 2017-12-17 13:35
 **/
@Mapper
public interface AllyMapper {

    @Update("update tb_ally set ihuodong = #{huodong}, ially_type = #{allyType}, creturninfo = #{result}  where nickid = #{nickid} and type = 1")
    Integer updateAliypayGreade(@Param("huodong") Integer huodong, @Param("allyType") Integer allyType, @Param("result") String result, @Param("nickid") String nickid);

    @Insert("insert into tb_ally(nickid,allyid,type,host,memo,apikeyid,cgender,cmobileno,cprovince,ccity,cavatar,crealname,cidcard,cusertype,creturninfo,CMOBILENOMD5,CREALNAMEMD5,CIDCARDMD5) " +
            "values (#{uid ,jdbcType=VARCHAR},#{aliypayid,jdbcType=VARCHAR},#{type,jdbcType=INTEGER},#{host,jdbcType=VARCHAR},#{pwdflag,jdbcType=VARCHAR}," +
            "#{partner,jdbcType=VARCHAR},#{gender,jdbcType=VARCHAR},#{mobileNo,jdbcType=VARCHAR},#{province,jdbcType=VARCHAR},#{city,jdbcType=VARCHAR}," +
            "#{avatar,jdbcType=VARCHAR},#{realName,jdbcType=VARCHAR},#{certNo,jdbcType=VARCHAR},#{allyType,jdbcType=INTEGER},#{returnInfo,jdbcType=VARCHAR}," +
            "#{md5Mobile,jdbcType=VARCHAR},#{md5RealName,jdbcType=VARCHAR},#{md5IdCard,jdbcType=VARCHAR})")
    int insertIntoTbAlly(AlipayLoginBean bean);

    @Select("select count(1) from tb_ally where nickid = #{uid} and istate = 0")
    int queryCaiyiCountBindAlly(@Param("uid") String uid);

    @Select("select type from tb_ally where nickid = #{uid}")
    String queryTypeAlly(@Param("uid") String uid);

}

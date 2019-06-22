package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.UserPhotoCashPojo;


/**
 * @author wxy
 * @create 2017-11-27 14:08
 **/
@Mapper
public interface UserPhotoCashMapper {

    @Select("SELECT cnickid as nickid,cuploadphoto as userImg ,cstatus as status,cadddate as addDate,crbackflag as rebackFlag" +
            " FROM (SELECT * FROM tb_user_photo_cash t" +
            " where t.cnickid = #{nickid} order by t.cadddate desc) where 1=1 and rownum =1 ")
    UserPhotoCashPojo queryUserPhotoStatus(@Param("nickid") String nickid);

    @Select("SELECT count(1) FROM tb_user_photo_cash  a where a.cnickid = #{cnickid}  and a.cstatus = '0' ")
    int getPhotoCash0Num(@Param("cnickid") String cnickid);

    @Select("SELECT count(1) FROM tb_user_photo_cash  a where a.cnickid = #{cnickid}  and a.cstatus = '1' and a.cadddate > to_date(#{beginDate},'yyyy-mm-dd') ")
    int getPhotoCash1Num(@Param("cnickid") String cnickid, @Param("beginDate") String beginDate);

    @Select("SELECT count(1) FROM tb_user_photo_cash  a where a.cnickid = #{cnickid}  and a.cstatus = '2' and a.cadddate > to_date(#{beginDate},'yyyy-mm-dd') ")
    int getBefore15PhotoCashNum(@Param("cnickid") String cnickid, @Param("beginDate") String beginDate);

    @Select("SELECT min(a.cadddate) adddate  FROM tb_user_photo_cash  a where a.cnickid = #{cnickid}  and a.cstatus  = '1' and a.cadddate > to_date(#{beginDate},'yyyy-mm-dd') ")
    String getPhotoInfo(@Param("cnickid") String cnickid,@Param("beginDate") String beginDate);

    @Insert("insert into tb_user_photo_cash(cid,cnickid,cuploadphoto,cadddate,cstatus) values(#{cid},#{cnickid},#{cuploadphoto},sysdate,#{cstatus})")
    int addUserPhoto(@Param("cid") String cid, @Param("cnickid") String cnickid, @Param("cuploadphoto") String cuploadphoto, @Param("cstatus") String cstatus);


}

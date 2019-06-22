package com.caiyi.lottery.tradesystem.activitycenter.dao;

import activity.pojo.ForcastMatchPojo;
import activity.pojo.ForcastShareUserPojo;
import org.apache.ibatis.annotations.Insert;
import activity.pojo.ForcastUserPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import activity.pojo.ForcastPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 拉新活动-预测比分
 *
 * @author GJ
 * @create 2018-04-23 10:03
 **/
@Mapper
public interface ForcastMapper {
    @Select("select count(1) from tb_activity_forcast_share_user where CNICKID =#{nickid}")
    int getShareUserCount(@Param("nickid") String nickid);
    @Insert("insert into tb_activity_forcast_share_user(CNICKID,CAPPCLIENT,ISOURCE,CIPADDRESS,CMOBILETYPE,CADDTIME) values(#{nickid},#{appclient},#{source},#{ipaddress},#{mobiletype},sysdate)")
    int addShareUser(ForcastShareUserPojo forcastShareUserPojo);

    @Select("select citemid itemid,cmname mname,csname sname,cmatchtime matchtime,cendtime endtime from tb_activity_forcast_match where icurrentmatch=1")
    ForcastMatchPojo getCurrnetForcastMatch();

    @Select("select cuserphoto userphoto from tb_activity_forcast_share_user  where cnickid=#{nickid}")
    String getShareUserImg(@Param("nickid") String nickid);
    @Select("select sum(IFORCASTNUM) forcastNum ,sum(IGETFORCASTAWARDNUM) forcastAwardNum from tb_activity_forcast_share where cnickid=#{nickid}")
    ForcastPojo getForcastNum(@Param("nickid") String nickid);

    //###################

    /**
     * 查询用户分享的比赛
     *
     * @param nickid
     * @return
     */
    @Select("SELECT M.IMATCHID AS MATCHID, M.CITEMID AS ITEMID,M.CMNAME AS HOMETEAMNAME,M.CSNAME AS AWAYTEAMNAME," +
                    "M.CMATCHNAME AS MATCHNAME,M.CMSCORE AS HOMESCORE,M.CSSCORE AS AWAYSCORE,M.IRESULT AS MATCHRESULT," +
                    "S.IFORCASTNUM AS FORCASTNUM,S.IGETFORCASTAWARDNUM AS FORCASTAWARDNUM,S.IUSEFORCASTAWARDNUM AS USEFORCASTAWARDNUM,S.IHAVEAWARD as HAVEAWARD" +
                    " FROM TB_ACTIVITY_FORCAST_SHARE S LEFT JOIN TB_ACTIVITY_FORCAST_MATCH M ON S.IMATCHID=M.IMATCHID " +
                    "WHERE S.CNICKID=#{nickid} AND SYSDATE > M.CENDTIME")
    List<ForcastPojo> queryMatchesByNickid(@Param("nickid") String nickid);

    /**
     * 查询用户分享的比赛
     *
     * @param nickid
     * @param matchid
     * @return
     */
    @Select("SELECT M.IMATCHID AS MATCHID, M.CITEMID AS ITEMID,M.CMNAME AS HOMETEAMNAME,M.CSNAME AS AWAYTEAMNAME," +
                    "M.CMATCHNAME AS MATCHNAME,M.CMSCORE AS HOMESCORE,M.CSSCORE AS AWAYSCORE,M.IRESULT AS MATCHRESULT," +
                    "S.IFORCASTNUM AS FORCASTNUM,S.IGETFORCASTAWARDNUM AS FORCASTAWARDNUM,S.IUSEFORCASTAWARDNUM AS USEFORCASTAWARDNUM,S.IHAVEAWARD as HAVEAWARD" +
                    " FROM TB_ACTIVITY_FORCAST_SHARE S LEFT JOIN TB_ACTIVITY_FORCAST_MATCH M ON S.IMATCHID=M.IMATCHID " +
                    "WHERE S.CNICKID=#{nickid} AND M.IMATCHID=#{matchid}")
    ForcastPojo queryMatchByNickidAndMatchid(@Param("nickid") String nickid, @Param("matchid") Long matchid);

    /**
     * 查询好友竞猜列表
     * @param nickid
     * @return
     */
    @Select("select copenid as openid,cnick_name as nickName,cmobile as mobile,chead_img_url as userImgUrl," +
                    "IISREGIST as isRegist,IMATCHID as matchId,ACDD_TIME as addTime,CITEMID as itemId,iisnew as isNew,IISLOGIN as isLogin ,iforcast_content as forcastContent " +
                    "from tb_activity_forcast_user where cshare_user=#{nickid}")
    List<ForcastUserPojo> queryForcastUser(@Param("nickid") String nickid);
}

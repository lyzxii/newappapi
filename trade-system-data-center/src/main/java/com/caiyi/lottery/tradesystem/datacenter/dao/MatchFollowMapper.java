package com.caiyi.lottery.tradesystem.datacenter.dao;


import data.pojo.MatchFollowPojo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * tb_match_follow
 * @author GJ
 * @create 2018-01-18 10:24
 **/
@Mapper
public interface MatchFollowMapper {

    @Select("select imatchid,igameid,igametype from tb_match_follow where istatus=1 and cnickid=#{nickic} and cadddate>sysdate-3 order by igameid,igametype,cexpect,isort")
    List<MatchFollowPojo> queryMatchFollow(@Param("nickic") String nickic);

    @Select("select count(0) count from tb_match_follow where cnickid=#{uid} and imatchid=#{rid} and igameid=#{gid} and igametype=#{gameType}")
    int queryFocusMatch(@Param("uid") String uid,@Param("rid") String rid, @Param("gid")String gid,@Param("gameType") Integer gameType);

    @Delete("delete from tb_match_follow where igameid=#{gameid} and imatchid=#{matchId} and cnickid=#{uid}")
    int deletFocus(@Param("gameid") String gameid, @Param("matchId") String matchId, @Param("uid") String uid);

    @Insert("insert into tb_match_follow (cnickid,imatchid,igameid,cexpect,isort,igametype) values (#{uid},#{matchId},#{gameid},#{expect},#{sort},#{gametype})")
    int insertFocus(@Param("uid") String uid, @Param("matchId") String matchId,  @Param("gameid")String gameid,  @Param("expect")String expect,  @Param("sort")String sort,  @Param("gametype")Integer gameType);

    @Select("select count(0) count from tb_match_follow where cnickid=#{uid} and imatchid=#{rid} and igametype=#{gameType}")
    int queryFocusBaskMatch(@Param("uid") String uid,@Param("rid") String rid, @Param("gameType") Integer gameType);
}

package com.caiyi.lottery.tradesystem.tradecenter.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;



//对应tb_match_follow表
@Mapper
public interface MatchFollowMapper {

	//用户是否关注过该场次
	@Select("select count(1) count from tb_match_follow where cnickid=#{uid} and imatchid=#{matchid} and igameid=#{gid}")
	int isFollowMatch(@Param("uid")String uid, @Param("matchid")String matchid, @Param("gid")String gid);
	
	//插入比赛关注
	@Insert("insert into tb_match_follow (cnickid,imatchid,igameid,cexpect,isort,igametype) "
			+ "values (#{uid},#{matchid},#{gid},#{expect},#{sort},#{gametype})")
	int insertMatchFollow(@Param("uid")String uid, @Param("matchid")String matchid, @Param("gid")String gid, 
			@Param("expect")String expect,@Param("sort")String sort, @Param("gametype")String gametype);
}

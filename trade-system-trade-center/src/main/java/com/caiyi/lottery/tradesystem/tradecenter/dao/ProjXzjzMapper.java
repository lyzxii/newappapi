package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


//对应tb_proj_xzjz表
@Mapper
public interface ProjXzjzMapper {
	@Insert("insert into tb_proj_xzjz(cprojid,cgameid,ccodes,codelist,cadddate) values(#{hid},#{gid},#{codes},#{codelist},sysdate)")
	int insertXzjzRecord(@Param("hid")String hid, @Param("gid")String gid,@Param("codes")String codes,@Param("codelist")String codelist);
}

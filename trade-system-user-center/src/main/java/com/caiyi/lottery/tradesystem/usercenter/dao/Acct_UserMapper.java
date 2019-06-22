package com.caiyi.lottery.tradesystem.usercenter.dao;



import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.Acct_UserPojo;


/**
 * tb_user_acct和tb_user表
 * @author A-0205
 *
 */
@Mapper
public interface Acct_UserMapper {

	@Select("select a.iexperience expir,a.ipoint as userpoint,b.cuserphoto as userImg, b.igradeid as gradeid" +
			" from tb_user_acct a,tb_user b where a.cnickid = b.cnickid and a.cnickid = #{nickid} ")
	Acct_UserPojo queryIpointAndUserPhoto(@Param("nickid") String nickid);

	public Acct_UserPojo queryMlotteryData(String username);

	@Select("select a.imobbind mobbindFlag,a.cidcard idcard,a.crealname realName,a.cbankcard drawBankCard,a.cagentid agentid,b.ibalance balance,nvl((b.idaigou + b.izhuihao),0) as amount,a.iopen as whitegrade " +
			"from tb_user a,tb_user_acct b where a.cnickid = b.cnickid and  a.cnickid= #{uid}")
    Acct_UserPojo queryUserAccountInfo(String uid);
}

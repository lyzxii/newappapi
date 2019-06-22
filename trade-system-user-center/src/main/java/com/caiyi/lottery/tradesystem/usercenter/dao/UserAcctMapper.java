package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;
import pojo.UserAcctPojo;

/**
 * @Author: wxy
 * @Date: created in 14:01 2017/12/5
 * @Description: tb_user_acct表
 */
@Mapper
public interface UserAcctMapper {

    @Select("select cnickid as \"uid\", ibalance as balance, alldrawmoney as allDrowMoney from tb_user_acct where cnickid = #{nickid}")
    UserAcctPojo getBalanceByNickid(@Param("nickid") String nickid);

    @Insert("insert into tb_user_acct (cnickid) values (#{nickid})")
    int insertWithNickid(@Param("nickid") String nickid);


    @Select("select nvl(sum(idaigou+ihemai+izhuihao),0) total from tb_user_acct where cnickid = #{uid}")
    String cannotSign(@Param("uid") String uid);

    @Select("select ipoint as userpoint from tb_user_acct where cnickid = #{nickid}")
    UserAcctPojo getUserPoint(@Param("nickid") String nickid);

    @Update("update tb_user_acct t set t.ipoint = t.ipoint+#{ipoint} where cnickid = #{uid}")
    int addUserPoint(@Param("ipoint") int ipoint, @Param("uid") String uid);

    @Update("update tb_user_acct t set t.ipoint = t.ipoint-#{ipoint} where cnickid = #{uid}")
    int decreaseUserPoint(@Param("ipoint")int ipoint,@Param("uid")String uid);

   // 查询可提款金额上海导购用户 ,对应u_query_23_shyfk
    @Select("select cnickid,ibalance,(iaward + ihmaward + ihmget + izhaward) - icash dmoney, 0 rmoney  from tb_user_acct where cnickid = #{uid}")
    UserAcctPojo getAvilable(@Param("uid") String uid);
}

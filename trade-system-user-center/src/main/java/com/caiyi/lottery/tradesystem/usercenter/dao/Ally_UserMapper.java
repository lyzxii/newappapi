package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.Ally_UserPojo;

import java.util.List;

/**
 * 支付宝快登Mapper
 *
 * @author GJ
 * @create 2017-12-14 20:53
 **/
@Mapper
public interface Ally_UserMapper {

    @Select("select tu.cnickid as \"uid\",tu.cpassword as pwd,tu.cmobileno as mobileNo,tu.cmobilenomd5 as mobilenomd5 ,tu.imobbind as mobileBind from tb_ally ta " +
            "left join tb_user tu on ta.nickid=tu.cnickid where ta.allyid =#{alipayUserid} and ta.type=1 and  tu.itype=0 and ta.istate=0")
    List<Ally_UserPojo> queryAllyBindCaiyiAccount(@Param("alipayUserid") String alipayUserid);


    @Select("select count(tu.cnickid) from tb_ally ta left join tb_user tu on ta.nickid=tu.cnickid where allyid =#{alipayUserid} and ta.type=1 and  tu.itype=0 and ta.istate=0")
    int queryAllyBindCaiyiCount(@Param("alipayUserid") String alipayUserid);
}

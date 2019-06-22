package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.UserChargePojo;

import java.util.List;

/**
 * 用于 tb_user_charge 表
 */
@Mapper
public interface UserChargeMapper {
    List<UserChargePojo> getChargeByNickidAndDate(@Param("uid") String uid, @Param("stime") String stime, @Param("etime") String etime, @Param("flag") Integer flag);

    @Select("select count(ICHARGEID) CHARGEID from tb_user_charge where ITYPE=0 and IBIZTYPE=200 and CNICKID =#{uid}")
    int countUserCharge(String uid);

    @Select("select count(1) from tb_user_charge t where t.itype=1 and cnickid=#{nickid} and cadddate>sysdate-365")
    int countOutByNickidInOneYear(@Param("nickid") String nickid);

    @Select("select count(cnickid) from tb_user_charge where cnickid =#{uid} and (ibiztype = 100 or ibiztype = 101) and itype = 1")
    Integer countSelfBuy(String uid);
}

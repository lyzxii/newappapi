package com.caiyi.lottery.tradesystem.ordercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by tiankun on 2017/12/22.
 * tb_proj_xx表
 */
@Mapper
public interface ProjBuyMapper {

    @Select("select count(1) from tb_proj_buy_${gid} where cnickid=#{uid} and icancel=0")
    int queryProjNumByUid(@Param("gid") String gid, @Param("uid") String uid);

    @Update("UPDATE tb_proj_buy_${gid} SET ihide=1,chidedate=sysdate where ibuyid=#{bid}")
    int updateProjBuy(@Param("gid")String gid,@Param("bid")String bid);

    @Update("UPDATE tb_proj_buy_${gid} SET ihide=1,chidedate=sysdate where cprojid=#{hid}")
    int updateProjBuyByHid(@Param("gid")String gid,@Param("hid")String hid);
}

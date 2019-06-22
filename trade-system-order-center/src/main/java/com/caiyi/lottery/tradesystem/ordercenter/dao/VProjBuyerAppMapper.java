package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.dto.ProjDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 视图
 */
@Mapper
public interface VProjBuyerAppMapper {

    @Select("select ibuyid buyid from v_proj_buyer_app where cprojid=#{did}")
    List<ProjDTO> queryBuyid(@Param("did") String did);
    
    @Select("select count(1) from v_proj_buyer_app where ireturn < 2 and icancel=0 and cnickid = #{uid}")
    int countUnbeginNum(@Param("uid") String uid);
}

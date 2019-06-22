package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ProjPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 视图
 * v_proj/v_proj_xx
 */
@Mapper
public interface VProjMapper {

    @Select("select v.cgameid, v.cprojid, v.itmoney, v.iagnum, v.iaunum, v.cadddate, v.itype, v.iopen, v.cnickid, v.cuserid, v.istate, v.icast, v.cwininfo, v.ibonus, v.iaward from v_proj v where v.cperiodid = #{perid} and v.allid = #{type} and v.ibonus>0")
    List<ProjPojo> selectByDiffType(@Param("perid") String perid, @Param("type") String type);

    @Select("select v.cgameid, v.cprojid, v.itmoney, v.iagnum, v.iaunum, v.cadddate, v.itype, v.iopen, v.cnickid, v.userid, v.istate, v.icast, v.cwininfo, v.ibonus, v.iaward from tb_proj_${gameid} v where v.cperiodid = #{perid} and v.ibonus>0")
    List<ProjPojo> selectByGameid(@Param("gameid") String gameid, @Param("perid") String perid);

    @Select("select v.cgameid, v.cprojid, v.itmoney, v.iagnum, v.iaunum, v.cadddate, v.itype, v.iopen, v.cnickid, v.cuserid, v.istate, v.icast, v.cwininfo, v.ibonus, v.iaward from v_proj v where v.cperiodid = #{perid} and v.allid = #{type}")
    List<ProjPojo> selectByDiffBdType(@Param("perid") String perid, @Param("type") String type);

    @Select("select v.cgameid, v.cprojid, v.itmoney, v.iagnum, v.iaunum, v.cadddate, v.itype, v.iopen, v.cnickid, v.userid cuserid, v.istate, v.icast, v.cwininfo, v.ibonus, v.iaward from tb_proj_${gameid} v where v.cperiodid = #{perid} ")
    List<ProjPojo> selectByBdGameid(@Param("gameid") String gameid,@Param("perid") String perid);
}

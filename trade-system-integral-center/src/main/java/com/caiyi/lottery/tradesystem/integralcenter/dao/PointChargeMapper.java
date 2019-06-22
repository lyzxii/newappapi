package com.caiyi.lottery.tradesystem.integralcenter.dao;

import integral.pojo.PointsCharge;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * tb_point_charge表
 */
@Mapper
public interface PointChargeMapper {

    @Insert("insert into tb_point_charge(ipointid,cnickid,ipoint,itype,cmemo,cadddate,ibiztype,ioldpoint,ibalance,isource,igradeid) " +
            "values(seq_point_charge.nextval,#{cnickid},#{ipoint},#{itype},#{cmemo},sysdate,#{ibiztype},#{ioldpoint},#{ibalance},#{source},#{igradeid})")
    int insertPointCharge(PointsCharge pointCharge);


}

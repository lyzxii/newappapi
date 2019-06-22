package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import redpacket.bean.RedpacketCharge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * tb_redpacket_charge表
 */
@Mapper
public interface RedpacketChargeMapper {
    /**
     * 查询红包流水分页
     * @param cupacketid
     * @return
     */
    @Select("select t.ioperate,t.cupacketid,t.cnickid,t.cgameid,t.imoney,t.itype, to_char(t.cadddate,'yyyy-MM-dd HH24:mi:ss') \n" +
            "cadddate,t.ibiztype,t.ioldmoney,t.ibalance,t.cprojid,t.cmemo,t.ibmoney from tb_redpacket_charge t " +
            "where 1=1 and cupacketid=#{cupacketid} and ibiztype!=260")
    List<RedpacketCharge> queryRedPacketCharge(String cupacketid);

    @Select("select count(cupacketid) from tb_redpacket_charge t where cnickid =#{uid} and (ibiztype = 100 or ibiztype = 101)")
    Integer countGroupBuy(String uid);
}

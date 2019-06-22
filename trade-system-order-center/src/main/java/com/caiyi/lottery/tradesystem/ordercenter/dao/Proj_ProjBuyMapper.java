package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.bean.BetRecordBean;
import order.dto.ProjDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Proj_ProjBuyMapper {

    @Select("select t.ibuyid,t.cnickid,t.ihide,t.icancel,t2.ireturn from tb_proj_buy_${gid} t,tb_proj_${gid} t2 where t.ibuyid=#{bid} and t.cprojid=t2.cprojid")
    List<ProjDTO> selectBuyStatus(@Param("gid")String gid, @Param("bid")String bid);

    @Select("select t.ibuyid,t.cnickid,t.ihide,t.icancel,t2.ireturn from tb_proj_buy_${gid} t,tb_proj_${gid} t2 where t.cprojid=#{hid} and t.cprojid=t2.cprojid")
    List<ProjDTO> selectBuyStatusByHid(@Param("gid")String gid, @Param("hid")String hid);

    List<BetRecordBean> queryBuyByLotid(@Param("stime")String stime, @Param("etime")String etime,  @Param("uid")String uid, @Param("aid")String aid, @Param("rid")String rid, @Param("newValue")String newValue, @Param("tid")String tid, @Param("gidCondition")String gidCondition);
}

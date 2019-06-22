package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.bean.AllRecordBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 视图 v_all_proj_buyer_app
 */
@Mapper
public interface VAllProjBuyerAppMapper {

    List<AllRecordBean> queryAllRecord(@Param("stime")String stime,@Param("etime")String etime, @Param("uid") String uid,@Param("gidCondition")String gidCondition);
}

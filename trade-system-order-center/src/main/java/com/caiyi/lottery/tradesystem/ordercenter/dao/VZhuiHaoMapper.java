package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.bean.OrderBean;
import order.bean.ZhuihaoRecordBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 视图 v_zhuihao
 */
@Mapper
public interface VZhuiHaoMapper {

    List<ZhuihaoRecordBean> queryZhuihaoRecord(@Param("stime") String stime,@Param("etime")String etime, @Param("uid")String uid, @Param("qtype")String qtype, @Param("newValue")String newValue);
}

package com.caiyi.lottery.tradesystem.paycenter.dao;


import java.util.List;

import org.apache.ibatis.annotations.*;

import pay.bean.PayBean;
import pay.pojo.RechargeWayPojo;


//对应tb_recharge_way
@Mapper
public interface RechargeWayMapper {
	//根据channel+product+key组成的主键查询
	public RechargeWayPojo queryRechWayByPK(PayBean bean);
	
	//根据分类查询充值渠道
	public List<RechargeWayPojo> queryOpenRechWayByCategory(String category);
	
}

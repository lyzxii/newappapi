package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pay.bean.PaySftBean;

/**
 * 对应b_shengpay_order
 */
@Mapper
public interface ShengpayOrderMapper {

    @Insert("insert into tb_shengpay_order (CMERCHANTORDERNO,CSFTORDERNO,CSESSIONTOKEN,CNICKID,IORDERMONEY,IPAYMONEY,ISTATE,CORDERCREATETIME,CMEMO,CADDDATE,CBANKID) " +
            "values (#{applyid},#{sftOrderNo},#{sessionToken},#{uid},#{addmoney},#{amount},0,to_date(#{orderCreateTime},'yyyyMMddHH24miss'),#{desc},sysdate,#{bankid})")
    int saveShengpayOrderInfo(PaySftBean bean);
}

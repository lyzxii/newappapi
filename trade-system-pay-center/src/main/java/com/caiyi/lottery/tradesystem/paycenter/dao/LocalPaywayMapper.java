package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import pay.pojo.LocalPaywayPojo;

//tb_local_payway
@Mapper
public interface LocalPaywayMapper {

    @Select("select cchannelcode channelcode,cproducttype producttype from tb_local_payway where cpaymentway = ?")
    LocalPaywayPojo queryChannelAndProduct(String bankid);

}

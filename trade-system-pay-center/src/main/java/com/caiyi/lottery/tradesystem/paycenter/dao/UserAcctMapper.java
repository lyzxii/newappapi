package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pay.pojo.UserAcctPojo;

import java.util.List;

/**
 * 对应tb_user_acct
 * Created by XQH on 2017/12/28.
 */
@Mapper
public interface UserAcctMapper {
    /**
     * 提款-手续费检测,小于100元收取2元手续费(优先从不可提款余额收取),大于100则不收手续费
     */
    @Select("select ibalance - #{tkMoney} as ibalance,nodrawmoney from tb_user_acct where cnickid = #{cnickid}")
    List<UserAcctPojo> checkBrokerage(@Param("tkMoney")Double tkMoney, @Param("cnickid")String cnickid);

    /**
     * 提款-合法性校验
     */
    @Select("select ibalance,nodrawmoney, alldrawmoney from tb_user_acct where cnickid = #{cnickid}")
    List<UserAcctPojo> checkDrawMoneyValidity(@Param("cnickid")String cnickid);

}

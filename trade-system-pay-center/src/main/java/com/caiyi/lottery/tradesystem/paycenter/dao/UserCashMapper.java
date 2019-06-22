package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pay.pojo.UserCashPojo;
import pojo.UserPojo;

import java.util.HashMap;

/**
 * Created by XQH on 2017/12/26.
 */
@Mapper
public interface UserCashMapper {

    /**
     * 获取用户当天的提款次数
     * @param nickid 用户昵称
     * @return 用户当天的提款次数
     */
    @Select("select count(1) num from tb_user_cash where isuccess in (0,1,4,5,7,8,11,12) and cnickid = #{nickid} and to_char(ccashdate,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')")
    int getTakeMoneyDailyNum(String nickid);

    @Select("select cbankcode bankCode，cuserid cuserId,cadddate addDate,crealname realName,cidcard idcard from tb_user where cnickid = #{nickid}")
    UserPojo findRealName(String nickid);

    /**
     * 用户提现调用存储过程
     * @param map
     */
    void userDrawMoney(HashMap map);

    @Select("select icashid ,imoney ,irate ,ccashdate ,isuccess ,cmemo ,cconfdate,cpredicttime  from tb_user_cash  where  icashid = #{icashid,jdbcType=VARCHAR} ")
    UserCashPojo findDrawMoneyStatus(String icashid);
}

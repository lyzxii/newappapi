package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.UserCashPojo;

import java.util.List;

/**
 * 用于 tb_user_cash 表
 */
@Mapper
public interface UserCashMapper {
    @Select("select icashid cashId,imoney money,irate rate,ccashdate cashTime,istate state,isuccess success,cmemo memo,\n" +
                    "\t\t  cconfdate confTime,cpredicttime predtictTime  from tb_user_cash  \n" +
                    "\t\twhere cnickid = #{uid} and ccashdate >= to_date(#{stime},'yyyy-mm-dd') and ccashdate <= to_date(#{etime},'yyyy-mm-dd')+1 order by ccashdate desc")
    List<UserCashPojo> getCashByNickidAndDate(@Param("uid") String uid, @Param("stime") String stime, @Param("etime") String etime);

    /**
     * 查询提款进度
     * @param uid 用户昵称
     * @param cashid 流水号
     * @return
     */
    @Select("select icashid cashid,imoney money,irate rate,ccashdate cashTime,istate state,isuccess success,cmemo memo,\n" +
                    "            cconfdate confTime,cpredicttime predtictTime from tb_user_cash  \n" +
                    "        where cnickid = #{uid} and icashid = #{cashid}")
    UserCashPojo getCashByNickidAndCashid(@Param("uid") String uid, @Param("cashid") Integer cashid);

    /**
     * 获取用户正在进行中的提款
     * @param nickid 用户昵称
     * @return 用户正在进行中的提款数
     */
    @Select("select count(1) num from tb_user_cash where isuccess in (0,4,7,11) and cnickid = #{nickid}")
    int getProgressTakeMoney(@Param("nickid") String nickid);

    /**
     * 获取用户当天的提款次数
     * @param nickid 用户昵称
     * @return 用户当天的提款次数
     */
    @Select("select count(1) num from tb_user_cash where isuccess in (0,1,4,5,7,8,11,12) and cnickid = #{nickid} and to_char(ccashdate,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd')")
    int getTakeMoneyDailyNum(@Param("nickid") String nickid);

}

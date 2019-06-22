package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 对应tb_recharge_card 和 tb_recharge_card_channel连表查
 *
 */
@Mapper
public interface RechargeCard_Channel {
    //查询协议显示状态
    @Select("select count(1) from tb_recharge_card t1,tb_recharge_card_channel t2 where t1.cnickid=t2.cnickid " +
            "and t1.csafekey=t2.csafekey and t1.cnickid=#{uid} and t1.csafekey=#{safeKey}\n" +
            "and cstatus = '1' and cuserpayid is not null and cchannel = #{channel}")
    int queryProtocolStatus(@Param("uid") String uid, @Param("safeKey") String safeKey, @Param("channel") String channel);
}

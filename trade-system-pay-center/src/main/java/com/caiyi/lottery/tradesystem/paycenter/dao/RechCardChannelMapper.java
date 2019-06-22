package com.caiyi.lottery.tradesystem.paycenter.dao;


import org.apache.ibatis.annotations.*;

import pay.bean.PayBean;
import pay.pojo.RechCardChannelPojo;

import java.util.List;


//对应tb_recharge_card_channel
@Mapper
public interface RechCardChannelMapper {

    //插入用户充值银行卡渠道
    @Insert("insert into tb_recharge_card_channel (CNICKID,CSAFEKEY,CCHANNEL) values(#{uid,jdbcType=VARCHAR},#{safeKey,jdbcType=VARCHAR},#{channel,jdbcType=VARCHAR})")
    public int insertRechCardChannel(PayBean bean);

    //查询充值成功的渠道数
    @Select("select count(1) from tb_recharge_card_channel where cnickid = #{uid} and csafekey = #{safekey} and cuserpayid is not null")
    public int countSuccessChannel(@Param("uid") String uid, @Param("safekey") String safeKey);

    @Select("select count(1) from tb_recharge_card_channel where cnickid = #{uid} and csafekey = #{safeKey} and cchannel = #{channel}")
    public int countUserChannelCard(PayBean bean);

    //更新充值协议
    @Update("update tb_recharge_card_channel set cmerid = #{dealid},cuserbusiid= #{userbusiid},cuserpayid = #{userpayid} " +
            "where cnickid = #{uid} and csafekey= #{safeKey} and cchannel = #{channel}")
    public int updateBindStatus(PayBean bean);

    @Select("select t.cuserbusiid userbusiid,t.cuserpayid userpayid from tb_recharge_card_channel t where cnickid=#{uid} and cchannel=#{channel} and csafekey=#{safeKey}")
    RechCardChannelPojo queryUserbusiIdAndUserPayID(PayBean bean);

    //查询银行卡渠道绑定信息
    @Select("select t.cuserbusiid userbusiid,t.cuserpayid userpayid,t.cchannel channel from tb_recharge_card_channel t where cnickid=#{uid} and csafekey=#{safeKey}")
    List<RechCardChannelPojo> queryRechCardBindInfo(PayBean bean);

    //将银行卡绑定渠道协议信息置为空
    @Delete("update tb_recharge_card_channel set CMERID='',CUSERBUSIID='',CUSERPAYID='' where cnickid=#{uid} and csafekey=#{safeKey}")
    int updateRechCardBindChannel(PayBean bean);

}

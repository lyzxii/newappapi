package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import trade.bean.TradeBean;
import trade.pojo.SharelistPojo;

/**
 * tb_sharelist
 */

@Mapper
public interface SharelistMapper {

    @Select("select itmoney tmoney,ifollowmoney followmoney from tb_sharelist where cprojid = #{hid}")
    SharelistPojo queryShareProjStatus(TradeBean bean);


    @Select("select iwrate from tb_sharelist where cprojid = #{zid}")
    int queryShareWrate(TradeBean bean);

    @Update("update tb_sharelist set ifollownums = ifollownums + 1,ifollowmoney = ifollowmoney + #{money} where cprojid = #{zid}")
    int updateShareFollowData(TradeBean bean);

    @Update("update tb_sharelist set iusernums = iusernums + 1 where cprojid = #{zid}")
    int updateFollowUserNum(TradeBean bean);

}

package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import redpacket.bean.Card_CardType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * tb_card 表
 */
@Mapper
public interface CardMapper {

    /**
     * 根据手机号、身份证号、用户名来查询激活次数
     */
    @Select("select count(*) from tb_card where itype=#{itype} and (cmobilenomd5=#{mobilenomd5} or cactivenick=#{cactivenick} or cidcardmd5=#{idcardmd5})")
    int countCardType1(Card_CardType card);

    /**
     * 根据手机号、身份证号、用户名、真实姓名来查询激活次数
     */
    @Select("select count(*) from tb_card where itype=#{itype} and (cmobilenomd5=#{mobilenomd5} or " +
            "cactivenick=#{cactivenick} or cidcardmd5=#{idcardmd5} or crealnamemd5=#{realnamemd5})")
    int countCardType2(Card_CardType card);

    /**
     *更新激活次数
     */
    @Update("update tb_card set cactivedate=sysdate,cactivenick=#{cactivenick},cactiveip=#{cactiveip},cidcardmd5=#{idcardmd5},cmobilenomd5=#{mobilenomd5},crealnamemd5=#{realnamemd5}," +
            "CUPACKETID =#{cupacketid},cactivesn=#{idcard},cactivemob=#{mobileno},cactivename=#{realname},iactive=1 where ccardid=#{ccardid} and ccardpwd=#{ccardpwd} and crpid=#{crpid} and iactive=0")
    int updateActiveCnt(Card_CardType card);
}

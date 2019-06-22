package com.caiyi.lottery.tradesystem.integralcenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
  * tb_jf_exrecord 表
 */
@Mapper
public interface JFExchangeMapper {

    /**
     * 查询用户此种兑换物品当天兑换数量
     * @param ex_time 当天日期
     * @param uid  用户名
     * @param ex_goods_id 兑换物品id
     * @return
     */
    @Select("select count(1) as userExCnt from tb_jf_exrecord where to_char(ex_time,'yyyy-MM-dd')=#{ex_time} " +
            "and cnickid=#{uid} and ex_goods_id=#{ex_goods_id} and ex_status=1")
    int getUserExchangeCnt(@Param("ex_time") String ex_time, @Param("uid") String uid, @Param("ex_goods_id") String ex_goods_id);

    /**
     * 查询此种兑换物品当天已兑换数量
     * @return ex_goods_id
     */
    @Select("select count(1) as goodsCntDay from tb_jf_exrecord where to_char(ex_time,'yyyy-MM-dd')=#{ex_time}" +
            "and ex_goods_id=#{ex_goods_id} and ex_status=1")
    int getExchangedCnt(@Param("ex_time")String ex_time,@Param("ex_goods_id")String ex_goods_id);
}

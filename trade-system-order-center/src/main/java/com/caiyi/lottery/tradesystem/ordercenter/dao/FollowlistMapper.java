package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.FollowlistPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created by tiankun on 2018/1/2.
 */
@Mapper
public interface FollowlistMapper {

    @Select("select sum(ireward) reward,sum(ibonus) bonus from tb_followlist where cshareprojid = #{hid}")
    FollowlistPojo queryFollowResult(@Param("hid") String hid);
}

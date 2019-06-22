package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.GameFilterPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * tb_game_filter
 *
 * @author GJ
 * @create 2018-01-08 18:26
 **/
@Mapper
public interface GameFilterMapper {

    @Select("select * from tb_game_filter where gid=#{gid} and hid=#{hid}")
    GameFilterPojo queryFilter(@Param("gid") String gid, @Param("hid") String hid);

}

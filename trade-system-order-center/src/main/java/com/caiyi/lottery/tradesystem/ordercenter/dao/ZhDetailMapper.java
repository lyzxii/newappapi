package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ZhuihaoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 复杂查询DAO
 *
 * @author GJ
 * @create 2017-11-30 15:41
 **/
@Mapper
public interface ZhDetailMapper {
    /**
     * 追号详情
     * @param gid
     * @param hid
     * @param isdone true 已追号 flag=44，false 待追号 flag=45
     * @return
     */

    Integer queryDoneChaseCount(@Param("gid") String gid, @Param("hid") String hid);

}

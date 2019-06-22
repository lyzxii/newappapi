package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ComplexPojo;
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
public interface ComplexMapper {
    /**
     * 追号详情
     * @param gid
     * @param hid
     * @param isdone true 已追号 flag=44，false 待追号 flag=45
     * @return
     */
    List<ComplexPojo> queryChaseNumber(@Param("gid") String gid, @Param("hid") String hid, @Param("isdone") Boolean isdone);

    ComplexPojo queryChaseNumberTitile(@Param("gid") String gid, @Param("hid") String hid);

}

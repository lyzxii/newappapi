package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ProjXzjzPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * tb_proj_xzjz
 *
 * @author GJ
 * @create 2018-01-12 10:07
 **/
@Mapper
public interface ProjXzjzMapper {

    /**
     * 旋转矩阵
     * @param gid
     * @param hid
     * @return
     */
    @Select("select t.ccodes,t.codelist from tb_proj_xzjz t where t.cgameid = #{gid} and t.cprojid = #{hid}")
    List<ProjXzjzPojo> queryMatrix(@Param("gid") String gid, @Param("hid") String hid);

}

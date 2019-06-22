package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ShareUserStatPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by tiankun on 2018/1/3.
 */
@Mapper
public interface ShareUserStatMapper {

    /**
     * 查询大神数据
     * @param uid
     * @param uptype
     * @param day
     * @return
     */
    @Select("select * from TB_SHARE_USER_STAT where cnickid = #{uid} and cstattype = #{uptype} and cstatday = #{day} order by cstatday desc")
    List<ShareUserStatPojo> queryGodData(@Param("uid") String uid, @Param("uptype") String uptype,@Param("day") String day);

    @Select("SELECT count(1) FROM TB_SHARE_USER_STAT t where t.cstatday = #{date}")
    int queryCount(@Param("date") String date);
}

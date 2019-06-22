package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ShareUserDetailPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by tiankun on 2018/1/2.
 */
@Mapper
public interface ShareUserDetailMapper {

    /**
     * 查询大神命中相关数据
     * @param uid
     * @param uptype
     * @return
     */
    @Select("select iprojallnum allnum,iprojrednum rednum,cshootrate shootrate,ibuymonry buymoney,iwinmoney winmoney,creturnrate returnrate "+
            "from tb_share_user_detail where cnickid = #{uid} and cstattype = #{uptype} order by cstatday desc")
    List<ShareUserDetailPojo> queryGodHitData(@Param("uid") String uid, @Param("uptype") String uptype);

    @Select("SELECT count(1) FROM tb_share_user_detail t where t.cstatday = #{currentDate} ")
    int queryCountShareDetail(@Param("currentDate") String currentDate);

    @Select("select iprojallnum allnum,cstattype cstattype,cshootrate shootrate,creturnrate returnrate from tb_share_user_detail where cnickid = #{uid} and cstatday = #{day} and cstattype in('7','15','30') order by cstatday desc,to_number(cstattype)")
    List<ShareUserDetailPojo> queryAllPeriodData(@Param("uid") String uid, @Param("day") String day);

    /**
     * 查询用户最近几天的数据
     * @param uid
     * @param day
     * @return
     */
    @Select("select iprojallnum allnum,iprojrednum rednum from tb_share_user_detail where cnickid = #{uid} and cstattype = #{day} order by cstatday desc")
    List<ShareUserDetailPojo> queryLatestData(@Param("uid") String uid, @Param("day") String day);

}

package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ShareUserListPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by tiankun on 2018/1/2.
 */
@Mapper
public interface ShareUserListMapper {

    @Select("select cusertype usertype,iprojallnum allnum,iprojrednum rednum,cuptype uptype,cranking rank from tb_share_user_list where cnickid = #{uid}")
    ShareUserListPojo queryGodStatus(@Param("uid") String uid);

    @Select("select * from (select cnickid,cranking rank from tb_share_user_list where cranking <> 0 order by cranking)")
    List<ShareUserListPojo> queryGodRank();

    @Select("select * from (select cnickid,cranking rank from tb_share_user_list where cranking <> 0 order by cranking) where rownum <= #{num}")
    List<ShareUserListPojo> queryGodRank1(@Param("num") String num);
}

package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.UserFollowPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TbUserFollowlistMapper {

    @Select("select f.cnickid \"uid\" ,f.itmoney buymoney,to_char(f.cadddate,'MM-dd hh24:mi') addtime,u.cuserphoto photo,f.ibonus bonus from tb_followlist f,tb_user u  where cshareprojid = #{newValue} and f.cnickid = u.cnickid order by buymoney desc,addtime desc")
    List<UserFollowPojo> queryGodFollowList(@Param("newValue") String newValue);
}

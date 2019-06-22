package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.ShareGodUserPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface Tb_share_sharelist_followlistMapper {

    List<ShareGodUserPojo> queryShareUserData(@Param("stime") String stime,@Param("flag") String flag);
}

package com.caiyi.lottery.tradesystem.ordercenter.dao;

import order.pojo.UserListDetailPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by tiankun on 2018/1/10.
 */
@Mapper
public interface ShareUserList_ShareUserDetailMapper {

    @Select("select a.cnickid,b.iprojallnum projallnum,b.iprojrednum projrednum from tb_share_user_list a ,tb_share_user_detail b where a.cnickid = b.cnickid and b.cstatday = #{sort} and b.cstattype = decode(a.cuptype,0,7,7,7,15,15,30,30) and a.cnickid = #{nickid} ")
    List<UserListDetailPojo> getUserProjAllNum(@Param("sort") String sort,@Param("nickid") String nickid);


}

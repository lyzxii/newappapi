package com.caiyi.lottery.tradesystem.usercenter.dao;

import dto.UserAutoDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Created by tiankun on 2017/12/1.
 */
@Mapper
public interface UserAutoMapper {

    @Select("select cnickid,cgameid,cowner,ilimit,iminmoney,imaxmoney,ibmoney,itype,irate,ibuy,itimes,istate state  from tb_user_auto where cnickid =? and cgameid=? and cowner=?")
    UserAutoDTO queryUserFromUserauto(String uid,String gid,String owner);
}

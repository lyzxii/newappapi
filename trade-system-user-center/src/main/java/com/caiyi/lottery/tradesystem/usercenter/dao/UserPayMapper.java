package com.caiyi.lottery.tradesystem.usercenter.dao;

import com.caiyi.lottery.tradesystem.BaseBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import pojo.UserPayPojo;

import java.util.List;

@Mapper
public interface UserPayMapper {
    List<UserPayPojo> getPayRecordByNickidAndDate(@Param("uid") String uid, @Param("stime") String stime, @Param("etime") String etime, @Param("bankId") String bankId);

}

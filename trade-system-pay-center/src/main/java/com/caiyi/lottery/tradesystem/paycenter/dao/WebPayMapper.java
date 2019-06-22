package com.caiyi.lottery.tradesystem.paycenter.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WebPayMapper {
    @Select("select itype from TB_WEBPAY where cnickid=#{uid}")
    Integer queryWhiteliststatus(String uid);
}

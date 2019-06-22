package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * tb_totalbuymoney 暂时放在user中心
 *
 * @author GJ
 * @create 2017-12-17 15:14
 **/
@Mapper
public interface TotalBuyMoneyMapper {

    @Select("select count(1) from tb_totalbuymoney  where money >=500 and cnickid=#{nickid}")
    int getAbout500Money(@Param("nickid") String nickid);

    @Select("select count(1) from tb_totalbuymoney  where money >=500  and cidcard=#{idcard}")
    int getAbout500MoneyByidCard(@Param("idcard") String idcard);
}

package com.caiyi.lottery.tradesystem.redpacketcenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * tb_card_type 表
 */
@Mapper
public interface CardTypeMapper {

    @Select("select ISOURCE from tb_card_type where ICID = #{itype}")
    String queryCardType(String itype);
}

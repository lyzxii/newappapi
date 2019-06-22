package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 虚拟表dual
 *
 * @author GJ
 * @create 2017-12-19 11:37
 **/
@Mapper
public interface DualMapper {

    @Select("select seq_agentid.nextval vid from dual")
    String getAgentNextval();
}

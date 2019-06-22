package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * tb_appagent
 *
 * @author GJ
 * @create 2017-12-16 15:24
 **/
@Mapper
public interface AppagentMapper {

    /**
     * 根据source值查询代理商id
     * @param source
     * @return
     */
    @Select("select agentid  from tb_appagent where isource = #{source}")
    String queryAgentId(@Param("source") Integer source);

}

package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * tb_agent_rate
 *
 * @author GJ
 * @create 2017-12-19 11:52
 **/
@Mapper
public interface AgentRateMapper {

    @Insert("insert into tb_agent_rate (cagentid,cgameid,irate) values (#{agentid},#{gameid},0)")
    int addAgentRate(@Param("agentid") String agentid, @Param("gameid") String gameid);
}

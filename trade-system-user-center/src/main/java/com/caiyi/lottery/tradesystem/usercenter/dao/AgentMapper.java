package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import pojo.AgentPojo;

import java.util.List;

@Mapper
public interface AgentMapper {
    @Select("select cagentid from tb_agent where cnickid=#{nickid}")
    String queryAgentId(@Param("nickid") String nickid);

    @Select("select * from (select cagentid from tb_agent where isdaili='1' or teamid>0 connect by cagentid=prior cparentid start with cagentid=#{oldagentid} order by ccreatedate desc) where rownum<2")
    List<String> getParenAgentId(@Param("oldagentid") String oldagentid);

    @Select("select level+1 from tb_agent where cparentid='vip' connect by cagentid=prior cparentid start with cagentid=#{agentid}")
    List<String> getVipLevelByAgentId(@Param("agentid") String agentid);

    AgentPojo queryAgentLevel(String agentid, String uid);
    
    @Select("select nvl(a.ibalance,0) vmoney from tb_agent a where a.cagentid = #{agentid}")
    double getVipReturnMoney(@Param("agentid")String agentid);

    @Select("select count(1) from tb_agent where cnickid=#{nickid}")
    int getCountAgent(@Param("nickid") String nickid);

    @Insert("insert into tb_agent (cagentid,cagentname,ccreatedate,cpassword,istate,cparentid,cnickid,isdaili)" +
            " values (#{agentid},#{agentname},sysdate,'888888',0,'alipayvip',#{nickid},0)")
    int addAgent(@Param("agentid") String agentid, @Param("agentname") String agentname, @Param("nickid") String nickid);
}

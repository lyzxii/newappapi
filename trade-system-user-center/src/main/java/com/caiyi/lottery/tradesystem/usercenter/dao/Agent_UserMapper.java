package com.caiyi.lottery.tradesystem.usercenter.dao;

/**
 * tb_agent和tb_user表联合查询使用
 * @author wxy
 * @create 2017-12-01 10:47
 **/

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.Agent_UserPojo;
import pojo.UserPojo;

import java.util.List;

@Mapper
public interface Agent_UserMapper {
    /**
     * 根据用户名查询基本绑定信息
     * 身份证号，手机号，是否绑定手机号，代理id
     * @param nickid 用户名
     * @return 身份证号，手机号，是否绑定手机号，代理id
     */
    @Select("select u.cidcard as idCard,u.cmobileno as mobileNumber,u.imobbind as mobileBind ,u.cagentid as agentId ,u.cmobilenomd5 as mobilenoMD5 ,u.CIDCARDMD5 as idCardMD5 " +
                    "from tb_user u,tb_agent t " +
                    "where u.cnickid =#{nickid} and u.cagentid=t.cagentid and u.cnickid!=decode(t.cnickid,null,'-1',t.cnickid) ")
    Agent_UserPojo getBindInfo(@Param("nickid") String nickid);

    /**
     * 查询vip层下是代理的代理信息
     * @param nickid 用户昵称
     * @param idCardMD5 身份证MD5
     * @return 代理id，代理更新时间，父id，是否为代理
     */
    List<Agent_UserPojo> getAgentByIdCardAndNidcidWithIsAgent(@Param("nickid") String nickid ,@Param("idCardMD5") String idCardMD5);

    /**
     * 查询vip层下的代理信息
     * @param nickid 用户昵称
     * @param idCardMD5 身份证MD5
     * @return 代理id，代理更新时间，父id，是否为代理
     */
    List<Agent_UserPojo> getAgentByIdCardAndNidcid(@Param("nickid") String nickid ,@Param("idCardMD5") String idCardMD5);

    /**
     * 查询normal层下的不是alipay的代理信息
     * @param nickid 用户昵称
     * @param idCardMD5 身份证MD5
     * @return 代理id，代理更新时间，父id，是否为代理
     */
    List<Agent_UserPojo> getAgentByIdCardAndNickidWithNormal(@Param("nickid") String nickid ,@Param("idCardMD5") String idCardMD5);

    /**
     * 查询vip层下是代理的代理信息
     * @param nickid 用户昵称
     * @param mobilenoMD5 手机号MD5
     * @return 代理id，代理更新时间，父id，是否为代理
     */
    List<Agent_UserPojo> getAgentByMobilenoAndNickidWidthIsAgent(@Param("nickid") String nickid, @Param("mobilenoMD5") String mobilenoMD5);

    /**
     * 查询vip层下的代理信息
     * @param nickid 用户昵称
     * @param mobilenoMD5 手机号MD5
     * @return 代理id，代理更新时间，父id，是否为代理
     */
    List<Agent_UserPojo> getAgentByMobilenoAndNickid(@Param("nickid") String nickid, @Param("mobilenoMD5") String mobilenoMD5);

    /**
     * 查询normal层下的不是alipay的代理信息
     * @param nickid 用户昵称
     * @param mobilenoMD5 手机号MD5
     * @return 代理id，代理更新时间，父id，是否为代理
     */
    List<Agent_UserPojo> getAgentByMobilenoAndNickidWithNormal(@Param("nickid") String nickid, @Param("mobilenoMD5") String mobilenoMD5);

    @Select("select nvl((select level+1 from tb_agent where cparentid='vip' " +
            "connect by cagentid=prior cparentid start with cagentid= a.cagentid),-1)" +
            " as isvip,iopen as state from tb_user a  where a.cnickid= #{cnickid}")
    UserPojo queryUserVipAndWhitelistLevel(String cnickid);


    @Select("select count(1) from tb_agent a ,tb_user u where u.cnickid=#{uid} and a.cagentid = u.cagentid and\n" +
            "u.cagentid in (select ta.cagentid from tb_agent ta  connect by ta.cparentid=prior ta.cagentid start with ta.cagentid='vip')")
    int queryUserVipAgentCount(String uid);

}

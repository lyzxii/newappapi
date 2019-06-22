package com.caiyi.lottery.tradesystem.usercenter.dao;

import com.caiyi.lottery.tradesystem.pojo.TbTokenPojo;

import bean.TokenBean;

import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户中心-TokenDao
 * @create 2017-11-27 14:41:19
 */
@Mapper
public interface TokenManageMapper {

    @Insert("insert into tb_token (ACCESSTOKEN,EXPIRESIN,MOBILETYPE,CNICKID,CPASSWORD,APPID,PARAMJSON) values(#{accesstoken},#{expiresin},#{mobiletype},#{cnickid},#{cpassword},#{appid},#{paramjson})")
    int saveTokenInDB(TbTokenPojo tbTokenPojo);


    @Select("select appid,accesstoken from (select appid,accesstoken from tb_token where cnickid=#{uid} and istate=0 order by createtime desc) where rownum<=1")
    List<TokenBean> selectLatestTokenByNickid(@Param("uid") String uid);

    @Update("update tb_token set LASTTIME=sysdate,cpassword=#{newPwd} where accesstoken=#{accesstoken} and appid=#{appid}")
    int updateTokenPwd(@Param("appid")String appid, @Param("accesstoken")String accesstoken, @Param("newPwd")String newPwd);

    /**
     * 查询指定用户可用token信息,appid和accesstoken
     */
    @Select("select appid,accesstoken from tb_token where cnickid=#{cnickid} and istate=0")
    List<TokenBean> queryAvailableTokenByNickid(@Param("cnickid") String uid);

    /**
     * 注销token信息
     */
    @Update("update tb_token set istate=1,LASTTIME=sysdate,deadtime=sysdate where appid=#{appid} and accesstoken=#{accessToken}")
    int disableToken(@Param("appid") String appid, @Param("accessToken") String accessToken);

    @Update("update tb_token set LASTTIME=sysdate,PARAMJSON=#{paramJson} where accesstoken=#{accesstoken} and appid=#{appid}")
	int updateTokenParam(@Param("appid") String appid, @Param("accesstoken")String accesstoken, @Param("paramJson") String paramJson);

    @Select("select LASTTIME,EXPIRESIN,ISTATE,CNICKID,CPASSWORD,PARAMJSON from tb_token where accesstoken = #{accesstoken} and appid = #{appid}")
    public List<TbTokenPojo> findByAccesstokenAppid(@Param("accesstoken") String accesstoken, @Param("appid") String appid);

    @Update("update tb_token set LASTTIME=sysdate where accesstoken = #{accesstoken} and appid = #{appid}")
    public int updateByAccesstokenAppid(@Param("accesstoken") String accesstoken, @Param("appid") String appid);

    @Update("update tb_token set DEADTIME=sysdate,ISTATE=#{istate},CAUSE=#{cause} where accesstoken=#{accesstoken} and appid=#{appid}")
    public int updateByAccesstokenAppid1(@Param("istate") int istate, @Param("cause") String cause, @Param("accesstoken") String accesstoken, @Param("appid") String appid);



}

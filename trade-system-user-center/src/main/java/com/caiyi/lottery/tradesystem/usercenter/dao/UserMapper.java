package com.caiyi.lottery.tradesystem.usercenter.dao;


import dto.UserInfoDTO;
import org.apache.ibatis.annotations.*;

import bean.UserBean;
import pojo.AppagentPojo;
import pojo.PartPojo;
import pojo.UserPojo;

import java.util.List;

@Mapper
public interface UserMapper {
    @Update("update tb_user set calipay_email = #{newvalue} where cnickid = #{nickid} and cpassword = #{pwd}  and calipay_email is null")
    Integer updateAliyId(@Param("newvalue") String newvalue, @Param("nickid") String nickid, @Param("pwd") String pwd);

    @Select("select cpwdflag as pwdflag from tb_user where cnickid = #{nickid} ")
    String queryPwdFlag(@Param("nickid")String nickid);

    @Select("SELECT imobbind as mobileBind " +
            " FROM tb_user WHERE  cnickid=#{nickid}")
    Integer queryUserMobileBind(@Param("nickid")String nickid);

    @Select("select iopen from tb_user where cnickid= #{cnickid}")
    Integer queryUserWhitelistGrade(@Param("cnickid") String cnickid);

    @Select("select CPASSWORD as pwd,ISTATE as state from tb_user where cnickid=#{cnickid}")
    List<UserPojo> queryByNickid(@Param("cnickid") String cnickid);

    @Select("select cpassword pwd,istate state,cuserid from tb_user where cnickid=#{cnickid}")
    UserPojo querysByNickid(@Param("cnickid") String cnickid);

    @Select("select count(id) from tb_public_task where value1 = #{nickid} and iflag=2 and itype=9")
    int queryPublicTaskCount(@Param("nickid") String nickid);

    @Update("update tb_public_task set iflag = 0 where value1 = #{nickid} and iflag=2 and itype = 9")
    int updatePublicTask(@Param("nickid") String nickid);

    @Select("select nvl(imodifynickid, 0) from tb_user where cmobilenomd5=#{uid} and ilgphone=1")
    int queryNickidModifyCountByPhone(@Param("uid") String uid);

    @Select("select nvl(imodifynickid, 0) from tb_user where cnickid=#{uid}")
    Integer queryNickidModifyCountByNickname(@Param("uid") String uid);

    @Select("select  1  flag from dual " +
            " WHERE exists ( " +
            " select * from tb_user_charge where cnickid = #{uid} and cadddate >= to_date(#{stime},'yyyy-mm-dd hh24:mi:ss') and ibiztype != 200 and ibiztype != 300 and itype=0" +
            ")")
    String queryUserAccount(@Param("uid") String uid, @Param("stime") String stime);

    @Select("select iopen from tb_user where cnickid = #{uid}")
    Integer queryUserIsopen(@Param("uid") String uid);

    @Select("select itype from tb_webpay where cnickid =#{uid}")
    List<String> queryWebPayItype(@Param("uid") String uid);

    @Select("select nvl((idaigou + izhuihao),0) as amount from tb_user_acct t where cnickid = #{uid}")
    List<String> queryUserAccAmount(@Param("uid") String uid);

    @Select("select cpassword from tb_user where cnickid = #{cnickid}")
    String checkUserExist(@Param("cnickid") String cnickid);

    @Select("select count(1) from tb_user where cidcard = #{idCardNo} and istate = 0")
    int queryCountByidCardNo(@Param("idCardNo") String idCardNo);

    @Update("update tb_user set imodifynickid=0 where cmobilenomd5=#{nickid} and ilgphone=1")
    int updateNickidModifyAs0ByCmobileno(@Param("nickid") String nickid);

    @Update("update tb_user set imodifynickid=0 where cnickid=#{nickid}")
    int updateNickidModifyAs0ByCnickid(@Param("nickid") String nickid);

    @Update("update tb_user set cidcardmd5 = #{idCardMD5} ,crealnamemd5 = #{realNameMD5}, crealname = #{realName} ,cidcard = #{idCardNo} where cnickid = #{uid} and cpassword = #{upwdd}  and crealname is null and cidcard is null")
    int bindIdcard(@Param("idCardMD5") String idCardMD5,@Param("realNameMD5") String realNameMD5,@Param("realName") String realName, @Param("idCardNo") String idCardNo, @Param("uid") String uid, @Param("upwdd") String upwdd);


    @Select("select count(1) from tb_user where cnickid=#{nickid} and (crealname is not null or cidcard is not null)")
    int queryCountByidcardAndRealname(@Param("nickid") String nickid);

    /**
     * 检测输入密码是否正确
     *
     * @return
     */
    @Select("select count(1) from tb_user t where t.cnickid = #{uid} and t.cpassword=#{pwd}")
    int verifyLoginPwdSql(@Param("uid") String uid, @Param("pwd") String pwd);

    /**
     * 修改用户密码
     */
    @Update("update tb_user set cpassword = #{newValue} where cnickid = #{uid} and cpassword = #{upwd}")
    int updateUserPwd(@Param("newValue") String newValue, @Param("uid") String uid, @Param("upwd") String upwd);

    /**
     * 查询用户基本信息
     * @param uid
     * @return
     */
    @Select("select cuserid,crealname realName,cidcard idcard,iopen whitegrade,cmobileNo mobileNo," +
            "cuserid cuserId,imobbind mobileBind,igradeid gradeid,cagentid agentid," +
            "CREALNAMEMD5 realNameMD5,CMOBILENOMD5 mobileNoMD5,CIDCARDMD5 idCardMD5 from tb_user where cnickid = #{uid}")
    UserPojo queryUserInfo(@Param("uid") String uid);

    @Select("select cpassword from tb_user where cnickid=#{nickid}")
    String queryUserPwd(@Param("nickid") String nickid);

    @Select("SELECT cuserid as userid,cnickid as nickid,cpassword as pwd,istate as state,itype as userType,ilgphone as phoneLoginFlag,iopen as whitelistGrade FROM tb_user WHERE cnickid=#{nickid}")
    List<UserInfoDTO> queryLoginInfoByNickid(@Param("nickid") String nickid);

    @Select({"<script>", "SELECT cuserid as userid,cnickid as nickid,cpassword as pwd,istate as state,itype as usertype,ilgphone as phoneloginflag,iopen as whitelistgrade FROM tb_user WHERE cmobilenomd5=#{mobileno} and imobbind!=0 ",
            "<when test='isPhoneLogin==true'>", "AND ilgphone=1", "</when>", "</script>"})
    List<UserInfoDTO> queryLoginInfoByMobileno(@Param("mobileno") String mobileno, @Param("isPhoneLogin") boolean isPhoneLogin);

    @Select({"<script>", "SELECT cuserid as userid,cnickid as nickid,cpassword as pwd,istate as state,itype as usertype,ilgphone as phoneloginflag,iopen as whitelistgrade FROM tb_user WHERE cnickid=#{nickid} and imobbind!=0 ",
            "<when test='isPhoneLogin==true'>", "AND ilgphone=1", "</when>", "</script>"})
    List<UserInfoDTO> queryLoginInfoByNickidBind(@Param("nickid") String nickid, @Param("isPhoneLogin") boolean isPhoneLogin);

    @Select("SELECT cuserid as userid,cnickid as nickid,cpassword as pwd,istate as state,itype as usertype,ilgphone as phoneloginflag,iopen as whitelistgrade FROM tb_user WHERE cmobileno=#{mobileno} and imobbind!=0 order by cadddate desc")
    List<UserInfoDTO> queryLatestUserByMobileno(@Param("mobileno") String mobileno);

    @Select({"<script>", "SELECT count(1) FROM tb_user WHERE cmobilenomd5=#{mobileno} and imobbind!=0 ", "<when test='isPhoneLogin == true'>", "AND ilgphone=1", "</when>", "</script>"})
    int queryMobilenoBindCount(@Param("mobileno")String mobileno,@Param("isPhoneLogin")boolean isPhoneLogin);

    @Select({"<script>", "SELECT count(1) FROM tb_user WHERE cnickid=#{nickid} and imobbind!=0 ", "<when test='isPhoneLogin == true'>", "AND ilgphone=1", "</when>", "</script>"})
    int queryUserBindMobile(@Param("nickid")String nickid,@Param("isPhoneLogin")boolean isPhoneLogin);

    @Select("select count(1) from tb_user where cnickid=#{mobileno}")
    int queryMobilenoLoginCount(@Param("mobileno") String mobileno);

    @Update("update tb_user set ilgphone=1 where cnickid=#{nickid} and imobbind=1")
    int updateMobilenoLogin(@Param("nickid") String nickid);

    @Select("select count(1) from tb_user where cnickid = #{cnickid} and cpwdflag = '0'")
    int selectPwd(@Param("cnickid")String cnickid);

    @Select("select * from tb_user where cnickid = #{cnickid} and cpwdflag = '1'")
    List<Object> selectPwdBycpwdflag(@Param("cnickid")String cnickid);//TODO this object

    @Update("update tb_user set cpassword = #{pwd},cpwdflag = '1' where cnickid = #{cnickid} and cpwdflag = '0'")
    int updatePwd(@Param("cnickid")String cnickid, @Param("pwd")String pwd);


    @Update("update tb_user set cpassword = #{pwd}  where cnickid = #{uid} ")
    int updatePwdRests(@Param("uid")String uid, @Param("pwd")String pwd);

    @Select("select count(1) from tb_active_data where cimei = #{imei}")
    int selectImei(@Param("imei")String imei);

    @Select("select count(1) from tb_user where cmobilenomd5 = #{mobilemd5} and ilgphone =1")
    int selectRegist(@Param("mobilemd5")String mobilemd5);

    @Select("select count(1) from tb_user where cmobilenomd5 = #{mobilemd5} and istate = 0")
    int selectAccountNum(@Param("mobilemd5")String mobilemd5);

    @Select("select * from tb_appagent where  isource = #{source}")
    List<AppagentPojo> selectAgentBySource(@Param("source")int source);

    @Select("select count(1) from tb_agent where cagentid = #{agentid}")
    int selectAgent(@Param("agentid")String agentid);

    @Select("select nvl((select level+1 from tb_agent where cparentid = 'vip' connect by cagentid = prior cparentid start with cagentid = #{agentid}),-1) from dual")
    int selectAgentCascade(@Param("agentid")String agentid);

    @Select("select cagentid from tb_user where cnickid = #{uid}")
    List<PartPojo> selectUserAgent(@Param("uid")String uid);

    @Update("update tb_user set imobbind=1 where cnickid=#{uid} and cmobilenomd5=#{mobileNo}")
    int bindMobileno(@Param("uid")String uid, @Param("mobileNo")String mobileNo);

    @Select("select count(1) from tb_user where cnickid=#{cnickid}")
    int selectNickidCount(@Param("cnickid")String cnickid);

    @Select("select count(1) from tb_active_data where cidfa = #{idfa}")
    int selectActiveData(@Param("idfa")String idfa);

    @Select("select count(1) from tb_sms where CMOBILENOMD5 = #{mid} and cadddate > sysdate - 1")
    int selectSendSmsCount(@Param("mid")String mobileid);

    @Select("select count(1) from tb_user where cnickid = #{uid}")
    int selectUserCount(@Param("uid")String uid);

    String selectPwdFlag(@Param("uid")String uid);
    /**
     * 更新代理与vip状态
     * @param agentid 代理id
     * @param nickid 用户昵称
     * @param isvip 是否VIP
     * @return 执行成功件数
     */
    @Update("update tb_user set cagentid = #{agentid},cagentdate=sysdate,isvip=#{isvip}  where cnickid =#{nickid} ")
    int updateAgentIdByNickId(@Param("agentid") String agentid, @Param("nickid") String nickid, @Param("isvip") Integer isvip);

    /**
     * 查询用户手机号
     * @param nickid 用户昵称
     * @return 添加日期，手机号，是否绑定手机
     */
    @Select("select cadddate as addDate,cmobileno as mobileno,imobbind as mobileBind,cmobilenomd5 as mobileNoMD5 from tb_user where cnickid=#{nickid}")
    UserPojo getUserMobileByNickId(@Param("nickid") String nickid);

    @Select("select count(1) from tb_user where cmobilenomd5=#{mobileno}")
    int countMobileNo(@Param("mobileno") String mobileno);

    @Select("select count(1) from tb_user where cnickid=#{nickid} and cagentid=#{agentid}")
    int countWithNickIdAndAgentId(@Param("nickid") String nickid, @Param("agentid") String agentid);

    int countWithInNickIdAndAgentId(@Param("nickid") String nickid, @Param("agentids") String[] agentids);

    String getMobileNoByNickIdAndAgentId(@Param("nickid") String nickid, @Param("agentids") String[] agentids);


    UserPojo queryIdBankBinding(@Param("nickid") String nickid);

    @Select("select decode(cidcard, null, '', cidcard) as idcardNo,decode(cmobileno, null, '', cmobileno) as mobileNo, decode(crealname, null, '', crealname) as realname,imobbind as mobileBindFlag,decode(cbankcard, null, '', cbankcard) as bankcardNo,cbankcode as bankCode,cbankname as branchName,cbankpro as bankProvince,cbankcity as bankCity,ilgphone as phoneLoginFlag,cuserid as userId,iopen  from tb_user where cnickid = #{cnickid}")
    List<UserInfoDTO> queryUserBankinfoByNickid(@Param("cnickid") String cnickid);

    int updateUserDrawBankCard(UserBean bean); 
    
    @Update("update tb_user set cbankcode = #{drawBankCode}, cbankpro = #{provid}, cbankcity = #{cityid}, cbankname = #{bankName} where cnickid = #{uid} and cbankcard is not null ")
    int modifyUserDrawBankCard(UserBean bean);

    @Update("update tb_user_photo_cash a  set a.crbackflag = '1' where a.cnickid = #{uid} and a.cstatus = '2' ")
    int updateUserPhotoReback(@Param("uid") String uid);

    @Select("select count(1) from tb_user t where t.cpassword= #{pwd} and t.cnickid = #{uid}")
    int selectValidatePwd(@Param("pwd")String pwd, @Param("uid")String uid);
    
    @Select("select cnickid as \"uid\",cuserid as cuserId,cmobileno as mobileNo,imobbind as mobbindFlag from tb_user where cnickid=#{nickid}")
    UserPojo getUserMobileBindInfoByNickId(@Param("nickid") String nickid);
    
    @Select("select cnickid as \"uid\",cuserid as cuserId,cmobileno as mobileNo,imobbind as mobbindFlag from tb_user where imobbind=1 and cmobilenomd5=#{mobileNo}")
    List<UserPojo> getUserMobileBindInfoByMobileno(@Param("mobileNo") String mobileNo);

    @Select("select decode(cidcard,null,'',cidcard) idcard,imobbind mobbindFlag from tb_user  where cnickid = #{nickid}")
    UserPojo queryUserIdcardBinding(String nickid);

    @Update("update tb_bind_msg set counts=counts+1 where crec=#{mobileno} and crandom=#{verifyCode} and itype=1 and types=#{smsType} and iverify=0 and cadddate>=sysdate-1")
    int updateSmsVerifyTimes(@Param("mobileno") String mobileno, @Param("verifyCode") String verifyCode, @Param("smsType") String smsType);

    
    @Update("update tb_user set cpassword=#{newPwd} where cmobileno=#{mobileNo} and ilgphone=1")
    int forgetPwdResetPwd(@Param("newPwd") String newPwd,@Param("mobileNo") String mobileNo);
    
    int setNickidModifyAs0(@Param("mobileNo")String mobileNo,@Param("nickId")String nickId);

    @Select("select cnickid,isource from tb_user t where t.cnickid = #{cnickid} and t.imobbind=1 and itype =0 and add_months(cadddate,6)>sysdate")
    UserPojo queryMobilenoBindAccountWechat(String cnickid);


    @Select("select cnickid as \"uid\",isource as source from tb_user t where t.cmobileno = #{mobileNo} and t.imobbind=1 and itype =0 and add_months(cadddate,6)>sysdate")
    List<UserPojo> queryMobilenoBindAccount(@Param("mobileNo") String mobileNo);

    @Select("select count(1) from tb_user where cnickid = #{uid} and ilgphone =1 ")
    int selectMobileNoRegist(@Param("uid")String uid);

    @Select("select count(1) from tb_user where cnickid = #{nickid}")
    int queryUserCountByNickid(@Param("nickid") String nickid);

    @Insert("insert into tb_user (cnickid, cpassword, cagentid,cregip, cuserid, isource,cprivatekey) values " +
                    "(#{userPojo.uid,jdbcType=VARCHAR},#{userPojo.pwd,jdbcType=VARCHAR} " +
                    ",#{userPojo.agentid,jdbcType=VARCHAR} ,#{userPojo.ipAddr,jdbcType=VARCHAR} " +
                    ",#{userPojo.cuserId,jdbcType=VARCHAR} ,#{userPojo.source,jdbcType=VARCHAR} ,#{userPojo.privateKey,jdbcType=VARCHAR})")
    int insertWithoutMobile(@Param("userPojo") UserPojo userPojo);

    @Insert("insert into tb_user (cnickid, cpassword, cagentid,cregip, cuserid, isource,cprivatekey,cmobileno,imobbind,cmobilenomd5) values " +
                    "(#{userPojo.uid,jdbcType=VARCHAR},#{userPojo.pwd,jdbcType=VARCHAR} ,#{userPojo.agentid,jdbcType=VARCHAR} " +
                    ",#{userPojo.ipAddr,jdbcType=VARCHAR} ,#{userPojo.cuserId,jdbcType=VARCHAR} ,#{userPojo.source,jdbcType=VARCHAR} " +
                    ",#{userPojo.privateKey,jdbcType=VARCHAR},#{userPojo.mobileNo,jdbcType=VARCHAR},1,#{userPojo.mobileNoMD5,jdbcType=VARCHAR})")
    int insertWithMobile(@Param("userPojo") UserPojo userPojo);

    @Select("select cidcard idcard,cmobileno mobileNo,crealname realName,cagentid agentid from tb_user where cmobileno is not null and cnickid=#{uid} and IMOBBIND=1 and CIDCARD is not null")
    UserPojo queryUserInfoForCardCharge(@Param("uid") String uid);

    @Update("update tb_user set cagentid=#{agentid} where cnickid = #{nickid}")
    int updateAgentid(@Param("agentid") String agentid, @Param("nickid") String nickid);

    /**
     * 用户首次注册，将密码flag设置为0，可修改
     * @param nickid
     * @return
     */
    @Update("update tb_user set cpwdflag = '0' where cnickid = #{nickid} ")
    int updatePwdFlag( @Param("nickid") String nickid);

    /**
     * 根据用户昵称查询登录密码加密因子
     * @param nickid
     * @return
     */
    @Select("select cprivatekey as privateKey,cuserid as cuserId,cpwdflag as pwdflag from tb_user where cnickid=#{nickid}")
    UserPojo queryPrivateKeyAndUseridByNickid(@Param("nickid") String nickid);

    @Select("select cmobileno as mobileNo,imobbind as mobbindFlag,cpassword as pwd,itype as type , cmobilenomd5 as mobileNoMD5 from tb_user where cnickid = #{nickid}")
    UserPojo queryLoginByNickid(@Param("nickid") String nickid);

    @Update("update tb_user set imobbind=1,cmobileno=#{mobileno},CMOBILENOMD5=#{mobileNoMD5} where cnickid=#{nickid} and imobbind=0")
    int bindMobilenoToCaiyi(@Param("mobileno") String mobileno, @Param("mobileNoMD5") String mobileNoMD5, @Param("nickid") String nickid);

    @Select("select crealname rname,cidcard idcard,cbankcode code,cbankname name,cbankpro prov,cbankcity city,cbankcard bank,cuserid userid from tb_user where cnickid = #{uid}")
    UserPojo selectUserdnaParam(@Param("uid")String uid);

    @Update("update tb_user set itaskinit = #{itask,jdbcType=INTEGER} where cnickid = #{uid} and bitand(#{task},#{bitand}) = #{bitand}")
    int clickToGetPoints(@Param("itask") Integer itask, @Param("uid") String uid, @Param("task") String task, @Param("bitand") String bitand);

    @Update("update tb_user set CAGENTID = #{agentid} where CNICKID = #{uid}")
    int updateUserAgentId(@Param("agentid") String agentid, @Param("uid") String uid);

    @Select("select count(1) from tb_user  where cmobilenomd5=#{mobilenoMd5} and sysdate-CADDDATE <= 1/60/24 ")
    int selectLtOneMinMsg(@Param("mobilenoMd5")String mobilenomd5);

   @Select("select cnickid from tb_user where cidcardmd5=#{idcard}")
    List<String> selectIdBycard(@Param("idcard")String idcard);

   @Select("select CBANKCARDMD5 from tb_user where cnickid = #{uid}")
    String selectMd5BankCard(@Param("uid") String uid);

   @Select("select cidcard as idcard,crealname as realname from tb_user where cnickid = #{uid}")
    UserPojo queryRealNameAndIdCard(@Param("uid") String uid);

   @Select("select count(1) from tb_user where cnickid = #{uid} and cmobilenomd5 =#{mobilemd5} ")
    int queryCountByNickidAndMobileNo(@Param("uid")String uid, @Param("mobilemd5")String mobileMd5);
}

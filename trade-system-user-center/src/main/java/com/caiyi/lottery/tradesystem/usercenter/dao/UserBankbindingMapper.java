package com.caiyi.lottery.tradesystem.usercenter.dao;

import bean.UserBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pojo.UserBankbindPojo;

/**
 * 用于 tb_user_bankbinding 表
 */
@Mapper
public interface UserBankbindingMapper {
    /**
     * 检查用户是否已经存在待审核的申请
     * @param nickid 用户昵称
     * @return 当前用户待审核数
     */
    @Select("select count(1) applynum from tb_user_bankbinding t where t.cnickid = #{nickid} and t.applystate = 0")
    int getAppllyNumByNickidAndState0(@Param("nickid") String nickid);

    /**
     * 检查用户是否满足申请变更条件【即每天不能超过三次被审核驳回】
     * @param nickid 用户昵称
     * @return 当前用户指定时间内驳回请求数
     */
    @Select("select count(1) applynum from tb_user_bankbinding t " +
                    "where t.cnickid = #{nickid} and t.applystate = 2 " +
                    "and t.applydate > to_date(#{stime}, 'yyyy-MM-dd HH24:mi:ss') " +
                    "and t.applydate < to_date(#{etime}, 'yyyy-MM-dd HH24:mi:ss')")
    int getApplyNumByNickidByNickidAndState2(@Param("nickid") String nickid, @Param("stime") String stime, @Param("etime") String etime);

    /**
     * 检查用户是否满足申请变更条件【即15天只能有一条待审核和已审核通过的变更申请】
     * @param nickid 用户昵称
     * @return 该用户15天内审核和已审核通过的申请数
     */
    @Select("select count(1) applynum from tb_user_bankbinding t where t.cnickid = #{nickid} and t.applystate != 2 and t.applydate > sysdate -15 ")
    int getApplyNumByNickidAndIn15Days(@Param("nickid") String nickid);

    @Select("select '*'||substr(crealname,2) realName,\n" +
                    "        \t\tdecode(cbankcard,null,'',cbankcard) bankCard,\n" +
                    "        \t\tdecode(cbindmobile,null,'',substr(cbindmobile,1,3) || '****' || substr(cbindmobile,length(cbindmobile)-3,4)) cardMobile,\n" +
                    "        \t\tcbankname bankName,decode(csubbankname,null,'',csubbankname) subBankName ,cbankpro bankProvince,cbankcity bankCity ,cbankcode bankCode  \n" +
                    "     \t\tfrom tb_user_bankbinding \n" +
                    "      \t \twhere cnickid= #{nickid} and applystate = 0")
    UserBankbindPojo getBankInfoByNickid(@Param("nickid") String nickid);

    @Select("select count(1) applynum from tb_user_bankbinding t where t.cnickid = #{uid} and t.applystate = 0")
    int selectCheckApply(@Param("uid")String uid);

    @Select("select count(1) applynum " +
            "from tb_user_bankbinding t " +
            "where t.cnickid = #{uid} and t.applystate = 2  and t.applydate > to_date(#{stime}, 'yyyy-MM-dd HH24:mi:ss') and t.applydate < to_date(#{etime}, 'yyyy-MM-dd HH24:mi:ss')")
    int selectExceedReject(@Param("uid")String uid, @Param("stime")String stime, @Param("etime")String etime);

    @Select("select count(1) applynum from tb_user_bankbinding t where t.cnickid = #{uid} and t.applystate != 2 and t.applydate > sysdate -15 ")
    int selectAuthSupply(@Param("uid")String uid);

    int insertSupplyAlterInfo(UserBean bean);
}

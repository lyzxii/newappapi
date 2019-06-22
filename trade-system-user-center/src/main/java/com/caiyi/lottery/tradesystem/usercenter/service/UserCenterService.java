package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.PushBean;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.Response;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import integral.bean.IntegralParamBean;
import dto.UserPhotoDTO;
import pojo.Acct_UserPojo;
import pojo.UserPojo;
import pojo.UserRecordPojo;

import java.util.Set;

/**
 * 用户中心Service 业务接口
 *
 * @author GJ
 * @create 2017-11-24 17:47
 **/
public interface UserCenterService {
    /**
     * 代理商检查
     *
     * @param bean
     * @param flag 1：手机绑定
     */
    public void agentCheck(UserBean bean, int flag);

     /**
      * 上传用户头像更新入库
      * @param bean
      * @return
      * @throws Exception
      */
      BaseResp upLoadUserPhoto(UserPhotoDTO bean) throws Exception;

     void bindUserCheck(UserBean bean) throws Exception;

     /**
       * @Author: tiankun
       * @Description: 修改用户登录密码.
       * @Date: 14:25 2017/11/30
       */
      Result modifyLoginPwd(UserBean bean,Result result) throws Exception;

      /**
        * @Author: tiankun
        * @Description: 绑定身份证
        * @Date: 15:55 2017/11/30
        */
      Result bindIdcard(UserBean bean,Result result) throws Exception;

      /**
        * @Author: tiankun
        * @Description: 修改用户登录密码.
        * @Date: 19:40 2017/12/4
        */
      //此接口暂时不用
     //Result modifyUser(UserBean bean,Result result);

     /**
      * 用户追号中奖推送开关设置
      * @param bean
      * @return
      */
     int updateWinAndChaseNumberSwitch(UserBean bean) throws Exception;

     /**
      * 保存激活数据
      * @param bean
      * @return
      * @throws Exception
      */
     int saveActiveData(UserBean bean) throws Exception;

     /**
      * 查询个推tag
      * @param bean
      * @return
      */
     PushBean queryGtTag(UserBean bean);

    void insertIntoData(UserBean bean) throws Exception;

    PushBean setData(PushBean pushBean,UserBean userBean);
     PushBean queryGtTagDetail(PushBean pushBean, UserBean userBean);

     /**
      * 查询身份证银行卡绑定信息
      * @param bean
      * @return
      */
     BaseResp queryIdBankBinding(BaseBean bean) throws Exception;

     /**
      * 忘记密码-参数合法性校验
      * @param bean
      * @return
      */
     int checkParamByForget(UserBean bean);

     /**
      * 忘记密码-用户名手机号匹配
      * @param bean
      */
     void matchUidAndMobile(UserBean bean);

     /**
      * 设置新密码-参数合法性校验
      * @param bean
      * @return
      */
     int checkParamBySet(UserBean bean);

     /**
      * 设置新密码
      * @param bean
      */
     void setNewPwd(UserBean bean) throws Exception;

     /**
      * 校验发送短信参数
      * @param bean
      * @return
      */
     int checkParamByMobSms(UserBean bean);

     /**
      * 发送短信(新)
      * @param bean
      */
     void sendMobMsg(UserBean bean);

     /**
      * 检测用户名
      * @param bean
      */
     int checkParamByCheckUserNick(UserBean bean);

     /**
      * 检测用户名是否重复
      * @param bean
      */
     void checkUserNick(UserBean bean);

     /**
      * 查询用户密码状态
      * @param bean
      */
     String queryUserDefaultPwd(UserBean bean);

     /**
      * 获取客服电话
      *
      * @param bean
      * @return
      */
     String getHotLineString(UserBean bean);

     /**
      * 用户反馈
      *
      * @param bean
      * @return
      */
     int rebackUserPhotoStatus(UserBean bean) throws Exception;

     void checkMobileAccount(UserBean bean);

     /**
      *校验提款银行卡号
      *
      * @return
      */
     Result checkBankCard(UserBean bean);

     /**
      * 用户修改银行卡申请
      * @param bean
      * @return
      */
     void applyModifyBankCard(UserBean bean) throws Exception ;

 	/**
 	 * 检测验证码
 	 * @param bean
 	 */
 	public void CheckYZM(UserBean bean);
 	
 	/**
 	 * 忘记密码-检测用户输入手机号是否已经注册绑定,如果是彩票用户则发送短信验证码
 	 * @param bean
 	 */
 	public void forgetPwdCheckPreCondition(UserBean bean);
 	
 	/**
 	 * lilei
 	 * 忘记密码重置密码功能
 	 */
 	public Response forgetPwdRestPwd(UserBean userBean) throws Exception;
	
 	/**
 	 * lilei
 	 * @param bean
 	 * @param plainPwd
 	 * @return
 	 * @throws Exception
 	 * 对密码进行MD5加密
 	 */
    public String encryptPwd(BaseBean bean, String plainPwd) throws Exception;

    /**
     * 查询短信验证码
     * @param bean
     */
    BaseResp querySmsAuthCode(UserBean bean);

    /**
     * 银行鉴权
     * @param bean
     */
    boolean authenticBankCard(UserBean bean);
    
    /** 查头像、等级、当前积分值
     * @param uid
     * @return
     */
    Acct_UserPojo integralQueryBasicInfo(String uid);

    /**
     * 查询是否绑定银行卡和身份证
     * @param uid
     * @return
     */
    UserPojo integralQueryIdBankBinding(String uid);

    /**
     * 查询是否有签到资格
     * @param uid
     * @return
     */
    String cannotSign(String uid);

    /**
     * 获取积分
     * @param params
     * @return
             */
    int clickToGetPoints(IntegralParamBean params);

    /**
     * 会员中心数据
     * @param uid
     * @return
     */
    UserRecordPojo queryVipUserInfo(String uid);

    /**
     * 查询等级对应经验
     * @param level
     * @return
     */
    String queryLevelExper(String level);
}

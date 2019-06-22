package com.caiyi.lottery.tradesystem.redpacketcenter.service.impl;

import bean.SafeBean;
import bean.SourceConstant;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import redpacket.bean.Card_CardType;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.*;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.CardChargeRpService;
import com.caiyi.lottery.tradesystem.redpacketcenter.util.RedPacketCenterUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.UserPojo;
import util.UserErrCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 卡密兑换
 */

@Slf4j
@Service
public class CardChargeRpServiceImpl implements CardChargeRpService{

    @Autowired
    private Card_CardTypeMapper card_cardTypeMapper;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Autowired
    private UserRedpacket_RedpacketMapper rpMapper;

    @Autowired
    private UserBasicInfoInterface userInfoInterface;

    @Autowired
    private SafeCenterInterface safeCenterInterface;


    @Override
    public void CardCharge(RedPacketBean bean) throws Exception{
        Card_CardType card=card_cardTypeMapper.queryCard_CardType(bean.getCcardid(),bean.getCcardpwd());
        if(card!=null){
            if(card.getIactive()==1){//已激活
                bean.setBusiErrCode(1006);
                bean.setBusiErrDesc("红包已使用过，不能重复使用。");
            }else{
                if(card.getIstate()==0){
                    bean.setBusiErrCode(1100);
                    bean.setBusiErrDesc("你的卡密已作废不能激活。");
                    return ;
                }
                checkUserCardInfo(bean,card);
                if(bean.getBusiErrCode()!=0){//检查出错
                    return;
                }
                BaseBean baseBean=new BaseBean();
                baseBean.setUid(bean.getUid());
                BaseResp<UserPojo> resp=userInfoInterface.queryUserInfoForCardCharge(new BaseReq<>(baseBean, SysCodeConstant.REDISCENTER));//请求用户中心获取用户信息
                UserPojo user=getUserRealInfoFromSafeCenter(bean.getUid());
                if(resp.getData()!=null&&user!=null){
                    bean.setOldAgentId(resp.getData().getAgentid());
                    handleCardCharge(bean, card,user);
                }else {
                    handleCardChargeNoUser(bean, baseBean);
                }
            }
        }else{
            bean.setBusiErrCode(1002);
            bean.setBusiErrDesc("红包卡号密码不匹配！");
        }
    }

    private void handleCardChargeNoUser(RedPacketBean bean, BaseBean baseBean) {
        BaseResp<UserPojo> resp = userInfoInterface.queryUserInfo(new BaseReq<>(baseBean, SysCodeConstant.REDISCENTER));
        if (resp.getData() != null) {
            UserPojo user = resp.getData();
            String idcard = user.getIdcard();
            int mobbind = user.getMobileBind();
            if (StringUtil.isEmpty(idcard) && mobbind == 0) {
                bean.setBusiErrCode(1020);
                bean.setBusiErrDesc("您还未绑定手机号、身份证，无法使用红包充值，请绑定手机号、身份证后重试");
            } else {
                if (StringUtil.isEmpty(idcard)) {
                    bean.setBusiErrCode(1030);
                    bean.setBusiErrDesc("您还未绑定身份证，无法使用红包充值，请绑定身份证后重试");
                } else {
                    bean.setBusiErrCode(1040);
                    bean.setBusiErrDesc("您还未绑定手机号，无法使用红包充值，请绑定手机号后重试");
                }
            }
        }else{
            bean.setBusiErrCode(1050);
            bean.setBusiErrDesc("没有查询到您的相关个人信息");
        }
    }

    private void handleCardCharge(RedPacketBean bean, Card_CardType card, UserPojo user) {
        card.setItype(card.getItype());
        card.setCactivenick(bean.getUid());
        card.setRealnamemd5(MD5Helper.md5Hex(user.getRealName()));
        card.setIdcardmd5(MD5Helper.md5Hex(user.getIdcard()));
        card.setMobilenomd5(MD5Helper.md5Hex(user.getMobileNo()));
        int cnt1=cardMapper.countCardType1(card);
        log.info("已激活次数:" + cnt1 +"---itype:"+card.getItype()+",cardid:" + bean.getCcardid() + ",卡密：" + bean.getCcardpwd());
        if(cnt1>=1){ //该用户已经激活过该批次的卡密
            int inums=card.getInums();
            log.info("该批次的卡密同一用户能用:"+inums+"次");
            if(cnt1>=card.getInums()){
                bean.setBusiErrCode(1005);
                bean.setBusiErrDesc("您已使用过" + cnt1 +"次该类型的卡密，同一用户最多只能使用" + inums+ "次");
                return;
            } else {
                inums = 10*inums; //真实姓名可激活次数10倍关系
                int cnt2=cardMapper.countCardType2(card);
                //log.info("用户名称：" + card.getRealname() + "，激活次数：" + cnt2);
                if (cnt2>=inums) {
                    bean.setBusiErrCode(1005);
                    bean.setBusiErrDesc("激活次数已超过最大次数");
                    return;
                }
            }
        }
        sendPacketToUser(bean,card,user);//送红包
    }

    //执行卡密兑换红包
    private void sendPacketToUser(RedPacketBean bean, Card_CardType card, UserPojo user) {
        bean.setCrpid(card.getCrpid());
        bean.setCnickid(bean.getUid());
        bean.setCdeaddate(card.getCrpdiedate());
        bean.setImoney(String.valueOf(card.getImoney()));
        bean.setCoperator(bean.getUid());
        bean.setIgetType("3"); //卡密激活
        bean.setIcardid(bean.getCcardid());
        bean.setCmemo(bean.getCcardpwd());
        bean.setDispatchtime("");
        rpMapper.sendRedPacket(bean);
        if(bean.getBusiErrCode()==0&& !CheckUtil.isNullString(bean.getCupacketid())){//送红包成功
            String cupacketid = bean.getCupacketid();
            card.setCactivenick(bean.getUid());
            card.setCactiveip(bean.getIpAddr());//此处要在web层做
            card.setCcardid(bean.getCcardid());
            card.setCcardpwd(bean.getCcardpwd());
            card.setCupacketid(cupacketid);
            card.setMobileno(user.getMobileNo());
            card.setIdcard(user.getIdcard());
            card.setRealname(user.getRealName());
            int number=cardMapper.updateActiveCnt(card);
            log.info("cupacketid:" + cupacketid + ";errdesc:" + bean.getBusiErrDesc() );
            if( number== 1){
                log.info("卡密激活成功【用户名：" + bean.getUid()+"ip:" + bean.getIpAddr() + ";" +
                        "卡号:" + bean.getCcardid() + ";卡密：" + bean.getCcardpwd() + ";用户红包id:" + cupacketid + "】");
                changeUserAgent(bean);
            }else{
                bean.setBusiErrCode(UserErrCode.ERR_CHECK);
                bean.setBusiErrDesc("卡密激活异常");
            }

        }else {
            log.info("激活红包失败，错误原因：" + bean.getBusiErrDesc());
        }
    }

    /**
     * 卡密激活红包后  修改source值对应的agentid代理
     */
    private void changeUserAgent(RedPacketBean bean) {
        String from = bean.getComeFrom();
        log.info("用户：" + bean.getUid() + " from: " + from);
        boolean boolIos = "ios".equals(from);
        boolean boolAndroid = "android".equals(from);
        if (boolIos || boolAndroid){//ios域名为iphone.9188.com  android域名为mobile.9188.com
            String uid = bean.getUid();
            BaseBean baseBean=new BaseBean();
            baseBean.setUid(uid);
            BaseResp<Integer> resp=userInfoInterface.countUserCharge(new BaseReq<>(baseBean, SysCodeConstant.REDISCENTER));
            if(!"0".equals(resp.getCode())){
                log.error("用户中心查询充值次数失败，用户:{}",bean.getUid());
                 return;
            }
            int cidNum=resp.getData();
            if (cidNum == 0) {//如果没有则使用这次卡密source对应的代理 来修改用户表用户的代理
                updateAgentId(bean, boolIos, boolAndroid, uid, baseBean);
            }
        }

    }

    private void updateAgentId(RedPacketBean bean, boolean boolIos, boolean boolAndroid, String uid, BaseBean baseBean) {
        log.info("用户：" + uid + " 没有充值记录。");
        String source=cardTypeMapper.queryCardType(bean.getItype());//数据库中存储ios和Android的source，|前的为ios  | 后的为Android  比如： 2005|1005
        if(!StringUtil.isEmpty(source)){
            String newSource = "";
            String[] sourceArr = source.split("\\|");
            if ((sourceArr.length == 1) && boolIos) {    //ios 或者 ios|
                newSource = sourceArr[0];
            } else if (sourceArr.length == 2) {  //ios|android   或者  |android
                if (boolAndroid) {
                    newSource = sourceArr[1];
                } else if (boolIos) {
                    newSource = sourceArr[0];
                }
            }
            log.info("source: " + source + "newSource: " + newSource);
            baseBean.setSource(Integer.valueOf(newSource));
            BaseResp<String> res=userInfoInterface.queryAppagentId(new BaseReq<>(baseBean, SysCodeConstant.REDISCENTER));
            if(res.getCode()==null||!"0".equals(res.getCode())){
                log.error("用户中心查询appagentid失败,用户:{}",bean.getUid());
                return;
            }
            String agentid=res.getData();
            if(!StringUtil.isEmpty(agentid)){
                log.info("用户：" + uid + " isource 为： " + newSource);
                UserBean userBean=new UserBean();
                userBean.setAgentid(agentid);
                userBean.setUid(bean.getUid());
                BaseResp ret=userInfoInterface.updateAgentId(new BaseReq<>(userBean, SysCodeConstant.REDISCENTER));
                if(ret==null||!"0".equals(ret.getCode())){
                    log.info("用户：" + uid + " 卡密激活红包后  source值对应的 " + bean.getOldAgentId() +" 修改为  " + agentid + " 代理    修改失败！");
                    return;
                }
                 log.info("用户：" + uid + " 卡密激活红包后  source值对应的 " + bean.getOldAgentId() +" 修改为  " + agentid + " 代理    修改成功！");
            }
        }
    }

    private void checkUserCardInfo(RedPacketBean bean, Card_CardType card) throws ParseException {
        String ccarddiedate = card.getCexpireddate();//卡密激活截止时间
        Date carddiedate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ccarddiedate);
        String crpdiedate = card.getCrpdiedate(); //红包过期时间
        crpdiedate = RedPacketCenterUtil.getCupacketDeadDate(ccarddiedate, crpdiedate);
        card.setCrpdiedate(crpdiedate);
        if (null == crpdiedate){
            bean.setBusiErrCode(1101);
            bean.setBusiErrDesc("红包过期时间不正确。");
            return ;
        }
        int itype = card.getItype();//批次
        int crpid = card.getCrpid();//卡密对应的红包id
        bean.setItype(String.valueOf(itype));
        if(crpid == 741){
            bean.setBusiErrCode(1009);
            bean.setBusiErrDesc("卡密已失效，活动停止。");
            return ;
        }
        if(crpid == 941){
            bean.setBusiErrCode(1010);
            bean.setBusiErrDesc("卡密暂停使用，敬请期待。");
            return ;
        }
        if(carddiedate.compareTo(new Date())<=0){//卡密已过期
            bean.setBusiErrCode(1007);
            bean.setBusiErrDesc("卡密已过期，不能激活");
        }
    }

    /**
     * 根据用户从安全中心获取用户真实信息
     * @param uid 用户名
     * @return
     */
    private UserPojo getUserRealInfoFromSafeCenter(String uid){
       UserPojo user=new UserPojo();
       SafeBean bean=new SafeBean();
       bean.setNickid(uid);
       bean.setUsersource(SourceConstant.CAIPIAO);
       BaseResp<SafeBean> resp=safeCenterInterface.getUserTable(new BaseReq<>(bean, SysCodeConstant.REDISCENTER));
       if(!(BusiCode.SUCCESS).equals(resp.getCode())){//安全中心没有用户真实信息
           return null;
       }
       bean=resp.getData();
       String realname=bean.getRealname();
       user.setRealName(realname);
       String idcard=bean.getIdcard();
       user.setIdcard(idcard);
       String mobileno=bean.getMobileno();
       user.setMobileNo(mobileno);
       if(StringUtil.isEmpty(realname)||StringUtil.isEmpty(idcard)||StringUtil.isEmpty(mobileno)){
           return null;
       }
       return user;
    }

}

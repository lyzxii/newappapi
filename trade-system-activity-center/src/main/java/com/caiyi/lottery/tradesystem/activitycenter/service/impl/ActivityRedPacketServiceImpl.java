package com.caiyi.lottery.tradesystem.activitycenter.service.impl;

import activity.bean.ActivityBean;
import com.caiyi.lottery.tradesystem.activitycenter.service.ActivityRedPacketService;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.clientwrapper.PayBasicWrapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.clientwrapper.RedPacketBasicWrapper;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.UserPojo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wxy
 * @create 2017-12-27 11:46
 **/
@Slf4j
@Service
public class ActivityRedPacketServiceImpl implements ActivityRedPacketService {
    @Autowired
    private UserBasicInfoWrapper userBasicInfoWrapper;
    @Autowired
    private RedPacketBasicWrapper redPacketBasicWrapper;
    @Autowired
    private PayBasicWrapper payBasicWrapper;
    /**
     * 获取新用户88元活动红包验证
     * @param bean
     * @throws Exception
     */
    @Override
    public void getEightyRedPacketCheck(ActivityBean bean) throws Exception {
        log.info("[充20元送68红包活动] 用户名:" + bean.getUid());
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.CZSHB_HUODONG));
        List<String> list = new ArrayList<String>();
        if (xml != null) {
            List<JXmlWrapper> nodes = xml.getXmlNodeList("activity");
            if (nodes != null && nodes.size() > 0) {
                for (JXmlWrapper node : nodes) {
                    if ("czshb2017".equals(node.getStringValue("@id"))) {
                        list = Arrays.asList(node.getStringValue("@agentid").split(","));
                    }
                    String now = TimeUtil.currentDateTime();
                    String stime = node.getStringValue("@begindate");
                    String etime = node.getStringValue("@enddate");
                    if (!StringUtil.isEmpty(stime) && now.compareTo(stime) < 0) {
                        bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_FIT));
                        bean.setBusiErrDesc("不满足活动条件");
                        return ;
                    }
                    if (!StringUtil.isEmpty(etime) && now.compareTo(etime) > 0) {
                        bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_FIT));
                        bean.setBusiErrDesc("不满足活动条件");
                        return ;
                    }
                }
            }
        }
        UserPojo userPojo = userBasicInfoWrapper.queryUserInfo(bean,log, SysCodeConstant.ACTIVITYCENTER);
        if (list == null || list.size() <=0) {
            log.info("[充20元送68红包活动] 无代理商限制，全渠道可领取。");
        } else {
            if (!StringUtil.isEmpty(userPojo.getAgentid())) {
                if (!list.contains(userPojo.getAgentid())) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_AGENT));
                    bean.setBusiErrDesc("该渠道不参与活动");
                    return ;
                }
            }

        }
        int num = redPacketBasicWrapper.isGetRedPacket(bean, log, SysCodeConstant.ACTIVITYCENTER);
        if (num > 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("红包已领取");
            return ;
        }

        //用户名检测新用户,一年内无消费记录
        num = userBasicInfoWrapper.countOutByNickidInAYear(bean, log, SysCodeConstant.ACTIVITYCENTER);
        if (num > 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_NEW));
            bean.setBusiErrDesc("不是新用户");
            return ;
        }
        //检测绑定绑定手机号
        //检测绑定身份证
        String cmobileno = userPojo.getMobileNo();
        String cidcard = userPojo.getIdcard();
        if (StringUtil.isEmpty(cmobileno)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_BIND_MOBILE));
            bean.setBusiErrDesc("用户未绑定手机号");
            return ;
        }
        if (StringUtil.isEmpty(cidcard)) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_BIND_IDCARD));
            bean.setBusiErrDesc("用户未绑定身份证");
            return ;
        }
        log.info("[充20元送68红包活动] 手机号:" + cmobileno + " 身份证:" + cidcard);
        //手机号、身份证检测新用户
        bean.setMd5Mobile(userPojo.getMobileNoMD5());
        bean.setMd5IdCard(userPojo.getIdCardMD5());
        num = userBasicInfoWrapper.isNewUser(bean, log, SysCodeConstant.ACTIVITYCENTER);
        if (num > 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_NEW_MOBILE_IDCARD));
            bean.setBusiErrDesc("手机号和身份证检测不是新用户");
            return ;
        }

        // 是否以其他用户身份领取
        num = redPacketBasicWrapper.havenRedPacket(bean, log, SysCodeConstant.ACTIVITYCENTER);
        if (num > 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_FIT));
            bean.setBusiErrDesc("不满足活动条件");
        } else {
            Integer state = redPacketBasicWrapper.getRedPacketStateByNickid(bean, log, SysCodeConstant.ACTIVITYCENTER);
            if (state != null ) {
                if (state.intValue() == 1) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("红包已领取");
                } else if (state.intValue() == 2 || state.intValue() == 3) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_NOT_FIT));
                    bean.setBusiErrDesc("不满足活动条件");
                }
            } else {
                num = payBasicWrapper.queryFirstIsLower20(bean, log, SysCodeConstant.ACTIVITYCENTER);
                if (num > 0) {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_PAY_LOWER20));
                    bean.setBusiErrDesc("单笔充值订单金额小于20元");
                    return ;
                } else {
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.ACTIVITY_CHECK_REDPACKET_PAY_UNACCOUNT));
                    bean.setBusiErrDesc("未充值或者充值未到账,请等待");
                    return ;
                }
            }
        }
    }
}

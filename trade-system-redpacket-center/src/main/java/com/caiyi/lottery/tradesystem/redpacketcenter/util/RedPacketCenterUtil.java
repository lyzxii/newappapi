package com.caiyi.lottery.tradesystem.redpacketcenter.util;

import com.caiyi.lottery.tradesystem.util.CheckUtil;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 红包中心工具类
 */
@Slf4j
public class RedPacketCenterUtil {

    public static long[] getDistanceTimes(String str1, String str2)throws java.text.ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            log.error("getDistanceTimes",e);
        }
        long[] times = { day, hour, min, sec };
        return times;
    }

    public static String calLeftdays(String deaddate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date ddate = sdf.parse(deaddate);
            Date now = new Date();
            if (ddate.getTime()-now.getTime() < 3 * 24 * 60 * 60 * 1000) {
                return String.valueOf((int)((ddate.getTime()-now.getTime())/(24 * 60 * 60 * 1000)) + 1);
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static boolean checkScale(RedPacketBean packetBean) {
        if(packetBean.getItid()==2){//限制使用比例
            String little_Money = packetBean.getScale().split("/")[1];
            log.info("trade_imoney:"+packetBean.getTrade_imoney()+"----little_money:"+little_Money);
            if(Integer.parseInt(packetBean.getTrade_imoney())<Integer.parseInt(little_Money)){//不满足最小金额使用条件
                return false;
            }

        }
        return true;
    }

    /**
     *
     * @param carddiedate:卡密激活截止时间
     * @param crpdiedate:红包过期时间
     * @return
     */
    public static String getCupacketDeadDate(String carddiedate, String crpdiedate){
        try {
            if (CheckUtil.isNullString(crpdiedate)){ //卡密修改之前数据   只有一个红包过期时间
                return carddiedate;
            } else {
                if (crpdiedate.indexOf(":") != -1 && crpdiedate.split(":").length == 2){ //激活后   xx  后过期
                    String[] strs = crpdiedate.split(":");
                    int space = Integer.parseInt(strs[1]);

                    Calendar cd = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    if ("MONTH".equals(strs[0])){
                        cd.add(Calendar.MONTH, space);
                    } else {
                        if ("WEEK".equals(strs[0])){
                            cd.add(Calendar.DAY_OF_YEAR, space*7);
                        } else {
                            return null;
                        }
                    }
                    String cdeaddate = sdf.format(cd.getTime());
                    return cdeaddate;
                } else { //固定时间过期
                    return crpdiedate;
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }

    }

    public static boolean checkAgent(RedPacketBean bean) {
        boolean checkAgent = false;
        log.info("支持代理商:"+bean.getCagent()+"---支持渠道:"+bean.getIsource());
        log.info("投注代理商:"+bean.getTrade_agent()+"----投注渠道:"+bean.getTrade_isource());
        if ("alipay".equals(bean.getCagent())){ //alipay 用户不能使用红包
            return false;
        }

        if(CheckUtil.isNullString(bean.getCagent())&&CheckUtil.isNullString(bean.getIsource())){//不限代理商和渠道
            checkAgent = true;
        }else{
            if(!CheckUtil.isNullString(bean.getCagent())){//限制代理商
                for(String agentid:bean.getCagent().split(",")){
                    if(bean.getTrade_agent().equals(agentid)){
                        checkAgent = true;
                        break;
                    }
                }
                if(checkAgent){
                    if(!CheckUtil.isNullString(bean.getIsource())){
                        if(bean.getIsource().equals("30000")){//全站均可使用
                            checkAgent = true;
                        }else if(bean.getIsource().equals("20000")){//所有ios
                            if(Integer.parseInt(bean.getTrade_isource())>=2000){
                                checkAgent = true;
                            }else{
                                checkAgent = false;
                            }
                        }else if(bean.getIsource().equals("10000")){//所有android
                            if(Integer.parseInt(bean.getTrade_isource())>=1000&&Integer.parseInt(bean.getTrade_isource())<2000){
                                checkAgent = true;
                            }else {
                                checkAgent = false;
                            }
                        }else{
                            for(String source:bean.getIsource().split(",")){
                                if(source.equals("10000")){
                                    if(Integer.parseInt(bean.getTrade_isource())>=1000&&Integer.parseInt(bean.getTrade_isource())<2000){
                                        checkAgent = true;
                                        break;
                                    }else {
                                        checkAgent = false;
                                    }
                                }else if(source.equals("20000")){
                                    if(Integer.parseInt(bean.getTrade_isource())>=2000){
                                        checkAgent = true;
                                        break;
                                    }else{
                                        checkAgent = false;
                                    }
                                }else{
                                    if(bean.getTrade_isource().equals(source)){
                                        checkAgent = true;
                                        break;
                                    }else {
                                        checkAgent = false;
                                    }
                                }
                            }
                        }

                    }else{
                        checkAgent = true;
                    }
                }
            }else{//不限代理商
                if(bean.getIsource().equals("30000")){//全站均可使用
                    checkAgent = true;
                }else if(bean.getIsource().equals("20000")){//所有ios
                    if(Integer.parseInt(bean.getTrade_isource())>=2000){
                        checkAgent = true;
                    }else{
                        checkAgent = false;
                    }
                }else if(bean.getIsource().equals("10000")){//所有android
                    if(Integer.parseInt(bean.getTrade_isource())>=1000&&Integer.parseInt(bean.getTrade_isource())<2000){
                        checkAgent = true;
                    }else {
                        checkAgent = false;
                    }
                }else{
                    for(String source:bean.getIsource().split(",")){
                        if(source.equals("10000")){
                            if(Integer.parseInt(bean.getTrade_isource())>=1000&&Integer.parseInt(bean.getTrade_isource())<2000){
                                checkAgent = true;
                                break;
                            }else {
                                checkAgent = false;
                            }
                        }else if(source.equals("20000")){
                            if(Integer.parseInt(bean.getTrade_isource())>=2000){
                                checkAgent = true;
                                break;
                            }else{
                                checkAgent = false;
                            }
                        }else{
                            if(bean.getTrade_isource().equals(source)){
                                checkAgent = true;
                                break;
                            }else {
                                checkAgent = false;
                            }
                        }
                    }
                }
            }
        }
        return checkAgent;
    }
    
    public static boolean check(RedPacketBean bean){
		Double sum_money = Double.parseDouble(bean.getImoney());
		Double remain_money = Double.parseDouble(bean.getIrmoney());
		if(checkAgent(bean)){
			if(bean.getIstate()!=1){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
				bean.setBusiErrDesc("红包状态不可用");
				return false;
			}
			
			if((bean.getItid()==2&&(CheckUtil.isNullString(bean.getScale())))||
			   (bean.getItid()==3&&(CheckUtil.isNullString(bean.getCgameid())))||
			   (bean.getItid()==4&&(!CheckUtil.isNullString(bean.getCgameid())||!CheckUtil.isNullString(bean.getScale())))){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
				bean.setBusiErrDesc("红包类型和限制条件不匹配");
				return false;
			}
			
			if(remain_money<=0||sum_money<remain_money){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
				bean.setBusiErrDesc("红包余额不足");
				return false;
			}
			
			if(!CheckUtil.isNullString(bean.getCdeaddate())){
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date deadDate = sdf.parse(bean.getCdeaddate());
					if(deadDate.getTime()<=System.currentTimeMillis()+5000){//5秒
						bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
						bean.setBusiErrDesc("红包已过期");
						return false;
					}
				} catch (ParseException e) {
				    log.error("check",e);
					bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
					bean.setBusiErrDesc("红包已过期");
					return false;
				}
			}
			
			if(bean.getItid()==3){
				boolean check = false;
				for(String s:bean.getCgameid().split(",")){
					if(s.equals(bean.getTrade_gameid())){
						check = true;//支持该彩种
						break;
					}		
				}
				if(!check){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
					bean.setBusiErrDesc("彩种"+bean.getTrade_gameid()+"不能使用该"+"红包");
					return false;
				}
			}
			
			if(bean.getItid()==2){	
				int use_money = Integer.parseInt(bean.getScale().split("/")[0]);//用1块
				int su_money = Integer.parseInt(bean.getScale().split("/")[1]);//如满10块
				
				int temp_trade_imoney = new Double(bean.getTrade_imoney()).intValue();//投注总金额
				int temp_trade_redPacket_money = new Double(bean.getTrade_redPacket_money()).intValue();//使用红包金额
				
				if((temp_trade_imoney/su_money)*use_money<temp_trade_redPacket_money){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
					bean.setBusiErrDesc("总金额"+temp_trade_imoney+"元不能使用"+bean.getTrade_redPacket_money()+"元满" + su_money + "减" + use_money + "红包");
					return false;
				}
			}
		} else {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_RP_CANNOT_USE));
			bean.setBusiErrDesc("投注所属代理商【"+bean.getTrade_agent()+"】,所属渠道【"+bean.getTrade_isource()+"】"+"不能使用该"+"红包");
			return false;
		}
		return true;
    }

    public static String getKy_money(RedPacketBean bean) {
        String ky_money = bean.getIrmoney();
        if(!CheckUtil.isNullString(bean.getTrade_imoney())){
            if(bean.getItid()==2){//限制使用比例
                int use_money = Integer.parseInt(bean.getScale().split("/")[0]);//用1块
                int su_money = Integer.parseInt(bean.getScale().split("/")[1]);//如满10块
                int money = (Integer.parseInt(bean.getTrade_imoney())/su_money)*use_money;
                ky_money = String.valueOf(money);
                if(money>Double.parseDouble(bean.getIrmoney())){//可用金额大于余额
                    ky_money = bean.getIrmoney();
                }

            }else{
                if(Integer.parseInt(bean.getTrade_imoney())<Double.parseDouble(bean.getIrmoney())){//投注金额小于余额
                    ky_money = bean.getTrade_imoney();
                }

            }
        }
        return ky_money;
    }
}

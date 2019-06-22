package com.caiyi.lottery.tradesystem.util;

import com.caipiao.game.GameContains;
import java.util.HashMap;

/**
 * Created by tiankun on 2018/1/8.
 */
public class AlipayUtil {
    private static final String PARTNER = "2088701979587548";
    private static final String KEY = "nwf3nqon295ou4z8p16uza1kabmkillj";
    private static HashMap<String, String> ibizs = new HashMap<String, String>();
    private static HashMap<String, String> banks = new HashMap<String, String>();

    private static String ENCRYPT_FACTOR = "000000000000_";
    static{
        ibizs.put("200","用户充值");
        ibizs.put("201","代购中奖");
        ibizs.put("202","跟单中奖");
        ibizs.put("203","中奖提成");
        ibizs.put("204","追号中奖");
        ibizs.put("210","代购撤单返款");
        ibizs.put("211","认购撤单返款");
        ibizs.put("212","追号撤销返款");
        ibizs.put("213","提现撤销返款");
        ibizs.put("214","提款失败转款");
        ibizs.put("215","保底返款");
        ibizs.put("216","红包派送");
        ibizs.put("300","转款");
        ibizs.put("100","代购");
        ibizs.put("101","认购");
        ibizs.put("102","追号");
        ibizs.put("103","保底认购");
        ibizs.put("104","提现");
        ibizs.put("105","保底冻结");
        ibizs.put("99","转账");

        banks.put("1", "快钱");
        banks.put("2", "财付通");
        banks.put("3", "支付宝");
        banks.put("4", "百付宝");
        banks.put("5", "手机充值卡");
        banks.put("6", "银联手机支付");
        banks.put("9", "手机充值卡");
        banks.put("10", "快捷支付");
    }


    private static String getBizType(String bittype){
        String ct = ibizs.get(bittype);
        if(ct == null){
            ct = "其他";
        }
        return ct;
    }

    private static String getGame(String gameid){
        String ct = GameContains.getGameName(gameid);
        if(ct == null){
            ct = "其他";
        }
        return ct;
    }

    private static String getBank(String gameid){
        String ct = banks.get(gameid);
        if(ct == null){
            ct = "其他";
        }
        return ct;
    }

    private static String getMemo(String memo, String ibiztype){
        String [] arr= com.mina.rbc.util.StringUtil.splitter(memo, "|");
        String rmemo = "";
        if (arr.length>1){
            switch (Integer.valueOf(ibiztype)){
                case 200:
                    rmemo = getBank(arr[0])+"充值 订单号" +arr[1];
                    break;
                case 100:
                case 101:
                case 103:
                case 105:
                case 201:
                case 202:
                case 203:
                case 204:
                case 210:
                case 211:
                case 215:
                    rmemo = getGame(arr[0]) + getBizType(ibiztype);
                    break;
                case 102:
                case 212:
                    String [] NT= com.mina.rbc.util.StringUtil.splitter(arr[0], "ZH");
                    rmemo = getGame(NT[0]) + getBizType(ibiztype);
                    break;
                case 300:
                    rmemo = "转款";
                    break;
                case 213:
                default:
                    break;
            }
        }
        if(rmemo.length() > 0){
            return rmemo;
        }
        return memo;
    }

    /**
     * 加密字符串
     * @param minwen
     * @return
     */
    public static String encryptStr(String minwen){
        String encryptStr = CaiyiEncrypt.encryptStr(minwen);
        String replaceAll = encryptStr.replaceAll("\\+","\\*");
        return ENCRYPT_FACTOR + replaceAll;
    }

    /**
     * 解密字符串
     * @param
     * @return
     */
    public static String dencryptStr(String miwen){
        String replaceAll = miwen.replaceAll("\\*", "\\+");
        String replaceFirst = replaceAll.replaceFirst(ENCRYPT_FACTOR, "");
        return CaiyiEncrypt.dencryptStr(replaceFirst);
    }

}

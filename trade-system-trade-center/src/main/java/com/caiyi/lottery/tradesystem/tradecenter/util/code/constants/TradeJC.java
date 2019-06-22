package com.caiyi.lottery.tradesystem.tradecenter.util.code.constants;

import java.util.HashMap;
import java.util.Map;

public class TradeJC {

    public static HashMap<String, String> HHMaps = new HashMap<String, String>();//混合投注
    static {
        HHMaps.put("3-3", "胜-胜");
        HHMaps.put("3-1", "胜-平");
        HHMaps.put("3-0", "胜-负");
        HHMaps.put("1-3", "平-胜");
        HHMaps.put("1-1", "平-平");
        HHMaps.put("1-0", "平-负");
        HHMaps.put("0-3", "负-胜");
        HHMaps.put("0-1", "负-平");
        HHMaps.put("0-0", "负-负");
        HHMaps.put("胜其它", "胜其它");
        HHMaps.put("1:0", "1:0");
        HHMaps.put("2:0", "2:0");
        HHMaps.put("2:1", "2:1");
        HHMaps.put("3:0", "3:0");
        HHMaps.put("3:1", "3:1");
        HHMaps.put("3:2", "3:2");
        HHMaps.put("4:0", "4:0");
        HHMaps.put("4:1", "4:1");
        HHMaps.put("4:2", "4:2");
        HHMaps.put("5:0", "5:0");
        HHMaps.put("5:1", "5:1");
        HHMaps.put("5:2", "5:2");
        HHMaps.put("平其它", "平其它");
        HHMaps.put("0:0", "0:0");
        HHMaps.put("1:1", "1:1");
        HHMaps.put("2:2", "2:2");
        HHMaps.put("3:3", "3:3");
        HHMaps.put("负其它", "负其它");
        HHMaps.put("0:1", "0:1");
        HHMaps.put("0:2", "0:2");
        HHMaps.put("1:2", "1:2");
        HHMaps.put("0:3", "0:3");
        HHMaps.put("1:3", "1:3");
        HHMaps.put("2:3", "2:3");
        HHMaps.put("0:4", "0:4");
        HHMaps.put("1:4", "1:4");
        HHMaps.put("2:4", "2:4");
        HHMaps.put("0:5", "0:5");
        HHMaps.put("1:5", "1:5");
        HHMaps.put("2:5", "2:5");
        HHMaps.put("0", "0球");
        HHMaps.put("1", "1球");
        HHMaps.put("2", "2球");
        HHMaps.put("3", "3球");
        HHMaps.put("4", "4球");
        HHMaps.put("5", "5球");
        HHMaps.put("6", "6球");
        HHMaps.put("7", "7球");
        HHMaps.put("让胜", "让胜");
        HHMaps.put("让平", "让平");
        HHMaps.put("让负", "让负");
        HHMaps.put("胜", "胜");
        HHMaps.put("平", "平");
        HHMaps.put("负", "负");
    }

    public static HashMap<String, String> SPFMapname = new HashMap<String, String>();
    static {
        SPFMapname.put("3", "胜");
        SPFMapname.put("1", "平");
        SPFMapname.put("0", "负");
    }


    public static HashMap<String, String> HHSPMaps = new HashMap<String, String>();//混合投注
    static {
        HHSPMaps.put("3-3", "0");
        HHSPMaps.put("3-1", "1");
        HHSPMaps.put("3-0", "2");
        HHSPMaps.put("1-3", "3");
        HHSPMaps.put("1-1", "4");
        HHSPMaps.put("1-0", "5");
        HHSPMaps.put("0-3", "6");
        HHSPMaps.put("0-1", "7");
        HHSPMaps.put("0-0", "8");
        HHSPMaps.put("胜其它", "21");
        HHSPMaps.put("1:0", "9");
        HHSPMaps.put("2:0", "10");
        HHSPMaps.put("2:1", "11");
        HHSPMaps.put("3:0", "12");
        HHSPMaps.put("3:1", "13");
        HHSPMaps.put("3:2", "14");
        HHSPMaps.put("4:0", "15");
        HHSPMaps.put("4:1", "16");
        HHSPMaps.put("4:2", "17");
        HHSPMaps.put("5:0", "18");
        HHSPMaps.put("5:1", "19");
        HHSPMaps.put("5:2", "20");
        HHSPMaps.put("平其它", "26");
        HHSPMaps.put("0:0", "22");
        HHSPMaps.put("1:1", "23");
        HHSPMaps.put("2:2", "24");
        HHSPMaps.put("3:3", "25");
        HHSPMaps.put("负其它", "39");
        HHSPMaps.put("0:1", "27");
        HHSPMaps.put("0:2", "28");
        HHSPMaps.put("1:2", "29");
        HHSPMaps.put("0:3", "30");
        HHSPMaps.put("1:3", "31");
        HHSPMaps.put("2:3", "32");
        HHSPMaps.put("0:4", "33");
        HHSPMaps.put("1:4", "34");
        HHSPMaps.put("2:4", "35");
        HHSPMaps.put("0:5", "36");
        HHSPMaps.put("1:5", "37");
        HHSPMaps.put("2:5", "38");
        HHSPMaps.put("0", "40");
        HHSPMaps.put("1", "41");
        HHSPMaps.put("2", "42");
        HHSPMaps.put("3", "43");
        HHSPMaps.put("4", "44");
        HHSPMaps.put("5", "45");
        HHSPMaps.put("6", "46");
        HHSPMaps.put("7", "47");
        HHSPMaps.put("让胜", "48");
        HHSPMaps.put("让平", "49");
        HHSPMaps.put("让负", "50");
        HHSPMaps.put("胜", "51");
        HHSPMaps.put("平", "52");
        HHSPMaps.put("负", "53");
    }

    public static HashMap<String, String> HHWFMaps=new HashMap<String, String>();
    static{
        HHWFMaps.put("354", "RQSPF");
        HHWFMaps.put("269", "SPF");
        HHWFMaps.put("270", "JQS");
        HHWFMaps.put("271", "CBF");
        HHWFMaps.put("272", "BQC");
    }

    public static HashMap<String, String> SPF = new HashMap<String, String>();
    static {
        SPF.put("3", "0");
        SPF.put("1", "1");
        SPF.put("0", "2");
    }

    public static Map<String, String> playid = new HashMap<String,String>();
    static {
//		playid.put("34", "90");//让球胜平负
//		playid.put("40", "93");//总进球数
//		playid.put("42", "91");//比分
//		playid.put("51", "92");//半全场
//		playid.put("43", "90");//让球胜平负单关配
        playid.put("70", "70");//混合过关
//		playid.put("44", "72");//混合过关
//		playid.put("45", "72");//混合过关
    }

    public static Map<String, String> ds_playid = new HashMap<String, String>();
    static {
        ds_playid.put("90", "RQSPF");// 让球胜平负
        ds_playid.put("91", "CBF");// 比分
        ds_playid.put("92", "BQC");// 半全场
        ds_playid.put("93", "JQS");// 总进球数
        ds_playid.put("70", "HH");// 总进球数
        ds_playid.put("72", "SPF");// 胜平负
    }
}

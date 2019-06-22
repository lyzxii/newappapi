package com.caiyi.lottery.tradesystem.util;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**.
 * 出票明细
 * @author huzhiqiang
 *
 */
public class TransCodeUtil {
    
    public static HashMap<String, String> WF = new HashMap<String, String>();
    static{
        WF.put("SPF", "胜平负");
        WF.put("RQSPF", "让球胜平负");
        WF.put("BQC", "半全场");
        WF.put("CBF", "比分");
        WF.put("JQS", "总进球");
        WF.put("SF", "胜负");
        WF.put("RFSF", "让分胜负");
        WF.put("DXF", "大小分");
        WF.put("SFC", "胜分差");
    }
    
    
    public static HashMap<String, String> SPF = new HashMap<String, String>();
    static{
        SPF.put("3", "胜");
        SPF.put("1", "平");
        SPF.put("0", "负");
    }
    
    public static HashMap<String, String> RQSPF = new HashMap<String, String>();
    static{
        RQSPF.put("3", "让胜");
        RQSPF.put("1", "让平");
        RQSPF.put("0", "让负");
    }
    
    public static HashMap<String, String> BQC = new HashMap<String, String>();
    static{
        BQC.put("3-3", "胜-胜");
        BQC.put("3-1", "胜-平");
        BQC.put("3-0", "胜-负");
        BQC.put("1-3", "平-胜");
        BQC.put("1-1", "平-平");
        BQC.put("1-0", "平-负");
        BQC.put("0-3", "负-胜");
        BQC.put("0-1", "负-平");
        BQC.put("0-0", "负-负");
    }
    
    public static HashMap<String, String> CBF = new HashMap<String, String>();
    static{
        CBF.put("9:0", "胜其它");
        CBF.put("9:9", "平其它");
        CBF.put("0:9", "负其它");
    }
    
    public static HashMap<String, String> JQS = new HashMap<String, String>();
    static{
        JQS.put("7", "7+");
    }
    
    public static HashMap<String, String> SF = new HashMap<String, String>();
    static{
        SF.put("3", "主胜");
        SF.put("0", "主负");
    }
    
    public static HashMap<String, String> RFSF = new HashMap<String, String>();
    static{
        RFSF.put("3", "让分主胜");
        RFSF.put("0", "让分主负");
    }
    
    public static HashMap<String, String> DXF = new HashMap<String, String>();
    static{
        DXF.put("3", "大分");
        DXF.put("0", "小分");
    }
    
    public static HashMap<String, String> SFC = new HashMap<String, String>();
    static{
        SFC.put("1", "1-5分");
        SFC.put("2", "6-10分");
        SFC.put("3", "11-15分");
        SFC.put("4", "16-20分");
        SFC.put("5", "21-25分");
        SFC.put("6", "26+分");
    }
    
    public static String wk [] = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
    
    public static void main(String[] args) {
        /** 翻译比赛对阵明细
         * 足球
         * HH|141108025>RQSPF=0(5.10),141108041>RQSPF=0(2.02)|2*1
         * CBF|141113003=2:0(10.00)/3:0(23.00)/3:3(60.00)|1*1
         * SPF|141109067=1(3.35)|1*1
         * JQS|141107021=1(4.100)|1*1
         * BQC|141107012=1-3(5.200),141107013=1-3(4.200)|2*1
         * 篮球
         * HH|141108301>DXF(206.5)=3(1.77),141108307>RFSF(+5.5)=0(1.61)|3*1
         * SF|141112301=3(1.20)/0(3.16),141112305=3(1.23)/0(3.00)|2*1
         * HH|141112301>SFC=12(7.700),141112302>SF=3(1.190)|2*1
         * **/
        
        String code = "RQSPF|141109067=1(3.35)|1*1";
        System.out.println(transCode(code));
    }
    
    public static String transCode(String code){
        if (StringUtil.isEmpty(code)) {
            return "";
        }
        String[] match = code.split("\\|");
        
        if (match.length == 3){
            String wfstr = match[0];
            String dz = match[1];
            String[] ms = dz.split(",");

            String id = "";
            String m = "";
            String lxmc = "";
            String html = "";
            for (int i = 0; i < ms.length; i++){
                String matchs = ms[i];
                String lx = "";
                if ("HH".equals(wfstr)){
                    lx = matchs.substring(matchs.indexOf(">") + 1, matchs.indexOf("="));
                    id = matchs.substring(0, matchs.indexOf(">"));
                    id = id.indexOf("(") > -1 ? id.substring(0, id.indexOf("(")) : id;
                    String wf = lx.indexOf("(") > -1 ? lx.substring(0, lx.indexOf("(")) : lx;
                    String rf = lx.indexOf("(") > -1 ? lx.substring(lx.indexOf("(")) : "";
                    lxmc = ">" + WF.get(wf) + rf;
                    m = minfo(wf, matchs.substring(matchs.indexOf("=") + 1));
                } else {
                	lx = match[0];
                    id = matchs.substring(0, matchs.indexOf("="));
                    String rf = ("RFSF".equals(lx) || "DXF".equals(lx)) ? id.substring(id.indexOf("(")) : "";
                    lxmc = rf;
                    m = minfo(wfstr, matchs.substring(matchs.indexOf("=") + 1));
                }
                
                String tDATE = "20" + id.substring(0, 2) + "-" + id.substring(2, 4)
                                + "-" + id.substring(4, 6) + " 00:00:00";
                
                Date mDate = DateTimeUtil.parserDateTime(tDATE);
                Calendar cal = Calendar.getInstance();
                cal.setTime(mDate);
                String pname = wk[cal.get(Calendar.DAY_OF_WEEK) - 1] + "" + id.substring(6, 9);
                String trans = pname + lxmc + "=" + m;
                
                if (StringUtil.isEmpty(html)) {
                    html =  trans;
                } else {
                    html += "\n" + trans;
                }
               
            }
            return html;
        } else {
            return code;
        }
    }
    
    //比赛具体翻译(彩种，赛程)
    public static String minfo(String lot, String m){
        String info = "";
        String[]  mm = m.split("/");
        String sp = "";
        for (int k = 0; k < mm.length; k++){
            String hz;
            String p = mm[k].substring(0, mm[k].indexOf("("));
            if ("CBF".equals(lot) || "JQS".equals(lot)){
                hz = getMap(lot).get(p);
                sp = hz == null  ? p : hz;
                if ("JQS".equals(lot)){ 
                    sp += "球";
                }
                sp += mm[k].substring(mm[k].indexOf("("));
            } else if ("SFC".equals(lot)){
                String zk =mm[k].substring(0, 1);
                String fs = mm[k].substring(1, 2);
                if("0".equals(zk)){
                	zk = "主胜";
                }else{
                	zk = "客胜";
                }
               // zk = zk == "0" ? "主胜" : "客胜";
                fs = getMap(lot).get(fs);
                sp = zk + fs + mm[k].substring(2);
            } else {
                hz = getMap(lot).get(p);
                sp = hz + mm[k].substring(mm[k].indexOf("("));
            }
            if (StringUtil.isEmpty(info)) {
                info = sp;
            } else {
                info += " " + sp;
            }
        }
        return info;
    }
    
    public static HashMap<String, String> getMap(String lot){
        if ("SPF".equals(lot)) {
            return SPF;
        } else if ("RQSPF".equals(lot)) {
            return RQSPF;
        } else if ("BQC".equals(lot)) {
            return BQC;
        } else if ("CBF".equals(lot)) {
            return CBF;
        } else if ("JQS".equals(lot)) {
            return JQS;
        } else if ("SF".equals(lot)) {
            return SF;
        } else if ("RFSF".equals(lot)) {
            return RFSF;
        } else if ("DXF".equals(lot)) {
            return DXF;
        } else if ("SFC".equals(lot)) {
            return SFC;
        }
        return null;
    }
    
    public static String getGuoGuan(String code){
        if (StringUtil.isEmpty(code)) {
            return "";
        }
        String[] match = code.split("\\|");
        
        if (match.length == 3){
           String guoguan = match[2];
           guoguan = guoguan.replace("1*1", "单关").replace("*", "串");
           return guoguan;
        } else {
            return "";
        }
    }
}

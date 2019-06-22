package com.caiyi.lottery.tradesystem.util.proj;

import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.Constants;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LiveBfUtil {
   private static final String QIUPING_NEW_OLD_SWITCH= FileConstant.FILE_90;
    /**
     * 竞彩缓存
     */
    public static Map<String, Map<String, String>> jc_data = new HashMap();
    /**
     *北单缓存
     */
    public static Map<String, Map<String, String>> bd_data = new HashMap();


    public void clearJcData(){
        if (jc_data!=null&&!jc_data.isEmpty()){
            jc_data.clear();
        }
    }
    public void clearBdData(){
        if (bd_data!=null&&!bd_data.isEmpty()){
            bd_data.clear();
        }

    }
    //及时比分
   public static Map<String, String> jsbf(String lotid, String expect,Map<String, Map<String, String>> zqDataMap,boolean isZqDataMap){
       if("84".equals(lotid)&&isZqDataMap){//北单胜负过关没有相关数据
           return null;
       }
        Map<String, String> bfMap = new  HashMap<String, String>();
        
        String  url = "";
        //北单
        if ("84".equals(lotid) || "85".equals(lotid) || "86".equals(lotid) || "87".equals(lotid) 
                        || "88".equals(lotid) || "89".equals(lotid)) {
            if ("84".equals(lotid)) {
                expect = "1" + expect; //胜负过关期次少了一个1
            }

            if( bd_data!= null&&!bd_data.isEmpty()){
                zqDataMap.putAll(bd_data);
                getDatafromCache(bfMap,zqDataMap);
                return bfMap;
            }

           //     url = LiveBfUtil.getPostUrl("jsbfbddetail") + expect + ".xml?rnd=" + Math.random();
            //简化的数据
            url = Constants.BDURL+ expect + ".xml?rnd=" + Math.random();
        }
        //竞彩足球
        if ("70".equals(lotid) || "72".equals(lotid) || "90".equals(lotid) 
                        || "91".equals(lotid) || "92".equals(lotid) || "93".equals(lotid)) {

            if( jc_data!= null&&!jc_data.isEmpty()){
                zqDataMap.putAll(jc_data);
                getDatafromCache(bfMap,zqDataMap);
                return bfMap;
            }
            url = LiveBfUtil.getPostUrl("jsbfjcdetail") + expect + ".xml?rnd=" + Math.random();
        }
        
        JXmlWrapper xml = null;
        try {
            xml = JXmlWrapper.parseUrl(url, "", "utf-8", 10);
            getData(xml, bfMap, zqDataMap, isZqDataMap);

        } catch (Exception e) {
            log.error("获取资料库即时比分错误",e);
        }
        return bfMap;
    }

    private static void getDatafromCache(Map<String, String> bfMap,Map<String, Map<String, String>>  zqDataMap){
        try {
            for (Map.Entry<String, Map<String, String>> entry : zqDataMap.entrySet()) {
                String sort=entry.getKey();
                Map<String, String> temp = entry.getValue();
                String score = temp.get("score");
                String halfScore = temp.get("halfScore");
                String matchState = temp.get("matchState");
                String time = temp.get("time");
                String htime = temp.get("htime");
                getbf(bfMap,sort, score, halfScore, matchState, time, htime);

            }
        } catch (Exception e) {
            log.error("从缓存中获取即时比分错误",e);
        }

    }

    private static void getData( JXmlWrapper xml, Map<String, String> bfMap,Map<String, Map<String, String>>  zqDataMap,boolean isZqDataMap){
        int count = xml.countXmlNodes("row");
        for (int j = 0; j < count; j++) {
            String sort = xml.getStringValue("row[" + j + "].@sort");
            String score = xml.getStringValue("row[" + j + "].@score");
            String halfScore = xml.getStringValue("row[" + j + "].@halfScore");
            String matchState = xml.getStringValue("row[" + j + "].@matchState");
            String bstime = xml.getStringValue("row[" + j + "].@time");
            String htime = xml.getStringValue("row[" + j + "].@htime");
            if (isZqDataMap){
                String qc = xml.getStringValue("row[" + j + "].@stage");
                String roundItemId = xml.getStringValue("row[" + j +"].@itemId");
                String rid = xml.getStringValue("row[" + j + "].@rid");
                String sid = xml.getStringValue("row[" + j + "].@sid");
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("sort", sort);
                dataMap.put("qc", qc);
                dataMap.put("roundItemId", roundItemId);
                dataMap.put("rid", rid);
                dataMap.put("sid", sid);
                zqDataMap.put(sort, dataMap);
            }
            getbf(bfMap,sort, score, halfScore, matchState, bstime, htime);
        }
    }

    private static Map<String, String>  getbf( Map<String, String> bfMap,  String sort, String score, String halfScore,String matchState, String bstime,String htime){
        try {
            java.text.DateFormat mddf = new SimpleDateFormat("MM-dd HH:mm");
            String html = "";
            if (!StringUtil.isEmpty(bstime)) {
                html = mddf.format((Date) Timestamp.valueOf(bstime)) + " 开赛";
                if (Integer.valueOf(matchState) < 4){ //比赛进行中
                    String [] aa = score.split("-");
                    String now = DateUtil.getCurrentFormatDate("yyyy-MM-dd HH:mm:ss");
                    if (!StringUtil.isEmpty(htime)) {
                        String def = dateDiff(htime, now); //上半场开始时间
                        if (Integer.valueOf(def) > 45){
                            def = 45 + "+";
                        }
                        html = def + "′ " + aa[0] + ":" + aa[1];
                    }else {
                        String def = dateDiff(bstime, now); //上半场开始时间
                        if (Integer.valueOf(def) > 45){
                            def = 45 + "+";
                        }
                        html = def + "′ " + aa[0] + ":" + aa[1];
                    }

                    if (Integer.valueOf(matchState) == 2){
                        html = "中 " + aa[0] + ":" + aa[1];
                    } else if (Integer.valueOf(matchState) == 3){
                        if (!StringUtil.isEmpty(htime)) {
                            String hdef  = dateDiff(htime, now); //下半场开始时间
                            int hd = 45 + Integer.valueOf(hdef);
                            hdef = hd + "";
                            if (hd > 90){
                                hdef = 90 + "+";
                            }
                            html = hdef + "′ " + aa[0] + ":" + aa[1];
                        }
                    }
                }
                if (Integer.valueOf(matchState) == 4){   //比赛结束
                    String [] aa = score.split("[-|:]");
                    String [] bb = halfScore.split("[-|:]");
                    html = "已完 " + bb[0] + ":" + bb[1] + "/" + aa[0] + ":" + aa[1];
                }
                bfMap.put(sort, html);
            }
        } catch (Exception e) {
            log.error("组装即时比分错误，sort:{}", sort, e);
        }
        return bfMap;
    }
    
    //比较时间分钟差
    public static String dateDiff(String startTime, String endTime) {   
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
        long nd = 1000 * 24 * 60 * 60; // 一天的毫秒数    
        long nh = 1000 * 60 * 60; // 一小时的毫秒数    
        long nm = 1000 * 60; // 一分钟的毫秒数    
        long diff;    
        long day = 0;    
        long min = 0;    
        // 获得两个时间的毫秒时间差异    
        try {    
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();    
            min = diff % nd % nh / nm + day * 24 * 60; // 计算差多少分钟    
        } catch (ParseException e) {
            log.error("时间转换异常 starttime:"+startTime+" endtime:"+endTime,e);
        }
        return String.valueOf(min);
    }  
    
    //球评、即时比分接口切换选择
    public static String getPostUrl(String type) {
		String url = "";
		JXmlWrapper xml = JXmlWrapper.parse(new File(QIUPING_NEW_OLD_SWITCH));
		String key = xml.getStringValue("@key");
		try{
			if("old".equals(key)){
				JXmlWrapper row = xml.getXmlNode("rowold");
				url = row.getXmlNode(type).getStringValue("@url");
			}else{
				JXmlWrapper row = xml.getXmlNode("rownew");
				url = row.getXmlNode(type).getStringValue("@url");
			}
		}catch(Exception e){
			 return "";
		}
		return url;     
	}

    public static String getUrl(String tag,String property) {
        String url = "";
        JXmlWrapper xml = JXmlWrapper.parse(new File(QIUPING_NEW_OLD_SWITCH));
        String key = xml.getStringValue("@key");
        try{
            if("old".equals(key)){
                JXmlWrapper row = xml.getXmlNode("rowold");
                url = row.getXmlNode(tag).getStringValue("@"+property);
            }else{
                JXmlWrapper row = xml.getXmlNode("rownew");
                url = row.getXmlNode(tag).getStringValue("@"+property);
            }
        }catch(Exception e){
            return "";
        }
        return url;
    }

    //是否新版球评即时比分的接口
    public static boolean isQtVersion() {
		JXmlWrapper xml = JXmlWrapper.parse(new File(QIUPING_NEW_OLD_SWITCH));
		String key = xml.getStringValue("@key");
		try{
			if("old".equals(key)){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			 return false;
		}    
	}


    //篮球资料库数据
    public static Map<String, Map<String, String>> lcDataMap(){

        Map<String, Map<String, String>> jcDataMap = new  HashMap<>();

        String  url = "http://www.9188.com/lqzlk/projbf/match.xml?rnd=" + Math.random();
        JXmlWrapper xml = null;
        try {
            xml = JXmlWrapper.parseUrl(url, "", "utf-8", 10);

            int count = xml.countXmlNodes("row");

            for (int j = 0; j < count; j++) {
                String rid = xml.getStringValue("row[" + j + "].@rid");
                String qc = xml.getStringValue("row[" + j + "].@stage");
                String mid = xml.getStringValue("row[" + j + "].@mid");
                String sort = xml.getStringValue("row[" + j + "].@itemId");
                String ln = xml.getStringValue("row[" + j +"].@league");

                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("rid", rid);
                dataMap.put("qc", qc);
                dataMap.put("mid", mid);
                dataMap.put("sort", sort);
                dataMap.put("ln", ln);
                jcDataMap.put(sort, dataMap);
            }
        } catch (Exception e) {
        }
        return jcDataMap;
    }
   
}

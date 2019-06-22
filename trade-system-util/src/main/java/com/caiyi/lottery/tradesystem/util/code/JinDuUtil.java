package com.caiyi.lottery.tradesystem.util.code;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.caiyi.lottery.tradesystem.util.ConcurrentSafeDateUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;


public class JinDuUtil {
	
	private static final String pattern = "MM-dd";  
	private static final String pattern2 = "MM-dd HH:mm";
	public static final String patternDatabase = "yyyy-MM-dd HH:mm:ss";
	
	private static final String GUOGUAN_DIR = "/opt/export/data/guoguan/";
	
    /**
     * 竞彩篮球各玩法id.
     */
    private static List<String> gidJCLQ = new ArrayList<String>();
    /**
     * 竞彩足球各玩法id.
     */
    private static List<String> gidJCZQ = new ArrayList<String>();
    static {
        gidJCLQ.add("71");
        gidJCLQ.add("94");
        gidJCLQ.add("95");
        gidJCLQ.add("96");
        gidJCLQ.add("97");
        
        gidJCZQ.add("70");
        gidJCZQ.add("72");
        gidJCZQ.add("90");
        gidJCZQ.add("91");
        gidJCZQ.add("92");
        gidJCZQ.add("93");
    }

	public static JinDuUtil  mark(JXmlWrapper xml, int grade) throws ParseException{
		int award=xml.getXmlNode("row").getIntValue("@award");// 计奖标志（0 未计奖 1 正在计奖 2 已计奖)
		int istate=xml.getXmlNode("row").getIntValue("@istate");// 状态(-1未支付 0 禁止认购 1 认购中,2 已满员 3过期未满撤销 4主动撤销 5已出票 6 已派奖)
		int icast=xml.getXmlNode("row").getIntValue("@cast");// 出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）
		int ireturn=xml.getXmlNode("row").getIntValue("@return");// 是否派奖（0 未派奖 1 正在派 2 已派奖）
		
		String awarddate=xml.getXmlNode("row").getStringValue("@awarddate");// 计奖时间
		String endtimes=xml.getXmlNode("row").getStringValue("@endtime");// 截止时间 
		String retdate=xml.getXmlNode("row").getStringValue("@retdate");// 派奖时间
		String gid=xml.getXmlNode("row").getStringValue("@gid");// 游戏编号
	   
		String kjtime="",pjtime="";
	
		if("01".equals(gid)||"07".equals(gid)){
			if(StringUtil.isEmpty(awarddate)){
				kjtime="(预计:"+ ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:45)";
				pjtime="(预计:"+ ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 22:00)";
			}else if((StringUtil.isEmpty(retdate))&&(!StringUtil.isEmpty(awarddate))){
				kjtime="("+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern2)+")";
				pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 22:00)";
			}else if(!StringUtil.isEmpty(retdate)){
				kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
				pjtime="("+ConcurrentSafeDateUtil.convert(retdate, patternDatabase, pattern2)+")";
			}
		}else if("03".equals(gid)){
			if(StringUtil.isEmpty(awarddate)){
				kjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:15)";
				pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:50)";
			}else if((StringUtil.isEmpty(retdate))&&(!StringUtil.isEmpty(awarddate))){
				kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
				pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:50)";
			}else if(!StringUtil.isEmpty(retdate)){
				kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
				pjtime="("+ConcurrentSafeDateUtil.convert(retdate, patternDatabase, pattern2)+")";
			}
		}else if("50".equals(gid)||"51".equals(gid)||"52".equals(gid)||"53".equals(gid)){
			if(StringUtil.isEmpty(awarddate)){
				kjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 20:40)";
				pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:00)";
			}else if((StringUtil.isEmpty(retdate))&&(!StringUtil.isEmpty(awarddate))){
				kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
				pjtime="(预计:"+ConcurrentSafeDateUtil.convert(endtimes, patternDatabase, pattern)+" 21:00)";
			}else if(!StringUtil.isEmpty(retdate)){
				kjtime="("+ConcurrentSafeDateUtil.convert(awarddate, patternDatabase, pattern2)+")";
				pjtime="("+ConcurrentSafeDateUtil.convert(retdate, patternDatabase, pattern2)+")";
			}
        } else if (gidJCZQ.contains(gid) || gidJCLQ.contains(gid)) {
		    // 获取竞彩篮球,竞彩足球方案预计开奖时间
            String pid = xml.getXmlNode("row").getStringValue("@pid");
            String hid = xml.getXmlNode("row").getStringValue("@hid").toLowerCase();
            String xmlpath = GUOGUAN_DIR + gid + File.separator + pid + File.separator + "proj";
            File file = new File(xmlpath, hid + ".xml");
            if (file.exists()) {
                String t = getKjTimeForJincai(file, gid);
                if (!StringUtil.isEmpty(t)) {
                    kjtime = "(预计:" + ConcurrentSafeDateUtil.convert(t, patternDatabase, pattern2) + ")";
                }
            }
        }
		
		
		int kj = award >= 2 ? 1:0;//计奖标志（0 未计奖 1 正在计奖 2 已计奖)   
		
		int isflg = 0;
		if(istate>0){
			isflg = (icast == 3) ? (istate > 2 ? 1 : 5) : (istate > 2 && istate<6) ? 1 : (icast == 2) ?  2 : 3; //出票状态5	
			isflg = (kj == 1) ? ((isflg == 5) ? 6 : isflg ) : isflg; //开奖状态6
			isflg = (award == 2) ? ((isflg == 6) ? 7 : isflg ) : isflg;//计奖状态7
			isflg = (ireturn == 2) ? ((isflg == 7) ? 12 : isflg) : (ireturn == 1) ? ((isflg == 7)? 8 : isflg) : isflg; // 派奖中、已派奖
		}else{
			if(istate==0){
				isflg = 14;
			}else{
				isflg = 13;
			}
		}
		
		String phase = null;
		String percent = null;
		String state = null;
		switch (isflg) {
		case 1://撤单
			phase = "1";
			percent="30";
			state="已撤单";
			break;
		case 2://出票中2
			phase = "1";
			percent="80";
			state="出票中";
			break;
		case 3://等待出票3
			phase = "1";
			percent="70";
			state="等待出票";
			break;
		case 5://出票成功5
			phase = "1";
			percent="100";
			state="出票成功";
			break;
		case 6://已开奖6
			phase = "2";
			percent="100";
			state="已开奖";
			break;
		case 7://已计奖7
			phase = "3";
			percent="60";
			state="已计奖";
			break;
		case 8://派奖中8
			phase = "3";
			percent="80";
			state="派奖中";
			break;
		case 12://已派奖12
			phase = "3";
			percent="100";
			state="已派奖";
			break;
		case 13://未支付
			phase = "1";
			percent="10";
			state="未支付";
			break;
		case 14://处理中
			phase = "1";
			percent="15";
			state="处理中";
			break;
		default://发起0
			phase = "1";
			percent="20";
			state="已发起";
			break;
		}
		if (grade >= 1) {
			if ("出票中".equals(state) || "等待出票".equals(state)) {
				state = "约单中";
			} else if ("已撤单".equals(state)){
				state = "约单失败";
			} else if ("已发起".equals(state)) {
				state = "发起约单";
			} else if ("出票成功".equals(state)){
				state = "约单成功";
			}
		}
		return new JinDuUtil(phase, percent, state, kjtime, pjtime,(isflg + ""));
	}
	
	
	public static String getJindu(JXmlWrapper xml, int grade) throws ParseException{
		StringBuffer sb=new StringBuffer();
		//增加进度节点
		JinDuUtil jindu=mark(xml, grade);
		sb.append("<jindu ");	
		sb.append(JXmlUtil.createAttrXml("node", jindu.getNode()));
		sb.append(JXmlUtil.createAttrXml("percent", jindu.getPercent()));
		sb.append(JXmlUtil.createAttrXml("paint", jindu.getPaint()));
		sb.append(JXmlUtil.createAttrXml("kjtime", jindu.getKjtime()));
		sb.append(JXmlUtil.createAttrXml("pjtime", jindu.getPjtime()));
		sb.append(JXmlUtil.createAttrXml("isflag", jindu.getIsflag()));
		sb.append("/>");	
		return sb.toString();
	}
	
	private String  node;//显示节点
	private String  percent;//百分比
	private String  paint;//方案描述
	private String  kjtime;//开奖时间
	private String  pjtime;//派奖时间
	private String isflag;//实际方案状态码
	
	public JinDuUtil(String node, String percent, String paint ,String kjtime,String pjtime,String isflag) {
		super();
		this.node = node;
		this.percent = percent;
		this.paint = paint;
		this.kjtime=kjtime;
		this.pjtime=pjtime;
		this.isflag = isflag;
	}
	
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getPaint() {
		return paint;
	}
	public void setPaint(String paint) {
		this.paint = paint;
	}
	public String getKjtime() {
		return kjtime;
	}
	public void setKjtime(String kjtime) {
		this.kjtime = kjtime;
	}
	public String getPjtime() {
		return pjtime;
	}
	public void setPjtime(String pjtime) {
		this.pjtime = pjtime;
	}

	public String getIsflag() {
		return isflag;
	}

	public void setIsflag(String isflag) {
		this.isflag = isflag;
	}

	//计算竞技彩开奖时间
    public static String getKjTimeForJincai(File file, String gid) throws ParseException{ 
    	String time = "";
        JXmlWrapper  xml = JXmlWrapper.parse(file);
        List<JXmlWrapper> items = xml.getXmlNodeList("item");
        if (items != null && items.size() > 0) {
        	long maxTimes = 0L;
        	Calendar ca = Calendar.getInstance();
        	for (JXmlWrapper item : items) {
        		long beginTimes = ConcurrentSafeDateUtil.parse(item.getStringValue("@bt"), patternDatabase).getTime();
        		if (beginTimes > maxTimes) {
        			maxTimes = beginTimes;
        		}
        	}
            ca.setTimeInMillis(maxTimes);
            if (gidJCZQ.contains(gid)) {
                // 竞彩足球后推2小时20分钟
                ca.add(Calendar.HOUR_OF_DAY, 2);
                ca.add(Calendar.MINUTE, 40);
            } else if (gidJCLQ.contains(gid)) {
                // 竞彩篮球后推3小时
                ca.add(Calendar.HOUR_OF_DAY, 3);
            }
            time = ConcurrentSafeDateUtil.format(ca.getTime(), patternDatabase);
        }
        
        return time;
    }
	
    
}

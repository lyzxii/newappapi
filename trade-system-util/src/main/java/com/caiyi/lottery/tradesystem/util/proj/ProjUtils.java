package com.caiyi.lottery.tradesystem.util.proj;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caiyi.lottery.tradesystem.util.ConcurrentSafeDateUtil;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;


public class ProjUtils 
{
	private static final String patternDatabase = "yyyy-MM-dd HH:mm:ss";
	/** 竞彩篮球彩种id集合  */
	private static List<String> JCLQ_GID_LIST = new ArrayList<String>();
	
	/** 竞彩足球彩种id集合  */
	private static List<String> JCZQ_GID_LIST = new ArrayList<String>();
	
	static
	{
		JCLQ_GID_LIST.add("71");
		JCLQ_GID_LIST.add("94");
		JCLQ_GID_LIST.add("95");
		JCLQ_GID_LIST.add("96");
		JCLQ_GID_LIST.add("97");
		JCZQ_GID_LIST.add("70");
		JCZQ_GID_LIST.add("72");
		JCZQ_GID_LIST.add("90");
		JCZQ_GID_LIST.add("91");
		JCZQ_GID_LIST.add("92");
		JCZQ_GID_LIST.add("93");
	}
	
	/**
     * 数字彩
     */
    public static HashMap<String, String> SZMaps = new HashMap<>();
    static{
        SZMaps.put("50", "大乐透");
        SZMaps.put("51", "七星彩");
        SZMaps.put("53", "排列三");
        SZMaps.put("52", "排列五");
        SZMaps.put("01", "双色球");
        SZMaps.put("03", "福彩3D");
        SZMaps.put("07", "七乐彩");
    }

	public static HashMap<String, String> zCMaps = new HashMap<>();
	static{
		zCMaps.put("80", "胜负彩");
		zCMaps.put("81", "任选九");
	}
    
    public static HashMap<String, String> SSCMaps = new HashMap<>();
    static{
        SSCMaps.put("04", "老时时彩");
        //SSCMaps.put("20", "新时时彩");
    }
    
    public static HashMap<String, String> X5Maps = new HashMap<>();
    static{
        X5Maps.put("54", "11选5");
        X5Maps.put("55", "广东11选5");
        X5Maps.put("56", "十一运夺金");
        X5Maps.put("57", "上海11选5");
        X5Maps.put("58", "快乐扑克3");
        X5Maps.put("59", "新11选5");
    }
    
    public static HashMap<String, String> K3Maps = new HashMap<>();
    static{
        K3Maps.put("06", "快3");
        K3Maps.put("08", "福彩快3");
        K3Maps.put("09", "江苏快3");
        K3Maps.put("10", "江西快三");
    }
    
    /**
     * 慢频数字彩
     */
    public static HashMap<String, String> SSZMaps = new HashMap<>();
    static{
    	SSZMaps.put("50", "大乐透");
    	SSZMaps.put("51", "七星彩");
    	SSZMaps.put("53", "排列三");
    	SSZMaps.put("52", "排列五");
    	SSZMaps.put("01", "双色球");
    	SSZMaps.put("03", "福彩3D");
    	SSZMaps.put("07", "七乐彩");
    }

	public static HashMap<String, String> GUANYJMaps = new HashMap<String, String>();
	static{
		GUANYJMaps.put("98", "冠军竞猜");
		GUANYJMaps.put("99", "冠亚军竞猜");
	}

	/**
	 *快频各彩种延时开奖时间
	 */
	public static HashMap<String, Integer> DELAYMAPS = new HashMap<String, Integer>();
	static{
		//key彩种id    value开奖延迟时间(秒)
		DELAYMAPS.put("04", 60+20);//老时时彩
		DELAYMAPS.put("54", 20+20);//11选5
		DELAYMAPS.put("55", 45+20);//广东11选5
		DELAYMAPS.put("56", 45+20);//十一运夺金
		//DELAYMAPS.put("20", 80+20);//新时时彩
		DELAYMAPS.put("06", 50+20);//快3
		DELAYMAPS.put("08", 20+20);//福彩快3
		DELAYMAPS.put("57", 100+20);//上海11选5
		DELAYMAPS.put("58", 50);//快乐扑克3
		DELAYMAPS.put("59", 45+20);//新11选5
		DELAYMAPS.put("09", 50);//江苏快3
		DELAYMAPS.put("10", 50);//江西快3
	}
    
	
	 /**
     * 根据当前日期确定方案相关时间的具体显示形式
     * @author 	sjq
     * @param 	addtime		方案状态
     * @param 	endtime		出票状态
     * @param 	casttime		计奖状态
     * @param 	ireturn		派奖状态
     */
    public static Map<String,String> getShowTimeForApp(String addtime,String endtime,String casttime)
    {
    	Calendar calendar = Calendar.getInstance();	//当前时间
		Calendar pjcalendar = Calendar.getInstance();
    	try
    	{
    		//根据当前日期确定方案发起时间的显示形式
    		if(!StringUtil.isEmpty(addtime))
    		{
    			pjcalendar.setTime(DateTimeUtil.parseDate(addtime,DateTimeUtil.DATETIME_FORMAT));
				if(DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 0)
				{
					addtime = "今天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//与当前日期同一天,则只显示 今天 + 时:分
				}
				else
				{
					addtime = DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_MDHM_FORMAT);	//其它情况,则显示: 月-日 时:分
				}
    		}
    		//根据当前日期确定方案截止时间的显示形式
    		if(!StringUtil.isEmpty(endtime))
    		{
    			pjcalendar.setTime(DateTimeUtil.parseDate(endtime,DateTimeUtil.DATETIME_FORMAT));
				if(DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 0)
				{
					endtime = "今天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//与当前日期同一天,则只显示 今天 + 时:分
				}
				else
				{
					endtime = DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_MDHM_FORMAT);	//其它情况,则显示: 月-日 时:分
				}
    		}
    		//根据当前日期确定方案出票时间的显示形式
    		if(!StringUtil.isEmpty(casttime))
    		{
    			pjcalendar.setTime(DateTimeUtil.parseDate(casttime,DateTimeUtil.DATETIME_FORMAT));
				if(DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 0)
				{
					casttime = "今天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//与当前日期同一天,则只显示 今天 + 时:分
				}
				else
				{
					casttime = DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_MDHM_FORMAT);	//其它情况,则显示: 月-日 时:分
				}
    		}
    	}
    	catch(Exception e){}
    	Map<String,String> params = new HashMap<String, String>();
		params.put("addtime",addtime);
		params.put("endtime",endtime);
		params.put("casttime",casttime);
		return params;
    }
	
	/**
	 * 根据彩种id及方案状态获取该方案的开奖时间及派奖时间
	 * @author 	sjq
	 * @param 	gid			彩种id
	 * @param 	pid			期次编号
	 * @param 	hid			方案编号
	 * @param 	iaward		计奖状态
	 * @param	ireturn		派奖状态
	 * @param 	awardtime	计奖时间(如尚未计奖则传递空字符串)
	 * @param 	returntime	派奖时间(如尚未派奖则传递空字符串)
	 * @param 	endtime		方案截止时间
	 */
	public static Map<String,String> getKjPjTimeForApp(String gid,String pid,String hid,int iaward,int ireturn,String awardtime,String returntime,String endtime)
	{
		//根据方案的彩种及计奖派奖状态,获取方案的开奖时间/派奖时间
		Calendar calendar = Calendar.getInstance();		//当前时间
		Calendar pjcalendar = Calendar.getInstance();
		try
		{
			//判断开奖/派奖状态,如果未开奖/未派奖,则计算理论开奖时间
			if((iaward < 2 || ireturn < 2) && !StringUtil.isEmpty(endtime))
			{
				String datestr = DateTimeUtil.formatDate(DateTimeUtil.parseDate(endtime,DateTimeUtil.DATETIME_FORMAT),DateTimeUtil.DATE_FORMAT);
				if("01".equals(gid) || "07".equals(gid))
				{
					awardtime = (iaward < 2)? (datestr + " 21:45:00") : awardtime;
					returntime = (ireturn < 2)? (datestr + " 22:00:00") : returntime;
				}
				else if("03".equals(gid))
				{
					awardtime = (iaward < 2)? (datestr + " 20:40:00") : awardtime;
					returntime = (ireturn < 2)? (datestr + " 20:50:00") : returntime;
				}
				else if("50".equals(gid) || "51".equals(gid)|| "52".equals(gid) || "53".equals(gid))
				{
					awardtime = (iaward < 2)? (datestr + " 20:40:00") : awardtime;
					returntime = (ireturn < 2)? (datestr + " 21:00:00") : returntime;
				} 
				else if(JCLQ_GID_LIST.contains(gid) || JCZQ_GID_LIST.contains(gid)) 
				{
					//获取竞彩篮球/竞彩足球方案的开奖时间
					if(iaward < 2)
					{
						String lasttime = getLastGameBtime(gid,pid,hid);
						if(!StringUtil.isEmpty(lasttime))
						{
							pjcalendar.setTime(DateTimeUtil.parseDate(lasttime,DateTimeUtil.DATETIME_FORMAT));
							if(JCLQ_GID_LIST.contains(gid))
							{
								pjcalendar.add(Calendar.HOUR_OF_DAY,3);	//竞彩篮球开奖时间 = 本方案最晚开赛的场次的开赛时间 推后3小时
							}
							else if(JCZQ_GID_LIST.contains(gid))
							{
								//竞彩足球开奖时间 = 本方案最晚开赛的场次的开赛时间 推后2小时20分
								pjcalendar.add(Calendar.HOUR_OF_DAY,2);
								pjcalendar.add(Calendar.MINUTE,20);
							}
							awardtime = DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATETIME_FORMAT);
						}
					}
				}
			}
			//根据当前日期与开奖时间的早晚来确定开奖时间的显示形式
			String pre = (iaward < 2)? "预计" : "";
			if(!StringUtil.isEmpty(awardtime))
			{
				pjcalendar.setTime(DateTimeUtil.parseDate(awardtime,DateTimeUtil.DATETIME_FORMAT));
				if(DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 0)
				{
					awardtime = pre + "今天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//当前日期与开奖时间同一天,则只显示:时:分
				}
				else if(pjcalendar.after(calendar) && DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 1)
				{
					awardtime = pre + "明天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//开奖日期在当前日期的下一天,则显示:明天 时:分
				}
				else
				{
					awardtime = pre + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_MDHM_FORMAT);	//其它情况,则显示: 月-日 时:分
				}
			}
			else
			{
				awardtime = StringUtil.isEmpty(awardtime)? "" : DateTimeUtil.formatDate(DateTimeUtil.parseDate(awardtime,DateTimeUtil.DATETIME_FORMAT),DateTimeUtil.DATE_MDHM_FORMAT);
			}
			//根据当前日期与派奖时间的早晚来确定派奖时间的显示形式
			pre = (ireturn < 2)? "预计" : "";
			if(!StringUtil.isEmpty(returntime))
			{
				pjcalendar.setTime(DateTimeUtil.parseDate(returntime,DateTimeUtil.DATETIME_FORMAT));
				if(DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 0)
				{
					returntime = pre + "今天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//当前日期与开奖时间同一天,则只显示:时:分
				}
				else if(pjcalendar.after(calendar) && DateTimeUtil.getDaysBetween(calendar,pjcalendar) == 1)
				{
					returntime = pre + "明天" + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_HM_FORMAT);	//开奖日期在当前日期的下一天,则显示:明天 时:分
				}
				else
				{
					returntime = pre + DateTimeUtil.formatDate(pjcalendar.getTime(),DateTimeUtil.DATE_MDHM_FORMAT);	//其它情况,则显示: 月-日 时:分
				}
			}
			else
			{
				returntime = StringUtil.isEmpty(returntime)? "" : DateTimeUtil.formatDate(DateTimeUtil.parseDate(returntime,DateTimeUtil.DATETIME_FORMAT),DateTimeUtil.DATE_MDHM_FORMAT);
			}
		}
		catch(Exception e){}
		Map<String,String> params = new HashMap<String, String>();
		params.put("awardtime",awardtime);
		params.put("returntime",returntime);
		return params;
	}
	
	/**
     * 根据方案状态及用户白名单等级获取方案当前的进度及描述
     * @author 	sjq
     * @param 	istate		方案状态
     * @param 	icast		出票状态
     * @param 	iaward		计奖状态
     * @param 	ireturn		派奖状态
     * @param	bonus		方案中奖金额(未计奖则传递0)
     * @param 	grade		用户白名单等级
     * @return	Map			方案进度信息集合
     */
    public static Map<String,String> getPhaseForApp(int istate,int icast,int iaward,int ireturn,double bonus,int grade)
    {
    	//根据方案状态来判断方案当前的进度信息
    	int flag = 0;
    	int kj = iaward >= 2? 1 : 0;	//是否开奖(1 已开奖 0 未开奖)
		if(istate > 0)
		{
			flag = (icast == 3)? (istate > 2? 1 : 5) : (istate > 2 && istate < 6)? 1 : (icast == 2)? 2 : 3;		//出票状态
			flag = (kj == 1)? ((flag == 5)? 6 : flag) : flag; 		//开奖状态
			flag = (iaward == 2)? ((flag == 6)? 7 : flag) : flag;	//计奖状态
			flag = (ireturn == 2)? ((flag == 7) ? 12 : flag) : (ireturn == 1)? ((flag == 7)? 8 : flag) : flag; //派奖中/已派奖状态
		}
		else
		{
			flag = (istate == 0)? 14 : 13;
		}
		String node = "";		//方案进度(节点)
		String cnode = "0";		//是否有子节点(0 没有子节点 1 有子节点)
		String cnodedesc = "";	//子节点描述
		String zjdesc = (iaward >= 2)?((bonus > 0)? "方案已中奖" : "方案未中奖") : "";	//中奖描述
		String nodedesc1 = "发起预约";
		String nodedesc2 = "预约成功";
		String nodedesc3 = "预约失败";
		String nodedesc4 = "开奖";
		String nodedesc5 = "派奖";
		String iszj = (iaward >= 2)?((bonus > 0)? "1" : "0") : "";
		switch (flag)
		{
			case 1:		//撤单
				node = "3";
				cnode = "0";
				cnodedesc = "";
				break;
			case 2:		//出票中
				node = "1";
				cnode = "1";
				cnodedesc = "预约中";
				break;
			case 3:		//等待出票
				node = "1";
				cnode = "1";
				cnodedesc = "预约中";
				break;
			case 5:		//出票成功
				node = "2";
				cnode = "1";
				cnodedesc = "等待开奖";
				break;
			case 6:		//未计奖
				node = "2";
				cnode = "1";
				cnodedesc = "等待开奖";
			case 7:		//已计奖
				node = "4";
				if(ireturn == 1)
				{
					cnode = "1";
					cnodedesc = "派奖中";
					zjdesc = bonus > 0? "方案已中奖" : "方案未中奖";
				}
				else
				{
					cnode = "0";
					cnodedesc = "0";
					zjdesc = "统计中奖中,请稍后";
				}
				break;
			case 8:		//派奖中
				node = "4";
				cnode = "1";
				cnodedesc = "派奖中";
				break;
			case 12:	//已派奖
				node = "5";
				cnode = "0";
				cnodedesc = "";
				break;
			case 13:	//未支付
				node = "1";
				cnode = "1";
				cnodedesc = "预约中";
				break;
			case 14:	//处理中
				node = "1";
				cnode = "1";
				cnodedesc = "预约中";
				break;
			default:	//发起
				node = "1";
				cnode = "1";
				cnodedesc = "处理中";
				break;
		}
		//返回进度信息
		Map<String,String> jdmaps = new HashMap<String, String>();
		jdmaps.put("node",node);
		jdmaps.put("cnode",cnode);
		jdmaps.put("cnodedesc",cnodedesc);
		jdmaps.put("nodedesc1",nodedesc1);
		jdmaps.put("nodedesc2",nodedesc2);
		jdmaps.put("nodedesc3",nodedesc3);
		jdmaps.put("nodedesc4",nodedesc4);
		jdmaps.put("zjdesc",zjdesc);
		jdmaps.put("iszj",iszj);
		jdmaps.put("nodedesc5",nodedesc5);
		return jdmaps;
    }
    
    /**
     * 根据彩种及方案获取该方案最后一场比赛的时间
     * @author 	sjq
     * @param	gid			彩种id
     * @param	periodid	期次编号
     * @param	cprojid		方案编号
     * @throws ParseException 
     */
    public static String getLastGameBtime(String gid,String periodid,String cprojid) throws ParseException
    {
    	String time = "";
    	String ppath = "/opt/export/data/guoguan"; //方案文件保存路径
    	File file = new File(ppath + File.separator + gid + File.separator + periodid + File.separator + "proj" + File.separator + cprojid.toLowerCase() + ".xml");
    	if(!file.exists())
    	{
    		 return time;
    	}
    	List<JXmlWrapper> items = JXmlWrapper.parse(file).getXmlNodeList("item");
        if (items != null && items.size() > 0) {
        	long maxTimes = 0L;
        	for (JXmlWrapper item : items) {
        		long beginTimes = ConcurrentSafeDateUtil.parse(item.getStringValue("@bt"), patternDatabase).getTime();
        		if (beginTimes > maxTimes) {
        			maxTimes = beginTimes;
        		}
        	}
        }
         return time;
    }
    
    
    /**
     * 检测当前彩种是否为数字彩.
     * @param gid 彩种id
     */
    public static boolean isSzc(String gid) {
        boolean result = false;
        if (SZMaps.containsKey(gid)||zCMaps.containsKey(gid)) {
            result = true;
        } else if (X5Maps.containsKey(gid)) {
            result = true;
        } else if (K3Maps.containsKey(gid)) {
            result = true;
        } else if (SSCMaps.containsKey(gid)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
    
    /**
     * 检测当前彩种是否为慢频数字彩.
     * @param gid 彩种id
     */
    public static boolean isSlowSzc(String gid) {
        boolean result = false;
        if (SSZMaps.containsKey(gid)) {
            result = true;
        } 
        return result;
    }
}
package com.caiyi.lottery.tradesystem.util.push.bean;

import java.util.HashMap;
import java.util.Map;

public class PushConstant {
	//消息类型
	public final static String MESSAGE_AWAKE_USER = "1";   //唤醒用户
	public final static String MESSAGE_BUY_REMIND = "2";   //购彩提醒
	public final static String MESSAGE_OPERATION_ACTIVITY = "11"; //运营活动
	public final static String MESSAGE_SYSTEM__NOTICE = "12";//系统公告
	public final static String MESSAGE_VERSION_UPDATE = "13"; //版本更新
	public final static String MESSAGE_LOTTERY_NEWS = "14"; //彩票资讯
	public final static String MESSAGE_DAILY_ACTIVE = "15";  //日常拉活
	public final static String MESSAGE_OTHER = "16"; //其他
	public final static String MESSAGE_LOTTERY_NOTICE = "21"; //开奖通知
	public final static String MESSAGE_REPACKET_REMIND = "22"; //红包提醒
	public final static String MESSAGE_ZHUIHAO_REMIND = "23"; //追号提醒
	public final static String MESSAGE_TICKET_FAIL_REMIND = "24";//出票失败
	public final static String MESSAGE_AWARD_FIRST = "101"; //中奖推送(1)
	public final static String MESSAGE_AWARD_DOUBLE = "102"; //中奖推送(2)
	public final static String MESSAGE_AWARD_MORE = "103";  //中奖推送(3+)
	
	//操作类型
	public final static String OPERATION_OPEN_NOTHING = "1";//打开应用无
	public final static String OPERATION_OPEN_INAPP = "2";//打开应用内页面
	public final static String OPERATION_OPEN_WEB = "3";//打开WEB页面
	public final static String OPERATION_OPEN_EXPLORER = "4";//打开应用无
	
	//条件关系
	public final static String OR = "0"; //并集
	public final static String AND = "1"; //交集
	public final static String NOT = "2"; //非
	public final static String EXCEPT = "3"; //差集
	
	//标签常量
	public final static String TAG_PLATFORM_ANDROID = "android";   //安卓平台
	public final static String TAG_PLATFORM_IOS = "ios";   //ios平台
	public final static String TAG_SSQ_REMIND = "kjtz_ssq"; //双色球开奖
	public final static String TAG_DLT_REMIND = "kjtz_dlt";//大乐透开奖
	public final static String TAG_FC3D_REMIND = "kjtz_fc3d"; //福彩3D开奖
	public final static String TAG_QXC_REMIND = "kjtz_qxc"; //七星彩开奖
	public final static String TAG_QLC_REMIND = "kjtz_qlc";  //七乐彩开奖
	public final static String TAG_PL3_REMIND = "kjtz_pl3"; //排列三开奖
	public final static String TAG_PL5_REMIND = "kjtz_pl5"; //排列五开奖
	public final static String TAG_AWARD_REMIND = "zjtz_all"; //中奖推送
	public final static String TAG_ZH_REMIND = "zhtz_all"; //追号提醒
	public final static String TAG_SSQ_BUY_REMIND = "gctx_ssq";//双色球购彩提醒
	public final static String TAG_DLT_BUY_REMIND = "gctx_dlt"; //大乐透购彩提醒
	public final static String TAG_FC3D_BUY_REMIND = "gctx_fc3d"; //福彩3D购彩提醒
	public final static String TAG_QXC_BUY_REMIND = "gctx_qxc";  //七星彩购彩提醒
	public final static String TAG_QLC_BUY_REMIND = "gctx_qlc"; //七乐彩购彩提醒
	public final static String TAG_PL3_BUY_REMIND = "gctx_pl3";//排列三购彩提醒
	public final static String TAG_PL5_BUY_REMIND = "gctx_pl5"; //排列五购彩提醒
	public final static String TAG_NO_DISTURB = "yjmdr_switch";  //夜间免打扰模式
	
	//推送方式
	public final static String PUBLISHWAY_BY_USENAME = "0"; //按用户名推送
	public final static String PUBLISHWAY_BY_TAG = "1"; //按Tag推送
	
	//是否是定时任务
	public final static String PUSH_IS_NOT_SCHEDULE = "0"; //不是定时任务
	public final static String PUSH_IS_SCHEDULE = "1"; //是定时任务
	
	//是否发送通知
	public final static String PUSH_NOT_NOTIFY = "0"; //不发送通知
	public final static String PUSH_NOTIFY = "1"; //发送通知
	
	//缓存常量Key
	public final static String CACHEKEY_MAIN_MAP = "main_map_switch";//总开关map的key
	public final static String CACHEKEY_MAIN_MAP_LOTTERY = "main_lottery_switch";//开奖通知
	public final static String CACHEKEY_MAIN_MAP_REDPACKET = "main_redpacket_switch";//红包提醒
	public final static String CACHEKEY_MAIN_MAP_ZHUIHAO = "main_zh_switch";//追号提醒
	public final static String CACHEKEY_MAIN_MAP_TICKET = "main_ticketfail_switch";//出票失败提醒
	public final static String CACHEKEY_MAIN_MAP_AWARD = "main_zj_switch";//中奖推送推送
	
	public final static String CACHEKEY_OWNER_MAP = "_zjzh_switch";//个人map的key(使用时在前面拼接用户名)
	public final static String CACHEKEY_OWNER_MAP_AWARD = "zj_switch";//个人中奖key
	public final static String CACHEKEY_OWNER_MAP_ZHUIHAO = "zh_switch";//个人追号key
	
	//对应消息类型的离线时间
	public final static Map<String, Integer> MESSAGE_TIME_MAP = new HashMap<>();
	static{
		MESSAGE_TIME_MAP.put(MESSAGE_OPERATION_ACTIVITY, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_SYSTEM__NOTICE, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_VERSION_UPDATE, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_LOTTERY_NEWS, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_DAILY_ACTIVE, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_OTHER, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_LOTTERY_NOTICE, 1000*3600*2);
		MESSAGE_TIME_MAP.put(MESSAGE_REPACKET_REMIND, 1000*3600*12);
		MESSAGE_TIME_MAP.put(MESSAGE_ZHUIHAO_REMIND, 1000*3600*2);
		MESSAGE_TIME_MAP.put(MESSAGE_TICKET_FAIL_REMIND, 1000*3600*2);
		MESSAGE_TIME_MAP.put(MESSAGE_AWARD_FIRST, 1000*3600*2);
		MESSAGE_TIME_MAP.put(MESSAGE_AWARD_DOUBLE, 1000*3600*2);
		MESSAGE_TIME_MAP.put(MESSAGE_AWARD_MORE, 1000*3600*2);
	}
}

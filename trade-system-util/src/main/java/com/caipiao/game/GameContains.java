package com.caipiao.game;

import java.util.HashMap;

public class GameContains {

	static HashMap<String, String> games = new HashMap<String, String>();
	static HashMap<String, String> kps = new HashMap<String, String>();
	public static HashMap<String, String> szc = new HashMap<String, String>();
	static HashMap<String, String> zcs = new HashMap<String, String>();
	static HashMap<String, String> bds = new HashMap<String, String>();
	static HashMap<String, String> jcs = new HashMap<String, String>();
	static HashMap<String, String> lqs = new HashMap<String, String>();
	static HashMap<String, String> gyj = new HashMap<String, String>();
	static HashMap<String, String> gxsingle = new HashMap<String, String>(); //国信彩出票只能单注
	
	public static HashMap<String, String> names = new HashMap<String, String>();
	
	public static HashMap<String, String> gidNameMap = new HashMap<String, String>();

	static{
		gxsingle.put("01", "01");
		gxsingle.put("03", "03");
		gxsingle.put("07", "07");
		gxsingle.put("55", "55");
		gxsingle.put("56", "56");
		gxsingle.put("59", "59");
		
		kps.put("04", "04");
		kps.put("05", "05");
		kps.put("06", "06");
		kps.put("08", "08");
		kps.put("09", "09");
		kps.put("10", "10");
		kps.put("20", "20");
		kps.put("54", "54");
		kps.put("55", "55");
		kps.put("56", "56");
		kps.put("57", "57");
		kps.put("58", "58");
		kps.put("59", "59");
		games.putAll(kps);
		
		szc.put("01", "01");
		szc.put("03", "03");
		szc.put("07", "07");
		szc.put("50", "50");
		szc.put("51", "51");
		szc.put("52", "52");
		szc.put("53", "53");
		games.putAll(szc);
		
		zcs.put("80", "80");
		zcs.put("81", "81");
		zcs.put("82", "82");
		zcs.put("83", "83");
		games.putAll(zcs);
		
		bds.put("84", "84");
		bds.put("85", "85");
		bds.put("86", "86");
		bds.put("87", "87");
		bds.put("88", "88");
		bds.put("89", "89");
		games.putAll(bds);
		
		jcs.put("70", "70");
		jcs.put("90", "90");
		jcs.put("91", "91");
		jcs.put("92", "92");
		jcs.put("93", "93");
		jcs.put("72", "72");
		games.putAll(jcs);

		lqs.put("71", "71");
		lqs.put("94", "94");
		lqs.put("95", "95");
		lqs.put("96", "96");
		lqs.put("97", "97");
		games.putAll(lqs);
		
		gyj.put("98", "98");
		gyj.put("99", "99");
		games.putAll(gyj);
		
		names.put("01", "双色球");
		names.put("03", "福彩3D");
		names.put("04", "重庆时时彩");
		names.put("05", "快3(JL)");
		names.put("06", "快3(AH)");
		names.put("07", "七乐彩");
		names.put("08", "快3(NM)");
		names.put("20", "江西时时彩");
		names.put("50", "超级大乐透");
		names.put("51", "七星彩");
		names.put("52", "排列五");
		names.put("53", "排列三");
		names.put("54", "11选5");
		names.put("55", "广东11选5");
		names.put("56", "十一运夺金");
		names.put("57", "上海11选5");
		names.put("58", "快乐扑克3");
		names.put("59", "新11选5");
		names.put("80", "胜负彩");
		names.put("81", "任选九");
		names.put("82", "进球彩");
		names.put("83", "半全场");
		names.put("84", "足球单场-胜负过关");
		names.put("85", "足球单场-让球胜平负");
		names.put("86", "足球单场-比分");
		names.put("87", "足球单场-半全场");
		names.put("88", "足球单场-上下单双");
		names.put("89", "足球单场-总进球数");
		names.put("70", "竞彩足球-混合过关");
		names.put("90", "竞彩足球-让球胜平负");
		names.put("72", "竞彩足球-胜平负");
		names.put("91", "竞彩足球-比分");
		names.put("92", "竞彩足球-半全场");
		names.put("93", "竞彩足球-总进球数");
		names.put("71", "竞彩篮球-混合过关");
		names.put("94", "竞彩篮球-胜负");
		names.put("95", "竞彩篮球-让分胜负");
		names.put("96", "竞彩篮球-胜分差");
		names.put("97", "竞彩篮球-大小分");
		names.put("98", "冠军竞猜");
		names.put("99", "冠亚军竞猜");
		
		gidNameMap.put("01", "双色球");
		gidNameMap.put("03", "福彩3D");
		gidNameMap.put("04", "时时彩");
		gidNameMap.put("05", "吉林快3");
		gidNameMap.put("06", "快3");
		gidNameMap.put("07", "七乐彩");
		gidNameMap.put("08", "福彩快三");
		gidNameMap.put("09", "江苏快三");
		gidNameMap.put("10", "新快三");
		gidNameMap.put("50", "大乐透");
		gidNameMap.put("51", "七星彩");
		gidNameMap.put("52", "排列五");
		gidNameMap.put("53", "排列三");
		gidNameMap.put("54", "11选5");
		gidNameMap.put("55", "粤11选5");
		gidNameMap.put("56", "11运夺金");
		gidNameMap.put("57", "沪11选5");
		gidNameMap.put("58", "快乐扑克3");
		gidNameMap.put("59", "新11选5");
		gidNameMap.put("70", "竞彩足球");
		gidNameMap.put("71", "竞彩篮球");
		gidNameMap.put("72", "竞彩足球");
		gidNameMap.put("73", "竞彩足球");
		gidNameMap.put("74", "竞彩篮球");
		gidNameMap.put("80", "胜负彩");
		gidNameMap.put("81", "任选九");
		gidNameMap.put("82", "进球彩");
		gidNameMap.put("83", "半全场");
		gidNameMap.put("84", "北京单场");
		gidNameMap.put("85", "北京单场");
		gidNameMap.put("86", "北京单场");
		gidNameMap.put("87", "北京单场");
		gidNameMap.put("88", "北京单场");
		gidNameMap.put("89", "北京单场");
		gidNameMap.put("90", "竞彩足球");
		gidNameMap.put("91", "竞彩足球");
		gidNameMap.put("92", "竞彩足球");
		gidNameMap.put("93", "竞彩足球");
		gidNameMap.put("94", "竞彩篮球");
		gidNameMap.put("95", "竞彩篮球");
		gidNameMap.put("96", "竞彩篮球");
		gidNameMap.put("97", "竞彩篮球");
		gidNameMap.put("98", "冠军竞猜");
		gidNameMap.put("99", "冠亚军竞猜");

	}

	public static boolean canUse(String gid){
		if(gid == null || "".equals(gid) || "".equals(gid.trim())){
			return true;
		}
		
		try {
			Integer.parseInt(gid);
		} catch (Exception e) {
			return false;
		}
		
		return games.containsKey(gid);
	}
	
	public static boolean canNotUse(String gid){
		return !canUse(gid);
	}
	
	public static boolean isKP(String gid){
		return kps.containsKey(gid);
	}
	
	public static boolean isSZC(String gid){
		return szc.containsKey(gid);
	}
	
	public static boolean isR9(String gid){
		return "81".equals(gid);
	}

	public static boolean isFootball(String gid){
		return jcs.containsKey(gid);
	}
	public static boolean isBasket(String gid){
		return lqs.containsKey(gid);
	}
	public static boolean isBeiDan(String gid){
		return bds.containsKey(gid);
	}
	public static boolean isZc(String gid) {
		return zcs.containsKey(gid);
	}
	public static boolean isGYJ(String gid) {
		return gyj.containsKey(gid);
	}
	public static String getGameName(String gid){
		return names.get(gid);
	}
	public static boolean isGuoXin(String gid){
		return gxsingle.containsKey(gid);
	}
}

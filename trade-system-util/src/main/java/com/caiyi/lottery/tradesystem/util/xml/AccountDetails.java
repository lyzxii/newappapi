package com.caiyi.lottery.tradesystem.util.xml;

import java.util.HashMap;

/**
 * 账号明细
 * @author Administrator
 *
 */
public class AccountDetails {
	

	// 充值类型
	public static HashMap<Integer,String> addmoneytype= new HashMap<Integer,String>();
	static{
		addmoneytype.put(1,"快钱支付");
		addmoneytype.put(2,"财付通支付");
		addmoneytype.put(3,"支付宝支付");
		addmoneytype.put(4,"百付宝支付");
		addmoneytype.put(5,"手机充值卡(易宝)");
		addmoneytype.put(6,"银联手机支付");
		addmoneytype.put(9,"手机充值卡(19pay)");
		addmoneytype.put(10,"支付宝快捷支付");
		addmoneytype.put(11,"盛付通支付");
		addmoneytype.put(12,"联动支付(信用卡)");
		addmoneytype.put(13,"上海导购预付卡");
		addmoneytype.put(14,"支付宝扫码");
		addmoneytype.put(97,"提款失败转款");
		addmoneytype.put(98,"购彩返利");
		addmoneytype.put(99,"手工加款");
		addmoneytype.put(998,"红包派送");
		addmoneytype.put(999,"网吧充值");
		addmoneytype.put(9001,"支付宝(苹果)");
		addmoneytype.put(9000,"支付宝(安卓)");
		addmoneytype.put(9002,"支付宝wap(苹果)");
		addmoneytype.put(9005,"支付宝wap(安卓)");
		
		addmoneytype.put(2000,"支付宝(安卓)");
		addmoneytype.put(2001,"支付宝(苹果)");
		addmoneytype.put(2002,"支付宝wap(苹果)");
		addmoneytype.put(2005,"支付宝wap(安卓)");
		
		addmoneytype.put(9003,"联动支付(苹果)");
		addmoneytype.put(9004,"联动支付(安卓)");
		addmoneytype.put(9006,"银联手机(苹果)");
		addmoneytype.put(9007,"银联手机(安卓)");
		addmoneytype.put(9008,"手机充值卡(苹果)");
		addmoneytype.put(9009,"手机充值卡(安卓)");
		addmoneytype.put(9010,"联通华建(苹果)");
		addmoneytype.put(9011,"联通华建(安卓)");
		addmoneytype.put(2003,"支付宝(东方网)");
		addmoneytype.put(2010,"支付宝扫码(东方网)");
		addmoneytype.put(2014,"支付宝快捷(东方网)");
		addmoneytype.put(2015,"银联电话(3g&触屏)");
		addmoneytype.put(2016,"手机充值卡(3g&触屏)");
		addmoneytype.put(2017,"支付宝(3g&触屏)");
		addmoneytype.put(3014,"支付宝(4g&触屏)");
		
		addmoneytype.put(9012,"联通华建银联充值IOS");
		addmoneytype.put(9014,"支付宝(4g&触屏)");
		addmoneytype.put(9015,"联动支付(4g&触屏)");
		
		addmoneytype.put(2050,"连连支付(借记卡)");
		addmoneytype.put(2051,"连连支付(信用卡)");
		addmoneytype.put(2052,"连连支付(借记卡)");
		addmoneytype.put(2053,"连连支付(信用卡)");
		addmoneytype.put(2054,"连连支付(借记卡)");
		addmoneytype.put(2055,"连连支付(信用卡)");
		
		addmoneytype.put(22,"ApplePay(借记卡)");
		addmoneytype.put(23,"ApplePay(信用卡)");
		addmoneytype.put(24,"支付宝转账");
		addmoneytype.put(25,"支付宝转账");
		addmoneytype.put(27,"支付宝转账");
		addmoneytype.put(28,"支付宝转账");
		
		addmoneytype.put(6000,"银联支付(借记卡)");
		addmoneytype.put(6001,"银联支付(信用卡)");
		addmoneytype.put(6002,"银联支付(借记卡)");
		addmoneytype.put(6003,"银联支付(信用卡)");
		addmoneytype.put(6004,"银联支付(借记卡)");
		addmoneytype.put(6005,"银联支付(信用卡)");
		addmoneytype.put(6006,"银联支付(借记卡)");
		addmoneytype.put(6007,"银联支付(信用卡)");
		
		addmoneytype.put(7000,"易宝支付(借记卡)");
		addmoneytype.put(7001,"易宝支付(信用卡)");
		addmoneytype.put(7002,"易宝支付(借记卡)");
		addmoneytype.put(7003,"易宝支付(信用卡)");
		addmoneytype.put(7004,"易宝支付(借记卡)");
		addmoneytype.put(7005,"易宝支付(信用卡)");
		addmoneytype.put(7006,"易宝支付(借记卡)");
		addmoneytype.put(7007,"易宝支付(信用卡)");
		addmoneytype.put(7010,"智慧支付(借记卡)");
		addmoneytype.put(7011,"智慧支付(信用卡)");
		addmoneytype.put(7012,"智慧支付(借记卡)");
		addmoneytype.put(7013,"智慧支付(信用卡)");
		addmoneytype.put(7014,"智慧支付(借记卡)");
		addmoneytype.put(7015,"智慧支付(信用卡)");
		addmoneytype.put(7016,"智慧支付(借记卡)");
		addmoneytype.put(7017,"智慧支付(信用卡)");
		//梓微信
		addmoneytype.put(30,"微信支付");
		addmoneytype.put(31,"微信支付");
		addmoneytype.put(32,"微信支付");
		//中信微信
		addmoneytype.put(50,"微信支付");
		addmoneytype.put(51,"微信支付");
		//飞客支付宝
		addmoneytype.put(60,"支付宝(苹果)");
		addmoneytype.put(61,"支付宝(安卓)");
		addmoneytype.put(62,"支付宝(H5)");
		//盛付通微信
		addmoneytype.put(40,"微信支付");
		addmoneytype.put(41,"微信支付");
		addmoneytype.put(42,"微信支付");
		addmoneytype.put(44,"支付宝");
		addmoneytype.put(45,"支付宝");
		addmoneytype.put(46,"支付宝");
		addmoneytype.put(47,"支付宝");
		addmoneytype.put(70,"支付宝");
		addmoneytype.put(71,"支付宝");
		addmoneytype.put(72,"支付宝");

		//威富通支付宝sdk
		addmoneytype.put(78, "支付宝");
		addmoneytype.put(79, "支付宝");
		//威富通支付宝
		addmoneytype.put(80,"支付宝");
		addmoneytype.put(81,"支付宝");
		addmoneytype.put(82,"支付宝");
		//威富通微信
		addmoneytype.put(83,"微信支付");
		addmoneytype.put(84,"微信支付");
		addmoneytype.put(85,"微信支付");
		addmoneytype.put(122,"微信支付");
		addmoneytype.put(123,"微信支付");
		addmoneytype.put(124,"微信支付");

		//威富通微信
		addmoneytype.put(86,"微信支付");
		addmoneytype.put(87,"微信支付");
		addmoneytype.put(88,"微信支付");
		
		//贝付宝微信充值
		addmoneytype.put(104,"微信支付");
		addmoneytype.put(105,"微信支付");
		addmoneytype.put(106,"微信支付");
		addmoneytype.put(113,"微信支付");
		addmoneytype.put(114,"微信支付");
		addmoneytype.put(115,"微信支付");
		addmoneytype.put(116,"微信支付");
		addmoneytype.put(117,"微信支付");
		addmoneytype.put(118,"微信支付");
		addmoneytype.put(119,"微信支付");

		//京东支付
		addmoneytype.put(5000,"京东支付");
		addmoneytype.put(5001,"京东支付");
		addmoneytype.put(5002,"京东支付");
		addmoneytype.put(5003,"京东支付");
		addmoneytype.put(5004,"京东支付");
		addmoneytype.put(5005,"京东支付");
		addmoneytype.put(5006,"京东支付");
		addmoneytype.put(5007,"京东支付");
		addmoneytype.put(5008,"京东支付");

		//贝付宝支付宝
		addmoneytype.put(101,"支付宝");
		addmoneytype.put(102,"支付宝");
		addmoneytype.put(103,"支付宝");
		addmoneytype.put(110,"支付宝");
		addmoneytype.put(111,"支付宝");
		addmoneytype.put(112,"支付宝");
		addmoneytype.put(107,"支付宝");
		addmoneytype.put(108,"支付宝");
		addmoneytype.put(109,"支付宝");
		
		//BEECLOUD
		addmoneytype.put(220,"支付宝");
		addmoneytype.put(221,"支付宝");
		addmoneytype.put(222,"支付宝");
		
		//各银行代理微信，支付宝
		addmoneytype.put(120,"微信支付");
		addmoneytype.put(121,"微信支付");
		addmoneytype.put(130,"微信支付");
		addmoneytype.put(131,"微信支付");
		addmoneytype.put(132,"微信支付");
		addmoneytype.put(140,"微信支付");
		addmoneytype.put(141,"微信支付");
		addmoneytype.put(146,"微信支付");
		addmoneytype.put(147,"微信支付");
		addmoneytype.put(148,"微信支付");
		addmoneytype.put(153,"微信支付");
		addmoneytype.put(154,"微信支付");
		addmoneytype.put(156,"微信支付");
		addmoneytype.put(163,"微信支付");
		addmoneytype.put(164,"微信支付");
		addmoneytype.put(166,"微信支付");
		addmoneytype.put(173,"微信支付");
		addmoneytype.put(174,"微信支付");
		addmoneytype.put(176,"微信支付");
		addmoneytype.put(183,"微信支付");
		addmoneytype.put(184,"微信支付");
		addmoneytype.put(186,"微信支付");
		addmoneytype.put(190,"微信支付");
		addmoneytype.put(191,"微信支付");
		addmoneytype.put(192,"微信支付");
		addmoneytype.put(193,"微信支付");
		addmoneytype.put(194,"微信支付");
		addmoneytype.put(195,"微信支付");
		addmoneytype.put(196,"微信支付");
		addmoneytype.put(197,"微信支付");
		addmoneytype.put(198,"微信支付");
		addmoneytype.put(213,"微信支付");
		addmoneytype.put(214,"微信支付");
		addmoneytype.put(236,"微信支付");
		addmoneytype.put(237,"微信支付");
		addmoneytype.put(238,"微信支付");
		
		
		addmoneytype.put(133,"支付宝");
		addmoneytype.put(134,"支付宝");
		addmoneytype.put(135,"支付宝");
		addmoneytype.put(136,"支付宝");
		addmoneytype.put(142,"支付宝");
		addmoneytype.put(143,"支付宝");
		addmoneytype.put(144,"支付宝");
		addmoneytype.put(145,"支付宝");
		addmoneytype.put(150,"支付宝");
		addmoneytype.put(151,"支付宝");
		addmoneytype.put(152,"支付宝");
		addmoneytype.put(155,"支付宝");
		addmoneytype.put(160,"支付宝");
		addmoneytype.put(161,"支付宝");
		addmoneytype.put(162,"支付宝");
		addmoneytype.put(165,"支付宝");
		addmoneytype.put(170,"支付宝");
		addmoneytype.put(171,"支付宝");
		addmoneytype.put(172,"支付宝");
		addmoneytype.put(175,"支付宝");
		addmoneytype.put(180,"支付宝");
		addmoneytype.put(181,"支付宝");
		addmoneytype.put(182,"支付宝");
		addmoneytype.put(185,"支付宝");
		addmoneytype.put(200,"支付宝");
		addmoneytype.put(201,"支付宝");
		addmoneytype.put(202,"支付宝");
		addmoneytype.put(233,"支付宝");
		addmoneytype.put(234,"支付宝");
		addmoneytype.put(235,"支付宝");
		
		//微信扫码
		addmoneytype.put(48,"微信扫码支付");
		addmoneytype.put(49,"微信扫码支付");
		addmoneytype.put(157,"微信扫码支付");
		addmoneytype.put(158,"微信扫码支付");
		
		//联动优势充值
		addmoneytype.put(8000,"借记卡快捷(联动优势)");
		addmoneytype.put(8001,"借记卡快捷(联动优势)");
		addmoneytype.put(8002,"借记卡快捷(联动优势)");
		addmoneytype.put(8003,"信用卡快捷(联动优势)");
		addmoneytype.put(8004,"信用卡快捷(联动优势)");
		addmoneytype.put(8005,"信用卡快捷(联动优势)");
		
		//QQ钱包
		addmoneytype.put(230,"QQ钱包");
		addmoneytype.put(231,"QQ钱包");
		addmoneytype.put(232,"QQ钱包");
		addmoneytype.put(240,"QQ钱包");
		addmoneytype.put(241,"QQ钱包");
		addmoneytype.put(242,"QQ钱包");
		addmoneytype.put(243,"QQ钱包");
		addmoneytype.put(244,"QQ钱包");
		addmoneytype.put(245,"QQ钱包");
		
		//派洛贝微信支付
		addmoneytype.put(273,"微信支付");
		addmoneytype.put(274,"微信支付");
		addmoneytype.put(275,"微信支付");
		
		addmoneytype.put(276,"QQ钱包");
		addmoneytype.put(277,"QQ钱包");
		addmoneytype.put(278,"QQ钱包");

		//派洛贝支付宝
		addmoneytype.put(270,"支付宝");
		addmoneytype.put(271,"支付宝");
		addmoneytype.put(272,"支付宝");

		//微众支付宝
		addmoneytype.put(300,"支付宝");
		addmoneytype.put(301,"支付宝");
		addmoneytype.put(302,"支付宝");

		//伊蚊长沙平安支付宝扫码
		addmoneytype.put(290,"支付宝");
		addmoneytype.put(291,"支付宝");
		addmoneytype.put(292,"支付宝");
		
		//合利宝
		addmoneytype.put(5020,"借记卡快捷(合利宝)");
		addmoneytype.put(5021,"借记卡快捷(合利宝)");
		addmoneytype.put(5022,"借记卡快捷(合利宝)");
		addmoneytype.put(5023,"借记卡快捷(合利宝)");
		addmoneytype.put(5024,"借记卡快捷(合利宝)");
		addmoneytype.put(5025,"借记卡快捷(合利宝)");

		//有氧支付支付宝
		addmoneytype.put(330,"支付宝");
		addmoneytype.put(331,"支付宝");
		addmoneytype.put(332,"支付宝");
		addmoneytype.put(333,"微信");
		addmoneytype.put(334,"微信支付");
		addmoneytype.put(335,"微信支付");

	}
	
	public static HashMap<Integer,String> huodongjiajian= new HashMap<Integer,String>();
	static{
		huodongjiajian.put(80001,"胜负猜中12场活动奖金");
		huodongjiajian.put(81001,"任九猜中8场活动奖金");
		huodongjiajian.put(54001,"11选5擂台赛活动奖金");
		huodongjiajian.put(56001,"十一运夺金擂台赛活动奖金");
		huodongjiajian.put(04001,"时时彩擂台赛活动奖金");
		huodongjiajian.put(20001,"新时时彩擂台赛活动奖金");
		huodongjiajian.put(06001,"快3擂台赛活动奖金");
	}
	
	
	public static HashMap<Integer,String> lot= new HashMap<Integer,String>();
	static{
		lot.put( 1, "双色球");
		lot.put( 3, "福彩3D");
		lot.put( 4, "时时彩");
		lot.put( 5, "新快3");
		lot.put( 6, "快3");
		lot.put( 7, "七乐彩");
		lot.put( 8, "福彩快3");
		lot.put( 20, "新时时彩");
	
		lot.put( 50, "超级大乐透");
		lot.put( 51, "七星彩");
		lot.put( 52, "排列五");
		lot.put( 53, "排列三"); 
		lot.put( 54, "11选5");
		lot.put( 55, "广东11选5");
		lot.put( 56, "11运夺金");
		lot.put( 57, "上海11选5");
		lot.put( 58, "快乐扑克3");
		lot.put( 59, "新11选5");
	
		lot.put( 80, "胜负彩");
		lot.put( 81, "任选九");
		lot.put( 82, "进球彩");
		lot.put( 83, "半全场");
	
		lot.put( 84, "北单胜负过关");
		lot.put( 85, "足球单场-让球胜平负");
		lot.put( 86, "足球单场-比分");
		lot.put( 87, "足球单场-半全场");
		lot.put( 88, "足球单场-上下单双");
		lot.put( 89, "足球单场-总进球数");
	
		lot.put( 90, "竞彩足球-让球胜平负");
		lot.put( 91, "竞彩足球-比分");
		lot.put( 92, "竞彩足球-半全场");
		lot.put( 93, "竞彩足球-总进球数");
		lot.put( 70, "竞彩足球-混合过关");
		lot.put( 72, "竞彩足球-胜平负");
	
		lot.put( 94, "竞彩篮球-胜负");
		lot.put( 95, "竞彩篮球-让分胜负");
		lot.put( 96, "竞彩篮球-胜分差");
		lot.put( 97, "竞彩篮球-大小分");
		lot.put( 71, "竞彩篮球-混合过关");
		lot.put( 98, "冠军竞猜");
		lot.put( 99, "冠亚军竞猜");
	}
	
	public static String ShowCmemo(int ibiztype, String cmemo){
        String memo = null;
		String[] memoarr=cmemo.trim().split("\\|");
		if(memoarr.length>1){	
            switch(ibiztype){					
			case 200:
				//memo=getaddmoneyname(memoarr[0])+","+memoarr[1];
				memo=memoarr[0]+","+memoarr[1];
				break;				
			case 100:
			case 101:
			case 103:
				//memo=getlotname(memoarr[0])+","+memoarr[1];
				memo=memoarr[0]+","+memoarr[1];
				break;
			case 105:
				//memo=getlotname(memoarr[0])+","+memoarr[1];
				memo=memoarr[0]+","+memoarr[1].replaceAll("\\[事后保底\\]", "");//保底冻结
				break;
			case 201:		
			case 202:
			case 203:	
			case 210:
			case 211:	
            case 215:
            case 113: //分享神单 打赏分享人 
            case 256: //分享神单 跟买中奖 
	        case 257: //分享神单 中奖打赏 (分享人加款)
				//memo=getlotname(memoarr[0])+","+memoarr[1];
				memo=memoarr[0]+","+memoarr[1];
				break;				
			case 102:	
			case 98:
			case 212:
				String[] NT=memoarr[1].split("ZH");
				//memo=getlotname(NT[0])+","+memoarr[1];
				memo=NT[0]+","+memoarr[1];
				break;	
			case 204:	
				//memo=getlotname(memoarr[0])+","+memoarr[2];
				memo=memoarr[0]+","+memoarr[2];
				break;	
			case 300:
				memo=ibiztype+","+ibiztype;
				//memo="转款,后台转款";
				break;
			case 104:
				memo=ibiztype+","+ibiztype;
				//memo="提款,提款到银行卡";
				break;
			
			case 107:
			case 216:
			case 255:
				memo=ibiztype+","+memoarr[0];
				//memo="活动奖金,"+gethuodongjiajian(memoarr[0]);
				break;		
			case 213:	
			default:
				break;
			}
		}else {
			memo=memoarr[0];
		}
		return memo;
	}
	
	public static String getaddmoneyname(String value){
		for(Integer key : addmoneytype.keySet()){
			if (value.equals(key.toString())) {
				 return addmoneytype.get(key) ;       
			} 
		}
		return null;
	}
	
	public static String getlotname(String value){
		for(Integer key : lot.keySet()){
			if (Integer.valueOf(value)==key) {
				 return lot.get(key) ;       
			} 
		}
		return null;
	}
	

	
	public static String gethuodongjiajian(String value){
		for(Integer key : huodongjiajian.keySet()){
			if (value.equals(key.toString())) {
				 return huodongjiajian.get(key) ;       
			} 
		}
		return null;
	}
	
}

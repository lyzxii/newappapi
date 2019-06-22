package com.caiyi.lottery.tradesystem.util.code;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class CountCodeUtil {

	public static HashMap<String, String> maps = new HashMap<String, String>();
	static{
		maps.put("80", "45/24/60");
		maps.put("81", "45/24/60");
		maps.put("82", "45/24/60");
		maps.put("83", "45/24/60");
	}
	
	/**
	 * 功能描述：根据单倍注数（票张数）判断竞彩方案截止时间
	 * 1、截止时间小于5分钟 不能超过50注(含)
	 * 2、截止时间小于20分钟 大于5分钟不能超过100注(含)
	 * 3、截止时间小于25分钟 大于20分钟不能超过300注(含)
	 * 4、截止时间小于60分钟 大于25分钟不能超过400注(含)
	 * 5、截止时间小于90分钟 大于60分钟不能超过600注(含)
	 * 6、截止时间小于120分钟 大于90分钟不能超过800注(含)
	 * @param num         单倍注数（票张数）
	 * @param ifile       是否文件投注  1文件0非文件
	 * @param endTime     截止时间
	 * @throws Exception  
	 */
	public static void jc(int num, int ifile, String endTime) throws Exception{
		Date date = DateTimeUtil.parserDateTime(endTime);

		int max = 1000;
		int minute = 0;
		boolean iflag = false;
		long h = 0;

		if(ifile == 1){
			h = date.getTime() - System.currentTimeMillis() + 1000 * 60 * 15;
		} else {
			h = date.getTime() - System.currentTimeMillis();
		}
		
		if( h > 0 && h <= 1 * 1000 * 60 * 5){
			minute = 5;
			max = 50;
			iflag = true;
		} else if( h > 1 * 1000 * 60 * 5 && h <= 1 * 1000 * 60 * 20){
			minute = 20;
			max = 100;
			iflag = true;
		} else if( h > 1 * 1000 * 60 * 20 && h <= 1 * 1000 * 60 * 25){
			minute = 25;
			max = 300;
			iflag = true;
		} else if( h > 1 * 1000 * 60 * 25 && h <= 1 * 1000 * 60 * 60 ){
			minute = 60;
			max = 400;
			iflag = true;
		} else if(h > 1 * 1000 * 60 * 60 && h <= 1 * 1000 * 60 * 90){
			minute = 90;
			max = 600;
			iflag = true;
		} else if(h > 1 * 1000 * 60 * 90 && h <= 1 * 1000 * 60 * 120){
			minute = 120;
			max = 800;
			iflag = true;
		}

		if ( iflag ) {
			if ( num > max ) {
				throw new Exception("出票张数超过" + max + "张，请于第一场比赛截止前" + minute + "分钟提交");
			}
		}
	}

	
	public static void sz(int num, int ifile, String endTime,String gid) throws Exception{
		
		
		Date date = DateTimeUtil.parserDateTime(endTime);

		int max = 1000;
		int minute = 0;
		boolean iflag = false;
		
		long h = 0;
		if(ifile == 1){
			h = date.getTime() - System.currentTimeMillis() + 1000 * 60 * 30;
		} else {
			h = date.getTime() - System.currentTimeMillis();
		}
        if("81".equals(gid)){
        	if( h >= 0 && h <1 * 1000 * 60 * 30){
        		minute = 30;
    			max = 200;
    			iflag = true;
        	} else if( h >= 1 * 1000 * 60 * 30  && h <1 * 1000 * 60 * 60){
    			minute = 60;
    			max = 500;
    			iflag = true;
    		} else if( h >= 1 * 1000 * 60 * 60  && h <1 * 1000 * 60 * 90){
    			minute = 90;
    			max = 1000;
    			iflag = true;
    		}else if( h >= 1 * 1000 * 60 * 90  && h <1 * 1000 * 60 * 120){
    			minute = 120;
    			max = 2000;
    			iflag = true;
    		}
        }else{
        	if( h > 0 && h <= 1 * 1000 * 60 * 10){
    			minute = 10;
    			max = 100;
    			iflag = true;
    		} else if( h > 1 * 1000 * 60 * 10 && h <= 1 * 1000 * 60 * 30 ){
    			minute = 30;
    			max = 200;
    			iflag = true;
    		} else if(h > 1 * 1000 * 60 * 30 && h <= 1 * 1000 * 60 * 60){
    			minute = 60;
    			max = 500;
    			iflag = true;
    		} else if(h > 1 * 1000 * 60 * 60 && h <= 1 * 1000 * 60 * 120){
    			minute = 120;
    			max = 800;
    			iflag = true;
    		}

        }
		if ( iflag ) {
			if ( num > max ) {
				throw new Exception("出票张数超过" + max + "张，请于本期截止前" + minute + "分钟提交");
			}
		}
	}
	public static void bd(int num, int ifile, String endTime) throws Exception{
		Date date = DateTimeUtil.parserDateTime(endTime);

		int max = 1000;
		int minute = 0;
		long h ;
		boolean iflag = false;
		//截止时间小于30分钟 单复式单倍注数（票张数）不能超过500注(含),
		//单复式单倍注数（票张数）超过1000注(含)的方案截止时间大于30分钟,
		//单倍注数超过5000注(含)的方案截止时间大于60分钟，
		//超过1万注(含)的方案截止时间限制为截止时间大于90分钟。
		if(ifile == 1){
			h = date.getTime() - System.currentTimeMillis() + 1000 * 60 * 15;
		} else {
			h = date.getTime() - System.currentTimeMillis();
		}

		if( h > 0 && h <= 1 * 1000 * 60 * 5){
			minute = 5;
			max = 100;
			iflag = true;
		} else if( h > 1 * 1000 * 60 * 5 && h <= 1 * 1000 * 60 * 20){
			minute = 20;
			max = 200;
			iflag = true;
		} else if( h > 1 * 1000 * 60 * 20 && h <= 1 * 1000 * 60 * 25){
			minute = 25;
			max = 300;
			iflag = true;
		} else if( h > 1 * 1000 * 60 * 25 && h <= 1 * 1000 * 60 * 60 ){
			minute = 60;
			max = 400;
			iflag = true;
		} else if(h > 1 * 1000 * 60 * 60 && h <= 1 * 1000 * 60 * 90){
			minute = 90;
			max = 600;
			iflag = true;
		} else if(h > 1 * 1000 * 60 * 90 && h <= 1 * 1000 * 60 * 120){
			minute = 120;
			max = 800;
			iflag = true;
		}

		if ( iflag ) {
			if ( num > max ) {
				throw new Exception("出票张数超过" + max + "张，请于第一场比赛截止前" + minute + "分钟提交");
			}
		}
	}
	
	/**
	 * 竞技彩按倍数拆分计算出票数量
	 * @param mul
	 * @return
	 */
	public static int muliSize(int mul) {
		int maxMuli = 99;  //竞技彩单张票最大倍数 99
		int mc = 0;
		if (mul % 99 == 0) {
			mc = mul / maxMuli;
		} else {
			mc = mul / maxMuli + 1;
		}
		return mc;
	}
	
	
	
	/**
	 * 方案是否截止判断(截止后公开、截止后对参与人员公开) 
	 * 北单、竞彩公开截止时间 按最后一场比赛时间
	 * 其它彩种按方案截止时间
	 * @param gid
	 * @param periodid
	 * @param projid
	 * @param endtime
	 * @return
	 */
	public static boolean checkOpenEndTime(String gid, String periodid, String projid, Date endtime,int ifile){
		if(GameContains.isFootball(gid) || GameContains.isBasket(gid) || GameContains.isBeiDan(gid)){
			String projFile = "/opt/export/data/guoguan/" + gid + "/" + periodid + "/proj/" + projid.toLowerCase() + ".xml";
			File file = new File(projFile);
			if(file.exists()){
				JXmlWrapper xml = JXmlWrapper.parse(file);
				int count = xml.countXmlNodes("item");
				for(int i = 0; i < count; i++){
					Date bet = xml.getDateValue("item[" + i + "].@bt");
					if(bet.getTime() > endtime.getTime()){
						endtime = bet;
					}
				}
			}
		}
		 
		long ent = endtime.getTime();
		 int zcgid = Integer.parseInt(gid);
         if(zcgid>=80 && zcgid<=83 ){
         	if(ifile==1){
         		ent = ent + 1000*60*30;
         	}else{
         		ent = ent + 1000*60*10;
         	}
         }
		if(System.currentTimeMillis() > ent){
			return true;
		}
		return false;
	}

	
	/**
	 *功能描述：春节期间截止的竞彩先发起后上传方案截止时间延长到节后第一天
	 * @param endTime  方案截止时间
	 * @return String  
	 */
	public static String checkJCChunJieEndTime(String endTime){
		String start = "2015-02-17 22:30:00";
		String end = "2015-02-25 09:30:00";
		String chunJieEndTime = endTime;
		if(endTime.compareTo(start) > 0 && endTime.compareTo(end) <= 0){
			chunJieEndTime = "2015-02-25 21:00:00";
		}
		return chunJieEndTime;
	}
	
	/**
	 * 功能描述：春节期间截止的竞彩\北单方案截止时间为春节前最后一天
	 * @param gid   彩种
	 * @param endTime  截止时间
	 * @return
	 */
	public static String getSpecialTimeRange(String gid, String endTime){
		String start = "2015-02-17 23:59:00";
		String jzTime = "2015-02-17 23:45:00";
		String end = "2015-02-25 10:00:00";
		if(GameContains.isBeiDan(gid)){
			start = "2015-02-17 23:50:00";
			end = "2015-02-25 00:00:00";
			if(endTime.compareTo(start) >= 0 && endTime.compareTo(end) <= 0){
				return start;
			}
		}else if(GameContains.isFootball(gid)){
			if(endTime.compareTo(start) >= 0 && endTime.compareTo(end) <= 0){
				return jzTime;
			}
		}else if(GameContains.isBasket(gid)){
			if(endTime.compareTo(start) >= 0 && endTime.compareTo(end) <= 0){
				return jzTime;
			}
		}else if(GameContains.isGYJ(gid)){
			if(endTime.compareTo(start) >= 0 && endTime.compareTo(end) <= 0){
				return jzTime;
			}
		}
		return endTime;
	}
	
	
	/**
	 *功能描述：春节前不允许投注春节后期次
	 * @param endTime  方案截止时间
	 */
	public static void checkKPChunJieEndTime(BaseBean bean, String endTime){
		String start = "2017-01-27 00:00:00";
		Date nowdate = new Date();// 当前时间
		if(nowdate.getTime() <= DateTimeUtil.parserDateTime(start).getTime()){
			if(endTime.compareTo(start) > 0){
				bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_UNSALE_IN_SPRING_FESTIVAL));
				bean.setBusiErrDesc("春节期间（1.27-2.2）休市，节后的预售期次请在2月3日投注！");
			}
		}
	}
}

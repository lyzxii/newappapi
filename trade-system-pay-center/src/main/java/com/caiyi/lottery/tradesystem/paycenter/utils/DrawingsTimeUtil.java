package com.caiyi.lottery.tradesystem.paycenter.utils;

import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 提款预计到账时间帮助类
 * @author huzhiqiang
 *
 */
@Slf4j
public class DrawingsTimeUtil {
	
	private static int PREDICT_HOUR = 240;

 /**
  * 
code        银行名称            到账规则
 2          工商银行            21：00之前申请的提款，提交时间+2小时；21：00之后申请的提款，提交时间到第二天9:00+3小时
 1          招商银行
    
 6          交通银行            18：00之前申请的提款，提交时间+2.5小时；18：00之后申请的提款，提交时间到第二天9:00+3小时
 3          建设银行  
13          农业银行
 
10          光大银行            17：00之前申请的提款，提交时间+2小时；17：00之后申请的提款，提交时间到第二天9:00+3小时
12          民生银行    
 9          兴业银行    
 8          中信银行    
4000        浦发银行    
23          平安银行    
    
4           中国银行            16:00之前申请的提款，提交时间+2小时 ；16:00之后申请的提款，提交时间到第二天9:00+3小时
    
1000        广发银行            17:30之前申请的提款，提交时间到第二天9:00+3小时 ；17:30之后申请的提款，提交时间到第三天9:00+3小时
11          华夏银行  
    
25          中国邮政储蓄银行            工作日12:00之前申请的提款，提交时间+2小时；其它时间申请的提款，提交时间到下一个工作日9:00+3小时
    
                                    其它银行            21：00之前申请的提款，提交时间+2.5小时；21：00之后申请的提款，提交时间到第二天9:00+3小时
**/
    
    public static void main(String[] args) {
        System.out.println(DrawingsTimeUtil.predictTime(1));
    }
    
    public static String predictTime(int bankcode){
        String time = "";
        
        switch (bankcode) {
            case 1:
            case 2:
                time = calculateTime("21:00", 120 + PREDICT_HOUR, true, false);
                break;
                
                
            case 3:
            case 6:
            case 13:
                time = calculateTime("18:00", 150 + PREDICT_HOUR, true, false);
                break;
                
            case 8:
            case 9:
            case 10:
            case 12:
            case 23:
            case 4000:
                time = calculateTime("17:00", 120 + PREDICT_HOUR, true, false);
                break;
                
            case 4:
                time = calculateTime("16:00", 120 + PREDICT_HOUR, true, false);
                break;
                
            case 11:
            case 1000:
                time = calculateTime("17:30", 180 + PREDICT_HOUR, false, false);
                break;
                
            case 25:
                time = calculateTime("12:00", 120 + PREDICT_HOUR, true, true);
                break;

            default:
                time = calculateTime("21:00", 150 + PREDICT_HOUR, true, false);
                break;
        }
        
        return time;
    }
    
    private static final String sf = "yyyy-MM-dd HH:mm";
    private static final String df = "HH:mm";
    
    /**
     * 
     * @param timePoint 时间分割点
     * @param arriveMinute 到账时间(分钟)
     * @param today 是否当天到账(广发银行,华夏银行隔日才能到)
     * @param workday 是否计算工作日(中国邮政储蓄银行 )
     * @return
     */
    public static String calculateTime(String timePoint, int arriveMinute, boolean today, boolean workday){
        
        Calendar cal = Calendar.getInstance();

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Calendar c3 = Calendar.getInstance();
        Calendar c4 = Calendar.getInstance();

        try {
            c1.setTime(ConcurrentSafeDateUtil.convert(new Date(), df));
            //c1.setTime(ConcurrentSafeDateUtil.parse("00:30", df));
            c2.setTime(ConcurrentSafeDateUtil.parse(timePoint, df));
            
            c3.setTime(ConcurrentSafeDateUtil.parse("00:00", df));
            c4.setTime(ConcurrentSafeDateUtil.parse("06:00", df));
        } catch (ParseException e) {
            log.error("calculateTime Exception timepoint:"+timePoint,e);
        }
        
              
        int result = c1.compareTo(c2);
        if (result > 0){ //分割时间在当前系统时间之前
            if (today) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            } else {
                cal.add(Calendar.DAY_OF_YEAR, 2);
            }
            
            if (workday) {
                int day = dayForWeek(DateTimeUtil.getCurrentFormatDate("yyyy-MM-dd"));
                if (day == 5) { //周五
                    cal.add(Calendar.DAY_OF_YEAR, 3);
                } else if (day == 6) { //周六
                    cal.add(Calendar.DAY_OF_YEAR, 2);
                } else if (day == 7) {
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }
            }
           
            cal.set(Calendar.HOUR_OF_DAY, 12); // 12:00
            cal.set(Calendar.MINUTE, 0);

        }else if(c1.compareTo(c3) > 0 && c1.compareTo(c4) < 0){
        	cal.add(Calendar.DAY_OF_YEAR, 1);
        	cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
        } else { //当天到账
            cal.add(Calendar.MINUTE, arriveMinute);
            if (!today) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        return ConcurrentSafeDateUtil.format(cal.getTime(), sf);
        
    }
    
    /**
     * 
     * @param time 要计算的日期
     * @return  6,7 为周六、周日
     */
    public static int dayForWeek(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            log.error("dayForWeek Exception,time:"+time,e);
        }
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1){
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }
    
}

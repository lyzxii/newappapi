package pay.util;

import java.util.HashMap;
import java.util.Map;

public class RechargeComUtil {
	
	//各渠道无需切换通道code
		public static final Map<String,String> LINALIAN_ERROR_CODE = new HashMap<String, String>();
		static{
			LINALIAN_ERROR_CODE.put("0000", "交易成功");
			LINALIAN_ERROR_CODE.put("1002", "支付服务超时，请重新支付 用户长时间停留在支付页面不提交，导致超时");
			LINALIAN_ERROR_CODE.put("1003", "正在支付中,请稍后");
			LINALIAN_ERROR_CODE.put("1005", "该笔订单已支付，请不要重复支付以免造成损失[2007]");
			LINALIAN_ERROR_CODE.put("1007", "网络链接繁忙");
			LINALIAN_ERROR_CODE.put("1900", "短信码校验错误");
			LINALIAN_ERROR_CODE.put("1901", "短信码已失效");
			LINALIAN_ERROR_CODE.put("9700", "短信验证码错误");
			LINALIAN_ERROR_CODE.put("9701", "短信验证码和手机不匹配");
			LINALIAN_ERROR_CODE.put("9702", "验证码错误次数超过最大次数,请重新获取进行验证");
			LINALIAN_ERROR_CODE.put("9703", "短信验证码失效,请重新获取");
			LINALIAN_ERROR_CODE.put("9704", "短信发送异常,请稍后重试");
		}
		
		public static final Map<String,String> SHENGPAY_ERROR_CODE = new HashMap<String, String>();
		static{
			SHENGPAY_ERROR_CODE.put("SUCCESS",	"交易成功");
//			SHENGPAY_ERROR_CODE.put("BUSINESS_EXCEPTION",	"交易异常，为保障您的资金安全，请用其他方式支付。");
			SHENGPAY_ERROR_CODE.put("BUSINESS_EXCEPTION",	"手机验证码有误[输入的验证码有误]");
			
		}
		
		
		public static final Map<String,String> UMPAY_ERROR_CODE = new HashMap<String, String>();
		static{
			UMPAY_ERROR_CODE.put("0000",	"交易成功");
			UMPAY_ERROR_CODE.put("00060723",	"支付密码错误请重新输入");  
			UMPAY_ERROR_CODE.put("00060792",	"动态验证码错误");  
			UMPAY_ERROR_CODE.put("00060794",	"获取动态验证码失败");  
			UMPAY_ERROR_CODE.put("00060771",	"支付超过3次请重新下单");  
			UMPAY_ERROR_CODE.put("00060761",	"订单正在支付中请稍后");  
			UMPAY_ERROR_CODE.put("00060762",	"订单已过期请重新下单");  
			UMPAY_ERROR_CODE.put("00060763",	"订单已关闭");  
			UMPAY_ERROR_CODE.put("00060764",	"订单未支付，请继续支付");
			UMPAY_ERROR_CODE.put("00200006",	"验证码错误");
			UMPAY_ERROR_CODE.put("00060700",	"请求的参数[verifyCode]格式或值不正确");
			 
		}
		
		
		public static final Map<String,String> YEEPAY_ERROR_CODE = new HashMap<String, String>();
		static{
		
		}
		
		
		public static final Map<String,String> CHINAGPAY_ERROR_CODE = new HashMap<String, String>();
		static{
			CHINAGPAY_ERROR_CODE.put("0000","接受通知成功（异步交易时才会出现");
			CHINAGPAY_ERROR_CODE.put("1001","交易成功");
			CHINAGPAY_ERROR_CODE.put("2001","重复交易");
			CHINAGPAY_ERROR_CODE.put("2015","短信验证码错误"); 
			CHINAGPAY_ERROR_CODE.put("2021","短信校验码已过期"); 
			CHINAGPAY_ERROR_CODE.put("2025","余额不足"); 
			
			CHINAGPAY_ERROR_CODE.put("555","充值过于频繁"); 
		}
		
		public static final Map<String,Map<String,String>> ERROR_CODE = new HashMap<String, Map<String,String>>();
		static{
			ERROR_CODE.put("lianlianpay", LINALIAN_ERROR_CODE);
			ERROR_CODE.put("shengpay", SHENGPAY_ERROR_CODE);
			ERROR_CODE.put("umpay", UMPAY_ERROR_CODE);
			ERROR_CODE.put("yeepay", YEEPAY_ERROR_CODE);
			ERROR_CODE.put("chinagpay", CHINAGPAY_ERROR_CODE);
		}
		
		public static boolean checkErrorDesc(String channelCode, String returnMessage) {
			//非盛付通渠道不匹配desc
			if(!"shengpay".equals(channelCode)){
				return false;
			}
			if(returnMessage.equals("手机验证码有误[输入的验证码有误]")){
				return true;
			}
			if(returnMessage.contains("手机验证码")){
				return true;
			}
			if(returnMessage.equals("真实姓名不正确")){
				return true;
			}
			if(returnMessage.equals("CVV2不能为空")){
				return true;
			}
			if(returnMessage.equals("有效期不能为空")){
				return true;
			}
			if(returnMessage.equals("卡号与证件信息不匹配")){
				return true;
			}
			if(returnMessage.equals("没有CVV2或CVV2不正确")){
				return true;
			}
			return false;
		}

}

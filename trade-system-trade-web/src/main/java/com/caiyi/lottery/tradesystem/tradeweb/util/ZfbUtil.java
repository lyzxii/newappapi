package com.caiyi.lottery.tradesystem.tradeweb.util;

import com.mina.rbc.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
public class ZfbUtil {

	public static String CreateUrl(String paygateway, String service, String sign_type, String out_trade_no, String input_charset, String partner, String key, String show_url,
                                   String body, String total_fee, String payment_type, String seller_email, String subject, String notify_url, String return_url, String paymethod, String defaultbank, String token, String anti_phishing_key, String exter_invoke_ip, String it_b_pay) throws Exception {// ,
		// String
		// it_b_pay
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", service);
		params.put("partner", partner);
		params.put("subject", subject);
		// params.put("subject", URLEncoder.encode(subject,input_charset));
		params.put("body", body);
		params.put("out_trade_no", out_trade_no);
		params.put("total_fee", total_fee);
		params.put("show_url", show_url);
		params.put("payment_type", payment_type);
		params.put("seller_email", seller_email);
	    params.put("it_b_pay", it_b_pay);
		params.put("return_url", return_url);
		params.put("notify_url", notify_url);
		params.put("_input_charset", input_charset);
		if(!(defaultbank.equals("00"))){
			params.put("paymethod", paymethod);
			params.put("defaultbank", defaultbank);
		}
		if(!(token.equals(""))){
			params.put("token", token);
		}
		params.put("anti_phishing_key", anti_phishing_key);
		params.put("exter_invoke_ip", exter_invoke_ip);

		String prestr = "";

		prestr = prestr + key;
		// System.out.println("prestr=" + prestr);

		// logger.debug("subject=" + subject);
		// System.out.println("subject="+ URLEncoder.encode(subject
		// ,input_charset));

		String sign = md5(getContent(params, key));

		String parameter = "";
		parameter = parameter + paygateway;

		List<String> keys = new ArrayList<String>(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter + keys.get(i) + "=" + URLEncoder.encode((String) params.get(keys.get(i)), input_charset) + "&";
			} catch (UnsupportedEncodingException e) {
				log.error("CreateUrl",e);
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return parameter;

	}

	public static String CreateUrl(String paygateway, String service, String sign_type, String out_trade_no, String input_charset, String partner, String key, String show_url,
                                   String body, String total_fee, String payment_type, String seller_email, String subject, String notify_url, String return_url, String paymethod, String defaultbank, String token, String anti_phishing_key, String exter_invoke_ip) throws Exception {// ,
		// String
		// it_b_pay
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", service);
		params.put("partner", partner);
		params.put("subject", subject);
		// params.put("subject", URLEncoder.encode(subject,input_charset));
		params.put("body", body);
		params.put("out_trade_no", out_trade_no);
		params.put("total_fee", total_fee);
		params.put("show_url", show_url);
		params.put("payment_type", payment_type);
		params.put("seller_email", seller_email);
		// params.put("it_b_pay", it_b_pay);
		params.put("return_url", return_url);
		params.put("notify_url", notify_url);
		params.put("_input_charset", input_charset);
		
		if(paymethod.equals("kjzf")){
			params.put("paymethod", "expressGatewayDebit");
			params.put("defaultbank", defaultbank);
			params.put("default_login", "Y");
		}else if(paymethod.equals("kjzfxyk")){
			params.put("paymethod", "expressGatewayCredit");
			params.put("defaultbank", defaultbank);
			params.put("default_login", "Y");
		}else{
			if(!(defaultbank.equals("00")) && !(defaultbank.equals("01"))){
				params.put("paymethod", paymethod);
				params.put("defaultbank", defaultbank);
			}
			if(defaultbank.equals("01")){
				params.put("paymethod", paymethod);
				//params.put("default_login", "Y");
			}
		}
		
		if(!(token.equals(""))){
			params.put("token", token);
		}
		params.put("anti_phishing_key", anti_phishing_key);
		params.put("exter_invoke_ip", exter_invoke_ip);

		String prestr = "";

		prestr = prestr + key;
		// System.out.println("prestr=" + prestr);

		// logger.debug("subject=" + subject);
		// System.out.println("subject="+ URLEncoder.encode(subject
		// ,input_charset));

		String sign = md5(getContent(params, key));

		String parameter = "";
		parameter = parameter + paygateway;

		List<String> keys = new ArrayList<String>(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter + keys.get(i) + "=" + URLEncoder.encode((String) params.get(keys.get(i)), input_charset) + "&";
			} catch (UnsupportedEncodingException e) {
				log.error("CreateUrl",e);
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return parameter;

	}
	
	public static String CreateUrl_wbSecurityInfo(String paygateway, String service, String sign_type, String out_trade_no, String input_charset, String partner, String key, String show_url,
                                                  String body, String total_fee, String payment_type, String seller_email, String subject, String notify_url, String return_url, String paymethod, String defaultbank, String token, String anti_phishing_key, String exter_invoke_ip, String wbSecurityInfo , String it_b_pay, String qr_pay_mode) throws Exception {// ,
		// String
		// it_b_pay
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("service", service);
		params.put("partner", partner);
		params.put("subject", subject);
		// params.put("subject", URLEncoder.encode(subject,input_charset));
		params.put("body", body);
		params.put("out_trade_no", out_trade_no);
		params.put("total_fee", total_fee);
		params.put("show_url", show_url);
		params.put("payment_type", payment_type);
		params.put("seller_email", seller_email);
		if(!StringUtil.isEmpty(it_b_pay)){
			 params.put("it_b_pay", it_b_pay);
		}
		params.put("return_url", return_url);
		params.put("notify_url", notify_url);
		params.put("_input_charset", input_charset);
		if(!StringUtil.isEmpty(qr_pay_mode)){
			params.put("qr_pay_mode", qr_pay_mode);
		}
		
		
		if(paymethod.equals("kjzf")){
			params.put("paymethod", "expressGatewayDebit");
			params.put("defaultbank", defaultbank);
			params.put("default_login", "Y");
		}else if(paymethod.equals("kjzfxyk")){
			params.put("paymethod", "expressGatewayCredit");
			params.put("defaultbank", defaultbank);
			params.put("default_login", "Y");
		}else{
			if(!(defaultbank.equals("00")) && !(defaultbank.equals("01"))){
				params.put("paymethod", paymethod);
				params.put("defaultbank", defaultbank);
			}
			if(defaultbank.equals("01")){
				params.put("paymethod", paymethod);
				//params.put("default_login", "Y");
			}
		}
		
		if(!(token.equals(""))){
			params.put("token", token);
		}
		params.put("anti_phishing_key", anti_phishing_key);
		params.put("exter_invoke_ip", exter_invoke_ip);
		params.put("extend_param", "security_wbInfo^"+wbSecurityInfo);
		

		String prestr = "";

		prestr = prestr + key;
		// System.out.println("prestr=" + prestr);

		// logger.debug("subject=" + subject);
		// System.out.println("subject="+ URLEncoder.encode(subject
		// ,input_charset));

		String sign = md5(getContent(params, key));

		String parameter = "";
		parameter = parameter + paygateway;

		List<String> keys = new ArrayList<String>(params.keySet());
		for (int i = 0; i < keys.size(); i++) {
			try {
				parameter = parameter + keys.get(i) + "=" + URLEncoder.encode((String) params.get(keys.get(i)), input_charset) + "&";
			} catch (UnsupportedEncodingException e) {

				log.error("CreateUrl_wbSecurityInfo",e);
			}
		}

		parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

		return parameter;

	}

	public static String check(String urlvalue) {

		String inputLine = "";

		try {
			URL url = new URL(urlvalue);

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			inputLine = in.readLine().toString();
			// System.out.println(inputLine);
		} catch (Exception e) {
			log.error("check urlvalue:"+urlvalue,e);
		}
		// System.out.println(inputLine); 系统打印出抓取得验证结果
		/*
		 * 输出对应的参数对应错误： 1.invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空 2.true
		 * 返回正确信息 3.false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
		 */

		return inputLine;
	}

	public static String getContent(Map params, String privateKey) {
		List keys = new ArrayList(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = (String) params.get(key);

			if (i == keys.size() - 1) {
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		return prestr + privateKey;
	}

	public static String sign(Map params, String privateKey) throws Exception {
		Properties properties = new Properties();

		for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			Object value = params.get(name);

			if (name == null || name.equalsIgnoreCase("sign") || name.equalsIgnoreCase("sign_type")) {
				continue;
			}

			properties.setProperty(name, value.toString());

		}

		String content = getSignatureContent(properties);
		return sign(content, privateKey);
	}

	public static String getSignatureContent(Properties properties) {
		StringBuffer content = new StringBuffer();
		List keys = new ArrayList(properties.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			String value = properties.getProperty(key);
			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}

		return content.toString();
	}

	public static String sign(String content, String privateKey) throws Exception {
		if (privateKey == null) {
			return null;
		}
		String signBefore = content + privateKey;
		System.out.print("signBefore=" + signBefore);

		// *****************************************************************
		// 当alipay收到信息，会把接受的信息写程日志
		// 该文件存在于和应用服务器 启动文件同一目录下，文件名是alipay log加服务器时间
//		try {
//			FileWriter writer = new FileWriter("alipay_log" + System.currentTimeMillis() + ".txt");
//			writer.write(signBefore);
//			writer.close();
//		} catch (Exception e) {
//		}
		// *********************************************************************
		return md5(signBefore);

	}

	 public static String CreateUrl2(String paygateway, String service,
                                     String partner, String subject, String body,
                                     String out_trade_no, String price, String payment_type,
                                     String seller_email, String notify_url, String is_ivr_pay,
                                     String receive_mobile, String key, String sign_type,
                                     String show_url, String quantity, String input_charset) throws Exception {
	    	
			Map params = new HashMap();
			params.put("service", service);
			params.put("partner", partner);
			params.put("subject", subject);
			params.put("body", body);
			params.put("out_trade_no", out_trade_no);
			params.put("price", price);
			params.put("payment_type",payment_type);
			params.put("seller_email", seller_email);
			params.put("notify_url", notify_url);
			params.put("is_ivr_pay", is_ivr_pay);
			params.put("receive_mobile", receive_mobile);
			params.put("quantity", quantity);
			params.put("show_url", show_url);

			params.put("_input_charset", input_charset);

			String prestr = "";

			prestr = prestr + key;
			//System.out.println("prestr=" + prestr);

			String sign = md5(getContent(params, key));

			String parameter = "";
			parameter = parameter + paygateway;

			List keys = new ArrayList(params.keySet());
			for (int i = 0; i < keys.size(); i++) {
				try {
					parameter = parameter + keys.get(i) + "="
					+ URLEncoder.encode((String) params.get(keys.get(i)), input_charset) + "&";
				} catch (UnsupportedEncodingException e) {

					log.error("CreateUrl2",e);
				}
			}

			parameter = parameter + "sign=" + sign + "&sign_type=" + sign_type;

			return parameter;

		}

	/**
	 * Used building output as Hex
	 */
	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param text
	 *            明文
	 * 
	 * @return 密文
	 */
	public static String md5(String text) {
		MessageDigest msgDigest = null;
	
		try {
			msgDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"System doesn't support MD5 algorithm.");
		}
	
		try {
			msgDigest.update(text.getBytes("UTF-8"));//注意改接口是按照gbk编码形式加密
	
		} catch (UnsupportedEncodingException e) {
	
			throw new IllegalStateException(
					"System doesn't support your  EncodingException.");
	
		}
	
		byte[] bytes = msgDigest.digest();
	
		String md5Str = new String(encodeHex(bytes));
	
		return md5Str;
	}

	public static char[] encodeHex(byte[] data) {
	
		int l = data.length;
	
		char[] out = new char[l << 1];
	
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
	
		return out;
	}

}

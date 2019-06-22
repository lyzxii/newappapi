package com.caiyi.lottery.tradesystem.util.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;

public class CodesUtil {
	
	/**
	 * 数学排列组合中的组合公式，输出排列好的字符串存放到List中
	 * @author: wangjinyong
	 * @createTime：2013-9-30 下午3:14:44
	 * @Methods: getCmnList
	 * @param codes 输入的投注号码，格式：以逗号分隔，但第一个字符和最后一个字符不能是逗号
	 * @param m 排列公式中的下标 m>=n
	 * @param n 排列公式中的上标 m>=n
	 * @return 输出排列好的字符串存放到List<String>中
	 * @throws Exception
	 */
	public static List<String> getCmnList(String codes, int m, int n) throws Exception {
		List<String> resultList = new ArrayList<String>();// 输出Cmn排列组合后的List
		try {
			if ((m < 0 || n < 0) || (n > m)) {
				throw new Exception("排列组合输入参数格式错误! m=" + m + ", n=" + n);
			}
			if (codes == null || codes.trim().length() <= 0 || !codes.contains(",") || codes.endsWith(",") || codes.startsWith(",")) {
				throw new Exception("排列组合输入参数格式错误! codes=" + codes);
			}
			String[] inputToArray = codes.split(",");// 把输入的字符串转换成数组
			if (inputToArray.length != m) {
				throw new Exception("排列组合输入参数格式错误! m=" + m + ", codes=" + codes);
			}
			String binaryString = "";// 根据输入的字符串转换成由0和1拼成的二进制形式字符串
			for (int i = 0; i < n; i++) {
				binaryString += "1";
			}
			for (int i = 0; i < (m - n); i++) {
				binaryString += "0";
			}
			String outputString = "";// 待输出的字符串
			for (int i = 0; i < binaryString.length(); i++) {
				if ('1' == binaryString.charAt(i)) {
					outputString += inputToArray[i] + ",";
				}
			}
			resultList.add(outputString.substring(0, outputString.length() - 1));
			int index = 0;
			while ((index = binaryString.indexOf("10")) >= 0) {
				binaryString = binaryString.replaceFirst("10", "01");
				String startsString = index > 0 ? binaryString.substring(0, index) : "";// 分割的前部分字符串
				while (startsString != null && startsString.startsWith("0") && startsString.contains("1")) {
					startsString = startsString.replaceFirst("0", "");
					startsString += "0";
				}
				binaryString = startsString + (index > 0 ? binaryString.substring(index) : binaryString.substring(0));
				outputString = "";
				for (int i = 0; i < binaryString.length(); i++) {
					if ('1' == binaryString.charAt(i)) {
						outputString += inputToArray[i] + ",";
					}
				}
				resultList.add(outputString.substring(0, outputString.length() - 1));
			}
		} catch (Exception e) {
			throw new Exception("排列组合出错:" + e.getMessage(), e);
		}
		return resultList;
	}
	

	/**
	 * 过滤合买宣传
	 * @param text
	 */
	public static String filteText(String text){
		if(!CheckUtil.isNullString(text)){
			text = text.replaceAll("手机号码", "****").replaceAll("手机号", "***").replaceAll("手机", "**");
			text = text.replaceAll("QQ", "**").replaceAll("Q", "*").replaceAll("qq", "**").replaceAll("q", "*").replaceAll("扣扣号", "***").replaceAll("扣扣", "**").replaceAll("扣", "*")
					.replaceAll("扣扣群", "***").replaceAll("扣群", "**");
			text = text.replaceAll("微信号码", "****").replaceAll("微信号", "***").replaceAll("微信", "**");
			text = text.replaceAll("[0-9]{4,}", "****");
			text = text.replaceAll("([1-9]\\d*[\\||\\,|\\s]\\d*[\\||\\,|\\s]\\d*[\\||\\,|\\s]\\d*[\\||\\,|\\s]\\d*)", "");
			text = text.replaceAll("[\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u96f6]{4,}", "****");
			text = text.replaceAll("[\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe]", "*");
		}
		return text;
	}
	
	
	
/**
 * @param dgCodes
 * @return
 */
public static String formatCodeByJinCai(String dgCodes) {
        
        Map<String, String> spfMap = new HashMap<String, String>();
        Map<String, String> rqspfMap = new HashMap<String, String>();
        Map<String, String> cbfMap = new HashMap<String, String>();
        Map<String, String> jqsMap = new HashMap<String, String>();
        Map<String, String> bqcMap = new HashMap<String, String>();
        List<String> keylist = new ArrayList<String>();
        String [] tarr = dgCodes.split(";");
        String [] xz = null;
        String [] iz = null;
        String bs = null;
        for (int i = 0; i < tarr.length; i++) {
            xz = tarr[i].split("\\|");
            iz = xz[1].split(">");
            bs = xz[2].split("\\_")[1];
   
            if (!keylist.contains(iz[0])) {
                keylist.add(iz[0]);
            }
            
            if (iz[1].indexOf("RQSPF") > -1) {
                String v = rqspfMap.get(iz[0]); 
                if (!StringUtil.isEmpty(v)) {
                    rqspfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    rqspfMap.put(iz[0], iz[1] + "_" + bs);
                }
                    
            } else if (iz[1].indexOf("BQC") > -1) {
                String v = bqcMap.get(iz[0]); 
                if (!StringUtil.isEmpty(v)) {
                    bqcMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    bqcMap.put(iz[0], iz[1] + "_" + bs);
                }
                    
            } else if (iz[1].indexOf("JQS") > -1) {
                String v = jqsMap.get(iz[0]); 
                if (!StringUtil.isEmpty(v)) {
                    jqsMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    jqsMap.put(iz[0], iz[1] + "_" + bs);
                }
                    
            } else if (iz[1].indexOf("CBF") > -1) {
                String v = cbfMap.get(iz[0]); 
                if (!StringUtil.isEmpty(v)) {
                    cbfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    cbfMap.put(iz[0], iz[1] + "_" + bs);
                }   
            } else { //胜平负
                String v = spfMap.get(iz[0]); 
                if (!StringUtil.isEmpty(v)) {
                    spfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    spfMap.put(iz[0], iz[1] + "_" + bs);
                }
                    
            }
        }
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (String key : keylist) {  
            if (flag) {
                sb.append(",");
            } else {
                flag = true;
            }
            boolean start = true;
            if (rqspfMap.size() > 0) {
                String value = rqspfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
            if (jqsMap.size() > 0) {
                String value = jqsMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
            if (bqcMap.size() > 0) {
                String value = bqcMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
            if (cbfMap.size() > 0) {
                String value = cbfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
            if (spfMap.size() > 0) {
                String value = spfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
        }  
        return sb.toString();
    }
}
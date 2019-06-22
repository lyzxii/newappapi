package pay.util;

import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import pay.bean.PayBean;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

@Slf4j
public class PayUtil {

    /**
     * md5签名
     *
     * @param dataMap
     * @param dataMap
     * @param mkey
     * @return
     */
    public static String makeSign(Map<String, String> dataMap, String mkey) {
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = dataMap.keySet();
        List<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList);
        for (String key : keyList) {
            sb.append(key + "=");
            sb.append((String) dataMap.get(key));
            sb.append("&");
        }
        sb.append("key=" + mkey);
        try {
            String s1 = sb.toString();
            String sign = DigestUtils.md5Hex(s1.getBytes("UTF-8"));
            return sign;
        } catch (UnsupportedEncodingException e) {
            log.error("makeSign datamap:"+dataMap.toString()+" mkey:"+mkey,e);
        }
        return null;
    }

    public static String getMd5WithKey(Map<String, String> dataMap, String mchKey) throws Exception {
        if (dataMap == null)
            return null;
        Set<String> keySet = dataMap.keySet();
        List<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList);
        StringBuilder toMD5StringBuilder = new StringBuilder();
        for (String key : keyList) {
            String value = dataMap.get(key);
            if (value != null && value.length() > 0) {
                toMD5StringBuilder.append(key + "=" + value + "&");
            }
        }
        toMD5StringBuilder.append("key=" + mchKey);

        String toMD5String = toMD5StringBuilder.toString();

        return MD5Util.md5WithCharSet(toMD5String, "UTF-8");
    }


    /**
     * 针对NowPay目前统一的MD5签名方式：key1=value1&key2=value2....keyn=valuen&securityKeySignature  进行MD5
     *
     * @param dataMap     --需要参与MD5签名的数据
     * @param securityKey --密钥
     * @return
     */
    public static String getFormDataParamMD5(Map<String, String> dataMap, String securityKey, String charset) {
        StringBuilder builder = formatFormData(dataMap);
        String toMD5String = "";
        try {
            if (!StringUtil.isEmpty(securityKey)) {
                //带加密key
                String securityKeyMD5 = MD5Util.md5WithCharSet(securityKey, charset);
                builder.append(securityKeyMD5);
                toMD5String = builder.toString();
            } else {
                toMD5String = builder.toString();
                toMD5String = toMD5String.substring(0, toMD5String.length() - 1);
            }
            String lastMD5Result = MD5Util.md5WithCharSet(toMD5String, charset);
            return lastMD5Result;
        } catch (Exception ex) {
            //ignore
            return "";
        }
    }

    /**
     * @param dataMap
     * @return
     */
    public static StringBuilder formatFormData(Map<String, String> dataMap) {
        if (dataMap == null) return null;
        Set<String> keySet = dataMap.keySet();
        List<String> keyList = new ArrayList<String>(keySet);
        Collections.sort(keyList);
        StringBuilder builder = new StringBuilder();
        String str = "";
        for (String key : keyList) {
            String value = dataMap.get(key);
            if (value != null && value.length() > 0) {
                builder.append(key + "=" + value + "&");
            }
        }
        return builder;
    }

    /**
     * 表单类型报文解析成数据映射表
     *
     * @param reportContent
     * @param reportCharset --报文本身字符集
     * @param targetCharset --目标字符集
     * @return
     */
    public static Map<String, String> parseFormDataByDecode(String reportContent, String reportCharset, String targetCharset) {
        if (reportContent == null || reportContent.length() == 0) return null;

        String[] domainArray = reportContent.split("&");

        Map<String, String> key_value_map = new HashMap<String, String>();
        for (String domain : domainArray) {
            String[] kvArray = domain.split("=");

            if (kvArray.length == 2) {
                try {
                    String decodeString = URLDecoder.decode(kvArray[1], reportCharset);
                    String lastInnerValue = new String(decodeString.getBytes(reportCharset), targetCharset);
                    key_value_map.put(kvArray[0], lastInnerValue);
                } catch (Exception ex) {
                    // ignore
                }

            }
        }

        return key_value_map;
    }

    /**
     * 从配置文件读取商户信息
     *
     * @param bean
     * @param mch_id
     * @param mch_key
     * @return
     */
    public static Map<String, String> getMerchantInfo(PayBean bean, String mch_id, String mch_key, String type) {
        Map<String, String> merchantMap = new HashMap<>();
        merchantMap.put("mchId", mch_id);
        merchantMap.put("mchKey", mch_key);
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.MERCHANT_CONFIG));
        JXmlWrapper recharge = xml.getXmlNode(type);
        List<JXmlWrapper> mchInfoList = recharge.getXmlNodeList("row");
        for (JXmlWrapper mchInfo : mchInfoList) {
            String bankid = mchInfo.getStringValue("@bankid");
            String[] bankIdArr = bankid.split(",");
            for (String bankId : bankIdArr) {
                if ((bankId.trim()).equals(bean.getBankid() + "")) {
                    String mchId = mchInfo.getStringValue("@mch_id");
                    String mchKey = mchInfo.getStringValue("@mch_key");
                    merchantMap.put("mchId", mchId);
                    merchantMap.put("mchKey", mchKey);
                    return merchantMap;
                }
            }
        }
        return merchantMap;
    }

    public static double getRound(double m, int num) {
        BigDecimal dec = new BigDecimal(m);
        BigDecimal one = new BigDecimal("1");
        return dec.divide(one, num, BigDecimal.ROUND_CEILING).doubleValue();
    }

    public static void main(String[] args) {
        PayBean bean = new PayBean();
        bean.setBankid("8000");
        bean.setMerchantId("110263491002");
        bean.setRechargeType("bankCard");
        ReadAccountInfo(bean);

    }

    /**
     * 从recharge-info读取充值渠道配置信息
     * 参数为row 节点中的className
     */
    public static void ReadAccountInfo(PayBean bean) {
        JXmlWrapper wrapper = JXmlWrapper.parse(new File(FileConstant.RECHARGE_INFO));
        JXmlWrapper rechTypeNode = wrapper.getXmlNode(bean.getRechargeType());
        List<JXmlWrapper> rechargeInfoList = rechTypeNode.getXmlNodeList("rows");
        for (JXmlWrapper rechargeInfo : rechargeInfoList) {
            String bankids = rechargeInfo.getStringValue("@bankid");
            String[] bankidArr = bankids.split(",");
            for (String xmlBankid : bankidArr) {
                if (xmlBankid.equals(bean.getBankid())) {
                    String product = rechargeInfo.getStringValue("@product");
                    bean.setProduct(product);

                    String channel = rechargeInfo.getStringValue("@channel");
                    bean.setChannel(channel);

                    String className = rechargeInfo.getStringValue("@className");
                    if (StringUtil.isEmpty(bean.getClassName())) {//如果为空则设置配置文件中的channel
                        bean.setClassName(className);
                    }

                    //是否轮询
                    String cycle = rechargeInfo.getStringValue("@cycle");
                    List<JXmlWrapper> rechInfoDetailList = rechargeInfo.getXmlNodeList("row");
                    //依据merchantId查询
                    for (JXmlWrapper rechRow : rechInfoDetailList) {
                        String merid = rechRow.getStringValue("@mch_id");
                        if (merid.equals(bean.getMerchantId())) {
                            injectRechDetailInfo(bean, rechRow);
                        }
                    }
                    return;
                }
            }
        }
    }

    //注入具体的充值信息
    private static void injectRechDetailInfo(PayBean bean, JXmlWrapper rechInfoDetail) {
        String merchantId = rechInfoDetail.getStringValue("@mch_id");
        if(StringUtil.isEmpty(bean.getMerchantId())){
            bean.setMerchantId(merchantId);
        }

        String merchantKey = rechInfoDetail.getStringValue("@mch_key");
        if(!StringUtil.isEmpty(merchantKey)){
            bean.setMerchantKey(merchantKey);
        }
        String rechargeAppid = rechInfoDetail.getStringValue("@appid");
        bean.setRechargeAppid(rechargeAppid);
        String deskey = rechInfoDetail.getStringValue("@deskey");
        if(!StringUtil.isEmpty(deskey)){
            bean.setDesKey(deskey);
        }
        String rsapublickey = rechInfoDetail.getStringValue("@rsapublickey");
        if(!StringUtil.isEmpty(rsapublickey)){
            bean.setRsapublickey(rsapublickey);
        }
        String rsaprivatekey = rechInfoDetail.getStringValue("@rsaprivatekey");
        if(!StringUtil.isEmpty(rsaprivatekey)){
            bean.setRsaprivatekey(rsaprivatekey);
        }
    }

    public static Map<String, String> getValueFromRequest(HttpServletRequest request) throws UnsupportedEncodingException {
        // 参数Map
        Map<String, String[]> propMap = request.getParameterMap();
        // 返回值Map
        Map<String, String> returnMap = new HashMap<String, String>();
        for(String key:propMap.keySet()){
            String value="";
            if(!StringUtil.isEmpty(propMap.get(key)[0])){
                value=new String(propMap.get(key)[0]);
            }
            returnMap.put(key, value);
        }
        return returnMap;
    }


    public static TreeMap<String, String> getSortMap(String flag) {
        final String rc=flag;
        TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i = Integer.valueOf(o1.substring(o1.indexOf(rc) + 1, o1.lastIndexOf("_")));
                int j = Integer.valueOf(o2.substring(o2.indexOf(rc) + 1, o2.lastIndexOf("_")));
                return i > j ? 1 : i < j ? -1 : 0;
            }
        });
        return map;
    }
}

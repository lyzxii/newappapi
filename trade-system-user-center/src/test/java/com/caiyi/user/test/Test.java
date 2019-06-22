package com.caiyi.user.test;

import bean.SafeBean;
import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.safecenter.client.SafeCenterInterface;
import com.caiyi.lottery.tradesystem.util.AESUtil;
import com.caiyi.lottery.tradesystem.util.Base64;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import com.caiyi.lottery.tradesystem.util.SecurityTool;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试类
 *
 * @author GJ
 * @create 2017-11-24 18:15
 **/
public class Test {
//    public static void main(String[] args) throws  Exception {
//        TokenBean tokenBean = new TokenBean();
//        JSONObject tokenBeanJson = (JSONObject)JSONObject.toJSON(tokenBean);
//        tokenBeanJson.get("pwd");
//        System.out.println(tokenBeanJson.get("pwd")==null);
//        CpUserPojo cpUserPojo = new CpUserPojo();
//        AlipayLoginBean alipayLoginBean = new AlipayLoginBean();
//   //     alipayLoginBean.setIsNew(2);
//        BeanUtilWrapper.copyPropertiesIgnoreNull(alipayLoginBean,cpUserPojo);
//        Integer integer=1;
//        int i=1;
//        System.out.println(integer==i);
//        System.out.println(com.caiyi.lottery.tradesystem.util.MD5Util.compute("1234567890").toUpperCase());
//        System.out.println(MD5Util.compute("1234567890").toUpperCase());
//        String sre= MD5Util.compute("888888" + "http://www.9188.com/");
//        System.out.println(sre);
//        String sre1= com.caiyi.lottery.tradesystem.util.MD5Util.compute("888888" + "http://www.9188.com/");
//        System.out.println(sre1);
//       /* Map<String, String> signMsgMap = Maps.newLinkedHashMap();
//        signMsgMap.put("signtype", "12");
//        signMsgMap.put("merchantacctid", "13");
//        signMsgMap.put("uid", "14");
//        signMsgMap.put("pwd","15");
//        if (!CheckUtil.isNullString("16")) {
//            signMsgMap.put("newpwd", "16");
//        }
//        signMsgMap.put("key", "17");
//
//        for(Map.Entry<String,String> entry:signMsgMap.entrySet()){
//            System.out.println(entry.getKey()+"   "+entry.getValue());
//        }
//        UserCenterService userCenterService = new UserCenterServiceImpl();
//
//
//        BaseReq<UserBasicDTO> userRequest = new UserRequest();*/
//
//    }

    /*public static BaseResp test(BaseReq<UserBasicDTO> req){
        UserResponse
    }*/

    public static void test(UserBean userBean){

    }

    public static void test1(){
    }
//-----------------------------------------------------------------------
//    /**
//     * Aesbase64-测试
//     * @param args
//     */
//    public static void main(String[] args) {
//        String pwd = MD5Helper.md5Hex("231182199206062317");
//        System.out.println(pwd);
//    }
//
//    /**
//     * IOS  AES加密
//     * @param value
//     * @return
//     */
//    public static String iosencrypt(String value){
//        // 加解密统一使用的编码方式
//        String encoding = "UTF-8";
//        // 密钥
//        String secretKey = "umpay2015#12add7";
//        try {
//            byte[] temp = AESUtil.encrypt(secretKey,secretKey, value.getBytes(encoding));
//            return Base64.encode(temp, encoding).trim();
//        } catch (Exception e) {
//            System.out.println("加密发生异常");
//        }
//        return "";
//    }
//-----------------------------------------------------------------------
    public static void main(String[] args) {
        JXmlWrapper jXmlWrapper = JXmlWrapper.parse(new File(FileConstant.TOPIC_FOOTBALL));
        int count = jXmlWrapper.countXmlNodes("row");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String items = jXmlWrapper.getStringValue("row[" + i + "].@items");
            stringBuilder.append(items);
            if (i != count - 1) {
                stringBuilder.append(",");
            }
        }
        String[] itemsArr = stringBuilder.toString().split(",");
        List itemsList = Arrays.asList(itemsArr);
        itemsList.contains("180319001");
        String bk = "TpCOY47PHUI9cu75h1fG7lbrMpn2mGM0kE30k0bslv4=";
        String iosdecrypt = SecurityTool.iosdecrypt(bk);
        System.out.println(iosdecrypt);
    }

}

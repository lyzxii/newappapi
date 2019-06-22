package com.caiyi.lottery.tradesystem.payweb.controller;

import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.paycenter.client.PayCenterCardInfoInterface;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pay.bean.PayBean;
import pay.pojo.BankCardMapPojo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class DrawController {
    @Autowired
    private PayCenterCardInfoInterface payCenterCardInfoInterface;

    private static final String UMPAY_BANK_LIST="/opt/export/data/info/config/pay/xml/support-bank-message.xml";

    /**
     * 用户卡列表
     */
    @RequestMapping("/pay/recharge_bankcard_list.api")
    public Result recharge_bankcard_list(PayBean bean, HttpServletRequest request, HttpServletResponse response){
        Result result = new Result();
        log.info("获取"+bean.getVerifycode()+"所支持的银行列表");
        try {
//            StringBuilder builder = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
            JXmlWrapper xml = JXmlWrapper.parse(new File("/opt/export/data/info/config/pay/xml/bank_list_link.xml"));
            List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("row");
//            builder.append("<Resp code=\"" + 0 + "\" desc=\"" + "查询成功" + "\">");
            for (JXmlWrapper row : xmlNodeList) {
                HashMap<String,String> map = new HashMap<String,String>();
                String bankcode = row.getStringValue("@bankcode");
                String bankname = row.getStringValue("@bankname");
                String cardtype = row.getStringValue("@cardtype");
                String authOk = row.getStringValue("@authOk");
//                builder.append("<row ");
                map.put("bankid", bankcode);
                map.put("bankname", bankname);
                map.put("cardtype", cardtype);
                if(authenticationSwitch()){
                    map.put("authOk", "0");
                }else{
                    map.put("authOk", authOk);
                }
                list.add(map);
//                builder.append("/> ");
            }
//            builder.append("</Resp>");
//            BaseImpl.write_xml_response(builder.toString(),response);  改方法被下面代替
//            response.setContentType("text/xml; charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//            DataOutputStream out = new DataOutputStream(response.getOutputStream());
//            StringBuffer buffer = new StringBuffer();
//            buffer.append(builder.toString());
//            out.write((new String(buffer)).getBytes("UTF-8"));
//            out.flush();
//            out.close();
            result.setCode("0");
            result.setDesc("查询成功");
            result.setData(list);
        } catch (Exception e) {
            log.error("读取银行卡列表异常",e.getMessage());
            result.setCode("-1");
            result.setDesc("读取银行卡列表异常");
            return result;
        }
        return result;
    }

    /**
     * 充值银行卡卡bin校验
     */
    @RequestMapping("/pay/check_bankcard_bin.api")
    public Result check_bankcard_bin(UserBean bean){
        log.info("checkBankCardBin获取银行卡信息,nickid=" + bean.getUid() + ",bankCard=" + bean.getBankCard());
        Result<HashMap<String,String>> result =  new Result<HashMap<String,String>>();
        try{
            //解密银行卡信息
            String bankCard = bean.getBankCard().contains("%2B") ? bean.getBankCard().replaceAll("%2B", "+") : bean.getBankCard();
            String decrypt = SecurityTool.iosdecrypt(bankCard);
            bean.setBankCard(decrypt);
            BaseReq<UserBean> req = new BaseReq<UserBean>(SysCodeConstant.PAYWEB);
            req.setData(bean);
            BaseResp<List<BankCardMapPojo>> resp = payCenterCardInfoInterface.getBankCardInfoByBinno(req);
            List<BankCardMapPojo> bankCardMapPojos = resp.getData();
            if (bankCardMapPojos.size() == 1) {
                String bankInfo = "";
                BankCardMapPojo bankCardMapPojo = bankCardMapPojos.get(0);
                String cardtype = "";//银行卡类型
                String bankName = "";//银行卡所属银行
                String bankcode = "";
                String authOk = "";
                if(bankCardMapPojo != null){
                    cardtype = bankCardMapPojo.getCcardtypename();//银行卡类型
                    bankName = bankCardMapPojo.getCbankname();//银行卡所属银行
                    bankcode = bankCardMapPojo.getCbankcode();
                    authOk = bankCardMapPojo.getCcauthenticationflag();
                }else{
                    cardtype = bean.getCityid();
                    String[] split = bankInfo.split("_");
                    bankName = split[1];//银行卡所属银行
                    bankcode = split[0];
                    authOk = split[2];
                }
                if(StringUtil.isEmpty(bankName) || StringUtil.isEmpty(cardtype)
                        || (!"借记卡".equals(cardtype) && !"信用卡".equals(cardtype) && !"贷记卡".equals(cardtype))
                        || !checkSupportCardList(bankName,cardtype,bankcode)){
                    //无法获取，返回空
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("暂不支持该银行卡");
                    log.info("暂不支持该银行卡");
                }else{
                    String cardTypeCode = "0";
                    if("借记卡".equals(cardtype)){
                        cardTypeCode = "0";
                    }else{
                        cardTypeCode = "1";
                    }
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("bankName", bankName);
                    map.put("bankCode", bankcode);
                    map.put("cardType", cardtype);
                    map.put("cardTypeCode", cardTypeCode);
                    if(authenticationSwitch()){
                        map.put("authOk", "0");
                    }else{
                        map.put("authOk", authOk);
                    }
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("卡bin校验成功");
                    result.setData(map);
                }
            }else{
                bean.setBusiErrCode(102);
                bean.setBusiErrDesc("卡bin未能检测到匹配的银行卡信息");
            }
        }catch(Exception e){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("匹配银行卡信息出错!");
            log.error("UserBeanStub::bankCardInfo", e);
        }
        result.setCode(bean.getBusiErrCode()+"");
        result.setDesc(bean.getBusiErrDesc());
        return result;
    }


    /**
     * 提款卡bin校验
     */
    @RequestMapping("/pay/check_draw_cardbin.api")
    public Result checkDrawCardBin (UserBean bean){
        log.info("进行提款银行卡卡bin校验,nickid=" + bean.getUid() + ",bankCard=" + bean.getBankCard());
        Result<HashMap<String,String>> result =  new Result<HashMap<String,String>>();
        try{
            //解密银行卡信息
            String bankCard = bean.getBankCard().contains("%2B") ? bean.getBankCard().replaceAll("%2B", "+") : bean.getBankCard();
//            if(bean.getMtype() == 2){
            String decrypt = SecurityTool.iosdecrypt(bankCard);
            bean.setBankCard(decrypt);
//            }else{
//                String android = CaiyiEncrypt.dencryptStr(bankCard);
//                bean.setBankCard(android);
//            }
            log.info("提款卡bin检验,解密后银行卡号:"+bean.getBankCard()+" 用户名:"+bean.getUid());
            BaseReq<UserBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
            req.setData(bean);
            BaseResp<List<BankCardMapPojo>> resp = payCenterCardInfoInterface.drawBankCardInfoByBinno(req);
            List<BankCardMapPojo> bankCardMapPojos = resp.getData();
            if (bankCardMapPojos.size() == 1) {
                BankCardMapPojo bankCardMapPojo = bankCardMapPojos.get(0);
                String cardtype = bankCardMapPojo.getCcardtypename();//银行卡类型
                String bankName = bankCardMapPojo.getCbankname();//银行卡所属银行
                String bankcode = bankCardMapPojo.getCbankcode();//银行编码
                String bcode = bankCardMapPojo.getCbcode();//定义的银行提款对应码
                if("信用卡".equals(cardtype)){
                    bean.setBusiErrCode(-1);
                    bean.setBusiErrDesc("信用卡不可作为提款银行卡，请绑定借记卡");
                    log.info("该卡为信用卡不能进行提款,卡类型:"+cardtype+",银行:"+bankName+",银行卡号:"+bean.getBankCard()+",用户名:"+bean.getUid());
                }
                if(BankUtil.bankImageMap.containsKey(bcode)){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("bankName", bankName);
                    map.put("bankCode", bankcode);
                    map.put("cardType", cardtype);
                    map.put("bcode", bcode);
                    bean.setBusiErrCode(0);
                    bean.setBusiErrDesc("提款卡bin校验成功");
                    result.setData(map);
                }else{
                    bean.setBusiErrCode(-2);
                    bean.setBusiErrDesc("暂时不支持该银行进行提款");
                    log.info("暂时不支持该银行进行提款,卡类型:"+cardtype+",银行:"+bankName+",银行卡号:"+bean.getBankCard()+",用户名:"+bean.getUid());
                }
            }else{
                bean.setBusiErrCode(102);
                bean.setBusiErrDesc("未能检测出该卡的卡bin信息");
                log.info("暂时不支持该银行进行提款,银行卡号:"+bean.getBankCard()+",用户名:"+bean.getUid());
            }
        }catch(Exception e){
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("匹配银行卡信息出错!");
            log.error("UserBeanStub::bankCardInfo", e);
        }
        result.setCode(bean.getBusiErrCode()+"");
        result.setDesc(bean.getBusiErrDesc());
        return result;
    }

    /**
     * 支行信息查询
     */
    @RequestMapping("/pay/query_sub_bank.api")
    public Result querySubBank(PayBean bean){
        Result<ArrayList> result = new Result<ArrayList>();
        try {
            BaseReq<PayBean> req = new BaseReq<PayBean>(SysCodeConstant.PAYWEB);
            req.setData(bean);
            BaseResp<List<PayBean>> resp = payCenterCardInfoInterface.getBankBranchByBcodeProCity(req);
            List<PayBean> bankBranchPojos = resp.getData();
            if (bankBranchPojos!= null && bankBranchPojos.size() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                for (PayBean bankBranchPojo1 : bankBranchPojos){
                    if (bankBranchPojo1 != null){
                        String bankbranch = bankBranchPojo1.getBankbranch();
                        list.add(bankbranch);
                    }
                }
                result.setCode("0");
                result.setDesc("查询支行信息成功");
                result.setData(list);
            } else {
                log.info("没有查询到支行信息");
                result.setCode("-3");
                result.setDesc("没有查询到支行信息");
            }
        } catch (Exception e) {
            result.setCode("-1");
            result.setDesc("查询失败");
            log.info("查询支行信息失败", e);
        }
        return result;
    }

    /**
     * 新版提款接口
     */
    @CheckLogin(sysCode = SysCodeConstant.PAYWEB)
    @RequestMapping("/pay/new_draw_money.api")
    public Result newdrawmoney(PayBean bean){
        Result result = new Result();
        BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
        req.setData(bean);
        BaseResp<PayBean> baseResp = payCenterCardInfoInterface.newDrawTakeMoney(req);
        bean = baseResp.getData();
        result.setCode(String.valueOf(bean.getBusiErrCode()));
        result.setDesc(bean.getBusiErrDesc());
        result.setData(JSONObject.parseObject(bean.getBusiXml()));
        return  result;
    }

    /**
     * 提款-手续费检测
     */
    @CheckLogin(sysCode = SysCodeConstant.PAYWEB)
    @RequestMapping("/pay/check_draw_money_validity.api")
    public Result checkDrawMoneyValidity(PayBean bean){
        Result result = new Result();
        BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
        req.setData(bean);
        BaseResp<PayBean> resp = payCenterCardInfoInterface.checkDrawMoneyValidity(req);
        bean = resp.getData();
        result.setCode(String.valueOf(bean.getBusiErrCode()));
        result.setDesc(bean.getBusiErrDesc());
        result.setData(JSONObject.parse(bean.getBusiXml()));
        return result;
    }

    /**
     * 银行卡限额信息
     */
    @RequestMapping("/pay/bank_card_limit_info.api")
    public Result bankCardLimitInfo(PayBean bean){
        Result result = new Result();
        BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
        req.setData(bean);
        BaseResp<PayBean> baseResp = payCenterCardInfoInterface.queryBankCardLimitInfo(req);
        bean = baseResp.getData();
        result.setCode(String.valueOf(bean.getBusiErrCode()));
        result.setDesc(bean.getBusiErrDesc());
        result.setData(JSONObject.parse(bean.getBusiXml()));
        return result;
    }

    /**
     * 校验银行卡正确性
     */
    @CheckLogin(sysCode = SysCodeConstant.PAYWEB)
    @RequestMapping("/pay/check_cardno.api")
    public Result checkCardNo(PayBean bean){
        Result result = new Result();
        BaseReq<PayBean> req = new BaseReq<>(SysCodeConstant.PAYWEB);
        req.setData(bean);
        BaseResp<PayBean> baseResp= payCenterCardInfoInterface.checkCardNo(req);
        bean = baseResp.getData();
        result.setCode(String.valueOf(bean.getBusiErrCode()));
        result.setDesc(bean.getBusiErrDesc());
        return result;
    }


    /**
     * 联动优势所支持的银行列表(供页面选择)
     */
    @RequestMapping("/pay/select_support_card_list.api")
    public Result selectUmpaySupportCardList(PayBean bean){
        log.info("获取"+bean.getVerifycode()+"所支持的银行列表");
        Result result = new Result();
        try {
            String verifycode = bean.getVerifycode();
            String bankRow = "";
//            StringBuilder builder = new StringBuilder(BaseBean.XML_HEAD);
            JXmlWrapper xml = JXmlWrapper.parse(new File(UMPAY_BANK_LIST));
            if(StringUtil.isEmpty(verifycode) || "0".equals(verifycode)){
                bankRow = "row";
            }
            if("1".equals(verifycode)){
                bankRow = "kq_bank_row";
            }
            if("2".equals(verifycode)){
                bankRow = "shengpay_bank_row";
            }
            if("3".equals(verifycode)){
                bankRow = "yeepay_bank_row";
            }
            if("4".equals(verifycode)){
                bankRow = "chinagpay_bank_row";
            }
            List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList(bankRow);
            result.setCode("0");
            result.setDesc("查询成功");
            List<HashMap<String,String>> mapList = new ArrayList<HashMap<String,String>>();
            for (JXmlWrapper row : xmlNodeList) {
                HashMap<String,String> map = new HashMap<String,String>();
                String bankcode = row.getStringValue("@bankcode");
                String bankname = row.getStringValue("@bankname");
                String cardtype = row.getStringValue("@cardtype");
                if("0".equals(cardtype)){
                    map.put("bankid",bankcode);
                    map.put("bankname",bankname);
                    map.put("cardtype",cardtype);
                }
                //前端要求修改cardtype为1的信用卡不返回，为2的返回cardtype为0   2018-1-16修改
//                if("1".equals(cardtype) && "row".equals(bankRow)){
//                    map.put("bankid",bankcode);
//                    map.put("bankname",bankname);
//                    map.put("cardtype",cardtype);
//                }
//                if("1".equals(cardtype) && "yeepay_bank_row".equals(bankRow)){
//                    map.put("bankid",bankcode);
//                    map.put("bankname",bankname);
//                    map.put("cardtype",cardtype);
//                }
                if("2".equals(cardtype) && "row".equals(bankRow)){
                    map.put("bankid",bankcode);
                    map.put("bankname",bankname);
                    map.put("cardtype","0");
                }
                if("2".equals(cardtype) && "yeepay_bank_row".equals(bankRow)){
                    map.put("bankid",bankcode);
                    map.put("bankname",bankname);
                    map.put("cardtype","0");
                }
                if("2".equals(cardtype) && ("shengpay_bank_row".equals(bankRow) || "chinagpay_bank_row".equals(bankRow))){
                    map.put("bankid",bankcode);
                    map.put("bankname",bankname);
                    map.put("cardtype","0");
                }
                if (!map.isEmpty()){
                    mapList.add(map);
                }
            }
            result.setData(mapList);
        } catch (Exception e) {
            log.error("获取所支持的银行卡列表失败",e.getMessage());
            result.setCode("-1");
            result.setDesc("查询失败");
        }
        return result;
    }


    /**
     * 鉴权开关
     * @return
     */
    public boolean authenticationSwitch(){
        JXmlWrapper xml = null;
        boolean result = false;
        try {
            xml = JXmlWrapper.parse(new File("/opt/export/www/cms/news/ad/87.xml"));
            JXmlWrapper authSwitch = xml.getXmlNode("switch");
            String state = authSwitch.getStringValue("@authSwitch");
            if("0".equals(state)){
                result = true;
            }
            return result;
        } catch (Exception e) {
            log.info("解析鉴权是否打开失败，请检查配置文件:/opt/export/www/cms/news/ad/87.xml是否正确");
            log.error("解析鉴权是否打开失败",e.getMessage());
            return false;
        }
    }


    /**
     * 校验卡是否支持以及返回卡信息
     * @param bean
     * @param bankCardMapPojo
     * @return
     */
    private int checkBankCardAndBuilderInfo(UserBean bean,String  bankInfo,BankCardMapPojo bankCardMapPojo) {
        String cardtype = "";//银行卡类型
        String bankName = "";//银行卡所属银行
        String bankcode = "";
        String authOk = "";
        if(bankCardMapPojo != null){
            cardtype = bankCardMapPojo.getIcardtype();//银行卡类型
            bankName = bankCardMapPojo.getCbankname();//银行卡所属银行
            bankcode = bankCardMapPojo.getCbankcode();
            authOk = bankCardMapPojo.getCcauthenticationflag();
        }else{
            cardtype = bean.getCityid();
            String[] split = bankInfo.split("_");
            bankName = split[1];//银行卡所属银行
            bankcode = split[0];
            authOk = split[2];
        }
        if(StringUtil.isEmpty(bankName) || StringUtil.isEmpty(cardtype)
                || (!"借记卡".equals(cardtype) && !"信用卡".equals(cardtype) && !"贷记卡".equals(cardtype))
                || !checkSupportCardList(bankName,cardtype,bankcode)){
            //无法获取，返回空
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("暂不支持该银行卡");
            log.info("暂不支持该银行卡");
            return 0;
        }else{
            String cardTypeCode = "0";
            if("借记卡".equals(cardtype)){
                cardTypeCode = "0";
            }else{
                cardTypeCode = "1";
            }
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("bankName", bankName);
            map.put("bankCode", bankcode);
            map.put("cardType", cardtype);
            map.put("cardTypeCode", cardTypeCode);
            if(authenticationSwitch()){
                map.put("authOk", "0");
            }else{
                map.put("authOk", authOk);
            }
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("卡bin校验成功");
            return 1;
        }
    }


    private boolean checkSupportCardList(String bankName, String cardtype, String bankCode) {
        boolean result = false;
        JXmlWrapper config = JXmlWrapper.parse(new File("/opt/export/data/info/config/pay/xml/bank_list_link.xml"));
        if("借记卡".equals(cardtype)){
            cardtype = "0";
        }else if("信用卡".equals(cardtype) || "贷记卡".equals(cardtype) || "准贷记卡".equals(cardtype)){
            cardtype = "1";
        }else{
            return false;  //非借记卡或信用卡不支持
        }
        List<JXmlWrapper> releases = config.getXmlNodeList("row");
        if (releases == null || releases.size() == 0) {
            return false;
        }
        for (JXmlWrapper row : releases) {
            String bank_code = row.getStringValue("@bankcode");
            String card_type = row.getStringValue("@cardtype");
            if(bank_code.equals(bankCode) && (cardtype.equals(card_type) || card_type.equals("2"))){ //支持此银行
                return true;
            }
        }
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.PAYWEB)
    @RequestMapping("/pay/agent_transfer.api")
    public Result agentTransfer(BaseBean bean){
        BaseResp resp=payCenterCardInfoInterface.agentTransfer(new BaseReq<>(bean,SysCodeConstant.PAYWEB));
        Result result=new Result();
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

}

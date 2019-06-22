package com.caiyi.lottery.tradesystem.tradeweb.service.impl;

import com.caiyi.lottery.tradesystem.tradeweb.service.TradeBaseService;
import com.caiyi.lottery.tradesystem.tradeweb.util.ZfbUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import pay.bean.PayBean;
import pay.constant.PayConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TradeBaseServiceImpl implements TradeBaseService {

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";
    // 付完款后服务器通知的页面
    private static String notify_url = PayConstant.NOTIFY_HOST + "/user/zfb_notify.go";
    private static String key = "bhme28413slv4hsrl4637l2ghkr6ms47";
    private static String charset = "UTF-8";
    private static String partnerID = "2088801236877026";
    private static String sellerEmail = "zfb_shzs@9188.com";
    public static HashMap<String, String> OWEBS = new HashMap<String, String>();
    private static Map<String, String> playid = new HashMap<String,String>();
    private static String RETURN_HOST = "http://alipay.9188.com";
    static {
        OWEBS.put("alipay.shanghaicaiyi.com", "alipay.shanghaicaiyi.com");
        OWEBS.put("alipay.9188.com", "alipay.9188.com");

        playid.put("01", "双色球");
        playid.put("03", "福彩3D");
        playid.put("04", "重庆时时彩");
        playid.put("05", "吉林快3");
        playid.put("06", "安徽快3");
        playid.put("07", "七乐彩");
        playid.put("08", "内蒙快3");
        playid.put("09", "江苏快3");
        playid.put("10", "江西快3");
        playid.put("20", "江西时时彩");

        playid.put("50", "超级大乐透");
        playid.put("51", "七星彩");
        playid.put("52", "排列五");
        playid.put("53", "排列三");
        playid.put("54", "11选5");
        playid.put("55", "广东11选5");
        playid.put("56", "十一运夺金");
        playid.put("57", "上海11选5");
        playid.put("58", "快乐扑克3");

        playid.put("80", "胜负彩");
        playid.put("81", "任选九");
        playid.put("82", "进球彩");
        playid.put("83", "半全场");

        playid.put("85", "足球单场-让球胜平负");
        playid.put("86", "足球单场-比分");
        playid.put("87", "足球单场-半全场");
        playid.put("88", "足球单场-上下单双");
        playid.put("89", "足球单场-总进球数");

        playid.put("70","竞彩足球-混合过关");
        playid.put("72","竞彩足球-胜平负");
        playid.put("90","竞彩足球-让球胜平负");
        playid.put("91","竞彩足球-比分");
        playid.put("92","竞彩足球-半全场");
        playid.put("93","竞彩足球-总进球数");

        playid.put("71","竞彩篮球-混合过关");
        playid.put("94","竞彩篮球-胜负");
        playid.put("95","竞彩篮球-让分胜负");
        playid.put("96","竞彩篮球-胜分差");
        playid.put("97","竞彩篮球-大小分");
        playid.put("98","冠军竞猜");
        playid.put("99","冠亚军竞猜");

    }

    @Override
    public boolean isOrderPay(HttpServletRequest request) {
        String serverName = request.getServerName();
        if(OWEBS.get(serverName) != null){
            return true;
        }
        return false;
    }

    @Override
    public String createUrl(PayBean bean, HttpServletRequest request, HttpServletResponse response) throws Exception {
            log.debug("zfb-send");

            log.info("parse_first="+System.currentTimeMillis());

            // response.setHeader("Cache-Control", "no-cache");
            String paygateway = "https://www.alipay.com/cooperate/gateway.do?"; // 支付接口（不可以修改）
            paygateway = ALIPAY_GATEWAY_NEW; // 支付接口（不可以修改）
            String service = "create_direct_pay_by_user";// 快速付款交易服务（不可以修改）
            String sign_type = "MD5";// 文件加密机制（不可以修改）
            String out_trade_no = bean.getApplyid();// 商户网站订单（也就是外部订单号，是通过客户网站传给支付宝，不可以重复）

            String input_charset = charset;
            // 页面编码（不可以修改）
            // partner和key提取方法：登陆签约支付宝账户--->点击“商家服务”就可以看到
            String partner = partnerID; // 支付宝合作伙伴id (账户内提取)

            String body ="彩票"; //bean.getApplydate(); // 商品阿描述，推荐格式：商品名称（订单编号：订单编号）

            // 订单金额 10元必须 10.00
            double s = bean.getAddmoney();
            // Double D1 = new Double(s);
            // int addmoney = D1.intValue();
            // String orderAmount = addmoney + ".00";// 总金额，以分为单位
            String orderAmount = s + "";// 总金额，以分为单位

            String total_fee = orderAmount; // 订单总价

            //订单失效设置
            String it_b_pay="1c";
            String payment_type = "1";// 支付宝类型.1代表商品购买（目前填写1即可，不可以修改）
            String seller_email = sellerEmail; // 卖家支付宝帐户,例如：gwl25@126.com
            String subject = ""; // 商品名称---彩票_双色球__发起_01HM2012080510000067

            //HM-ZH-RG-BD
            if(bean.getApplyid().indexOf("HM")>0){
                subject="彩票_"+playid.get(bean.getApplyid().substring(0, 2))+"_合买_"+bean.getApplyid();
            }else if(bean.getApplyid().indexOf("DG")>0){
                subject="彩票_"+playid.get(bean.getApplyid().substring(0, 2))+"_代购_"+bean.getApplyid();
            }else if(bean.getApplyid().indexOf("ZH")>0){
                subject="彩票_"+playid.get(bean.getApplyid().substring(0, 2))+"_追号_"+bean.getApplyid();
            }else if(bean.getApplyid().indexOf("RG")>0){
                subject="彩票_"+playid.get(bean.getApplyid().substring(0, 2))+"_认购_"+bean.getApplyid();
            }else if(bean.getApplyid().indexOf("BD")>0){
                subject="彩票_"+playid.get(bean.getApplyid().substring(0, 2))+"_保底_"+bean.getApplyid();
            }

            String show_url = RETURN_HOST + "/trade/viewpath.go?pid="+bean.getApplyid();

            //网银-银行
            String payType="";
            String banktype = bean.getBankType();
            if (banktype.equals("00")) {
                payType = "directPay";
            } else {
                payType = "bankPay";
            }
            ///联合登录传值
            String token =  request.getSession().getAttribute("alipay_token")==null?"":(String)request.getSession().getAttribute("alipay_token");

            //防钓鱼时间戳
            String anti_phishing_key  = "";
            //获取客户端的IP地址，建议：编写获取客户端IP地址的程序
            String exter_invoke_ip= "";

            anti_phishing_key = query_timestamp();	//获取防钓鱼时间戳函数
            exter_invoke_ip = bean.getIpAddr();

            log.info("anti_phishing_key="+anti_phishing_key);
            log.info("exter_invoke_ip="+exter_invoke_ip);

            String redirect = ZfbUtil.CreateUrl(paygateway, service, sign_type, out_trade_no, input_charset, partner, key, show_url, body, total_fee, payment_type, seller_email, subject, notify_url, show_url,payType,banktype,token,anti_phishing_key,exter_invoke_ip,it_b_pay);

            String contents = "<meta http-equiv=\"Cache-Control\" content=\"no-cache\"/> \r\n";
            contents += "<script language=\"javascript\">document.location.href='" + redirect + "';</script>";

            bean.setContents(contents);
            log.info("parse_end="+System.currentTimeMillis());
            response.setHeader("Cache-Control", "no-cache");
            return redirect;
        }

    private String query_timestamp() throws MalformedURLException, DocumentException, IOException {
        //构造访问query_timestamp接口的URL串
        String strUrl = ALIPAY_GATEWAY_NEW + "service=query_timestamp&partner=" + partnerID;
        StringBuffer result = new StringBuffer();

        SAXReader reader = new SAXReader();
        Document doc = reader.read(new URL(strUrl).openStream());

        List<Node> nodeList = doc.selectNodes("//alipay/*");

        for (Node node : nodeList) {
            // 截取部分不需要解析的信息
            if (node.getName().equals("is_success") && node.getText().equals("T")) {
                // 判断是否有成功标示
                List<Node> nodeList1 = doc.selectNodes("//response/timestamp/*");
                for (Node node1 : nodeList1) {
                    result.append(node1.getText());
                }
            }
        }
        return result.toString();
    }
}

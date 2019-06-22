package com.caiyi.lottery.tradesystem.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CheckBannedIp {

    public static Map<String, String> sites = new HashMap<>();

    static {
        sites.put("CN", "CN");
        sites.put("HK", "HK");// 香港
        sites.put("TW", "TW");// 台湾
        sites.put("MO", "MO");// 澳门
    }

    public static boolean checkBannedIp(BaseBean bean) {
        int source = bean.getSource();
        String ipAddr = bean.getIpAddr();
        if (source <= 0 || StringUtil.isEmpty(ipAddr)) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_DATA_MISMATCH));
            bean.setBusiErrDesc("IP参数错误");
            return true;
        }
        try {
            List<Integer> unUsableIp = ReadBannedSourceFromConfig();
            log.info("uid=" + bean.getUid() + "ip:" + bean.getIpAddr() + ",source:" + bean.getSource() + ",sourceList:" + unUsableIp.toString());
            if (!unUsableIp.contains(source)) {
                log.info("uid=" + bean.getUid() + "source:" + bean.getSource() + ",非禁止source不检测Ip");
                return false;
            }

            String targetUrl = getUrl(ipAddr);

            String response = HttpClientUtil.httpGet(targetUrl);
            log.info("uid=" + bean.getUid() + "ip:" + bean.getIpAddr() + ",source:" + bean.getSource() + ",response:" + response);
            if (StringUtil.isEmpty(response)) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_DATA_MISMATCH));
                bean.setBusiErrDesc("IP检测失败");
                return true;
            }
            JSONObject json = JSON.parseObject(response);
            int code = json.getInteger("code");
            if (0 != code) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_DATA_MISMATCH));
                bean.setBusiErrDesc("检测IP出错");
                return true;
            }

            String dataString = json.getString("data");
            if (com.mina.rbc.util.StringUtil.isEmpty(dataString)) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_DATA_MISMATCH));
                bean.setBusiErrDesc("IP检测失败");
                return true;
            }

            JSONObject data = json.getJSONObject("data");
            String county = data.getString("country_id");
            String returnIp = data.getString("ip");
            if (sites.containsKey(county) && bean.getIpAddr().equalsIgnoreCase(returnIp)) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                bean.setBusiErrDesc("ip检测通过");
                log.info("检测通过，uid=" + bean.getUid() + ",ip:" + bean.getIpAddr());
                return false;
            }
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("根据相关法律规定您必须在中国境内使用此app");
            log.info("检测结束，uid=" + bean.getUid() + ",禁止当前ip:" + bean.getIpAddr());
        } catch (IOException e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_DATA_MISMATCH));
            bean.setBusiErrDesc("IP检测失败");
            log.error("请求检测ip错误，uid=" + bean.getUid(), e);
        }
        return true;
    }

    private static String getUrl(String ipAddr) {
        String prefix = "http://ip.taobao.com/service/getIpInfo.php";
        StringBuilder builder = new StringBuilder();
        builder.append(prefix).append("?").append("ip=").append(ipAddr);
        return builder.toString();

    }

    private static List<Integer> ReadBannedSourceFromConfig() {
        List<Integer> list = new ArrayList<>();
        String path = "/opt/export/www/cms/news/ad/source_check_ip.xml";
        JXmlWrapper config = JXmlWrapper.parse(new File(path));
        List<JXmlWrapper> rows = config.getXmlNodeList("row");
        int flag = -1;
        int source = -1;
        for (JXmlWrapper row : rows) {
            flag = row.getIntValue("@flag");
            if (1 == flag) { // 检测该source
                source = row.getIntValue("@source");
                list.add(source);
            }
        }
        return list;
    }


    public static void main(String[] args) throws IOException {
        BaseBean bean = new BaseBean();
        bean.setSource(2130);
        String ip = "65.199.22.137";
        bean.setIpAddr(ip);
        if(checkBannedIp(bean)){
            System.out.println("true");
            return;
        }
        System.out.println(false);

    }
}

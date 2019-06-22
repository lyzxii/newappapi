package com.caiyi.lottery.tradesystem.orderweb.service;

import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.TransCodeUtil;
import com.caiyi.lottery.tradesystem.util.TransDetailCodeUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import order.bean.OrderBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tiankun on 2017/12/25.
 */
@Service
public class OrderOtherService {

    private Logger logger = LoggerFactory.getLogger(OrderOtherService.class);

    /**
     * 出票明细翻译
     *
     * @param bean
     * @return
     */
    public  ArrayList<HashMap<String, Object>> transCode(OrderBean bean) throws Exception {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        if (!StringUtil.isEmpty(bean.toString())) {
            JXmlWrapper xml = JXmlWrapper.parse(bean.toString());
            logger.info("查询出的xml文件为:"+xml.toXmlString());
            String code = xml.getXmlRoot().getAttributeValue("code");
            if ("0".equals(code)) {
                xml = xml.getXmlNode("rows");
                int cot = xml.countXmlNodes("row");
                for (int i = 0; i < cot; i++) {
                    String tcode = xml.getStringValue("row[" + i + "].@code");
                    String transCode = TransCodeUtil.transCode(tcode);
                    xml.setValue("row[" + i + "].@code", transCode);
                    String guoguan = TransCodeUtil.getGuoGuan(tcode);
                    //获取出票组合明细
                    xml = TransDetailCodeUtil.getCodeDetail(xml, guoguan, tcode, i);
                    xml.addValue("row[" + i + "].@gg", guoguan);
                    //将一级目录放在map中
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("code", xml.getStringValue("row[" + i + "].@code"));
                    map.put("mul", xml.getStringValue("row[" + i + "].@mul"));
                    map.put("num", xml.getStringValue("row[" + i + "].@num"));
                    map.put("bonus", xml.getStringValue("row[" + i + "].@bonus"));
                    map.put("tax", xml.getStringValue("row[" + i + "].@tax"));
                    map.put("ticketDate", xml.getStringValue("row[" + i + "].@ticketDate"));
                    map.put("expand", xml.getStringValue("row[" + i + "].@expand"));
                    map.put("gg", xml.getStringValue("row[" + i + "].@gg"));
                    //将detail转为list放进map中
                    int count = xml.countXmlNodes("row[" + i + "].detail");
                    ArrayList<HashMap<String, String>> detailList = new ArrayList<>();
                    for (int j = 0; j < count; j++) {
                        HashMap<String, String> detailMap = new HashMap<>();
                        String dcode = xml.getStringValue("row[" + i + "].detail[" + j + "].@d_code");
                        if (dcode == null){
                            break;
                        }
                       // String dguoguan = TransCodeUtil.getGuoGuan(dcode);
                        String dtransCode = TransCodeUtil.transCode(dcode);
                        detailMap.put("d_code", dtransCode);
                        detailMap.put("d_mul", xml.getStringValue("row[" + i + "].detail[" + j + "].@d_mul"));
                        detailMap.put("d_num", xml.getStringValue("row[" + i + "].detail[" + j + "].@d_num"));
                        detailMap.put("d_bonus", xml.getStringValue("row[" + i + "].detail[" + j + "].@d_bonus"));
                        detailMap.put("d_tax", xml.getStringValue("row[" + i + "].detail[" + j + "].@d_tax"));
                        detailMap.put("d_gg", xml.getStringValue("row[" + i + "].detail[" + j + "].@d_gg"));
                        detailList.add(detailMap);
                    }
                    map.put("detail", detailList);
                    list.add(map);
                }
            }
        }
        System.out.println("list="+list);
       logger.info("出票明细翻译出row个数" + list.size() + "条");
        return list;
    }

}


























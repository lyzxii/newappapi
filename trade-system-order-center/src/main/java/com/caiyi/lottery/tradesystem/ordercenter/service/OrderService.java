package com.caiyi.lottery.tradesystem.ordercenter.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;

import order.bean.OrderBean;
import order.dto.NewTicketDetailDTO;
import order.response.XmlResp;

import java.util.List;
import java.util.Map;

/**
 * 订单Service
 *
 * @author GJ
 * @create 2017-12-21 15:49
 **/
public interface OrderService {
    /**
     * 旋转矩阵获取旋转之后的codes
     * @param bean
     * @return
     */
    List<String> getMatrixCodesList(OrderBean bean);
    /**
     * 检查神单是否到显示的时间
     */
    Boolean checkItemLastDate(String hid, boolean isFollow);

    /**
     * 删除追号记录
     * @param bean
     */
    void hideZhuihaoDetail(OrderBean bean);

    /**查询出票明细xml文件
     * @Description:
     * @Date: 11:28 2017/12/22
     * @param
     * @return:
    */
    XmlResp awarddetail(OrderBean bean) throws Exception;

    /**
     * 隐藏投注记录
     * @param bean
     */
    void hideBuyRecord(OrderBean bean);

    /**
     * 查询投注记录
     * @param bean
     */
    List queryCastDetail(OrderBean bean) throws Exception;

    /**
     * 快频排行
     * @param bean
     */
    Map ranking(OrderBean bean) throws Exception;

    /**
     * 获取彩种的开奖和遗漏值信息
     */
    List lotteryInfoNew(OrderBean bean) throws Exception;

    /**
     * 购彩记录查询
     * @param bean
     */
    Map queryLotteryDetail(OrderBean bean);
 
    /**
     * 查询用户未开奖订单数
     * @param bean
     * @return
     */
    public int queryUserUnbeginNum(BaseBean bean);


    /**
     * 查看乐善中奖明细
     * @param bean
     */
    NewTicketDetailDTO queryLsDetail(OrderBean bean);
}

package com.caiyi.lottery.tradesystem.integralcenter.service;

import com.caiyi.lottery.tradesystem.bean.Page;
import integral.pojo.ExchangeGood;
import integral.bean.PointsMallBean;
import integral.pojo.PointsMallGoods;
import integral.pojo.PointsMallGood;

/**
 * 积分商城查询
 */
public interface PointsMallQueryService {

    /**
     *积分商城页面用户积分和积分商城物品查询

     */
    PointsMallGoods queryJFMallGoods(String cnickid) throws Exception;

    /**
     * 查询积分商城用户兑换记录

     */
    Page queryExchangeRecord(PointsMallBean bean) throws Exception;


    /**
     * 获取兑换物品状态
     */
    ExchangeGood getExchangeGoodsDetail(String uid, String ex_goods_id) throws Exception;


    /**
     * 查询兑换物品详情
     */
    PointsMallGood getExchangeGood(String ex_goods_id) throws Exception;


    /**
     * 积分商城物品兑换
     */
    void exchangeJFGood(PointsMallBean bean) throws Exception;


    void checkIsExchanged(PointsMallBean bean) throws Exception;

    //更新积分
    boolean updatePoints(PointsMallBean bean, int flag, int point, int type, String desc) throws Exception;

    //插入红包任务
    boolean insertRedPacketTsk(PointsMallBean bean, String desc, int type) throws Exception;
}

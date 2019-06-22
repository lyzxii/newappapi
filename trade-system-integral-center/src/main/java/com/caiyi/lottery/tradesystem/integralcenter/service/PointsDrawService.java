package com.caiyi.lottery.tradesystem.integralcenter.service;

import integral.bean.PointsMallBean;

/**
 * 积分抽奖
 */
public interface PointsDrawService {

    /**
     * 获取每天用户剩余抽奖次数
     * @param bean
     * @return
     */
    void getLotteryLeftCnt(PointsMallBean bean) throws Exception;


    /**
     *积分抽奖
     */
    void getLotteryResult(PointsMallBean bean) throws Exception;




    String getPointFromUserCenter(PointsMallBean bean) throws Exception;



}

package com.caiyi.lottery.tradesystem.integralweb.controller;

import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.integralcenter.client.PointsDrawClient;
import com.caiyi.lottery.tradesystem.integralcenter.client.PointsMallClient;
import integral.bean.PointsMallBean;
import integral.pojo.ExchangeStatus;
import integral.pojo.PointsDrawResult;
import integral.pojo.PointsExchangeResult;
import integral.pojo.PointsMallGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wang tao
 * @Date: created in 19:48 2017/12/5
 * @Description: 积分商城web层controller
 */
@RestController
@RequestMapping("/integral")
public class PointsMallWebController {

    @Autowired
    private PointsMallClient pointsMallClient;

    @Autowired
    private PointsDrawClient pointsDrawClient;

    /**
     *获取兑换物品详细信息
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping("/get_exgood_detail.api")
    public Result<ExchangeStatus> getExGoodDetail(PointsMallBean bean) throws Exception{
        BaseResp<ExchangeStatus> resp=pointsMallClient.getExchangeGoodStatus(new BaseReq<>(bean, SysCodeConstant.INTEGRALWEB));
        return new Result<>(Result.SUCCESS,"请求成功",resp.getData());
    }

    /**
     *查询积分商城所有物品
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping("/query_jfmall_goods.api")
    public Result<PointsMallGoods> queryJFMallGoods(PointsMallBean bean){
        BaseResp<PointsMallGoods> resp=pointsMallClient.queryJFMallGoods(bean.getUid());
        return new Result<>(Result.SUCCESS,"请求成功",resp.getData());
    }

    /**
     *兑换物品
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping("/exchange_goods.api")
    public Result<PointsExchangeResult> exchangeGoods(PointsMallBean bean){
       BaseResp<PointsExchangeResult> resp=pointsMallClient.exchangeGoods(new BaseReq<>(bean, SysCodeConstant.INTEGRALWEB));
        return new Result<>(Result.SUCCESS,"请求成功",resp.getData());
    }

    /**
     *查询积分商城用户兑换记录
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping("/query_exchange_record.api")
    public Result<Page> queryExchangeRecord(PointsMallBean bean){
        BaseResp<Page> resp=pointsMallClient.queryExchangeRecord(new BaseReq<>(bean, SysCodeConstant.INTEGRALWEB));
        return new Result<>(Result.SUCCESS,"请求成功",resp.getData());
    }

    /**
     *获得剩余抽奖次数
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping("/get_left_lotterycnt.api")
    public Result<PointsDrawResult> getLeftLotteryCnt(PointsMallBean bean){
        BaseResp<PointsDrawResult> resp=pointsDrawClient.getLeftPointsDrawCnt(new BaseReq<>(bean, SysCodeConstant.INTEGRALWEB));
        return new Result<>(Result.SUCCESS,"请求成功",resp.getData());
    }

    /**
     *积分抽奖
     */
    @CheckLogin(sysCode = SysCodeConstant.INTEGRALWEB)
    @RequestMapping("/get_pointsdraw_result.api")
    public Result<PointsDrawResult> getLotteryResult(PointsMallBean bean){
        BaseResp<PointsDrawResult> resp=pointsDrawClient.getPointsDrawResult(new BaseReq<>(bean, SysCodeConstant.INTEGRALWEB));
        return new Result<>(Result.SUCCESS,"请求成功",resp.getData());
    }


}

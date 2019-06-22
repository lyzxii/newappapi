package com.caiyi.lottery.tradesystem.integralcenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import integral.bean.PointsMallBean;
import integral.pojo.ExchangeStatus;
import integral.pojo.PointsExchangeResult;
import integral.pojo.PointsMallGood;
import integral.pojo.PointsMallGoods;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsMallQueryService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分商城接口
 */
@RestController
@RequestMapping("/integral")
public class PointsMallController {

    @Autowired
    private PointsMallQueryService pointsMallQueryService;

    /**
     * 积分中心获取积分商城所有物品
     * @param cnickid 用户id
     */
    @RequestMapping("/query_jfmall_goods.api")
    public BaseResp<PointsMallGoods> queryJFMallGoods(@RequestParam String cnickid) throws Exception {
       return new BaseResp<>(pointsMallQueryService.queryJFMallGoods(cnickid));
    }

    /**
     * 积分中心获取用户兑换记录
     */
    @RequestMapping("/query_exchange_record.api")
    public BaseResp<Page> queryExchangeRecord(@RequestBody BaseReq<PointsMallBean> req) throws Exception {
        return new BaseResp<>(pointsMallQueryService.queryExchangeRecord(req.getData()));
    }


    /**
     * 获取兑换物品状态
     */
    @RequestMapping("/get_exgood_detail.api")
    public BaseResp<ExchangeStatus> getExchangeGoodStatus(@RequestBody BaseReq<PointsMallBean> req) throws Exception {
        ExchangeStatus status=new ExchangeStatus();
        PointsMallBean bean=req.getData();
        pointsMallQueryService.checkIsExchanged(bean);
        status.setGoods_status(bean.getCheckStatus()+"");
        status.setGoods_desc(bean.getCheckResult());
        PointsMallGood goods=pointsMallQueryService.getExchangeGood(bean.getEx_goods_id());
        status.getGood_detail().add(goods);
        return new BaseResp<>(status);
    }

    /**
     *兑换物品
     */
    @RequestMapping("/exchange_goods.api")
    public BaseResp<PointsExchangeResult> exchangeGoods(@RequestBody BaseReq<PointsMallBean> req)  throws Exception{
        PointsExchangeResult result=new PointsExchangeResult();
        PointsMallBean bean=req.getData();
        pointsMallQueryService.checkIsExchanged(bean);
        result.setCnickid(bean.getUid());
        result.setExt_goods_name(bean.getEx_goods_name());
        if(bean.getBusiErrCode()!=0){//物品可兑换状态检查
            result.setResult(bean.getCheckStatus()+"");
            result.setDesc(bean.getCheckResult());
            return new BaseResp<>(result);
        }
        pointsMallQueryService.exchangeJFGood(bean);
        if(bean.getBusiErrCode()!=0){
            if(bean.getBusiErrCode()==Integer.valueOf(ErrorCode.INTEGRAL_EXGOOD_USERPOINT_ERROR)){//扣除时发现积分不足
                result.setResult(BusiCode.INTEGRAL_EXGOOD_STATUS_NOT_ENOUGH_POINT);
                result.setDesc("无法兑换，积分不足");
            }else{
                result.setResult(ErrorCode.INTEGRAL_EXGOOD_SYS_ERROR);
                result.setDesc("系统内部错误");
            }
            return new BaseResp<>(result);
        }
        result.setResult(BusiCode.INTEGRAL_EXGOOD_STATUS_SUCCESS);
        result.setDesc("兑换成功");
        return new BaseResp<>(result);
    }

}

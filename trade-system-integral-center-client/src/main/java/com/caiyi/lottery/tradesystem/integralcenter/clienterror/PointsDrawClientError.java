package com.caiyi.lottery.tradesystem.integralcenter.clienterror;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.integralcenter.client.PointsDrawClient;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import integral.bean.PointsMallBean;
import integral.pojo.PointsDrawResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by A-0205 on 2018/2/6.
 */
@Slf4j
@Component
public class PointsDrawClientError implements PointsDrawClient{
    /**
     * 获取每天抽奖剩余次数
     * PointsMallBean 中必须传的属性值为uid
     *
     * @param req
     */
    @Override
    public BaseResp<PointsDrawResult> getLeftPointsDrawCnt(BaseReq<PointsMallBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心getLeftPointsDrawCnt调用失败,req:"+req.toJson());
        return resp;
    }

    /**
     * 进行积分抽奖，获取抽奖结果
     * PointsMallBean 中必须传的属性值为uid
     *
     * @param req
     */
    @Override
    public BaseResp<PointsDrawResult> getPointsDrawResult(BaseReq<PointsMallBean> req) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.INTEGRAL_REMOTE_INVOKE_ERROR);
        resp.setDesc("积分中心调用失败");
        log.info("积分中心getPointsDrawResult调用失败,req:"+req.toJson());
        return resp;
    }
}

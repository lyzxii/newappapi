package com.caiyi.lottery.tradesystem.integralcenter.client;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.integralcenter.clienterror.PointsDrawClientError;
import integral.pojo.PointsDrawResult;
import integral.bean.PointsMallBean;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "tradecenter-system-integralcenter-center",fallback = PointsDrawClientError.class)
@RequestMapping("/integral")
public interface PointsDrawClient {

    /**
     *获取每天抽奖剩余次数
     * PointsMallBean 中必须传的属性值为uid
     */
    @RequestMapping("/get_left_lotterycnt.api")
    BaseResp<PointsDrawResult> getLeftPointsDrawCnt(@RequestBody BaseReq<PointsMallBean> req);


    /**
     * 进行积分抽奖，获取抽奖结果
     * PointsMallBean 中必须传的属性值为uid
     *
     */
    @RequestMapping("/get_pointsdraw_result.api")
    BaseResp<PointsDrawResult> getPointsDrawResult(@RequestBody BaseReq<PointsMallBean> req);

}


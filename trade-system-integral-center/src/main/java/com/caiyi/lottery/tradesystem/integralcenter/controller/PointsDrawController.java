package com.caiyi.lottery.tradesystem.integralcenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import integral.bean.PointsMallBean;
import integral.pojo.PointsDrawResult;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsDrawService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分抽奖接口
 */
@RestController
public class PointsDrawController {

    @Autowired
    private PointsDrawService pointsDrawService;


    /**
     *获得用户剩余抽奖次数
     * @return
     */
    @RequestMapping("/integral/get_left_lotterycnt.api")
    public BaseResp<PointsDrawResult> getLeftPointsDrawCnt(@RequestBody BaseReq<PointsMallBean> req) throws Exception{
        PointsDrawResult result=new PointsDrawResult();
        PointsMallBean bean=req.getData();
        pointsDrawService.getLotteryLeftCnt(bean);
        result.setRequire_point(1000);
        result.setCnickid(bean.getUid());
        result.setCj_cnt(bean.getUserExCnt());
        return new BaseResp<>(result);
    }

    /**
     *抽奖接口
     */
    @RequestMapping("/integral/get_pointsdraw_result.api")
    public BaseResp<PointsDrawResult> getPointsDrawResult(@RequestBody BaseReq<PointsMallBean> req) throws Exception {
        PointsDrawResult result=new PointsDrawResult();
        PointsMallBean bean=req.getData();
        pointsDrawService.getLotteryResult(bean);
        result.setCnickid(bean.getUid());
        result.setRequire_point(1000);
        if(bean.getBusiErrCode()!=0){
            result.setStatus(StringUtil.isEmpty(bean.getCheckStatus())?
                    ErrorCode.INTEGRAL_POINTS_DRAW_SYS_ERROR:bean.getCheckStatus());
            result.setPoint(Integer.valueOf(bean.getJf()));
            result.setCj_cnt(bean.getUserExCnt());
            result.setResult(bean.getFlag());
            result.setDesc(bean.getCheckResult());
            return new BaseResp<>(result);
        }
        result.setStatus(BusiCode.INTEGRAL_POINTS_DRAW_SUCCESS);
        result.setPoint(Integer.valueOf(bean.getJf()));
        result.setCj_cnt(bean.getUserExCnt());
        result.setResult(bean.getFlag());
        result.setDesc(bean.getCheckResult());
        return new BaseResp<>(result);
    }

}

package com.caiyi.lottery.tradesystem.integralcenter.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.integralcenter.dao.CJConstraintMapper;
import com.caiyi.lottery.tradesystem.integralcenter.dao.CJRecordMapper;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsDrawService;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsDrawUtil;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsMallQueryService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import integral.bean.PointsMallBean;
import integral.pojo.PointsDrawResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.UserAcctPojo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 积分抽奖controller
 */
@Service
@Slf4j
public class PointsDrawServiceImpl implements PointsDrawService {


    @Autowired
    private CJRecordMapper cJRecordMapper;

    @Autowired
    private PointsMallQueryService pointsMallQueryService;

    @Autowired
    private CJConstraintMapper cJConstraint;

    @Autowired
    private UserBasicInfoInterface userInfoInterface;

    /**
     * 获取用户每天的剩余抽奖次数
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public void getLotteryLeftCnt(PointsMallBean bean) throws Exception {
        String point = getPointFromUserCenter(bean);
        bean.setJf(point);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int cnt= cJRecordMapper.getUserCJCntDay(bean.getUid(),date);//当天已经抽奖次数
        bean.setUserExCnt(10-cnt);
    }

    @Override
    public String getPointFromUserCenter(PointsMallBean bean) throws Exception{
        BaseBean baseBean= new BaseBean();
        baseBean.setUid(bean.getUid());
        BaseResp<UserAcctPojo> resp=userInfoInterface.getUserPoint(new BaseReq<>(baseBean, SysCodeConstant.INTEGRALCENTER));
        if(!"0".equals(resp.getCode())||resp.getData()==null){
            throw new RuntimeException("调用用户中心查询积分出错");
        }
        String jf=resp.getData().getUserpoint()+"";
        log.info("从用户中心查询积分,用户:{},积分:{}",bean.getUid(),jf);
        return jf;
    }


    /**
     * 进行积分抽奖
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getLotteryResult(PointsMallBean bean) throws Exception {
        log.info("用户:{},进入抽奖程序",new Object[]{bean.getUid()});
        getLotteryLeftCnt(bean);
        if(!checkCjCondition(bean)){// 前置条件检查 用户积分>1000 && 当日抽奖次数<10
            bean.setBusiErrCode(-1001);
            log.info("抽奖次数已用完或积分不足,用户:{},积分:{},抽奖剩余次数:{}",
                    bean.getUid(),bean.getJf(),bean.getUserExCnt());
            return;
        }
        log.info("用户:{}抽奖次数和积分检查完毕,满足抽奖条件",new Object[]{bean.getUid()});
        boolean jflag= pointsMallQueryService.updatePoints(bean,1,1000,110,"积分抽奖");
        if(!jflag){
            bean.setBusiErrCode(-1001);
            log.info("扣除积分出错,用户:{},积分：{}",new Object[]{bean.getUid(),bean.getJf()});
            throw new RuntimeException("扣除积分出错");
        }
        log.info("用户:{}抽奖,扣除积分完毕,剩余积分",new Object[]{bean.getUid(),bean.getJf()});
        // 获得抽奖结果
        int result = PointsDrawUtil.draw();
        log.info("用户:{}抽奖,抽奖结果:{}",new Object[]{bean.getUid(), PointsDrawUtil.prizeMap.get(result).getDesc()});
        boolean flag = true;
        if (result != 5) {
            // 检查抽出奖品数量
            boolean isFull = checkIsNotFull(bean,result);
            if (isFull) {// 中奖奖品未超出规定次数
                if (result <= 2) {// 红包
                    String id = PointsDrawUtil.prizeMap.get(result).getId();
                    bean.setEx_goods_id(id);
                    flag = pointsMallQueryService.insertRedPacketTsk(bean,"抽奖抽中红包",1);
                } else if (result == 3) {// 抽中10000积分
                    flag = pointsMallQueryService.updatePoints(bean,0,10000,212,"抽奖中积分");
                } else if (result == 4) {// 抽中5000积分
                    flag = pointsMallQueryService.updatePoints(bean,0,5000,212,"抽奖中积分");
                }
            } else {// 中奖但奖品已超出规定次数 标记为未中奖
                result = 5;
            }
        }
        //处理抽奖结果出错
        if (!flag) {
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.INTEGRAL_POINTS_DRAW_SYS_ERROR));
            log.info("处理抽奖结果出错,用户:{},抽中奖品:{}",new Object[]{bean.getUid(), PointsDrawUtil.prizeMap.get(result).getDesc()});
            throw new RuntimeException("处理抽奖结果出错");
        }
        log.info("用户::" + bean.getUid() + "抽奖,抽奖结果处理完毕");
        int iflag= cJRecordMapper.insertCjRecord(bean.getUid(),result);
        if(iflag!=1){
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.INTEGRAL_POINTS_DRAW_SYS_ERROR));
            log.info("抽奖记录入库出错,用户:{}",new Object[]{bean.getUid()});
            throw new RuntimeException("处理抽奖结果入库出错");
        }
        log.info("用户::" + bean.getUid() + "抽奖,抽奖记录入库完毕");
        getLotteryLeftCnt(bean);
        bean.setFlag(result);//设置抽奖标记
        bean.setCheckResult(PointsDrawUtil.prizeMap.get(result).getDesc());//设置抽奖结果
        log.info("用户::" + bean.getUid() + "离开抽奖程序");
    }

    //检查抽出奖品数量是否在规定范围内
    private boolean checkIsNotFull(PointsMallBean bean, int result) {
        log.info(" 查询抽中奖品是否超过每千次该物品发放数量，抽中奖品:{},用户:{}",
                new Object[]{PointsDrawUtil.prizeMap.get(result).getDesc(),bean.getUid()});
        int cnt= cJRecordMapper.getTotalCnt();
        log.info("截止目前，进行抽奖的总次数为:{}",new Object[]{cnt});
        int pageStart = 0;
        if (cnt != 0) {
            pageStart = (cnt / 1000) * 1000;
        }
        return  excuteCheck(bean,result,pageStart,cnt);
    }

    private boolean excuteCheck(PointsMallBean bean, int result, int killo_start, int total_cnt) {
        String type= PointsDrawUtil.prizeMap.get(result).getId();
        String goods= PointsDrawUtil.prizeMap.get(result).getDesc();
        int p_int= PointsDrawUtil.prizeMap.get(result).getTotalcnt();
        log.info("进入抽奖检查,用户:{},抽中奖品:{}",new Object[]{bean.getUid(),goods});
        PointsDrawResult lresult= cJConstraint.getLeftCnt(type);
        if(lresult==null){
            bean.setBusiErrCode(-1001);
            log.error("查询抽奖限制条件出错,type:{}",new Object[]{type});
            return false;
        }
        int remain_cnt=lresult.getCnt();//剩余次数
        int percnt=lresult.getPer_cnt();
        if(killo_start>percnt){
            int ct=(killo_start-percnt)/1000;
            int rc= cJConstraint.updatePerCnt(killo_start,remain_cnt+ct*p_int,type);
            if(rc!=1){
                bean.setBusiErrCode(-1001);
                return false;
            }
            log.info("第{}-{}次抽奖,奖品{},剩余{}次",new Object[]{killo_start,total_cnt,goods,remain_cnt+ct*p_int});
        }else{
            log.info("第{}-{}次抽奖,奖品{},剩余{}次",new Object[]{killo_start,total_cnt,goods,remain_cnt});
        }
        if(remain_cnt<=0){
            log.info("离开抽奖检查,用户:{},检查结果:{}",new Object[]{bean.getUid(),"奖品已经抽完"});
            return false;
        }
        int flag= cJConstraint.updateCnt(type);
        if(flag!=1){
            bean.setBusiErrCode(-1001);
            log.error("更新抽奖限制条件出错,type:{}",new Object[]{type});
            return false;
        }
        log.info("离开抽奖检查,用户:{},检查结果:{}",new Object[]{bean.getUid(),remain_cnt==0?false:true});
        return true;
    }

    private boolean checkCjCondition(PointsMallBean bean) throws Exception{
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int cnt= cJRecordMapper.getUserCJCntDay(bean.getUid(),date);//当天已经抽奖次数
        bean.setUserExCnt(10-cnt);//设置用户抽奖剩余次数
        if(cnt>=10){
            bean.setCheckStatus(BusiCode.INTEGRAL_POINTS_DRAW_NO_LEFT_CNT);
            bean.setBusiErrDesc("抽奖次数已用完");
            return  false;
        }
        String point = getPointFromUserCenter(bean);
        bean.setJf(point);//设置用户积分
        if(StringUtil.isEmpty(point)){
            bean.setCheckStatus(ErrorCode.INTEGRAL_POINTS_DRAW_SYS_ERROR);
            bean.setBusiErrDesc("查询积分错误");
            return  false;
        }
        if(Integer.valueOf(point)<1000){
            bean.setCheckStatus(BusiCode.INTEGRAL_POINTS_DRAW_NO_ENOUGH_POINT);
            bean.setBusiErrDesc("积分不足");
            return  false;
        }
        return true;
    }


}

package com.caiyi.lottery.tradesystem.integralcenter.service.impl;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.integralcenter.dao.JFExRecordMapper;
import com.caiyi.lottery.tradesystem.integralcenter.dao.JFExchangeMapper;
import com.caiyi.lottery.tradesystem.integralcenter.dao.JFGoodsMapper;
import com.caiyi.lottery.tradesystem.integralcenter.dao.PointChargeMapper;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsDrawService;
import com.caiyi.lottery.tradesystem.integralcenter.service.PointsMallQueryService;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketCenterInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import integral.bean.PointsMallBean;
import integral.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pojo.UserAcctPojo;
import pojo.UserPojo;
import redpacket.bean.RedPacketBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 积分商城查询类
 */
@Service
@Slf4j
public class PointsMallQueryServiceImpl implements PointsMallQueryService {

    @Autowired
    private JFGoodsMapper jfGoodsMapper;

    @Autowired
    private JFExRecordMapper jfExRecordsMapper;

    @Autowired
    private JFExchangeMapper jfExchangeMapper;


    @Autowired
    private PointChargeMapper pointChargeMapper;


    @Autowired
    private RedPacketCenterInterface redPacketCenterInterface;

    @Autowired
    private UserBasicInfoInterface userInfoInterface;

    @Autowired
    private PointsDrawService pointsDrawService;


    /**
     * 积分商城页面用户积分和积分商城物品查询
     * @param cnickid 用户id
     * @return
     */
    @Override
    public PointsMallGoods queryJFMallGoods(String cnickid) throws Exception{
        PointsMallGoods jfMallGoods=new PointsMallGoods();
        PointsMallBean bean=new PointsMallBean();
        bean.setUid(cnickid);
        String ipoint= pointsDrawService.getPointFromUserCenter(bean);//用户积分
        jfMallGoods.setJf(ipoint);
        List<PointsMallGood> jfGoods=jfGoodsMapper.getJFMallGoods();
        jfMallGoods.getGoodsList().addAll(jfGoods);
        return jfMallGoods;
    }

    /**
     * 积分商城用户兑换记录
     */
    @Override
    public Page queryExchangeRecord(PointsMallBean bean) throws Exception{
        int pn=bean.getPn();
        int ps=bean.getPs();
        String uid=bean.getUid();
        PageHelper.startPage(pn,ps);//分页
        List<PointsExchangeRecord> jfGoodsList=jfExRecordsMapper.queryExchangeRecord(uid);
        PageInfo<PointsExchangeRecord> pageInfo=new PageInfo<>(jfGoodsList);
        Page<List<PointsExchangeRecord>> page=new Page<>(ps,pn,pageInfo.getPages(),
                pageInfo.getTotal(),jfGoodsList);
        return page;
    }

    /**
     * 查询积分商城兑换物品详细信息
     * @param uid
     * @param ex_goods_id
     * @return
     */
    @Override
    public ExchangeGood getExchangeGoodsDetail(String uid, String ex_goods_id) throws Exception{
        if(uid==null||ex_goods_id==null){
            log.info("查询积分商城兑换物品详细信息,uid与ex_goods_id其中之一为空");
            throw new IllegalArgumentException("查询积分商城兑换物品详细信息,uid与ex_goods_id都不能为空");
        }
        PointsMallBean bean=new PointsMallBean();
        bean.setUid(uid);
        String ipoint= pointsDrawService.getPointFromUserCenter(bean);//用户积分
        String ex_time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        int userExCnt=jfExchangeMapper.getUserExchangeCnt(ex_time,uid,ex_goods_id);
        int goodsExCnt=jfExchangeMapper.getExchangedCnt(ex_time,ex_goods_id);
        return new ExchangeGood(ipoint,userExCnt,goodsExCnt);
    }

    /**
     * 查询兑换物品详情
     * @param ex_goods_id
     * @return
     * @throws Exception
     */
    @Override
    public PointsMallGood getExchangeGood(String ex_goods_id) throws Exception {
        return jfGoodsMapper.getJFGoodDetail(ex_goods_id);
    }

    private void getExchangeGoodsInfo(PointsMallBean bean) throws Exception {
        String uid=bean.getUid();
        String ex_goods_id=bean.getEx_goods_id();
        ExchangeGood good=getExchangeGoodsDetail(uid,ex_goods_id);//查询兑换物品状态
        PointsMallGood mallGood=jfGoodsMapper.getJFGoodDetail(ex_goods_id);
        bean.setJf(good.getJf());
        bean.setUserExCnt(good.getUserExCnt());
        bean.setGoodsExCnt(good.getGoodsExCnt());
        bean.setRequire_point(mallGood.getRequire_point());
        bean.setEx_goods_cnt(mallGood.getEx_goods_cnt());
        bean.setEx_goods_name(mallGood.getEx_goods_name());
    }
    /**
     * 积分商城商城物品兑换
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchangeJFGood(PointsMallBean bean) throws Exception{
        log.info("用户:{},兑换:{}处理开始",new Object[]{bean.getUid(),bean.getEx_goods_name()});
        boolean flag1=updatePoints(bean,1,bean.getRequire_point(),109,"积分兑换红包");
        if(!flag1){
            log.info("用户::"+bean.getUid()+"兑换"+bean.getEx_goods_name()+",扣除积分失败");
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("扣除积分失败");
            throw new RuntimeException("积分商城商城物品兑换,扣除积分失败");
        }

        boolean flag2=insertJFRecord(bean);
        if(!flag2){
            log.info("用户::"+bean.getUid()+"兑换"+bean.getEx_goods_name()+",插入兑换流水失败");
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("插入兑换流水失败");
            throw new RuntimeException("积分商城商城物品兑换,插入兑换流水失败");
        }
        boolean flag3=insertRedPacketTsk(bean,"积分商城积分兑换红包",5);
        if(!flag3){
            log.info("用户::"+bean.getUid()+"兑换"+bean.getEx_goods_name()+",插入红包任务失败");
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc(",插入红包任务失败");
            throw new RuntimeException("积分商城商城物品兑换,,插入红包任务失败");
        }
        log.info("用户::"+bean.getUid()+"兑换"+bean.getEx_goods_name()+"处理完成");
    }

    @Override
    public void checkIsExchanged(PointsMallBean bean) throws Exception {
        getExchangeGoodsInfo(bean);
        int jf=0;
        if(!StringUtil.isEmpty(bean.getJf())){
            jf=Integer.valueOf(bean.getJf());
        }
        bean.setBusiErrCode(Integer.valueOf(ErrorCode.INTEGRAL_EXGOOD_SYS_ERROR));
        if(bean.getUserExCnt()!=0){
            bean.setCheckStatus(BusiCode.INTEGRAL_EXGOOD_STATUS_HAVE_EXCHANGED);
            bean.setCheckResult("无法兑换，此种物品当天已兑换");
        }else if(bean.getGoodsExCnt()>=bean.getEx_goods_cnt()){
            bean.setCheckStatus(BusiCode.INTEGRAL_EXGOOD_STATUS_NO_LEFT);
            bean.setCheckResult("无法兑换，物品当天已兑完");
        }else if(jf<bean.getRequire_point()){
            bean.setCheckStatus(BusiCode.INTEGRAL_EXGOOD_STATUS_NOT_ENOUGH_POINT);
            bean.setCheckResult("无法兑换，积分不足");
        }else{
            bean.setBusiErrCode(0);
            bean.setCheckStatus(BusiCode.INTEGRAL_EXGOOD_STATUS_CAN_BEEXCHANGED);
            bean.setCheckResult("可以兑换");
        }
    }

    //更换积分
    @Override
    public boolean updatePoints(PointsMallBean bean, int flag, int point, int type, String desc) throws Exception{
        if(bean.getBusiErrCode()!=0){
            log.error("无法兑换，在进行扣除积分时出现错误，用户：{}",bean.getUid());
            return false;
        }
        // ******调用用户中心*********************
        UserBean userBean=new UserBean();
        userBean.setPoint(point);
        userBean.setUid(bean.getUid());
        BaseResp res=null;
        if(flag==1){
            userBean.setFlag(0);
            res=userInfoInterface.updateUserPoint(new BaseReq<>(userBean, SysCodeConstant.INTEGRALCENTER));//调用用户中心扣除积分
        }else if(flag==0){
            userBean.setFlag(1);
            res=userInfoInterface.updateUserPoint(new BaseReq<>(userBean, SysCodeConstant.INTEGRALCENTER));//调用用户中心增加积分
        }
        if(res==null||!"0".equals(res.getCode())){
            throw new RuntimeException("调用用户中心更新积分错误");
        }
        BaseBean baseBean=new BaseBean();
        baseBean.setUid(bean.getUid());
        BaseResp<UserPojo> rep=userInfoInterface.queryUserInfo(new BaseReq<>(baseBean, SysCodeConstant.INTEGRALCENTER));
        if(!"0".equals(rep.getCode())||rep.getData()==null){
            log.error("调用用户中心查询用户基本信息错误,用户名:{}",bean.getUid());
            return false;
        }
        int level=rep.getData().getGradeid();//用户等级
        BaseResp<UserAcctPojo> resp=userInfoInterface.getUserPoint(new BaseReq<>(baseBean, SysCodeConstant.INTEGRALCENTER));
        if(!"0".equals(resp.getCode())||resp.getData()==null){
            log.error("调用用户中心查询用户积分错误,用户名:{}",bean.getUid());
            return false;
        }
        String ipoint= resp.getData().getUserpoint()+"";//用户积分
        if(Integer.valueOf(ipoint)<0){
            bean.setBusiErrCode(Integer.valueOf(ErrorCode.INTEGRAL_EXGOOD_USERPOINT_ERROR));
            bean.setBusiErrDesc("积分为负");
            return false;
        }
        // ******调用用户中心*********************
        PointsCharge pointCharge=new PointsCharge();
        pointCharge.setCnickid(bean.getUid());
        pointCharge.setIpoint(point);
        pointCharge.setItype(flag);
        pointCharge.setCmemo(desc);
        pointCharge.setIbiztype(type);
        pointCharge.setIoldpoint(Integer.valueOf(bean.getJf()));
        pointCharge.setIbalance(Integer.valueOf(ipoint));
        pointCharge.setSource(bean.getSource());
        pointCharge.setIgradeid(level);
        int insertFlag=pointChargeMapper.insertPointCharge(pointCharge);
        if(insertFlag!=1){
           throw new RuntimeException("更新积分出错");
        }
        return true;
    }

    //插入兑换记录
    private boolean insertJFRecord(PointsMallBean bean) throws Exception{
        int flag=jfExRecordsMapper.insertJFExchangeRecord(bean);
        if(flag!=1){
            return false;
        }
        return true;
    }

    //插入红包任务
    @Override
    public boolean insertRedPacketTsk(PointsMallBean bean, String desc, int type) throws Exception{
        if(bean.getBusiErrCode()!=0){
            return false;
        }
        Calendar cd = Calendar.getInstance();
        cd.add(Calendar.DATE, 7);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cdeaddate = sdf.format(cd.getTime());
        //查询红包金额 红包类型
        String redpacketid=bean.getEx_goods_id();
        RedPacketBean redpacket=new RedPacketBean();
        redpacket.setCrpid(Integer.valueOf(redpacketid));
        BaseResp<RedPacketBean> resp=redPacketCenterInterface.getRedpacketDetail(new BaseReq<>(redpacket, SysCodeConstant.INTEGRALCENTER));
        if(!(BusiCode.SUCCESS).equals(resp.getCode())){
            log.error("获取红包详情失败，红包id:{} 用户:{}",redpacketid,bean.getUid());
            return false;
        }
        RedPacketBean redPacket=resp.getData();
        int money=Integer.valueOf(redPacket.getImoney());//红包金额
        int itype=Integer.valueOf(redPacket.getItype());
        redPacket.setCrpid(Integer.valueOf(redpacketid));
        redPacket.setCnickid(bean.getUid());
        redPacket.setCdeaddate(cdeaddate);
        redPacket.setCoperator("sys");
        redPacket.setIgetType(type+"");
        redPacket.setIcardid("");
        redPacket.setCmemo(desc);
        redPacket.setDispatchtime("");
        BaseResp<String> baseResp=redPacketCenterInterface.sendRedpacket(new BaseReq<>(redPacket, SysCodeConstant.INTEGRALCENTER));
       if("0".equals(baseResp.getCode())&& !CheckUtil.isNullString(baseResp.getData())){
            log.info("红包发放成功，领取用户：{},crpid:{},cdeaddate：{},imoney:{},cupacketid:{}",
                    bean.getUid(),redpacketid,cdeaddate,redPacket.getImoney(),redPacket.getCupacketid());
            RedPacketBean rpBean=new RedPacketBean();
            rpBean.setCnickid(bean.getUid());
            rpBean.setItype(itype+"");
            rpBean.setCrpid(Integer.valueOf(redpacketid));
            rpBean.setImoney(money+"");
            BaseResp res=redPacketCenterInterface.insertIntoRedpacketHuodong(new BaseReq<>(rpBean, SysCodeConstant.INTEGRALCENTER));
            if(!"0".equals(res.getCode())){
                log.error("插入红包活动表失败,红包id::"+redpacketid+" 用户::"+bean.getUid());
            }
            return true;
       }else{
           bean.setBusiErrCode(-1001);
           log.info("红包发放失败，用户名："+ bean.getUid()+" 红包id::"+redpacketid+" 出错信息::"+redPacket.getBusiErrDesc());
       }
        return  false;
    }
}

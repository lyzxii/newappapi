package com.caiyi.lottery.tradesystem.redpacketcenter.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.RedpacketChargeMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.RedpacketNewTaskMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.RedpacketMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.RedpacketActivityMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.RedpacketBasicService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RedpacketBasicServiceImpl implements RedpacketBasicService{

    @Autowired
    private RedpacketActivityMapper rp_activityMapper;
    @Autowired
    private RedpacketNewTaskMapper redpacketNewTaskMapper;

    @Autowired
    private RedpacketMapper rpMapper;

    @Autowired
    private RedpacketChargeMapper redpacketChargeMapper;

    @Override
    public int insertIntoRedpacketActivity(RedPacketBean bean) {
        try {
            return rp_activityMapper.insertIntoRedpacket_huodong(bean);
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
        }
        return 0;
    }

    @Override
    public RedPacketBean queryRepacketDetail(RedPacketBean bean){
        try {
            return  rpMapper.queryRpInfo(bean.getCrpid()+"");
        } catch (Exception e) {
            bean.setBusiErrCode(-1);
        }
        return null;
    }

    /**
     * 查询用户滚动打码
     * @param bean
     * @throws Exception
     */
    @Override
    public List<String> queryRolingCode(RedPacketBean bean) throws Exception {
        log.info("查询近30个滚动用户");
        List<String> nickidList = redpacketNewTaskMapper.queryRollingCode();
        if(nickidList != null && nickidList.size() > 0){
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("查询成功");
        }else{
            bean.setBusiErrCode(0);
            bean.setBusiErrDesc("没有数据");
        }
        return nickidList;
    }

    /**
     * 查询是否领取过红包
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public Integer isGetRedPacket(BaseBean bean) throws Exception {
        return redpacketNewTaskMapper.countByNickidAndState1(bean.getUid());
    }

    /**
     * 查询是否以其他身份获取过红包
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public Integer havenRedPacket(BaseBean bean) throws Exception {
        return redpacketNewTaskMapper.countByNickidAndMobileOrIdcard(bean.getUid(), bean.getMd5Mobile(), bean.getMd5IdCard());
    }

    /**
     * 通过用户名查询红包状态
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public BaseResp getRedPacketStateByNickid(BaseBean bean) throws Exception {
        BaseResp baseResp = new BaseResp();
        Integer state = redpacketNewTaskMapper.getStateByNickid(bean.getUid());
        if (state == null) {
            baseResp.setCode(BusiCode.NOT_EXIST);
            baseResp.setDesc("无数据");
            return baseResp;
        }
        baseResp.setCode(BusiCode.SUCCESS);
        baseResp.setDesc("查询成功");
        baseResp.setData(state);
        return baseResp;
    }

    @Override
    public Integer countGroupBuy(BaseBean bean) {
        try {
            bean.setBusiErrDesc("查询合买次数成功");
            return redpacketChargeMapper.countGroupBuy(bean.getUid());
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("查询合买次数失败");
            log.error("查询合买次数失败",e);
        }
        return null;
    }
}

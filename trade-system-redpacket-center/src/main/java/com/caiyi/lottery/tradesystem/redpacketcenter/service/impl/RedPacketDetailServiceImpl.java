package com.caiyi.lottery.tradesystem.redpacketcenter.service.impl;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.RedpacketChargeMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.UserRedpacket_RedpacketMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.RedPacketDetailService;
import com.caiyi.lottery.tradesystem.redpacketcenter.util.RedPacketCenterUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pojo.UserPojo;
import redpacket.bean.RedPacketBean;
import redpacket.bean.RedpacketCharge;
import redpacket.bean.UserRedpacket;
import redpacket.pojo.Rp_UserRpPojo;

import java.util.*;

@Service
@Slf4j
public class RedPacketDetailServiceImpl implements RedPacketDetailService {

    @Autowired
    private RedpacketChargeMapper rpChargeMapper;

    @Autowired
    private UserRedpacket_RedpacketMapper userRp_rpMapper;

    @Autowired
    private UserBasicInfoInterface userInfoInterface;


    @Override
    public Page queryRedPacketDetail(RedPacketBean bean) throws Exception {
        if (bean.getFlag() == 0) {
            bean.setFlag(31);
        }
        log.info("查询用户红包中，sql id:" + "query_redpacket_" + bean.getFlag());
        PageHelper.startPage(bean.getPn(), bean.getPs());
        List<RedpacketCharge> RpChargeList = rpChargeMapper.queryRedPacketCharge(bean.getCupacketid());
        PageInfo<RedpacketCharge> pageInfo = new PageInfo<>(RpChargeList);
        List<Map<String, Object>> dataslist = new ArrayList<>();//存储数据集合
        for (RedpacketCharge rpCharge : RpChargeList) {
            Map<String, Object> rcMap = new HashMap<>();
            rcMap.put("gid", StringUtil.isEmpty(rpCharge.getCgameid())?"":rpCharge.getCgameid().trim());
            rcMap.put("imoney",StringUtil.isEmpty(rpCharge.getImoney())?"":rpCharge.getImoney());
            rcMap.put("itype", rpCharge.getItype());
            rcMap.put("cadddate", StringUtil.isEmpty(rpCharge.getCadddate())?"":rpCharge.getCadddate());
            rcMap.put("ibiztype", rpCharge.getIbiztype());
            rcMap.put("ioldmoney", rpCharge.getIoldmoney());
            rcMap.put("ibalance", rpCharge.getIbalance());
            String cmemo = rpCharge.getCmemo();
            if(StringUtil.isEmpty(cmemo)){
                rcMap.put("hid", "");
            }else{
                rcMap.put("hid", cmemo.substring(3, cmemo.lastIndexOf("|")));
            }
            rcMap.put("ibmoney", rpCharge.getIbmoney());
            dataslist.add(rcMap);
        }
        Page<List<Map<String, Object>>> page = new Page<>(pageInfo.getPageSize(), pageInfo.getPageNum(),
                pageInfo.getPages(), pageInfo.getTotal(), dataslist);
        return page;
    }

    /**
     * 投注前查询用户可用红包
     */
    @Override
    public Page queryRedpacketBeforeCast(RedPacketBean bean) throws Exception {
        log.info("投注前查询用户可用红包,用户名=" + bean.getUid());
        return allRedpacket4Pay(bean);
    }

    private Page allRedpacket4Pay(RedPacketBean bean) throws Exception {
        Page page = new Page();
        List<Map<String, Object>> allList = new ArrayList<>();
        if (bean.getBusiErrCode() != 0) {
            log.info("停售期间用户不能使用红包,用户名=" + bean.getUid());
            page.setDatas(allList);
            return page;
        }
        List<UserRedpacket> list = userRp_rpMapper.query_cast_redpacket(bean);
        if (list.size() == 0) {
            bean.setBusiErrCode(1000);
            bean.setBusiErrDesc("您没有可用红包");
            log.info("用户:{}没有可用红包",bean.getUid());
            page.setDatas(allList);
            return page;
        }
        AssembleRedpacketPageResult(bean, page,getAgentIdFromUserCenter(bean), list,allList);
        return page;
    }

    private void AssembleRedpacketPageResult(RedPacketBean bean, Page page, String agentId, List<UserRedpacket> list, List<Map<String, Object>> allList) {
        for (UserRedpacket redpacket : list) {
            RedPacketBean packetBean = createRedPacketBean(redpacket, agentId, bean.getTrade_imoney(), bean.getTrade_isource());
            boolean vipAvailable = checkVip(bean.getUid(), redpacket);
            if (!vipAvailable) {
                log.info("红包:{} vip用户不可用",redpacket.getRedpacketId());
                continue;
            }
            boolean agentAvailable = RedPacketCenterUtil.checkAgent(packetBean);
            if (!agentAvailable) {
                log.info("红包:{} 渠道不可用",redpacket.getRedpacketId());
                continue;
            }
            boolean scaleAvailable = RedPacketCenterUtil.checkScale(packetBean);
            Map<String, Object> rMap = new HashMap<>();
            rMap.put("cptid", redpacket.getRedpacketId());
            rMap.put("itid", redpacket.getTid());
            rMap.put("crpname", StringUtil.isEmpty(redpacket.getRedpacketName())?"":redpacket.getRedpacketName());
            rMap.put("imoney", redpacket.getImoney());
            rMap.put("irmoney", redpacket.getBalance());
            rMap.put("scale", redpacket.getScale());
            rMap.put("cddate", redpacket.getDeaddate());
            rMap.put("kymoney", RedPacketCenterUtil.getKy_money(packetBean));
            if (!scaleAvailable) {
                rMap.put("isused", 0);
                rMap.put("cgameid",redpacket.getGameid());
                rMap.put("cleftdays", RedPacketCenterUtil.calLeftdays(redpacket.getDeaddate()));
            } else {
                rMap.put("isused", 1);
                rMap.put("cleftdays", redpacket.getDeaddate() == null ? "" : RedPacketCenterUtil.calLeftdays(redpacket.getDeaddate()));
            }
            allList.add(rMap);
        }

        Collections.sort(allList, (m1, m2) -> {
            double ky1 = Double.parseDouble(m1.get("kymoney").toString());
            double ky2 =  Double.parseDouble(m2.get("kymoney").toString());
            if (ky1 > ky2) {
                return -1;
            } else if (ky1 == ky2) {
                String dtime1 = (String) m1.get("kymoney");
                String dtime2 = (String) m2.get("kymoney");
                if (dtime1.compareTo(dtime2) > 0) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 1;
            }
        });
        page.setDatas(allList);
    }

    private String getAgentIdFromUserCenter(RedPacketBean bean) {
        BaseBean baseBean = new BaseBean();
        baseBean.setUid(bean.getUid());
        BaseResp<UserPojo> resp = userInfoInterface.queryUserInfo(new BaseReq<>(baseBean, SysCodeConstant.REDISCENTER));
        if (resp == null || resp.getData() == null || !"0".equals(resp.getCode())) {
            throw new RuntimeException("调用用户中心查询agentid错误");
        }
        return resp.getData().getAgentid();
    }

    private RedPacketBean createRedPacketBean(UserRedpacket redpacket, String agent, String tradeMoney, String tradeSource) {
        RedPacketBean packetBean = new RedPacketBean();
        packetBean.setItid(redpacket.getTid());
        packetBean.setCgameid(redpacket.getGameid());
        packetBean.setScale(redpacket.getScale());
        packetBean.setTrade_imoney(tradeMoney);
        packetBean.setCagent(redpacket.getAvailableAgent());
        packetBean.setIsource(redpacket.getAvailableSource());
        packetBean.setTrade_agent(agent);
        packetBean.setTrade_isource(tradeSource);
        packetBean.setIrmoney(redpacket.getBalance());
        packetBean.setImoney(redpacket.getImoney());
        return packetBean;
    }

    /**
     * 根据当前用户是否是vip 以及红包的vip属性来判断用户的红包是否可用
     */
    private boolean checkVip(String uid, UserRedpacket redpacket) {
        // vip用户不可用(所有vip用户包含返点为0的vip用户)
        if ("1".equals(redpacket.getVipuse())) {
            BaseBean baseBean = new BaseBean();
            baseBean.setUid(uid);
            BaseResp<Integer> resp = userInfoInterface.queryUserVipAgentCount(new BaseReq<>(baseBean, SysCodeConstant.REDISCENTER));
            if (resp == null && resp.getData() == null && !"0".equals(resp.getCode())) {
                throw new RuntimeException("查询用户中心vip数据异常");
            }
            int count = resp.getData();
            if (count > 0) {
                log.info("vip用户红包不可用,用户名=" + uid + ",红包id=" + redpacket.getRedpacketId());
                return false;
            }
        }
        return true;
    }


    public Rp_UserRpPojo queryUserRedpacketDetail(RedPacketBean bean) {
        Rp_UserRpPojo redpacketPojo = userRp_rpMapper.queryUserTradeRedpacket(bean);
        if (redpacketPojo == null) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.REDPACKET_NOTFOUND_RP));
            bean.setBusiErrDesc("未查询到用户红包");
            log.info("未查询到用户交易使用红包,用户名:" + bean.getUid() + " 红包id:" + bean.getCupacketid());
            return null;
        }
        return redpacketPojo;
    }

    //检测用户交易红包
    @Override
    public void checkTradeRedpacket(RedPacketBean bean) {
        Rp_UserRpPojo redpacketPojo = queryUserRedpacketDetail(bean);
        if (bean.getBusiErrCode() != 0) {
            return;
        }
        bean.setCrpid(redpacketPojo.getRpid());
        bean.setImoney(redpacketPojo.getImoney());
        bean.setIrmoney(redpacketPojo.getBalance());
        bean.setCdeaddate(redpacketPojo.getDeaddate());
        bean.setIstate(redpacketPojo.getState());
        bean.setScale(redpacketPojo.getScale());
        bean.setCgameid(redpacketPojo.getGameid());
        bean.setItid(redpacketPojo.getTid());
        RedPacketCenterUtil.check(bean);
    }

}

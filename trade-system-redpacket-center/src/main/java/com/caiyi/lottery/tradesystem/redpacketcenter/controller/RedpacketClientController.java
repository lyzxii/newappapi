package com.caiyi.lottery.tradesystem.redpacketcenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import redpacket.bean.MyRedPacketPage;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.CardChargeRpService;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.MyRedPacketService;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.RedPacketDetailService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 红包中心对客户端提供的接口
 */
@RestController
@Slf4j
public class RedpacketClientController {

    @Autowired
    private RedPacketDetailService redPacketListService;

    @Autowired
    private MyRedPacketService myRedPacketService;

    @Autowired
    private CardChargeRpService cardChargeRpService;


    /**
     * 查询红包列表详情
     */
    @RequestMapping("/redpacket/query_redpacket_detail.api")
    public BaseResp<Page> queryRedPackeDetail(@RequestBody BaseReq<RedPacketBean> req) throws Exception {
        RedPacketBean bean=req.getData();
        BaseResp<Page> resp=new BaseResp<>();
        try {
            Page page=redPacketListService.queryRedPacketDetail(bean);
            resp.setCode(BusiCode.SUCCESS);
            resp.setDesc("查询成功");
            resp.setData(page);
        } catch (Exception e) {
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("查询异常");
        }
        return resp;
    }

    /**
     * 查询我的红包
     */
    @RequestMapping("/redpacket/query_my_redpacket.api")
    public BaseResp<MyRedPacketPage> queryMyRedPacket(@RequestBody BaseReq<RedPacketBean> req) throws Exception {
        RedPacketBean bean = req.getData();
        BaseResp<MyRedPacketPage> resp = new BaseResp<>();
        try {
            MyRedPacketPage myRedPacketPage = myRedPacketService.queryMyRedPacket(bean);
            if (bean.getBusiErrCode()==0||myRedPacketPage != null) {
                resp.setCode(BusiCode.SUCCESS);
                resp.setDesc("查询成功");
                resp.setData(myRedPacketPage);
                return resp;
            }
            resp.setCode(BusiCode.FAIL);
            resp.setDesc(StringUtil.isEmpty(bean.getBusiErrDesc()) ? "查询我的红包出错" : bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("查询我的红包异常:uid:{}", bean.getUid(),e);
        }
        return resp;
    }

    /**
     * 卡密充值送红包
     */
    @RequestMapping("/redpacket/card_charge_redpacket.api")
    public BaseResp RedpacketCardCharge(@RequestBody BaseReq<RedPacketBean> req) throws Exception {
        BaseResp resp = new BaseResp();
        RedPacketBean bean = req.getData();
        try {
            cardChargeRpService.CardCharge(bean);
            resp.setCode(bean.getBusiErrCode() + "");
            resp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("卡密兑换异常");
        }
        return resp;
    }


}

package com.caiyi.lottery.tradesystem.redpacketcenter.controller;

import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.MyRedPacketService;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.RedPacketDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 红包中心对其他服务提供的接口
 *
 */
@RestController
public class RedpacketCenterController {

    @Autowired
    private RedPacketDetailService redPacketDetailService;

    @Autowired
    private MyRedPacketService myRedPacketService;


    /**
     * 投注前查询用户可用红包 此接口交易中心要用到
     */
    @RequestMapping("/redpacket/query_redpacket4pay.api")
    public BaseResp<Page> queryRedpacketBeforeCast(@RequestBody BaseReq<RedPacketBean> req) throws  Exception{
        RedPacketBean bean=req.getData();
        BaseResp<Page> resp=new BaseResp<>();
        Page page=redPacketDetailService.queryRedpacketBeforeCast(bean);
        resp.setCode(bean.getBusiErrCode()+"");
        resp.setDesc(bean.getBusiErrDesc());
        resp.setData(page);
        return resp;
    }

    /**
     * 发送红包接口
     */
    @RequestMapping("/redpacket/send_redpacket.api")
    public BaseResp<String>  sendRedpacket(@RequestBody BaseReq<RedPacketBean> req)throws  Exception{
        RedPacketBean bean=req.getData();
        BaseResp<String> resp=new BaseResp<>();
        myRedPacketService.sendRedpacket(bean);
        resp.setCode(bean.getBusiErrCode()+"");
        resp.setDesc(bean.getBusiErrDesc());
        resp.setData(bean.getCupacketid());
        return resp;
    }
    
    /**
     * 检测用户交易红包是否可用
     * @return
     */
    @RequestMapping(value = "/redpacket/check_trade_redpacket.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<BaseBean> checkTradeRedpacket(@RequestBody BaseReq<RedPacketBean> req){
    	RedPacketBean redPacketBean = req.getData();
    	redPacketDetailService.checkTradeRedpacket(redPacketBean);
    	BaseResp<BaseBean> resp = new BaseResp<>();
    	resp.setCode(redPacketBean.getBusiErrCode()+"");
    	resp.setDesc(redPacketBean.getBusiErrDesc());
    	return resp;
    }
}

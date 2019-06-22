package com.caiyi.lottery.tradesystem.redpacketweb.controller;

import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.RealIP;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import redpacket.bean.MyRedPacketPage;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.redpacketcenter.client.RedPacketClientInterface;
import com.caiyi.lottery.tradesystem.redpacketweb.service.RedPacketWebService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 红包中心对外提供接口
 */
@Slf4j
@RestController
public class RedPacketWebController {

    @Autowired
    private RedPacketClientInterface redPacketClientInterface;

    @Autowired
    private RedPacketWebService redPacketWebService;

    @RequestMapping(value = "/redpacket/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("红包中心redpacket-web启动运行正常");
        return response;
    }

    @RequestMapping(value = "/redpacket/checkhealth.api")
    public Result checkHealth(){
        Response response = redPacketClientInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        log.info("=====检测红包中心服务=====");
        return result;
    }


    @CheckLogin(sysCode = SysCodeConstant.REDPACKETWEB)
    @RequestMapping("/redpacket/query_redpacket_detail.api")
    public Result<Page> queryRedPacketDetail(RedPacketBean redPacketBean) throws Exception {
        Result<Page> result=new Result<>();
        BaseResp<Page> resp=redPacketClientInterface.queryRedPacketDetail(new BaseReq<>(redPacketBean,SysCodeConstant.REDPACKETWEB));
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }


    @CheckLogin(sysCode = SysCodeConstant.REDPACKETWEB)
    @RequestMapping("/redpacket/query_my_redpacket.api")
    public Result<MyRedPacketPage> queryMyRedPacket(RedPacketBean redPacketBean) throws Exception {
        Result<MyRedPacketPage> result=new Result<>();
        BaseResp<MyRedPacketPage> resp=redPacketClientInterface.queryMyRedPacket(new BaseReq<>(redPacketBean,SysCodeConstant.REDPACKETWEB));
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.REDPACKETWEB)
    @RealIP
    @RequestMapping("/redpacket/cardcharge_redpacket.api")
    public Result  CardChargeRedpacket(RedPacketBean bean)throws Exception{
        Result result=new Result();
        BaseResp resp=redPacketWebService.CardChargeRedpacket(bean);
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

}

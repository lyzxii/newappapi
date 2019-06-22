package com.caiyi.lottery.tradesystem.redpacketcenter.controller;


import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import redpacket.bean.RedPacketBean;
import com.caiyi.lottery.tradesystem.redpacketcenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.redpacketcenter.service.RedpacketBasicService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 对外提供红包中心所有相关表的增删改查
 */
@Slf4j
@RestController
public class RedpacketBasicController {

    @Autowired
    private RedpacketBasicService redpacketBasicService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;

    @RequestMapping(value = "/redpacket/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("红包中心redpacket-center启动运行正常");
        return response;
    }

    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/redpacket/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_redpacket");
        redisClient.exists(cacheBean,log, SysCodeConstant.REDISCENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("红包中心服务运行正常");
        return response;
    }

    @RequestMapping("/redpacket/insert_rp_huodong.api")
    public BaseResp insertIntoRedpacketHuodong(@RequestBody BaseReq<RedPacketBean> req)throws Exception{
        int flag=redpacketBasicService.insertIntoRedpacketActivity(req.getData());
        BaseResp resp=new BaseResp();
        if(flag!=1){
            resp.setCode(-1+"");
            resp.setDesc("插入红包活动表错误");
            return  resp;
        }
        resp.setCode(0+"");
        resp.setDesc("插入红包活动表成功");
        return  resp;
    }

    /**
     * 查询用户滚动打码
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/query_rolling_code.api",method = RequestMethod.POST)
    BaseResp queryRollingCode(@RequestBody BaseReq<RedPacketBean> req) {
        BaseResp baseResp = new BaseResp();
        RedPacketBean bean = new RedPacketBean();
        try {
            List<String> nickidList= redpacketBasicService.queryRolingCode(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(nickidList);
        } catch (Exception e) {
            log.error("查询用户滚动打码失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }

        return baseResp;
    }

    @RequestMapping("/redpacket/get_redpacket_detail.api")
    public BaseResp<RedPacketBean> getRedpacketDetail(@RequestBody BaseReq<RedPacketBean> req)throws Exception{
        RedPacketBean redPacketBean=redpacketBasicService.queryRepacketDetail(req.getData());
        BaseResp<RedPacketBean> resp=new BaseResp<>();
        if(redPacketBean==null){
            resp.setCode(-1+"");
            resp.setDesc("查询红包详情出错");
            return resp;
        }
        resp.setCode(0+"");
        resp.setDesc("查询红包详情成功");
        resp.setData(redPacketBean);
        return resp;
    }

    /**
     * 指定用户名是否领取红包
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/is_get_redpacket.api",method = RequestMethod.POST)
    BaseResp<Integer> isGetRedPacket(@RequestBody BaseReq<BaseBean> req) {
        BaseResp baseResp = new BaseResp();
        BaseBean bean = req.getData();
        try {
            Integer count = redpacketBasicService.isGetRedPacket(bean);
            baseResp.setCode(BusiCode.SUCCESS);
            baseResp.setDesc("查询成功");
            baseResp.setData(count);
        } catch (Exception e) {
            log.error("查询指定用户名是否领取红包失败，[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }

        return baseResp;
    }

    /**
     * 是否以其他身份得到过红包
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/haven_redpacket.api",method = RequestMethod.POST)
    BaseResp<Integer> havenRedPacket(@RequestBody BaseReq<BaseBean> req) {
        BaseBean bean = req.getData();
        BaseResp baseResp = new BaseResp();
        try {
            Integer num = redpacketBasicService.havenRedPacket(bean);
            baseResp.setCode(BusiCode.SUCCESS);
            baseResp.setDesc("查询成功");
            baseResp.setData(num);
        } catch (Exception e) {
            log.error("查询是否以其他身份得到过红包失败，[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }

    /**
     * 查询红包状态
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/get_redpacket_state_by_nickid.api",method = RequestMethod.POST)
    BaseResp<Integer> getRedPacketStateByNickid(@RequestBody BaseReq<BaseBean> req) {
        BaseBean bean = req.getData();
        BaseResp baseResp = new BaseResp();
        try {
            baseResp = redpacketBasicService.getRedPacketStateByNickid(bean);
        } catch (Exception e) {
            log.error("查询红包状态失败，[uid:{}]", bean.getUid(), e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("查询失败");
        }
        return baseResp;
    }

    /**
     * 查询合买次数
     * @param req
     * @return
     */
    @RequestMapping(value = "/redpacket/count_groupbuy.api",method = RequestMethod.POST)
    BaseResp<Integer> countGroupBuy(@RequestBody BaseReq<BaseBean> req){
        BaseBean bean=req.getData();
        BaseResp<Integer> resp = new BaseResp<>();
        Integer cnt=redpacketBasicService.countGroupBuy(bean);
        resp.setCode(bean.getBusiErrCode()+"");
        resp.setDesc(bean.getBusiErrDesc());
        if(cnt!=null&&bean.getBusiErrCode()==0){
            resp.setData(cnt);
        }
        return resp;
    }

}

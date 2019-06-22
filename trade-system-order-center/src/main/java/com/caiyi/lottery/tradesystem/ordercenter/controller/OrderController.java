package com.caiyi.lottery.tradesystem.ordercenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.ordercenter.service.OrderService;
import com.caiyi.lottery.tradesystem.ordercenter.service.PassService;
import com.caiyi.lottery.tradesystem.ordercenter.service.impl.OrderServiceImpl;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.NewTicketDetailDTO;
import order.response.XmlResp;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;

/**
 * controller
 *
 * @author GJ
 * @create 2017-12-21 15:50
 **/
@RestController
@Slf4j
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private PassService passService;

    static HashMap<String, String> games = new HashMap<>();
    static HashMap<String, String> kps = new HashMap<>();

    static {
        kps.put("04", "04");
        kps.put("05", "05");
        kps.put("06", "06");
        kps.put("08", "08");
        kps.put("09", "09");
        kps.put("10", "10");
        kps.put("20", "20");
        kps.put("54", "54");
        kps.put("55", "55");
        kps.put("56", "56");
        kps.put("57", "57");
        kps.put("58", "58");
        kps.put("59", "59");
        games.putAll(kps);
    }

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;

    @RequestMapping(value = "/order/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("订单中心integral-center启动运行正常");
        return response;
    }
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/order/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_order");
        redisClient.exists(cacheBean,logger, SysCodeConstant.ORDERCENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("订单中心服务运行正常");
        return response;
    }

    /**
     * 隐藏追号记录
     * @merder 571
     * @param baseReq
     * @return
     * @former /trade/hidezhdetail.go
     */
    @RequestMapping(value = "/order/hide_chasenumber_detail.api")
    public BaseResp hideZhuihaoDetail(@RequestBody BaseReq<OrderBean> baseReq){
        OrderBean bean = null;
        BaseResp rsp = new BaseResp();
        try {
            if(null == baseReq || null == baseReq.getData()){
                rsp.setCode(BusiCode.ORDER_PARAMETER_ERROR);
                rsp.setDesc("传入参数为空");
                return rsp;
            }
            bean = baseReq.getData();
            orderService.hideZhuihaoDetail(bean);
            rsp.setCode(bean.getBusiErrCode()+"");
            rsp.setDesc(bean.getBusiErrDesc());
        }catch (Exception e){
            logger.info("隐藏追号记录发生异常,游戏编号：{}，期次编号：{},异常：{}", bean.getGid(), bean.getPid(),e);
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("隐藏追号记录发生异常");
        }
        return rsp;
    }

    /**
     * 查询出票明细
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/ai_ticket_detail.api")
    public BaseResp<XmlResp> awarddetail(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        BaseResp<XmlResp> response = new BaseResp<>();
        try {
            XmlResp xmlResp = orderService.awarddetail(bean);
            response.setCode(xmlResp.getCode());
            response.setDesc(xmlResp.getDesc());
            response.setData(xmlResp);
        } catch (Exception e) {
            response.setCode(FAIL);
            response.setDesc("查询出票明细程序抛出异常");
            log.error("查询出票明细方法异常,方法名:awarddetail",e);
        }
        return response;
    }


    /**
     * 隐藏投注记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/hide_buy_record.api")
    public BaseResp hideBuyRecord(@RequestBody BaseReq<OrderBean> baseReq){
        OrderBean bean = null;
        BaseResp<XmlResp> rsp = new BaseResp<>();
        try {
            if(null == baseReq || null == baseReq.getData()){
                rsp.setCode(BusiCode.ORDER_PARAMETER_ERROR);
                rsp.setDesc("传入参数为空");
                return rsp;
            }
            bean = baseReq.getData();
            if(!StringUtils.isEmpty(bean.getHid())){
                bean.setDid(bean.getHid());//方案编号
            }else{
                bean.setDid(bean.getBid());//认购编号
            }
            orderService.hideBuyRecord(bean);
            rsp.setCode(bean.getBusiErrCode() + "");
            rsp.setDesc(bean.getBusiErrDesc());
        }catch (Exception e){

            logger.info("隐藏投注记录发生异常,传入参数为空,游戏编号：{}，期次编号：{},认购/方案编号:{},异常：{}", bean.getGid(), bean.getPid(), bean.getDid(),e);
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("隐藏投注记录发生异常");
        }
        return rsp;
    }

    /**
     * 查询投注记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_cast_detail.api")
    public BaseResp queryCastDetail(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean;
        if(null == baseReq || null == baseReq.getData()){
            baseResp.setCode(BusiCode.ORDER_PARAMETER_ERROR);
            baseResp.setDesc("传入参数为空");
            return baseResp;
        }
        bean = baseReq.getData();
        if (!checkLotteryType(bean.getGid())) {
            baseResp.setCode(BusiCode.ORDER_NONSUPPORT_LOTTER);
            baseResp.setDesc("不支持的彩种");
            return baseResp;
        }
        if(!StringUtils.isEmpty(bean.getUid())){
            try {
                List list = orderService.queryCastDetail(bean);
                if(null != list && list.size() > 0){
                    baseResp.setData(list);
                    baseResp.setCode(BusiCode.SUCCESS);
                    baseResp.setDesc("获取成功");
                }else{
                    baseResp.setCode(BusiCode.SUCCESS);
                    baseResp.setDesc("暂无数据");
                }
            }catch(Exception e){
                logger.error("查询投号记录发生异常,用户名：{}，游戏编号：{}，{},异常：{}",bean.getGid(), bean.getUid(), bean.getTid(), e);
                baseResp.setCode(BusiCode.FAIL);
                baseResp.setDesc("查询投号记录发生异常");
            }
        }
        return baseResp;
    }

    /**
     * 检测彩种
     * @param gid
     */
    private boolean checkLotteryType(String gid) {
        if(gid == null || "".equals(gid) || "".equals(gid.trim())){
            return true;
        }
        try {
            Integer.parseInt(gid);
        } catch (Exception e) {
            return false;
        }
       return  games.containsKey(gid);
    }

    /**
     * 快频排行
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/ranking.api")
    public BaseResp ranking(@RequestBody BaseReq<OrderBean> baseReq) {
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        try {
            Map ranking = orderService.ranking(bean);
            baseResp.setData(ranking);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            logger.info("获取快频排行发生异常:{}", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("系统异常，请稍后再试~");
        }
        return baseResp;
    }

    /**
     * 过关统计
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/stat_pass.api")
    public BaseResp statPass(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        try {
            if (StringUtils.isEmpty(bean.getGid())) {
                logger.info("[statPass]传入过关统计彩种id为空");
                baseResp.setCode(BusiCode.ORDER_PARAM_NULL);
                baseResp.setDesc("未传入正确的彩种信息");
                return baseResp;
            }
            baseResp = passService.statPass(bean);
        }catch (Exception e){
            logger.info("过关统计发生异常:{}",e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("系统异常，请稍后再试~");
        }
        return baseResp;
    }


    /**
     * 获取彩种的开奖和遗漏值信息
     * @param req
     * @return
     */
    @RequestMapping(value = "/order/lottery_info_new.api")
    public BaseResp<List> lotteryInfoNew(@RequestBody BaseReq<OrderBean> req){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = req.getData();
        logger.info("订单中心-->获取彩种的开奖和遗漏值信息,uid==" + bean.getUid() + ",gid==" + bean.getGid());
        List json = new ArrayList();
        try {
            json = orderService.lotteryInfoNew(bean);
        } catch (Exception e) {
            log.error("订单中心-->获取彩种的开奖和遗漏值信息出错,uid==" + bean.getUid() + ",gid==" + bean.getGid(), e);
        }
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        baseResp.setData(json);
        return baseResp;
    }


    /**
     * 查询购彩/追号/投注记录
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_lottery_detail.api")
    public BaseResp queryLotteryDetail(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp rsp = new BaseResp();
        OrderBean bean = baseReq.getData();
        try {
            Map map = orderService.queryLotteryDetail(bean);
            if(null != map && map.size() > 0){
                rsp.setData(map);
            }
            rsp.setCode(bean.getBusiErrCode()+"");
            rsp.setDesc(bean.getBusiErrDesc());
        }catch (Exception e){
            logger.info("购彩记录查询异常:{}",e);
            rsp.setCode(BusiCode.FAIL);
            rsp.setDesc("系统异常，请稍后再试~");
        }
        return rsp;
    }

    /**
     * 查询用户未开奖订单数
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/query_unbegin_num.api")
    public BaseResp<Integer> queryUserUnbeginNum(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp<Integer> rsp = new BaseResp<>();
        OrderBean bean = baseReq.getData();
        int num = orderService.queryUserUnbeginNum(bean);
        rsp.setCode(BusiCode.SUCCESS);
        rsp.setDesc("查询成功");
        rsp.setData(num);
        return rsp;
    }

    @RequestMapping(value = "/order/query_matrixinfos.api")
    public BaseResp<List<String>> getMatrixInfos(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        List<String> res = orderService.getMatrixCodesList(bean);
        baseResp.setData(res);
        return baseResp;
    }


    @RequestMapping(value = "/order/query_lsdetail.api")
    public BaseResp<NewTicketDetailDTO> queryLsDetail(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        try {
            NewTicketDetailDTO newTicketDetailDTO = orderService.queryLsDetail(bean);
            baseResp.setData(newTicketDetailDTO);
            baseResp.setCode(bean.getBusiErrCode()+"");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("乐善加奖明细查询失败");
            logger.error("查询大乐透乐善加奖中奖明细失败");
        }
        return baseResp;
    }
}

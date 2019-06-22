package com.caiyi.lottery.tradesystem.orderweb.controller;

import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.ordercenter.client.OrderInterface;
import com.caiyi.lottery.tradesystem.orderweb.service.OrderOtherService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.matrix.MatrixConstants;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.NewTicketDetailDTO;
import order.response.XmlResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;

/**
 * controller
 *
 * @author GJ
 * @create 2017-12-21 15:50
 **/
@RestController
@Slf4j
public class OrderWebController {

    private Logger logger = LoggerFactory.getLogger(OrderWebController.class);

    @Autowired
    private OrderInterface orderInterface;
    @Autowired
    private OrderOtherService orderOtherService;
    @RequestMapping(value = "/order/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("订单中心integral-web启动运行正常");
        return response;
    }
    @RequestMapping(value = "/order/checkhealth.api")
    public Result checkHealth(){
        Response response = orderInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        logger.info("=====检测订单中心服务=====");
        return result;
    }

    /**
     * 追号详情
     *
     * @param bean
     * @return /user/queryrecord.go
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/get_chasenumber_record.api", method = RequestMethod.POST)
    public Result getChaseNumberRecord(OrderBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.getChaseNumberRecord(baseReq);
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        result.setData(rsp.getData());
        return result;
    }

    /**
     * @param
     * @Description:出票明细
     * @Date: 11:13 2017/12/25
     * @return:
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/ai.api", method = RequestMethod.POST)
    public Result<ArrayList<HashMap<String, Object>>> ai(OrderBean bean) {
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        Result<ArrayList<HashMap<String, Object>>> result = new Result();
        try {
            BaseResp<XmlResp> baseResp = orderInterface.awarddetail(baseReq);
            XmlResp projXmlResp = baseResp.getData();
            bean.setBusiErrCode(Integer.valueOf(baseResp.getCode()));
            bean.setBusiErrDesc(baseResp.getDesc());
            if (projXmlResp.getCode().equals("0")) {
                String busiXml = projXmlResp.getData().getBusiXml();
                bean.setBusiXml(busiXml);
            }
            ArrayList<HashMap<String, Object>> list = orderOtherService.transCode(bean);
            result.setCode(baseResp.getCode());
            result.setDesc(baseResp.getDesc());
            result.setData(list);
        } catch (Exception e) {
            result.setCode(FAIL);
            result.setDesc("系统异常,请稍后重试");
            logger.error("查看出票明细程序异常" , e);
        }
        return result;
    }

    /**
     * 隐藏追号记录
     * @param bean
     * @return
     * @create 2017-12-27 16:53:59
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/hide_chasenumber_detail.api", method = RequestMethod.POST)
    public Result hideZhuihaoDetail(OrderBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.hideZhuihaoDetail(baseReq);
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 隐藏投注记录
     * @param bean
     * @return
     * @create 2017-12-27 17:46:32
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/hide_buy_record.api", method = RequestMethod.POST)
    public Result hidebuyrecord(OrderBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.hideBuyRecord(baseReq);
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 查询投注记录(投注+追号) 含投注号码 新版快频使用
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/query_cast_detail.api", method = RequestMethod.POST)
    public Result queryCastDetail(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.queryCastDetail(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 快频排行
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/ranking.api",method = RequestMethod.POST)
    public Result ranking(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.ranking(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 过关统计
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/stat_pass.api",method = RequestMethod.POST)
    public Result statPass(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.statPass(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 查询购彩/追号/投注记录
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/query_lottery_detail.api",method = RequestMethod.POST)
    public Result queryLotteryDetail(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.queryLotteryDetail(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
     }

    /**
     * 查询开奖历史、遗漏值和加奖信息
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/lottery_info_new.api",method = RequestMethod.POST)
    public Result zcastnew(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.lotteryInfoNew(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 竞技彩方案详情
     * @param bean
     * @return
     */

    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/get_athletics_projectdetail.api",method = RequestMethod.POST)
    public Result getAthleticsProjectDetail(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.getAthleticsProjectDetail(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 数字彩方案详情
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/get_digita_projectdetail.api",method = RequestMethod.POST)
    public Result getDigitaProjectDetail(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.getDigitaProjectDetail(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 胜负彩任九
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/get_zucai_projectdetail.api",method = RequestMethod.POST)
    public Result getZucaiProjectDetail(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.getZucaiProjectDetail(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }


    /**
     * 胜负彩任九对阵
     * @param bean
     * @return
     */
    @RequestMapping(value = "/order/get_zucai_match.api",method = RequestMethod.POST)
    public Result getZucaiMatch(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.getZucaiMatch(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    /**
     * 乐善加奖明细
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.ORDERWEB)
    @RequestMapping(value = "/order/query_lsdetail.api",method = RequestMethod.POST)
    public Result queryLsDetail(OrderBean bean){
        Result result = new Result();
        BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
        baseReq.setData(bean);
        BaseResp rsp = orderInterface.queryLsDetail(baseReq);
        result.setData(rsp.getData());
        result.setCode(rsp.getCode());
        result.setDesc(rsp.getDesc());
        return result;
    }

    @RequestMapping(value = "/order/get_matrixinfos.api",method = RequestMethod.GET)
    public String getMatrixCodes(OrderBean bean) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BaseReq baseReq = new BaseReq(SysCodeConstant.ORDERWEB);
            baseReq.setData(bean);
            BaseResp rsp= orderInterface.getMatrixInfos(baseReq);
            if (rsp != null && rsp.getData()!=null) {
                List<String> res = (List<String>) rsp.getData();
                String gamename = "01".equals(bean.getGid()) ?  "双色球" : ("07".equals(bean.getGid()) ?  "七乐彩" : "50".equals(bean.getGid()) ? "大乐透" : "");
                String filename = gamename + "旋转矩阵" + MatrixConstants.MatrixTypeMaps.get(bean.getCodes().substring(bean.getCodes().indexOf("-") + 1)) + "投注号码";

                stringBuilder.append("<html><head></head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>" + filename + "</title><body>");
                for (String c : res) {
                    stringBuilder.append(c + "<br/>");
                }
                stringBuilder.append("</body></html>");
                return stringBuilder.toString();
            }
            stringBuilder.append("<html><head></head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>旋转矩阵</title><body>旋转矩阵获取失败</body></html>");
            return stringBuilder.toString();

        } catch (Exception e) {
            log.error("获取旋转矩阵信息错误", e);
        }
        stringBuilder.append("<html><head></head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>旋转矩阵</title><body>旋转矩阵获取失败</body></html>");
        return stringBuilder.toString();
    }

}


















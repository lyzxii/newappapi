package com.caiyi.lottery.tradesystem.tradeweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.tradecenter.client.TradeInterface;
import com.caiyi.lottery.tradesystem.tradeweb.service.TradeWebServcie;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import trade.bean.TradeBean;
import trade.dto.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Slf4j
@RestController
public class TradeWebController {

    @Autowired
    TradeWebServcie tradeWebServcie;

    @Autowired
    TradeInterface tradeInterface;
    @RequestMapping(value = "/trade/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("交易中心user-web启动运行正常");
        return response;
    }
    @RequestMapping(value = "/trade/checkhealth.api")
    public Result checkHealth(){
        Response response = tradeInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        log.info("=====检测交易中心服务=====");
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/pcast.api", produces = {"application/json;charset=UTF-8"})
    public Result<CastDto> pcast(TradeBean bean) {
        BaseReq<TradeBean> req = new BaseReq<>(bean, SysCodeConstant.TRADEWEB);
        BaseResp<CastDto> resp = tradeInterface.pcast(req);
		Result<CastDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode()) || resp.getCode().equals(BusiCode.TRADE_MP_CAST_NEXT_PID)){
			result.setData(resp.getData());
		}else{
			log.info("用户数字彩投注失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
    }
    
    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/jcast.api", produces = {"application/json;charset=UTF-8"})
    public Result<CastDto> jcast(TradeBean bean) {
        BaseReq<TradeBean> req = new BaseReq<>(bean, SysCodeConstant.TRADEWEB);
        BaseResp<CastDto> resp = tradeInterface.jcast(req);
		Result<CastDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("用户竞技彩投注失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/jczq_optimize_proj.api", produces = {"application/json;charset=UTF-8"})
    public Result<CastDto> jczq_optimize_proj(TradeBean bean) {
        BaseReq<TradeBean> req = new BaseReq<>(bean, SysCodeConstant.TRADEWEB);
        BaseResp<CastDto> resp = tradeInterface.jczq_optimize_proj(req);
		Result<CastDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("用户竞彩足球奖金优化投注失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
    }
    
    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/jclq_optimize_proj.api", produces = {"application/json;charset=UTF-8"})
    public Result<CastDto> jclq_optimize_proj(TradeBean bean) {
        BaseReq<TradeBean> req = new BaseReq<>(bean, SysCodeConstant.TRADEWEB);
        BaseResp<CastDto> resp = tradeInterface.jclq_optimize_proj(req);
		Result<CastDto> result = new Result<>();
		result.setCode(resp.getCode());
		result.setDesc(resp.getDesc());
		if(BusiCode.SUCCESS.equals(resp.getCode())){
			result.setData(resp.getData());
		}else{
			log.info("用户竞彩篮球奖金优化投注失败,用户名:"+bean.getUid()+" code:"+result.getCode()+" desc:"+result.getDesc());
		}
		return result;
    }

    @SetUserData(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/encode_betInfo.api", produces = {"application/json;charset=UTF-8"})
    public Result<String> encodeBetInfo(TradeBean bean) {
        HttpServletRequest request = getRequestAttributes().getRequest();
        Result<String> result = new Result<>();
        BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        bean.setRequestUrl(request.getRequestURL().toString());
        req.setData(bean);
        BaseResp<String> resp = tradeInterface.encodeBetInfo(req);
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        result.setData(resp.getData());
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/encode_jjyh_bet_info.api", produces = {"application/json;charset=UTF-8"})
    public Result<String> encodeJjyhBetInfo(TradeBean bean) {
        HttpServletRequest request = getRequestAttributes().getRequest();
        Result<String> result = new Result<>();
        BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        bean.setRequestUrl(request.getRequestURL().toString());
        req.setData(bean);
        BaseResp<String> resp = tradeInterface.encodeJjyhBetInfo(req);
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        result.setData(resp.getData());
        return result;
    }

    /**
     * 合买和追号停售提示
     *
     * @param bean
     * @return
     */
    @RequestMapping(value = "/trade/hm_zh_remind.api", produces = {"application/json;charset=UTF-8"})
    public Result<String> hmzhremind(TradeBean bean) {
        Result<String> result = new Result<>();
        BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        req.setData(bean);
        BaseResp<String> resp = tradeInterface.hmzhremind(req);
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        result.setData(resp.getData());
        return result;
    }

    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/zcancel.api")
    public Result<String> zcancel(TradeBean bean) {
        log.info("交易中心web-->取消追号开始，uid==" + bean.getUid() + ",gid==" + bean.getGid() + ",zid==" + bean.getZid());
        Result<String> result = new Result<>();
        BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        req.setData(bean);
        BaseResp<String> resp = tradeInterface.zcancel(req);
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        result.setData(resp.getData());
        log.info("交易中心web-->取消追号结束，data==" + resp.getData());
        return result;
    }

    public static void main(String[] args) {

        JSONObject json = JSONObject.parseObject("table");
        System.out.println(json.toJSONString());
    }

    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/zcastnew.api")
    public Result<ZCastDto> zcastnew(TradeBean bean) {
        Result<ZCastDto> result = new Result<>();
        try {
            HttpServletRequest request = getRequestAttributes().getRequest();
            HttpServletResponse response = getRequestAttributes().getResponse();

            BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
            req.setData(bean);
            BaseResp<TradeBean> resp = tradeInterface.zcastnew(req);
            TradeBean resultBean = resp.getData();
            result.setCode(resultBean.getBusiErrCode() + "");
            result.setDesc(resultBean.getBusiErrDesc());

            String busiDesc = resultBean.getBusiXml();
            ZCastDto zCastDto = new ZCastDto();
            if(0 != resultBean.getBusiErrCode()){
                zCastDto.setPid(busiDesc);
            }
            if(0 == resultBean.getBusiErrCode()){
                zCastDto.setId(busiDesc);
            }
            result.setData(zCastDto);
        } catch (Exception e) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("异常出错");
            log.error("io异常,用户uid==" + bean.getUid(), e);
        }
        return result;
    }

    public static ServletRequestAttributes getRequestAttributes() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs;
    }


    /**
     * 投注前查询余额、红包、白名单状态
     * @param bean
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/prepare4pay.api")
    public Result<PrepareCastDto> prepare4Pay(TradeBean bean){
        Result<PrepareCastDto> result=new Result<>();
        BaseResp<PrepareCastDto> resp=tradeInterface.prepare4Pay(new BaseReq<>(bean, SysCodeConstant.TRADEWEB));
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

    /**
     * 一场致胜匹配场次筛选
     * @return
     */
    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/jczq/select_match_dz.api", produces = {"application/json;charset=UTF-8"})
    public Result<List<SelectMatchDto>> select_match_dz(TradeBean bean){
        Result<List<SelectMatchDto>> result=new Result<>();
        BaseResp resp= tradeInterface.select_match_dz(new BaseReq<>(bean, SysCodeConstant.TRADEWEB));
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

    /**
     * 神单跟买投注
     */
    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/fgpcast.api", produces = {"application/json;charset=UTF-8"})
    public Result<JcCastDto> fgpcast(TradeBean bean){
        Result<JcCastDto> result=new Result<>();
        BaseResp<JcCastDto> resp= tradeInterface.fgpcast(new BaseReq<>(bean, SysCodeConstant.TRADEWEB));
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

    /**
     * 一场致胜投注
     */
    @CheckLogin(sysCode = SysCodeConstant.TRADEWEB)
    @RequestMapping(value = "/trade/jczq/project_yczs_cast.api", produces = {"application/json;charset=UTF-8"})
    public Result<JcCastDto> project_yczs_cast(TradeBean bean){
        Result<JcCastDto> result=new Result<>();
        BaseResp<JcCastDto> resp= tradeInterface.project_yczs_cast(new BaseReq<>(bean, SysCodeConstant.TRADEWEB));
        BeanUtilWrapper.copyPropertiesIgnoreNull(resp,result);
        return result;
    }

    @RequestMapping(value = "/trade/decode_bet_info.api",method= RequestMethod.GET)
    public ModelAndView decodeBetInfo(TradeBean bean) throws ServletException, IOException {
        HttpServletRequest request = getRequestAttributes().getRequest();
        HttpServletResponse response = getRequestAttributes().getResponse();
        Result result = new Result<>();
        String incheckor = request.getParameter("checkor");
        String inmessage = request.getParameter("message");
        bean.setCheckor(incheckor);
        bean.setMessage(inmessage);

        BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        req.setData(bean);
        BaseResp<TradeBean> resp = tradeInterface.decodeBetInfo(req);
        bean = resp.getData();
        ModelAndView mav = tradeWebServcie.dispatcherForward(bean,request,response);
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        return mav;
    }

    @RequestMapping(value = "/trade/decode_jjyh_bet_info.api",method= RequestMethod.GET)
    public ModelAndView decodeJjyhBetInfo(TradeBean bean) throws ServletException, IOException {
        HttpServletRequest request = getRequestAttributes().getRequest();
        HttpServletResponse response = getRequestAttributes().getResponse();
        Result result = new Result<>();
        String incheckor = request.getParameter("checkor");
        String inmessage = request.getParameter("message");
        bean.setCheckor(incheckor);
        bean.setMessage(inmessage);

        BaseReq<TradeBean> req = new BaseReq(SysCodeConstant.TRADEWEB);
        req.setData(bean);
        BaseResp<TradeBean> resp = tradeInterface.decodeJjyhBetInfo(req);
        bean = resp.getData();
        ModelAndView mav = tradeWebServcie.dispatcherJjyhForward(bean,request,response);
        result.setCode(resp.getCode());
        result.setDesc(resp.getDesc());
        return mav;
    }


    @RequestMapping(value = "/trade/test.api",method= RequestMethod.GET)
    public ModelAndView test(TradeBean bean) throws ServletException, IOException {

        ModelAndView mav = new ModelAndView();
        mav.setViewName("redirect:/html/index.html");
        return mav;
    }
}

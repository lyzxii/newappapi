package com.caiyi.lottery.tradesystem.tradecenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.tradecenter.service.*;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trade.bean.TradeBean;
import trade.dto.CastDto;
import trade.dto.JcCastDto;
import trade.dto.PrepareCastDto;
import trade.dto.SelectMatchDto;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class TradeController {

    @Autowired
    UserBaseInterface userBaseInterface;

    @Autowired
    TradeService tradeService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private DualMapper dualMapper;

    @Autowired
    PrepareCastService prepareCastService;

    @Autowired
    MatchService matchService;

    @Autowired
    FollowCastService followCastService;

    @Autowired
    YczsCastService yczsCastService;

    @Autowired
    BaseService baseService;



    @RequestMapping(value = "/trade/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("交易中心user-center启动运行正常");
        return response;
    }
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/trade/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean= new CacheBean();
        cacheBean.setKey("checkhealth_trade");
        redisClient.exists(cacheBean,log, SysCodeConstant.TRADECENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("交易中心服务运行正常");
        return response;
    }

    @RequestMapping(value = "/trade/pcast.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<CastDto> pcast(@RequestBody BaseReq<TradeBean> req) {
    	BaseResp<CastDto> resp = new BaseResp<>();
    	TradeBean bean = req.getData();
    	CastDto castDto = tradeService.pcast(bean);
    	if(bean.getBusiErrCode() == 0 || bean.getBusiErrCode() == 30003){
    		resp.setData(castDto);
    	}
    	resp.setCode(bean.getBusiErrCode()+"");
    	resp.setDesc(bean.getBusiErrDesc());
        return resp;
    }

    /**
     * 首先判断是否登录，之后对ios投注信息进行编码并把编码结果返回给给ios端 .
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/encode_bet_info.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<String> encodeBetInfo(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<String> response = new BaseResp();
        TradeBean bean = req.getData();
        String result = tradeService.encodeBetInfo(bean);
        log.info("交易中心-->投注信息进行编码，uid==" + bean.getUid() + "结果=="+result);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(result);
        return response;
    }

    @RequestMapping(value = "/trade/encode_jjyh_bet_info.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<String> encodeJjyhBetInfo(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<String> response = new BaseResp();
        TradeBean bean = req.getData();
        String result = tradeService.encodeJjyhBetInfo(bean);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(result);
        return response;
    }

    @RequestMapping(value = "/trade/hmzh_remind.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<String> hmzhremind(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<String> response = new BaseResp();
        TradeBean bean = req.getData();
        tradeService.hmzhremind(bean);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(bean.getBusiErrDesc());
        return response;
    }

    @RequestMapping(value = "/trade/jcast.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<CastDto> jcast(@RequestBody BaseReq<TradeBean> req) {
    	BaseResp<CastDto> resp = new BaseResp<>();
    	TradeBean bean = req.getData();
    	CastDto castDto = tradeService.jcast(bean);
    	if(bean.getBusiErrCode() == 0){
    		resp.setData(castDto);
    	}
    	resp.setCode(bean.getBusiErrCode()+"");
    	resp.setDesc(bean.getBusiErrDesc());
        return resp;
    }
    
    @RequestMapping(value = "/trade/jczq_optimize_proj.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<CastDto> jczq_optimize_proj(@RequestBody BaseReq<TradeBean> req) {
    	BaseResp<CastDto> resp = new BaseResp<>();
    	TradeBean bean = req.getData();
    	CastDto castDto = tradeService.jczq_optimize_proj(bean);
    	if(bean.getBusiErrCode() == 0){
    		resp.setData(castDto);
    	}
    	resp.setCode(bean.getBusiErrCode()+"");
    	resp.setDesc(bean.getBusiErrDesc());
        return resp;
    }
    
    @RequestMapping(value = "/trade/jclq_optimize_proj.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<CastDto> jclq_optimize_proj(@RequestBody BaseReq<TradeBean> req) {
    	BaseResp<CastDto> resp = new BaseResp<>();
    	TradeBean bean = req.getData();
    	CastDto castDto = tradeService.jclq_optimize_proj(bean);
    	if(bean.getBusiErrCode() == 0){
    		resp.setData(castDto);
    	}
    	resp.setCode(bean.getBusiErrCode()+"");
    	resp.setDesc(bean.getBusiErrDesc());
        return resp;
    }

    /**
     * 取消追号
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/z_cancel.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<String> zcancel(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<String> response = new BaseResp();
        TradeBean bean = req.getData();
        log.info("交易中心center-->取消追号开始，uid==" + bean.getUid() + ",gid==" + bean.getGid() +",zid==" + bean.getZid());
        String result = tradeService.zcancel(bean);
        log.info("交易中心center-->取消追号结果，result==" + result + ",code==" + bean.getBusiErrCode() + ",desc==" + bean.getBusiErrDesc());
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(StringUtil.isEmpty(result)?"取消追号出错":result);
        return response;
    }

    /**
     * 带提示检测追号的期次限制
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/z_castnew.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<TradeBean> zcastnew(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<TradeBean> response = new BaseResp();
        TradeBean bean = req.getData();
        log.info("交易中心center-->带提示检测追号的期次限制开始，uid==" + bean.getUid() + ",gid==" + bean.getGid() +",pid==" + bean.getPid());
        tradeService.zcastnew(bean);
//        log.info("交易中心center-->带提示检测追号的期次限制结果，result==" + result);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(bean);
        return response;
    }

    /**
     * 投注前查询用户账户可用余额,可用红包列表及数字彩当前期次信息
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/prepare4Pay.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<PrepareCastDto> prepare4Pay(@RequestBody BaseReq<TradeBean> req)throws Exception{
        BaseResp<PrepareCastDto> resp = new BaseResp<>();
        TradeBean bean=req.getData();
        PrepareCastDto dto=prepareCastService.prepare4Pay(bean);
        if(bean.getBusiErrCode()!=0){
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("查询出错");
            return resp;
        }
        resp.setCode(BusiCode.SUCCESS);
        resp.setDesc("查询成功");
        resp.setData(dto);
        return resp;
    }

    /**
     * 一场致胜匹配场次筛选
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/jczq/select_match_dz.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<List<SelectMatchDto>> select_match_dz(@RequestBody BaseReq<TradeBean> req){
        BaseResp<List<SelectMatchDto>> resp = new BaseResp<>();
        TradeBean bean=req.getData();
        try {
            List<SelectMatchDto> dtoList=matchService.selectMatchingDz(bean);
            resp.setCode(BusiCode.SUCCESS);
            resp.setDesc("匹配生成成功");
            resp.setData(dtoList);
            return resp;
        } catch (Exception e){
            log.error("select_match_dz req:"+req.toJson(),e);
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("匹配生成失败");
        }
        return resp;
    }

    /**
     * 神单跟买
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/fgpcast.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<JcCastDto> fgpcast(@RequestBody BaseReq<TradeBean> req){
        BaseResp<JcCastDto> resp=new BaseResp<>();
        TradeBean bean=req.getData();
        try {
            JcCastDto dto=followCastService.fgpcast(bean);
            resp.setCode(bean.getBusiErrCode()+"");
            resp.setDesc(bean.getBusiErrDesc());
            if(dto!=null){
               resp.setData(dto);
            }
        } catch (Exception e) {
            log.error("fgpcast req:"+req.toJson(),e);
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("神单跟买异常");
        }
        return resp;
    }

    /**
     * 一场致胜投注
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/jczq/project_yczs_cast.api", produces = {"application/json;charset=UTF-8"})
    BaseResp<JcCastDto> project_yczs_cast(@RequestBody BaseReq<TradeBean> req){
        BaseResp<JcCastDto> resp=new BaseResp<>();
        TradeBean bean=req.getData();
        try {
            JcCastDto dto=yczsCastService.yczs_cast(bean);
            resp.setCode(bean.getBusiErrCode()+"");
            resp.setDesc(bean.getBusiErrDesc());
            if(dto!=null){
                resp.setData(dto);
            }
        } catch (Exception e) {
            log.error("project_yczs_cast req:"+req.toJson(),e);
            resp.setCode(BusiCode.FAIL);
            resp.setDesc("一场致胜投注异常");
        }
        return resp;
    }

    /**
     * 解密投注信息，展示投注详情
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/decode_bet_info.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<TradeBean> decodeBetInfo(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<TradeBean> response = new BaseResp();
        TradeBean bean = req.getData();
        Map<String,String> result = tradeService.decodeBetInfo(bean);
        bean.setMap(result);
        log.info("交易中心-->投注信息进行解密，uid==" + bean.getUid() + "结果=="+result);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(bean);
        return response;
    }

    /**
     * 解密投注信息，展示投注详情
     *
     * @param req
     * @return
     */
    @RequestMapping(value = "/trade/decode_jjyh_bet_info.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<TradeBean> decodeJjyhBetInfo(@RequestBody BaseReq<TradeBean> req) {
        BaseResp<TradeBean> response = new BaseResp();
        TradeBean bean = req.getData();
        Map<String,String> result = tradeService.decodeJjyhBetInfo(bean);
        bean.setMap(result);
        log.info("交易中心-->投注信息进行解密，uid==" + bean.getUid() + "结果=="+result);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(bean);
        return response;
    }
}

package com.caiyi.lottery.tradesystem.tradecenter.client;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;

import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.tradecenter.clienterror.TradeInterfaceError;
import org.springframework.web.bind.annotation.RequestBody;
import trade.bean.TradeBean;
import trade.dto.*;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "tradecenter-system-tradecenter-center")
public interface TradeInterface {
    /**
     * 服务检查
     * @return
     */
    @RequestMapping(value = "/trade/checkhealth.api")
    Response checkHealth() ;

    @RequestMapping(value = "/trade/encode_bet_info.api")
    BaseResp<String> encodeBetInfo(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/encode_jjyh_bet_info.api")
    BaseResp<String> encodeJjyhBetInfo(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/hmzh_remind.api")
    BaseResp<String> hmzhremind(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/pcast.api")
    public BaseResp<CastDto> pcast(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/jcast.api")
    public BaseResp<CastDto> jcast(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/jczq_optimize_proj.api")
    public BaseResp<CastDto> jczq_optimize_proj(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/jclq_optimize_proj.api")
    public BaseResp<CastDto> jclq_optimize_proj(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/z_cancel.api")
    BaseResp<String> zcancel(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/z_castnew.api")
    BaseResp<TradeBean> zcastnew(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/prepare4Pay.api")
    BaseResp<PrepareCastDto> prepare4Pay(@RequestBody BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/jczq/select_match_dz.api")
    BaseResp<List<SelectMatchDto>> select_match_dz(@RequestBody BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/fgpcast.api")
    BaseResp<JcCastDto> fgpcast(@RequestBody BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/jczq/project_yczs_cast.api")
    BaseResp<JcCastDto> project_yczs_cast(@RequestBody BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/decode_bet_info.api")
    BaseResp<TradeBean> decodeBetInfo(BaseReq<TradeBean> req);

    @RequestMapping(value = "/trade/decode_jjyh_bet_info.api")
    BaseResp<TradeBean> decodeJjyhBetInfo(BaseReq<TradeBean> req);
}

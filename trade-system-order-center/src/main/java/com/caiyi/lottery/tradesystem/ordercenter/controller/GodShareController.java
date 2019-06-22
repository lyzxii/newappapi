package com.caiyi.lottery.tradesystem.ordercenter.controller;

import com.caiyi.lottery.tradesystem.annotation.CheckLogin;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.ordercenter.dao.ProjTaskMapper;
import com.caiyi.lottery.tradesystem.ordercenter.service.GodShareService;
import com.caiyi.lottery.tradesystem.ordercenter.service.ProjectInfoService;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import order.dto.*;
import order.pojo.ShareGodUserPojo;
import order.response.XmlResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trade.constants.TradeConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caiyi.lottery.tradesystem.returncode.BusiCode.FAIL;
import static com.caiyi.lottery.tradesystem.returncode.BusiCode.NOT_EXIST;
import static com.caiyi.lottery.tradesystem.returncode.BusiCode.SUCCESS;

/**
 * Created by tiankun on 2017/12/28.
 * 神单详情
 */
@RestController
@Slf4j
public class GodShareController {

    @Autowired
    private GodShareService godShareService;
    @Autowired
    private ProjectInfoService projectInfoService;

    /**
     * 查询该方案大神其余进行中的神单
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/queryOtherItem.api")
    public BaseResp<List<HashMap>> queryOtherItem(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        BaseResp<List<HashMap>> rep = new BaseResp<>();
        try {
            List<HashMap> list = godShareService.queryOtherItem(bean);
            if (list != null && !list.isEmpty()) {
                rep.setCode(SUCCESS);
                rep.setDesc("获取成功");
                rep.setData(list);
            } else {
                rep.setCode(NOT_EXIST);
                rep.setDesc("未查到数据");
            }
        } catch (Exception e) {
            rep.setCode(FAIL);
            rep.setDesc("查询其他进行中的神单出现异常");
            log.error("查询其他进行中的神单出现异常 用户名:" + bean.getUid() + " 方案编号:" + bean.getHid(), e);
        }
        return rep;
    }

    /**
     * 查看神单详情
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/godShareDetail.api")
    public BaseResp<GodShareDetailDTO> godShareDetail(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        BaseResp<GodShareDetailDTO> rep = new BaseResp<>();
        try {
            GodShareDetailDTO dto = godShareService.godShareDetail(bean);
            //调用对阵详情,神单传true
            GamesProjectDTO gamesProjectDTO = projectInfoService.queryDuiZhenDetail(true, bean);
            dto.setGamesProject(gamesProjectDTO);
            if (bean.getBusiErrCode() != 0) {
                rep.setCode(String.valueOf(bean.getBusiErrCode()));
                rep.setDesc(bean.getBusiErrDesc());
            } else {
                rep.setCode(SUCCESS);
                rep.setDesc("查询成功");
                rep.setData(dto);
            }
        } catch (Exception e) {
            rep.setCode(FAIL);
            rep.setDesc("查询神单详情出现异常");
            log.error("查询神单详情出错 用户名:" + bean.getUid() + " 方案编号:" + bean.getHid() + " 错误信息:", e);
        }
        return rep;
    }

    /**
     * 竞彩大神页面
     *
     * @param baseReq
     */
    @RequestMapping(value = "/order/godShareItem.api")
    public BaseResp<HashMap<String, Object>> godShareItem(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        HashMap<String, Object> map = new HashMap<>();
        BaseResp<HashMap<String, Object>> rep = new BaseResp<>();
        try {
            map = godShareService.godShareItem(bean);
            if (bean.getBusiErrCode() != 0) {
                rep.setCode(String.valueOf(bean.getBusiErrCode()));
                rep.setDesc(bean.getBusiErrDesc());
            } else {
                rep.setCode(SUCCESS);
                rep.setDesc("查询成功");
                rep.setData(map);
            }
        } catch (Exception e) {
            rep.setCode(FAIL);
            rep.setDesc("查询竞彩大神页面出现异常");
            log.error("竞彩大神页面出错 用户名:" + bean.getUid() + " 错误信息:" , e);
        }
        return rep;
    }

    /**
     * 新版大神详情
     *
     * @param baseReq
     */
    @RequestMapping(value = "/order/share_user_details_new.api")
    public BaseResp<HashMap<String, Object>> shareUserDetailsNew(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        BaseResp<HashMap<String, Object>> rep = new BaseResp<>();
        HashMap<String, Object> map = new HashMap<>();
        try {
            map = godShareService.shareUserDetailsNew(bean);
            if (bean.getBusiErrCode() != 0) {
                rep.setCode(String.valueOf(bean.getBusiErrCode()));
                rep.setDesc(bean.getBusiErrDesc());
            } else {
                rep.setCode(SUCCESS);
                rep.setDesc("查询成功");
                rep.setData(map);
            }
        } catch (Exception e) {
            rep.setCode(FAIL);
            rep.setDesc("新版大神详情出现异常");
            log.error("新版大神详情出错 用户名:" + bean.getUid() + " 错误信息:", e);
        }
        return rep;
    }

    /**
     * 神单列表
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/god_proj_list.api")
    public BaseResp<HashMap<String, Object>> godProjList(@RequestBody BaseReq<OrderBean> baseReq) {
        OrderBean bean = baseReq.getData();
        BaseResp<HashMap<String, Object>> rep = new BaseResp<>();
        HashMap<String, Object> map = new HashMap<>();
        try {
            map = godShareService.queryGodProjListInfo(bean);
            if (bean.getBusiErrCode() != 0) {
                rep.setCode(String.valueOf(bean.getBusiErrCode()));
                rep.setDesc(bean.getBusiErrDesc());
            } else {
                rep.setCode(SUCCESS);
                rep.setDesc("查询成功");
                rep.setData(map);
            }
        } catch (Exception e) {
            rep.setCode(FAIL);
            rep.setDesc("查询分享神单信息出错");
            log.error("查询分享神单信息出错 用户名:" + bean.getUid() + " 错误信息:", e);
        }
        return rep;
    }

    /**
     * 分享神单
     *
     * @return
     */
    @RequestMapping(value = "/order/share_god_proj_buy.api", produces = {"application/json;charset=UTF-8"})
    public BaseResp<OrderBean> shareGodProj(@RequestBody BaseReq<OrderBean> req) {
        BaseResp<OrderBean> baseResp = new BaseResp<OrderBean>();
        OrderBean bean = req.getData();
        godShareService.shareGodProj(bean);
        baseResp.setData(bean);
        return baseResp;
    }

    @RequestMapping(value = "/order/share_user_data_list.api")
    public BaseResp<List<ShareGodUserPojo>> shareUserDataList(@RequestBody BaseReq<OrderBean> baseReq) {
        BaseResp<List<ShareGodUserPojo>> baseResp = new BaseResp();
        OrderBean bean = new OrderBean();
        bean = baseReq.getData();
        log.info("用户中心center-->查询盈利大神榜单，flag==" + bean.getFlag());
        List<ShareGodUserPojo> list = godShareService.queryShareUserDataList(bean);
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        baseResp.setData(list);
        return baseResp;
    }

    @RequestMapping(value = "/order/query_god_follow_list.api")
    public BaseResp<FollowListDTO> queryGodFollowList(@RequestBody BaseReq<OrderBean> baseReq) {
        BaseResp<FollowListDTO> response = new BaseResp<>();
        OrderBean bean = baseReq.getData();
        log.info("订单中心-->进入center查询跟买人，uid==" + bean.getUid());
        FollowListDTO data = godShareService.queryGodFollowList(bean);
        response.setCode(bean.getBusiErrCode() + "");
        response.setDesc(bean.getBusiErrDesc());
        response.setData(data);
        return response;
    }
}

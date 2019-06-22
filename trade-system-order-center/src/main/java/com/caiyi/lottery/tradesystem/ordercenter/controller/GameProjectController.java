package com.caiyi.lottery.tradesystem.ordercenter.controller;

import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.ordercenter.service.ProjectInfoService;
import com.caiyi.lottery.tradesystem.util.LotteryLogoUtil;
import lombok.extern.slf4j.Slf4j;
import order.bean.ChaseNumberPage;
import order.bean.OrderBean;
import order.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GJ
 * @create 2018-01-15 10:37
 **/
@RestController
@Slf4j
public class GameProjectController {
    @Autowired
    private ProjectInfoService projectInfoService;

    /**
     * 竞技彩方案详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_athletics_projectdetail.api")
    public BaseResp<GamesProjectDTO> getAthleticsProjectDetail(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        GamesProjectDTO gamesProjectDTO = projectInfoService.queryDuiZhenDetail(false, bean);
        ProjectInfoDTO projectInfoDTO = gamesProjectDTO.getProjectInfo();
        if (projectInfoDTO != null) {

            setNull(projectInfoDTO);
        }
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() != -1) {
            baseResp.setData(gamesProjectDTO);
        }
        return baseResp;
    }


    /**
     * 数字彩方案详情
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_digita_projectdetail.api")
    public BaseResp<FigureGamesDTO> getDigitaProjectDetail(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        FigureGamesDTO gamesProjectDTO = projectInfoService.figureGames(bean);
        ProjectInfoDTO projectInfoDTO = gamesProjectDTO.getProjectInfo();
        if (projectInfoDTO != null) {
            setNull(projectInfoDTO);
        }
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() != -1) {
            baseResp.setData(gamesProjectDTO);
        }
        return baseResp;
    }

    /**
     * 胜负彩任九
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_zucai_projectdetail.api")
    public BaseResp<ZucaiMatchProDTO> getZucaiProjectDetail(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        ZucaiMatchProDTO gamesProjectDTO = projectInfoService.zuCaiMatch(bean);
        ProjectInfoDTO projectInfoDTO = gamesProjectDTO.getProjectInfo();
        if (projectInfoDTO != null) {
            setNull(projectInfoDTO);
        }
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() != -1) {
            baseResp.setData(gamesProjectDTO);
        }
        return baseResp;
    }


    /**
     * 胜负彩任九
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/order/get_zucai_match.api")
    public BaseResp<ZuCaiMatchVSDTO> getZucaiMatch(@RequestBody BaseReq<OrderBean> baseReq){
        BaseResp baseResp = new BaseResp();
        OrderBean bean = baseReq.getData();
        ZuCaiMatchVSDTO gamesProjectDTO = projectInfoService.queryZucai(bean);
        baseResp.setCode(bean.getBusiErrCode() + "");
        baseResp.setDesc(bean.getBusiErrDesc());
        if (bean.getBusiErrCode() != -1) {
            baseResp.setData(gamesProjectDTO);
        }
        return baseResp;
    }

    public void setNull(ProjectDTO projectDTO) {
    //    projectDTO.setIfile(null);
        projectDTO.setIreturn(null);
        projectDTO.setCnickid(null);
        projectDTO.setRetdate(null);
        projectDTO.setNums(null);
        projectDTO.setOwins(null);
        projectDTO.setType(null);
        projectDTO.setWininfo(null);
   //     projectDTO.setPid(null);
        projectDTO.setHid(null);
        projectDTO.setGid(null);
        /*if (projectDTO.getCcodes().contains("txt")) {
            projectDTO.setCcodes(null);
        }*/
    }
}

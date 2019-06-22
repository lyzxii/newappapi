package com.caiyi.lottery.tradesystem.ordercenter.service;

import order.bean.OrderBean;
import order.dto.*;

import java.util.HashMap;

/**
 * 方案详情信息
 *
 * @author GJ
 * @create 2018-01-05 16:28
 **/
public interface ProjectInfoService {
    /**
     *  胜负彩和任九对阵
     * @param orderBean
     */
    ZuCaiMatchVSDTO queryZucai(OrderBean orderBean);
    /**
     * 胜负彩和任九
     * @param orderBean
     * @return
     */
    ZucaiMatchProDTO zuCaiMatch(OrderBean orderBean);

    /**
     * 方案详情分类
     * @param orderBean
     * @return
     */
    GamesProjectDTO queryDuiZhenDetail(boolean isShareGod,OrderBean orderBean);

    /**
     * 数字彩方案详情
     * @param orderBean
     * @return
     */
    FigureGamesDTO figureGames(OrderBean orderBean);



    /**
     * 竞技彩方案详情
     * //是否来自大神单排行榜
     * @param orderBean
     */
    GamesProjectDTO matchGames( boolean fromShareRank,int lottery,OrderBean orderBean);

    /**
     * 竞技彩进度，奖金详情
     * @param orderBean
     */
    ProjectDTO matchGamesProjectInfo(OrderBean orderBean);

    /**
     * 竞技彩方案对阵详情
     */
    GamesProjectDTO matchGamesMatchs(OrderBean bean, ProjectInfoDTO projectInfoDTO,int flag, boolean fromShareRank);

     GamesProjectDTO basketmatchGamesMatchs(OrderBean bean,ProjectInfoDTO projectInfoDTO,int flag);

    GamesProjectDTO beidanmatchGamesMatchs(OrderBean bean,ProjectInfoDTO projectInfoDTO,int flag);

    /**
     * 将GamesProjectDTO转为map
     * @param dto
     * @return
     */
    HashMap<String,Object> changeDtoToMap(GamesProjectDTO dto,OrderBean bean);
}

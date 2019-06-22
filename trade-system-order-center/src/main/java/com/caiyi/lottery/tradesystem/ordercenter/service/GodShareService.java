package com.caiyi.lottery.tradesystem.ordercenter.service;

import order.bean.OrderBean;
import order.dto.FollowListDTO;
import order.dto.GodShareDetailDTO;
import order.dto.XmlDTO;
import order.pojo.ShareGodUserPojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tiankun on 2017/12/27.
 * 神单
 */
public interface GodShareService {

    /**
     * 查询该方案大神其余进行中的神单
     * @param bean
     * @return
     */
    List<HashMap> queryOtherItem(OrderBean bean) throws Exception;

    /**
     * 神单详情信息
     * @param bean
     * @return
     * @throws Exception
     */
    GodShareDetailDTO godShareDetail(OrderBean bean) throws Exception;

    /**
     * 竞猜大神详情页
     * @param bean
     * @return
     * @throws Exception
     */
    HashMap<String, Object> godShareItem(OrderBean bean) throws Exception;

    /**
     * 我的详情/大神详情
     * @param bean
     */
    HashMap<String, Object> shareUserDetailsNew(OrderBean bean) throws Exception;

    /**
     * 大神 -- 分享单子列表信息
     *
     * @return
     */
    HashMap<String, Object> queryGodProjListInfo(OrderBean bean) throws Exception;

    /**
     * 分享神单
     * @return
     */
    public void shareGodProj(OrderBean bean);

    /**
     * 查询盈利大神榜单
     * @param bean
     * @return
     */
    List<ShareGodUserPojo> queryShareUserDataList(OrderBean bean);

    /**
     * 查询跟买人
     * @param bean
     * @return
     */
    FollowListDTO queryGodFollowList(OrderBean bean);
}

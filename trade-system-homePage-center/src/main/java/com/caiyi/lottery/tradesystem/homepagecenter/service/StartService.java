package com.caiyi.lottery.tradesystem.homepagecenter.service;

import bean.HomePageBean;
import dto.ConfigDTO;
import dto.StartUpDTO;

/**
 * @author wxy
 * @create 2018-01-18 15:25
 **/
public interface StartService {
    /**
     * 启动接口
     * @param bean
     * @return
     */
    StartUpDTO startup(HomePageBean bean) throws Exception;

    /**
     * 记载配置文件
     * @param bean
     * @return
     * @throws Exception
     */
    ConfigDTO loadMainConfig(HomePageBean bean) throws Exception;

    /**
     * 检查活动禁止状态
     * @param bean
     * @throws Exception
     */
    void checkBanActivity(HomePageBean bean) throws Exception;
}

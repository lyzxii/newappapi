package com.caiyi.lottery.tradesystem.homepagecenter.service;

import bean.HomePageBean;
import dto.LotteryHomePageDTO;

/**
 * @author wxy
 * @create 2018-01-09 20:23
 **/
public interface LotteryHomePageService {
    /**
     * 彩票主页
     * @param bean
     * @return
     * @throws Exception
     */
    LotteryHomePageDTO lotteryHomePage(HomePageBean bean) throws Exception;

}

package com.caiyi.lottery.tradesystem.homepagecenter.client;

import bean.HomePageBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Page;
import dto.*;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 首页中心客户端接口
 */
@FeignClient(name = "tradecenter-system-homePage-center")
public interface HomePageInterface {
    /**
     * 服务检查
     *
     * @return
     */
    @RequestMapping(value = "/home_page/checkhealth.api")
    Response checkHealth();

    /**
     * 彩票首页
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/lottery_home_page.api")
    BaseResp<LotteryHomePageDTO> lotteryHomePage(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * 发现页
     *
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/discover_page.api")
    BaseResp<List<DiscoverDTO>> discoverPage(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * 预测
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/forecast.api")
    BaseResp<Page<List<NewsDTO>>> forecast(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * 查询最新资讯列表
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/app_hot_news.api")
    BaseResp<Page<List<NewsDTO>>> appHotNews(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * 主题启动页
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/theme_start.api")
    BaseResp<ThemDTO> themeStart(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * 启动接口
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/startup.api")
    BaseResp<StartUpDTO> startup(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * IOS加载各种配置文件
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/load_main_config.api")
    BaseResp<ConfigDTO> loadMainConfig(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;

    /**
     * 检查活动禁止状态
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/check_ban_activity.api")
    BaseResp checkBanActivity(@RequestBody BaseReq<HomePageBean> baseReq) throws Exception;
}

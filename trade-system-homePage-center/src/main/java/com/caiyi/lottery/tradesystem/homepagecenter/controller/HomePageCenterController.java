package com.caiyi.lottery.tradesystem.homepagecenter.controller;

import bean.HomePageBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.homepagecenter.dao.DualMapper;
import com.caiyi.lottery.tradesystem.homepagecenter.service.DiscoverPageService;
import com.caiyi.lottery.tradesystem.homepagecenter.service.LotteryHomePageService;
import com.caiyi.lottery.tradesystem.homepagecenter.service.StartService;
import com.caiyi.lottery.tradesystem.homepagecenter.service.ThemeService;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


//客户端展示页面
@Slf4j
@RestController
public class HomePageCenterController {

    @Autowired
    private LotteryHomePageService lotteryHomePageService;
    @Autowired
    private DiscoverPageService discoverPageService;
    @Autowired
    private ThemeService themeService;
    @Autowired
    private StartService startService;

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private DualMapper dualMapper;

    @RequestMapping(value = "/home_page/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("首页中心home_page-center启动运行正常");
        return response;
    }

    /**
     * 服务检查
     *
     * @return
     */
    @RequestMapping(value = "/home_page/checkhealth.api")
    public Response checkHealth() {
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey("checkhealth_home_page");
        redisClient.exists(cacheBean, log, SysCodeConstant.HOMEPAGECENTER);
        dualMapper.check();
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("首页中心服务运行正常");
        return response;
    }

    /**
     * 彩票首页
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/lottery_home_page.api")
    public BaseResp<LotteryHomePageDTO> lotteryHomePage(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<LotteryHomePageDTO> baseResp = new BaseResp();
        try {
            bean.setBusiErrDesc("首页加载成功");
            LotteryHomePageDTO data = lotteryHomePageService.lotteryHomePage(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("彩票首页处理失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("首页加载失败");
        }
        return baseResp;
    }

    /**
     * 发现页
     *
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/discover_page.api")
    public BaseResp<List<DiscoverDTO>> discoverPage(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<List<DiscoverDTO>> baseResp = new BaseResp();
        try {
            bean.setBusiErrDesc("发现页加载成功");
            List<DiscoverDTO> data = discoverPageService.discoverPage(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("彩票发现页处理失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("发现页加载失败");
        }
        return baseResp;
    }

    /**
     * 预测
     *
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/forecast.api")
    public BaseResp<Page<List<NewsDTO>>> forecast(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<Page<List<NewsDTO>>> baseResp = new BaseResp();
        try {
            Page<List<NewsDTO>> data = discoverPageService.forecast(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("预测列表处理失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("预测列表查询失败");
        }
        return baseResp;
    }

    /**
     * 查询最新资讯列表
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/app_hot_news.api")
    public BaseResp<Page<List<NewsDTO>>> appHotNews(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<Page<List<NewsDTO>>> baseResp = new BaseResp();
        try {
            Page<List<NewsDTO>> data = discoverPageService.appHotNews(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("最新资讯列表处理失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("最新资讯列表查询失败");
        }
        return baseResp;
    }

    /**
     * 主题启动页
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/theme_start.api")
    public BaseResp themeStart(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<ThemDTO> baseResp = new BaseResp();
        try {
            ThemDTO data = themeService.themeStart(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("主题启动页处理失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("主题启动页加载失败");
        }
        return baseResp;
    }

    /**
     * 启动接口
     * @param baseReq
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/home_page/startup.api")
    public BaseResp<StartUpDTO> startup(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<StartUpDTO> baseResp = new BaseResp();
        try {
            StartUpDTO data = startService.startup(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("启动处理失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("启动处理失败");
        }
        return baseResp;
    }

    /**
     * IOS加载各种配置文件
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/load_main_config.api")
    public BaseResp<ConfigDTO> loadMainConfig(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp<ConfigDTO> baseResp = new BaseResp();
        try {
            ConfigDTO data = startService.loadMainConfig(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
            baseResp.setData(data);
        } catch (Exception e) {
            log.error("加载配置文件失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("加载配置文件失败");
        }
        return baseResp;
    }

    /**
     * 检查活动禁止状态
     * @param baseReq
     * @return
     */
    @RequestMapping(value = "/home_page/check_ban_activity.api")
    public BaseResp checkBanActivity(@RequestBody BaseReq<HomePageBean> baseReq) {
        HomePageBean bean = baseReq.getData();
        BaseResp baseResp = new BaseResp();
        try {
            startService.checkBanActivity(bean);
            baseResp.setCode(bean.getBusiErrCode() + "");
            baseResp.setDesc(bean.getBusiErrDesc());
        } catch (Exception e) {
            log.error("加载配置文件失败", e);
            baseResp.setCode(BusiCode.FAIL);
            baseResp.setDesc("加载配置文件失败");
        }
        return baseResp;
    }
}

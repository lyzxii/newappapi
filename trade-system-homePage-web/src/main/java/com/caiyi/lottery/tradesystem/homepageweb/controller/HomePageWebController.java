package com.caiyi.lottery.tradesystem.homepageweb.controller;


import bean.HomePageBean;
import bean.UserBean;
import com.caiyi.lottery.tradesystem.annotation.SetUserData;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.homepagecenter.client.HomePageInterface;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页
 */
@Slf4j
@RestController
public class HomePageWebController {
    @Autowired
    private HomePageInterface homePageInterface;

    @RequestMapping(value = "/home_page/checklocalhealth.api")
    public Response checkLocalHealth() {
        Response response = new Response();
        response.setCode(BusiCode.SUCCESS);
        response.setDesc("首页中心home_page-web启动运行正常");
        return response;
    }

    @RequestMapping(value = "/home_page/checkhealth.api")
    public Result checkHealth(){
        Response response = homePageInterface.checkHealth();
        Result result = new Result();
        result.setCode(response.getCode());
        result.setDesc(response.getDesc());
        log.info("=====检测首页中心服务=====");
        return result;
    }
    /**
     * 彩票主页内容
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.HOMEPAGEWEB)
    @RequestMapping(value = "/home_page/lottery_home_page.api" ,produces={"application/json;charset=UTF-8"})
    public Result lotteryHomePage(UserBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.lotteryHomePage(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("彩票主页加载失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("主页加载失败");
        }
        return result;
    }

    /**
     * 发现页接口
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.HOMEPAGEWEB)
    @RequestMapping(value = "/home_page/discover_page.api" ,produces={"application/json;charset=UTF-8"})
    public Result discoverPage(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.discoverPage(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("彩票发现页加载失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("页面加载失败");
        }
        return result;
    }

    /**
     * 查询最新预测
     * @param bean
     * @return
     */
    @RequestMapping(value = "/home_page/forecast.api", produces = {"application/json;charset=UTF-8"})
    public Result forecast(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.forecast(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("预测列表获取失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("预测信息查询失败");
        }
        return result;
    }

    /**
     * 查询最新资讯列表
     * @param bean
     * @return
     */
    @RequestMapping(value = "/home_page/app_hot_news.api", produces = {"application/json;charset=UTF-8"})
    public Result appHotNews(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.appHotNews(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("查询最新资讯列表失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("查询最新资讯列表失败");
        }
        return result;
    }

    /**
     * 主题启动页
     * @param bean
     * @return
     */
    @RequestMapping(value = "/home_page/theme_start.api")
    public Result themeStart(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.themeStart(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("主题启动页加载失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("主题启动页加载失败");
        }
        return result;
    }

    /**
     * 启动接口整合
     * @param bean
     * @return
     */
    @SetUserData(sysCode = SysCodeConstant.HOMEPAGEWEB)
    @RequestMapping(value = "/home_page/startup.api")
    public Result startup(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.startup(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("启动失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("启动失败");
        }
        return result;
    }

    /**
     * IOS加载各种配置文件
     * @param bean
     * @return
     */
    @RequestMapping(value = "/home_page/load_main_config.api")
    public Result loadMainConfig(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.loadMainConfig(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("加载配置文件失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("加载配置文件失败");
        }
        return result;
    }

    /**
     * 检查禁止活动状态
     * @param bean
     * @return
     */
    @RequestMapping(value = "/home_page/check_ban_activity.api")
    public Result checkBanActivity(HomePageBean bean) {
        Result result = new Result();
        BaseReq baseReq = new BaseReq(bean, SysCodeConstant.HOMEPAGEWEB);
        BaseResp baseResp;
        try {
            baseResp = homePageInterface.checkBanActivity(baseReq);
            BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp, result);
        } catch (Exception e) {
            log.error("检查禁止活动状态失败", e);
            result.setCode(BusiCode.FAIL);
            result.setDesc("检查禁止活动状态失败");
        }
        return result;
    }

}

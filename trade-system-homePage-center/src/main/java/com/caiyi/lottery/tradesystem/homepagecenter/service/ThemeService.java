package com.caiyi.lottery.tradesystem.homepagecenter.service;

import bean.HomePageBean;
import dto.ThemDTO;

/**
 * @author wxy
 * @create 2018-01-18 11:00
 **/
public interface ThemeService {
    /**
     * 主题启动页
     * @param bean
     * @return
     */
    ThemDTO themeStart(HomePageBean bean);
}

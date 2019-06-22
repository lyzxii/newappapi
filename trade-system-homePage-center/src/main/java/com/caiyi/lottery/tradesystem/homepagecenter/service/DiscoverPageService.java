package com.caiyi.lottery.tradesystem.homepagecenter.service;

import bean.HomePageBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import dto.DiscoverDTO;
import dto.NewsDTO;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @author wxy
 * @create 2018-01-15 16:24
 **/
public interface DiscoverPageService {
    /**
     * 发现页
     * @param bean
     * @return
     * @throws Exception
     */
    List<DiscoverDTO> discoverPage(HomePageBean bean) throws Exception;

    /**
     * 预测列表
     * @param bean
     * @return
     * @throws Exception
     */
    Page<List<NewsDTO>> forecast(HomePageBean bean) throws Exception;

    /**
     * 查询最新资讯列表
     * @param bean
     * @return
     */
    Page<List<NewsDTO>> appHotNews(HomePageBean bean) throws UnsupportedEncodingException;
}

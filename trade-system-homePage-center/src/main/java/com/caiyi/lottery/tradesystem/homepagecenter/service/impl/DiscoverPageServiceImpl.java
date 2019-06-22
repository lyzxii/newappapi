package com.caiyi.lottery.tradesystem.homepagecenter.service.impl;

import bean.HomePageBean;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.homepagecenter.service.DiscoverPageService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.ParseGeneralRulesUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import dto.DiscoverContentDTO;
import dto.DiscoverDTO;
import dto.NewsDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wxy
 * @create 2018-01-15 16:23
 **/
@Slf4j
@Service
public class DiscoverPageServiceImpl implements DiscoverPageService {
    @Autowired
    private UserBasicInfoWrapper userBasicInfoWrapper;

    @Override
    public List<DiscoverDTO> discoverPage(HomePageBean bean) throws Exception {
        List<DiscoverDTO> discoverDTOList;
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.FIND_MORE_FILE));
        versionDiscover(bean, xml);
        // 获取白名单等级
        String whiteGrade = null;
        if (!StringUtil.isEmpty(bean.getUid())) {
            whiteGrade = userBasicInfoWrapper.queryUserWhiteGrade(bean, log, SysCodeConstant.HOMEPAGECENTER);
        }
        if (StringUtil.isEmpty(whiteGrade)) {
            bean.setWhitelistGrade(0);
        } else {
            bean.setWhitelistGrade(Integer.parseInt(whiteGrade));
        }
        discoverDTOList = parseRulesDiscover(xml, bean);
        return discoverDTOList;
    }

    private void versionDiscover(HomePageBean bean, JXmlWrapper xml) {
        //更新发现文件
        List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("rows");
        for (JXmlWrapper rows : xmlNodeList) {
            List<JXmlWrapper> xmlrow = rows.getXmlNodeList("row");
            for (JXmlWrapper row : xmlrow) {
                int id = row.getIntValue("@id");
                switch (id) {
                    case 4:
                        String currentDateTime = DateUtil.getCurrentDateTime();
                        //客户端查看消息时间 大于 消息添加时间==没有新消息
                        if (StringUtil.isEmpty(bean.getStime())) {
                            row.setValue("@flag", "0");
                        } else if (DateTimeUtil.getDateInterval(row.getStringValue("@time"), bean.getStime()) > 0) {
                            row.setValue("@flag", "1");
                        } else {
                            row.setValue("@flag", "0");
                        }
                        row.setValue("@time", currentDateTime);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private List<DiscoverDTO> parseRulesDiscover(JXmlWrapper xml, HomePageBean bean) {
        List<DiscoverDTO> discoverDTOList = new ArrayList<>();
        DiscoverDTO discoverDTO;
        List<DiscoverContentDTO> discoverContentDTOList;
        DiscoverContentDTO discoverContentDTO;

        List<JXmlWrapper> rowsList = xml.getXmlNodeList("rows");
        for(JXmlWrapper rows : rowsList){
            discoverDTO  = new DiscoverDTO();
            discoverDTO.setGroup(rows.getStringValue("@rows"));
            // discoverDTO.setUserNames(rows.getStringValue("@usernames"));
            // discoverDTO.setContentAPI(rows.getStringValue("@contentAPI"));
            discoverDTO.setGroup(rows.getStringValue("@group"));

            int rowNum = 0;
            discoverContentDTOList = new ArrayList<>();
            List<JXmlWrapper> rowList = rows.getXmlNodeList("row");
            for(JXmlWrapper row : rowList){
                JXmlWrapper generalRules = row.getXmlNode("general-rules");
                boolean flag = ParseGeneralRulesUtil.parseGeneralRulesNew(generalRules, bean, log);
                if(flag){
                    rowNum ++;

                    discoverContentDTO = new DiscoverContentDTO();
                    discoverContentDTO.setItemId(row.getStringValue("@id"));
                    discoverContentDTO.setTitle(row.getStringValue("@title"));
                    discoverContentDTO.setSubTitle(row.getStringValue("@subtitle"));
                    discoverContentDTO.setContent(row.getStringValue("@content"));
                    discoverContentDTO.setLogoUrl(row.getStringValue("@newlogoUrl"));
                    discoverContentDTO.setLink(row.getStringValue("@newlink"));
                    discoverContentDTO.setEvid(row.getStringValue("@evid"));
                    discoverContentDTO.setFlag(row.getStringValue("@flag"));
                    discoverContentDTO.setTime(row.getStringValue("@time"));

                    discoverContentDTOList.add(discoverContentDTO);
                }
            }
            if(rowNum>0){
                discoverDTO.setDiscoverContent(discoverContentDTOList);
                discoverDTOList.add(discoverDTO);
            }
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("获取数据成功");

        return discoverDTOList;
    }

    /**
     * 预测列表
     * @param bean
     * @return
     * @throws Exception
     */
    @Override
    public Page<List<NewsDTO>> forecast(HomePageBean bean) throws Exception {
        Page<List<NewsDTO>> page = new Page<>();
        List<NewsDTO> newsDTOList = new ArrayList<>();
        NewsDTO newsDTO;
        String resp = null;
        if (bean.getPn() == 0) {
            bean.setPn(1);
        }
        if (StringUtil.isEmpty(bean.getGid())) {
            resp = NewsProvider.getAllForecast(bean.getPn(), bean.getSource());
        }else if("html5yuce".equalsIgnoreCase(bean.getGid())){
            resp = NewsProvider.getHtml5ForeCastsAndNews(bean.getPn());
        }else{
            resp = NewsProvider.getForecastList(bean.getGid(), bean.getPn());
        }

        JXmlWrapper xml = JXmlWrapper.parse(resp);
        if (xml != null) {
            bean.setBusiErrCode(xml.getIntValue("@code"));
            bean.setBusiErrDesc(xml.getStringValue("@desc"));
            if (bean.getBusiErrCode() == 0) {
                List<JXmlWrapper> rows = xml.getXmlNode("rows").getXmlNodeList("row");
                if (StringUtil.isEmpty(bean.getGid()) || "html5yuce".equalsIgnoreCase(bean.getGid())) {
                    for (JXmlWrapper row : rows) {
                        newsDTO = new NewsDTO();
                        newsDTO.setGid(row.getStringValue("@gid"));
                        newsDTO.setTitle(row.getStringValue("@name"));
                        newsDTO.setDescription(row.getStringValue("@title"));

                        newsDTOList.add(newsDTO);
                    }
                } else {
                    for (JXmlWrapper row : rows) {
                        newsDTO = new NewsDTO();
                        newsDTO.setArticleId(row.getStringValue("@aid"));
                        newsDTO.setArticleUrl(row.getStringValue("@arcurl"));
                        newsDTO.setTitle(row.getStringValue("@ntitle"));
                        newsDTO.setDescription(row.getStringValue("@description"));
                        newsDTO.setPraiseNum(row.getIntValue("@zan"));
                        newsDTO.setPublishDate(row.getStringValue("@ndate"));
                        newsDTO.setGid(row.getStringValue("@gid"));
                        newsDTO.setLitpic(row.getStringValue("@litpic"));

                        newsDTOList.add(newsDTO);
                    }
                }
                JXmlWrapper pageList = xml.getXmlNode("pagelist");
                if (pageList == null) {
                    page.setPageNumber(1);
                    page.setTotalPages(1);
                    page.setTotalRecords(1L);
                } else {
                    page.setPageNumber(pageList.getIntValue("@pageno"));
                    page.setTotalPages(pageList.getIntValue("@totalpage"));
                    page.setTotalRecords(pageList.getLongValue("@totalsize"));
                }
                page.setDatas(newsDTOList);
            }
        }
        return page;
    }

    @Override
    public Page<List<NewsDTO>> appHotNews(HomePageBean bean) throws UnsupportedEncodingException {
        Page<List<NewsDTO>> page = new Page<>();
        NewsDTO newsDTO;
        List<NewsDTO> newsDTOList = new ArrayList<>();

        String resp = NewsProvider.getHotNewsList(bean.getPn(), bean.getSource());

        JXmlWrapper xml = JXmlWrapper.parse(resp);
        if (xml == null) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
            return page;
        }

        bean.setBusiErrCode(xml.getIntValue("@code"));
        bean.setBusiErrDesc(xml.getStringValue("@desc"));
        if (bean.getBusiErrCode() != Integer.parseInt(BusiCode.SUCCESS)) {
            return page;
        }

        JXmlWrapper pageList = xml.getXmlNode("pagelist");
        page.setPageNumber(pageList.getIntValue("@pageno"));
        page.setTotalPages(pageList.getIntValue("@totalpage"));
        page.setTotalRecords(pageList.getLongValue("@totalsize"));

        List<JXmlWrapper> rows = xml.getXmlNode("rows").getXmlNodeList("row");
        for (JXmlWrapper row : rows) {
            newsDTO = new NewsDTO();
            newsDTO.setArticleId(row.getStringValue("@aid"));
            newsDTO.setArticleUrl(row.getStringValue("@arcurl"));
            newsDTO.setTitle(row.getStringValue("@ntitle"));
            newsDTO.setDescription(row.getStringValue("@description"));
            newsDTO.setPraiseNum(row.getIntValue("@zan"));
            newsDTO.setPublishDate(row.getStringValue("@ndate"));
            newsDTO.setGid(row.getStringValue("@gid"));
            newsDTO.setLitpic(row.getStringValue("@litpic"));

            newsDTOList.add(newsDTO);
        }
        page.setDatas(newsDTOList);
        return page;
    }
}

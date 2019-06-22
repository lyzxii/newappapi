package com.caiyi.lottery.tradesystem.ordercenter.service.impl;

import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.ordercenter.dao.ComplexMapper;
import com.caiyi.lottery.tradesystem.ordercenter.dao.ZhDetailMapper;
import com.caiyi.lottery.tradesystem.ordercenter.service.ChaseNumberService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.BaseUtil;
import com.caiyi.lottery.tradesystem.util.LotteryLogoUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import order.bean.ChaseNumberPage;
import order.bean.OrderBean;
import order.pojo.ComplexPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 追号相关Service
 *
 * @author GJ
 * @create 2017-12-27 14:05
 **/
@Slf4j
@Service("chaseNumberService")
public class ChaseNumberServiceImpl implements ChaseNumberService{

    @Autowired
    private ComplexMapper complexMapper;
    @Autowired
    private ZhDetailMapper zhDetailMapper;

    DecimalFormat df = new DecimalFormat("###.####");

    private Boolean checkTimeAndCode(OrderBean bean) {
        return BaseUtil.checkTimeOrCode(bean);
    }

    @Override
    public ChaseNumberPage getChaseNumberRecord(OrderBean bean) {
        if (!checkTimeAndCode(bean)){
            return null;
        }
        Boolean isDone=true;
        if (bean.getFlag() == 45) {
            isDone = false;
        }
        ChaseNumberPage page = new ChaseNumberPage();

        Integer doneCount = zhDetailMapper.queryDoneChaseCount(bean.getGid(), bean.getHid());
        page.setDoneChaseCount(doneCount == null ? 0 : doneCount);
        PageHelper.startPage(bean.getPn(), bean.getPs());
        List<ComplexPojo> complexPojoList = complexMapper.queryChaseNumber(bean.getGid(), bean.getHid(), isDone);
        PageInfo<ComplexPojo> pageInfo = new PageInfo<>(complexPojoList);
        if (complexPojoList.size() == 1) {
            if (bean.getFlag() == 44) {
                if ("0".equals(complexPojoList.get(0).getISTATE())) {
                    ComplexPojo temcom = complexPojoList.get(0);
                    try {
                        temcom.setLogo(LotteryLogoUtil.getLotteryLogo(bean.getGid()));
                    } catch (Exception e) {
                        log.error("读取彩种文件出错-flag={},istate={},hid={},uid={}",bean.getFlag(),complexPojoList.get(0).getISTATE(),bean.getHid(),bean.getUid(),e);
                    }
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("查询成功");
                    getNullData(temcom, bean, page);
                }else {
                    getComplexPojoList(page, bean, pageInfo, complexPojoList);
                }
            }
            if(bean.getFlag() == 45){
                if(!"0".equals(complexPojoList.get(0).getISTATE())){
                    ComplexPojo complexPojo = getTitle(bean);
                    getNullData(complexPojo, bean, page);
                    bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("查询成功");
                }else {
                    getComplexPojoList(page, bean, pageInfo, complexPojoList);
                }
            }
        } else if (complexPojoList.size() > 1) {
            getComplexPojoList(page, bean, pageInfo, complexPojoList);
        }else {
            getNullData(new ComplexPojo(), bean, page);
        }
        if (pageInfo.getTotal() == 0) {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
            bean.setBusiErrDesc("暂无数据!");
        } else {
            bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
            bean.setBusiErrDesc("查询成功");
        }
        return page;
    }

    private void addlsmoney(OrderBean bean,ComplexPojo complexPojo) {
        boolean flag=true;
        if ("50".equals(bean.getGid())){
            try {
                String lsPath = FileConstant.LSQC;
                JXmlWrapper lsXml = JXmlWrapper.parse(new File(lsPath));
                Long begin = lsXml.getLongValue("@begin");
                Long qc=Long.valueOf(complexPojo.getCPERIODID());
                Long end = lsXml.getLongValue("@end");
                flag=!(qc>end||qc<begin);
            } catch (Exception e) {
                log.error("追号记录获取大乐透乐善活动期次错误",e);
            }
        }

        try {
            if (flag&&"50".equals(bean.getGid())&&complexPojo.getCCODES().contains(":2:1")) {
                complexPojo.setIsaddreward("1");
                if (!StringUtil.isEmpty(complexPojo.getIlsmoney()) && "2".equals(complexPojo.getIlsaward()) && Double.parseDouble(complexPojo.getIlsmoney()) > 0d) {
                    /*double d1 = StringUtil.isEmpty(complexPojo.getITAX()) ? 0d : Double.parseDouble(complexPojo.getITAX());
                    double d2 = Double.parseDouble(complexPojo.getIlsmoney());
                    complexPojo.setITAX(df.format(d1 + d2));*/
                    complexPojo.setIscontainls("1");
                }
            }
        } catch (Exception e) {
            log.error("追号乐善加奖错误-hid:{}", bean.getHid(), e);
        }

    }

    private void getComplexPojoList(ChaseNumberPage page, OrderBean bean, PageInfo<ComplexPojo> pageInfo, List<ComplexPojo> complexPojoList) {
        ComplexPojo complexPojo = getTitle(bean);
        page.setPageNumber(bean.getPn());
        page.setPageSize(bean.getPs());
        page.setTotalPages(pageInfo.getPages());
        page.setTotalRecords(pageInfo.getTotal());
        page.setHasNextPage(pageInfo.isHasNextPage());
        page.setTitle(complexPojo);
        for (ComplexPojo complexPojo1 : complexPojoList) {
            addlsmoney(bean, complexPojo1);
        }
        page.setDatas(complexPojoList);
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
    }

    private ComplexPojo getTitle(OrderBean bean) {
        ComplexPojo complexPojo = complexMapper.queryChaseNumberTitile(bean.getGid(), bean.getHid());
        if (complexPojo == null) {
            complexPojo = new ComplexPojo();
            bean.setBusiErrCode(Integer.parseInt(BusiCode.USER_QUERYACCOUNT_NODATA));
            bean.setBusiErrDesc("没有该用户的相关记录");
        } else {
            buildTitle(complexPojo, bean.getGid());
            try {
                complexPojo.setLogo(LotteryLogoUtil.getLotteryLogo(bean.getGid()));
            } catch (Exception e) {
                log.error("读取彩种文件出错，hid={},uid={}",bean.getHid(),bean.getUid());
            }
            if (complexPojo.getLogo() == null) {
                complexPojo.setLogo("");
            }
        }
        return complexPojo;
    }

    private void getNullData(ComplexPojo complexPojo,OrderBean bean, ChaseNumberPage page){
        List<ComplexPojo> complexPojoList1 = new ArrayList<>();
        page.setPageNumber(bean.getPn());
        page.setPageSize(bean.getPs());
        page.setTotalRecords(0L);
        page.setTotalPages(0);
        page.setHasNextPage(false);
        page.setTitle(complexPojo);
        page.setDatas(complexPojoList1);
    }

    private void buildTitle( ComplexPojo complexPojo,String gid){
        try {
            if((!StringUtil.isEmpty(complexPojo.getCCODES()))&&complexPojo.getCCODES().contains("txt")){
                File codeFile = new File(FileConstant.BASE_PATH + File.separator + gid + File.separator
                        + "zhuihao"+ File.separator +complexPojo.getCCODES());
                BufferedReader reader = new BufferedReader(new FileReader(codeFile));
                StringBuilder builder = new StringBuilder();
                String content = "";
                while ((content=reader.readLine())!=null) {
                    builder.append(content);
                }
                String result = builder.toString();
                complexPojo.setCCODES(result);
            }
        } catch (Exception e) {
            log.error("解析追号文件投注内容出错,hid={}",complexPojo.getCZHID(),e);
        }


    }
}

package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbMobileDao;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbMobileService {

    @Autowired
    private TbMobileDao tbMobileDao;
    private Logger logger = LoggerFactory.getLogger(TbMobileService.class);

    public SafeBean addMobileVice(String mobileno, String md5mobileno){
        List<SafeBean> result = tbMobileDao.queryMobileByNo(mobileno);
        SafeBean safeBean = null;
        if(result != null && result.size() > 0){ // 手机号已经存在，直接返回
            safeBean = result.get(0);
            if(mobileno.equals(safeBean.getMobileno())){
                logger.info("手机号已经存在 mobileId=" + safeBean.getMobileId());
            } else {
                tbMobileDao.updateMobile(mobileno, safeBean.getMobileId());
                safeBean.setMobileno(mobileno);
                logger.info("手机号已经存在，更新手机号 mobileno=" + mobileno);
            }
        } else {
            int res = tbMobileDao.addMobileByNo(md5mobileno, mobileno);
            if(res > 0) {
                logger.debug("数据插入成功");
                result = tbMobileDao.queryMobileByNo(mobileno);
                safeBean = result.get(0);
                logger.debug("数据插入成功，返回序列号 mobileId=" + safeBean.getMobileId());
                //return Integer.parseInt(mid);
            }
        }
        //return Integer.parseInt(mid);
        return safeBean;
    }

    public SafeBean getMobileVice(String mid){
        List<SafeBean> result = tbMobileDao.queryMobileByMid(mid);
        if(result != null && result.size() > 0){
            return result.get(0);
        }
        return null;
    }
}

package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbBankCardDao;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbIdCardDao;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbIdCardService {

    @Autowired
    private TbIdCardDao tbIdCardDao;
    private Logger logger = LoggerFactory.getLogger(TbIdCardService.class);

    public SafeBean addIdCard(String idcard, String md5idcard){
        List<SafeBean> result = tbIdCardDao.queryIdCardByNo(idcard);
        SafeBean safeBean = null;
        if(result != null && result.size() > 0){ // 手机号已经存在，直接返回
            //SafeBean bean = result.get(0);
            //mid = bean.getMid();
            safeBean = result.get(0);
            logger.info("手机号已经存在 idCardId=" + safeBean.getIdCardId());
            //return Integer.parseInt(mid);
        } else {
            int res = tbIdCardDao.addIdCardByNo(md5idcard, idcard);
            if(res > 0) {
                logger.debug("数据插入成功");
                result = tbIdCardDao.queryIdCardByNo(idcard);
                safeBean = result.get(0);
                logger.debug("数据插入成功，返回序列号 idCardId=" + safeBean.getIdCardId());
                //return Integer.parseInt(mid);
            }
        }
        //return Integer.parseInt(mid);
        return safeBean;
    }

    public SafeBean getIdCard(String cid){
        List<SafeBean> result = tbIdCardDao.queryIdCardByCid(cid);
        if(result != null && result.size() > 0){
            return result.get(0);
        }
        return null;
    }
}

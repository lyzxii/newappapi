package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbBankCardDao;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbBankCardService {

    @Autowired
    private TbBankCardDao tbBankCardDao;
    private Logger logger = LoggerFactory.getLogger(TbBankCardService.class);

    public SafeBean addBankCard(String cbankcard, String md5bankcard){
        List<SafeBean> result = tbBankCardDao.queryBankCardByNo(cbankcard);
        SafeBean safeBean = null;
        if(result != null && result.size() > 0){ // 手机号已经存在，直接返回
            //SafeBean bean = result.get(0);
            //mid = bean.getMid();
            safeBean = result.get(0);
            logger.info("手机号已经存在 bankcardId=" + safeBean.getBankcardId());
            //return Integer.parseInt(mid);
        } else {
            int res = tbBankCardDao.addBankCardByNo(md5bankcard, cbankcard);
            if(res > 0) {
                logger.debug("数据插入成功");
                result = tbBankCardDao.queryBankCardByNo(cbankcard);
                safeBean = result.get(0);
                logger.debug("数据插入成功，返回序列号 bankcardId=" + safeBean.getBankcardId());
                //return Integer.parseInt(mid);
            }
        }
        //return Integer.parseInt(mid);
        return safeBean;
    }

    public SafeBean getBankCard(String bid){
        List<SafeBean> result = tbBankCardDao.queryBankCardByBid(bid);
        if(result != null && result.size() > 0){
            return result.get(0);
        }
        return null;
    }
}

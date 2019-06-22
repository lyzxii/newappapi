package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbUserPayLimitViceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbUserPayLimitViceService {

    @Autowired
    private TbUserPayLimitViceDao tbUserPayLimitViceDao;
    private Logger logger = LoggerFactory.getLogger(TbUserPayLimitViceService.class);

    public int addUserPayLimitVice(String cbankcard, String usersource, String cid){
        int result = tbUserPayLimitViceDao.getUserPayLimitCountByCid(cid, usersource);
        if(result > 0){ // 原来数据库中有数据
            result = tbUserPayLimitViceDao.updateUserPayLimitVice(cbankcard , usersource, cid);
            if (result > 0 ){
                logger.info("数据更新成功 cid=" + cid + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            } else {
                logger.debug("数据更新失败 cid=" + cid + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            }
        } else if(result <= 0){ // 添加数据
            result = tbUserPayLimitViceDao.addUserPayLimitVice(cbankcard , usersource, cid );
            if (result > 0 ){
                logger.info("数据插入成功 cid=" + cid + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            } else {
                logger.debug("数据插入失败 cid=" + cid + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            }
        } else {    // 数据有问题，日志记录错误
            logger.debug("数据库中 cid=" + cid + " 数据有多条");
        }
        return result;
    }

    public List<SafeBean> queryUserPayLimitViceByCid(String cid, String usersource){
        List<SafeBean> list = tbUserPayLimitViceDao.queryUserPayLimitViceByCid(cid , usersource);
        return list;
    }
}

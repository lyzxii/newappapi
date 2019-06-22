package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbUserCashViceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbUserCashViceService {

    @Autowired
    private TbUserCashViceDao tbUserCashViceDao;
    private Logger logger = LoggerFactory.getLogger(TbUserCashViceService.class);

    public int addUserCashVice(String crealname, String cbankcard,String usersource, String icashid){
        int result = tbUserCashViceDao.getUserViceCountByIcashid(icashid, usersource);
        if(result > 0){ // 原来数据库中有数据
            result = tbUserCashViceDao.updateUserCashVice(crealname, cbankcard,usersource, icashid);
            if (result > 0 ){
                logger.info("数据更新成功 icashid=" + icashid + " , crealname=" + crealname + " , cbankcard=" + cbankcard + " , usersource=" + usersource);
            } else {
                logger.debug("数据更新失败 icashid=" + icashid + " , crealname=" + crealname + " , cbankcard=" + cbankcard + " , usersource=" + usersource);
            }
        } else if(result <=0){ // 添加数据
            result = tbUserCashViceDao.addUserCashVice(crealname, cbankcard, usersource, icashid );
            if (result > 0 ){
                logger.info("数据插入成功 icashid=" + icashid + " , crealname=" + crealname +  " , cbankcard=" + cbankcard + " , usersource=" + usersource);
            } else {
                logger.debug("数据插入失败 icashid=" + icashid + " , crealname=" + crealname + " , cbankcard=" + cbankcard + " , usersource=" + usersource);
            }
        } else {    // 数据有问题，日志记录错误
            logger.debug("数据库中 icashid=" + icashid + " 数据有多条");
        }
        return result;
    }

    public List<SafeBean> queryUserCashViceByIcashid(String icashid , String usersource ){
        List<SafeBean> list = tbUserCashViceDao.queryUserCashViceByIcashid(icashid, usersource);
        return list;
    }
}

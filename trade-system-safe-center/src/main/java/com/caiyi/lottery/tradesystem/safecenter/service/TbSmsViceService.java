package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbSmsViceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbSmsViceService {

    @Autowired
    private TbSmsViceDao tbSmsViceDao;
    private Logger logger = LoggerFactory.getLogger(TbSmsViceService.class);

    public int addSmsVice(String mobileno, String usersource,String ismsid){
        int result = tbSmsViceDao.getSmsCountByIsmsid(ismsid, usersource);
        if(result > 0){ // 原来数据库中有数据
            result = tbSmsViceDao.updateSmsVice(mobileno, usersource, ismsid);
            if (result > 0 ){
                logger.info("数据更新成功 ismsid=" + ismsid + " , mobileno=" + mobileno + " , usersource=" + usersource );
            } else {
                logger.debug("数据更新失败 ismsid=" + ismsid + " , mobileno=" + mobileno + " , usersource=" + usersource );
            }
        } else if(result <= 0){ // 添加数据
            result = tbSmsViceDao.addSmsVice(mobileno, usersource, ismsid );
            if (result > 0 ){
                logger.info("数据插入成功 ismsid=" + ismsid + " , mobileno=" + mobileno + " , usersource=" + usersource );
            } else {
                logger.debug("数据插入失败 ismsid=" + ismsid + " , mobileno=" + mobileno + " , usersource=" + usersource );
            }
        } else {    // 数据有问题，日志记录错误
            logger.debug("数据库中 ismsid=" + ismsid + " 数据有多条");
        }
        return result;
    }

    public List<SafeBean> querySmsViceByIsmsid(String icashid, String usersource){
        List<SafeBean> list = tbSmsViceDao.querySmsViceByIsmsid(icashid, usersource);
        return list;
    }
}

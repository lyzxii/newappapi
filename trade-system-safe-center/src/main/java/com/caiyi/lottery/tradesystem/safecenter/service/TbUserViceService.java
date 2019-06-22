package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbUserViceDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbUserViceService {

    @Autowired
    private TbUserViceDao tbUserViceDao;
    private Logger logger = LoggerFactory.getLogger(TbUserViceService.class);

    public int addUserVice(String crealname,String cidcard, String cmobileno, String cbankcard, String cardmobile, String usersource, String cnickid){
        int result = tbUserViceDao.getUserViceCountByCnickid(cnickid,usersource);
        if(result > 0){ // 原来数据库中有数据
            result = tbUserViceDao.updateUserVice(crealname, cidcard, cmobileno, cbankcard, cardmobile,usersource, cnickid);
            if (result > 0 ){
                logger.info("数据更新成功 cnickid=" + cnickid + " , crealname=" + crealname + " , cidcard=" + cidcard + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard +" , cardmobile=" + cardmobile + " , usersource=" + usersource);
            } else {
                logger.debug("数据更新失败 cnickid=" + cnickid + " , crealname=" + crealname + " , cidcard=" + cidcard + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard +" , cardmobile=" + cardmobile + " , usersource=" + usersource);
            }
        } else if(result <= 0){ // 添加数据
            cidcard = cidcard == null? "":cidcard;
            cmobileno = cmobileno == null? "":cmobileno;
            cbankcard = cbankcard == null? "":cbankcard;
            crealname = crealname == null? "":crealname;
            cardmobile = cardmobile == null? "":cardmobile;
            result = tbUserViceDao.addUserVice(crealname, cidcard, cmobileno, cbankcard, cardmobile, usersource, cnickid );
            if (result > 0 ){
                logger.info("数据插入成功 cnickid=" + cnickid + " , crealname=" + crealname + " , cidcard=" + cidcard + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard + " , usersource=" + usersource);
            } else {
                logger.debug("数据插入失败 cnickid=" + cnickid + " , crealname=" + crealname + " , cidcard=" + cidcard + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard + " , usersource=" + usersource);
            }
        } else {    // 数据有问题，日志记录错误
            logger.debug("数据库中 cnickid=" + cnickid + " 数据有多条");
        }
        return result;
    }

    public List<SafeBean> queryUserViceByCnickid(String cnickid, String usersource){
        List<SafeBean> list = tbUserViceDao.queryUserViceByCnickid(cnickid, usersource);
        return list;
    }

    public List<SafeBean> queryUserInfoByMobileno(String mobileno, String usersource){
        List<SafeBean> list = tbUserViceDao.queryUserInfoByMobileno(mobileno, usersource);
        return list;
    }

    public List<SafeBean> queryUserInfoByIdcard(String idcard, String usersource){
        List<SafeBean> list = tbUserViceDao.queryUserInfoByIdcard(idcard, usersource);
        return list;
    }

}

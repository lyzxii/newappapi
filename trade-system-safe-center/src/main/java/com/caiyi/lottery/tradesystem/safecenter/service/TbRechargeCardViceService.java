package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbRechargeCardViceDao;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbRechargeCardViceService {

    @Autowired
    private TbRechargeCardViceDao tbRechargeCardViceDao;
    private Logger logger = LoggerFactory.getLogger(TbRechargeCardViceService.class);

    public int addRechargeCardVice(String cmobileno, String cbankcard, String md5bankcard, String usersource,String cnickid){
        int result = tbRechargeCardViceDao.getRechargeCardCountByCnickid(cnickid, usersource,cbankcard);
        cmobileno = cmobileno == null? "":cmobileno;
        if(result > 0){ // 原来数据库中有数据
            result = tbRechargeCardViceDao.updateRechargeCardVice(cmobileno,cbankcard ,usersource,cnickid);
            if (result > 0 ){
                logger.info("数据更新成功 cnickid=" + cnickid + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            } else {
                logger.debug("数据更新失败 cnickid=" + cnickid + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            }
        } else if(result <= 0){ // 添加数据
            result = tbRechargeCardViceDao.addRechargeCardVice(md5bankcard, cmobileno,cbankcard ,usersource,cnickid);
            if (result > 0 ){
                logger.info("数据插入成功 cnickid=" + cnickid + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            } else {
                logger.debug("数据插入失败 cnickid=" + cnickid + " , cmobileno=" + cmobileno + " , cbankcard=" + cbankcard + " , usersource=" + usersource );
            }
        } else {    // 数据有问题，日志记录错误
            logger.debug("数据库中 cnickid=" + cnickid + " 数据有多条");
        }
        return result;
    }

    public List<SafeBean> queryRechargeCardViceByPid(String pid, String cnickid, String usersource){
        List<SafeBean> list = tbRechargeCardViceDao.queryRechargeCardViceByPid(pid, cnickid, usersource);
        return list;
    }

    public List<SafeBean> queryRechargeCardViceByNickid(String nickid, String usersource){
        List<SafeBean> list = tbRechargeCardViceDao.queryRechargeCardViceByNickid(nickid, usersource);
        return list;
    }

    public List<SafeBean> queryRechargeCardInfo(String nickid, String usersource,String bankcard){
        List<SafeBean> list = tbRechargeCardViceDao.queryRechargeCardInfo(nickid, usersource, bankcard);
        return list;
    }

    public List<SafeBean> queryRechargeByRechargeId(String nickid, String usersource, List<String> conditionList){
        String rechargeIds = "";
        for(String str : conditionList){
            rechargeIds = rechargeIds + "'"+str +"',";
        }
        rechargeIds = rechargeIds.substring(0, rechargeIds.length()-1);
        List<SafeBean> list = tbRechargeCardViceDao.queryRechargeByRechargeId(nickid,usersource,rechargeIds);
        return list;
    }

    public String getRechargeCardSequence(String cnickid, String usersource, String cbankcard){
        List<SafeBean> list= tbRechargeCardViceDao.getRechargeCardSequence(cnickid, usersource,cbankcard);
        String result = "" ;
        if(list != null && list.size() > 0){
            result = list.get(0).getRechargeCardId();
        }
        return result;
    }
}

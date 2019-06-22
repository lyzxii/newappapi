package com.caiyi.lottery.tradesystem.safecenter.service;

import bean.SafeBean;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbIdCardDao;
import com.caiyi.lottery.tradesystem.safecenter.dao.TbRealNameDao;
import com.caiyi.lottery.tradesystem.util.MD5Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TbRealNameService {

    @Autowired
    private TbRealNameDao tbRealNameDao;
    private Logger logger = LoggerFactory.getLogger(TbRealNameService.class);

    public SafeBean addRealName(String realname, String md5realname){
        List<SafeBean> result = tbRealNameDao.queryRealNameByNo(realname);
        SafeBean safeBean = null;
        if(result != null && result.size() > 0){ // 手机号已经存在，直接返回
            //SafeBean bean = result.get(0);
            //mid = bean.getMid();
            safeBean = result.get(0);
            logger.info("手机号已经存在 realnameId=" + safeBean.getRealnameId());
            //return Integer.parseInt(mid);
        } else {
            int res = tbRealNameDao.addRealNameByNo(md5realname, realname);
            if(res > 0) {
                logger.debug("数据插入成功");
                result = tbRealNameDao.queryRealNameByNo(realname);
                safeBean = result.get(0);
                logger.debug("数据插入成功，返回序列号 realnameId=" + safeBean.getRealnameId());
                //return Integer.parseInt(mid);
            }
        }
        //return Integer.parseInt(mid);
        return safeBean;
    }

    public SafeBean getRealName(String cid){
        List<SafeBean> result = tbRealNameDao.queryRealNameByRid(cid);
        if(result != null && result.size() > 0){
            return result.get(0);
        }
        return null;
    }
}

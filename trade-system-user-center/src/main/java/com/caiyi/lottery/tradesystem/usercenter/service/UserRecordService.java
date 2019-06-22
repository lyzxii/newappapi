package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.Response;

/**
 * 用户相关记录Service
 *
 * @author GJ
 * @create 2017-12-01 15:06
 **/
public interface UserRecordService {

    int productOperationInfo(UserBean bean);

    /**
     * 添加用户操作日志
     * @param bean
     * @param type
     * @param memo
     */
    void addUserOperLog(BaseBean bean, String type, String memo);

     /**
       * @Author: tiankun
       * @Description: 用户检测网络统计错误信息
       * @Date: 20:43 2017/12/6
       */
    Response calcUserpingNeterror(UserBean bean, Response resp) throws Exception;

    /**
     * @Author: tiankun
     * @Description: 统计网络错误信息
     * @Date: 10:05 2017/12/7
     */
    Response calculateNeterror(UserBean bean,Response resp) throws Exception;

    /**
     * 统计APP崩溃信息
     * @return
     */
    int calculateBreakdownError(JSONObject errorData);

    /**
     * 查询用户是否存在
     * @return
     */
    String checkUserExist(String cnickid);

    /**
     * 保存用户反馈记录
     * @param bean
     * @return
     */
    int addProductFeedBack(UserBean bean);
}

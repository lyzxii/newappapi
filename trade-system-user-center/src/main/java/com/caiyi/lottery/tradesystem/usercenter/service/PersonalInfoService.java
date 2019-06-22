package com.caiyi.lottery.tradesystem.usercenter.service;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import dto.IdcardBindingDTO;
import response.UserPersonalInfoResq;


/**
 * 用户和账户相关信息
 *
 * @author GJ
 * @create 2017-12-04 11:25
 **/
public interface PersonalInfoService {

    /**
     * 个人中心数据
     * @param bean
     * @return
     */
    UserPersonalInfoResq personalCenterInfo(UserBean bean);

    /**
     * 获取是否可以修改
     * @param bean
     * @return
     */
    String getFlagValue(UserBean bean);


    /**
     * 获取用户白名单
     * @param bean
     * @return
     */
    UserPersonalInfoResq getUserWhitelistGrade(UserBean bean);

    /**
     * 提交银行卡号修改申请前，查看是否有提交资格
     * @param bean
     * @return
     * @throws Exception
     */
    boolean checkBeforeSubmit(BaseBean bean) throws Exception;


    void check_level(BaseBean bean);

    /**
     * 得宝查询是否绑定身份证
     */
    IdcardBindingDTO queryUserInfoBind(BaseBean bean);
}

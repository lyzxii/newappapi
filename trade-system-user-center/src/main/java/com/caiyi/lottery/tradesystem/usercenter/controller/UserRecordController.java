package com.caiyi.lottery.tradesystem.usercenter.controller;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.usercenter.service.TokenManageService;
import com.caiyi.lottery.tradesystem.usercenter.service.UserRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by XQH on 2017/12/6.
 */
@RestController
public class UserRecordController {

    @Autowired
    private UserRecordService userRecordService;
    @Autowired
    private TokenManageService manageService;

    //------------------------------------------------------------------接口--------------------------XQH-start
    /**
     * 产品操作记录
     */
    @RequestMapping("/user/product_opertion_info.api")
    public BaseResp<UserBean> productOperationInfo(@RequestBody BaseReq<UserBean> baseReq){
        UserBean bean = baseReq.getData();
        int flag = userRecordService.productOperationInfo(bean);
        BaseResp<UserBean> baseResp = new BaseResp<UserBean>();
        baseResp.setCode(flag+"");
        baseResp.setDesc(bean.getBusiErrDesc());
        return baseResp;
    }

    /**
     * 产品反馈
     */
    @RequestMapping("/user/product_feedback_info.api")
    public BaseResp<UserBean> check_login_feedback_multipart(@RequestBody BaseReq<UserBean> baseReq){
        UserBean bean = baseReq.getData();
        int flag = userRecordService.addProductFeedBack(bean);
        BaseResp<UserBean> baseResp = new BaseResp<UserBean>();
        baseResp.setCode(flag+"");
        baseResp.setDesc(bean.getBusiErrDesc());
        return baseResp;
    }

    @RequestMapping("/user/query_user_token.api")
    public void queryUserToken(@RequestBody BaseReq<BaseBean> baseReq){
        BaseBean bean = baseReq.getData();
        manageService.queryUserToken(bean);
    }
    //------------------------------------------------------------------接口--------------------------XQH-end
}

package com.caiyi.lottery.tradesystem.dataweb.service;

import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import data.bean.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wxy
 * @create 2018-01-17 15:39
 **/
@Service
public class DataService {
    @Autowired
    private UserBaseInterface userBaseInterface;

    public String checkLogin(DataBean bean) {
        BaseBean baseBean = new BaseBean();
        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, baseBean);
        BaseReq<BaseBean> baseReq = new BaseReq<>(baseBean, SysCodeConstant.DATAWEB);
        BaseResp<BaseBean> baseResp = userBaseInterface.checkLogin(baseReq);

        if (!BusiCode.SUCCESS.equals(baseResp.getCode())) {
            JSONObject json = new JSONObject();
            json.put("code", baseResp.getCode());
            json.put("desc", baseResp.getDesc());
            return json.toString();
        }
        BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp.getData(), bean);

        return null;
    }

    public void setUserData(DataBean bean) {
        BaseBean baseBean = new BaseBean();
        BeanUtilWrapper.copyPropertiesIgnoreNull(bean, baseBean);
        BaseReq<BaseBean> baseReq = new BaseReq<>(baseBean, SysCodeConstant.DATAWEB);
        BaseResp<BaseBean> baseResp = userBaseInterface.setUserData(baseReq);

        BeanUtilWrapper.copyPropertiesIgnoreNull(baseResp.getData(), bean);
    }
}

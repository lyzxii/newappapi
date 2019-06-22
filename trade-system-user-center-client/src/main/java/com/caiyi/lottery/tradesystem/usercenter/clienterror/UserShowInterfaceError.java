package com.caiyi.lottery.tradesystem.usercenter.clienterror;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserShowInterface;
import dto.MyLotteryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserShowInterfaceError implements UserShowInterface {
    @Override
    public BaseResp<MyLotteryDTO> mlottery(BaseReq<UserBean> baseReq) {
        BaseResp resp = new BaseResp();
        resp.setCode(ErrorCode.USER_REMOTE_INVOKE_ERROR);
        resp.setDesc("用户中心调用失败");
        log.info("用户中心mlottery调用失败,请求req:" + baseReq.toJson());
        return resp;
    }
}

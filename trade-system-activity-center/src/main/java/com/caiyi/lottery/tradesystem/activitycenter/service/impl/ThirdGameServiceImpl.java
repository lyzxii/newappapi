package com.caiyi.lottery.tradesystem.activitycenter.service.impl;

import activity.bean.ActivityBean;
import activity.pojo.ThirdGameInfoPojo;
import com.caiyi.lottery.tradesystem.activitycenter.dao.ThirdGameInfoMapper;
import com.caiyi.lottery.tradesystem.activitycenter.dao.ThirdGameMapper;
import com.caiyi.lottery.tradesystem.activitycenter.service.ThirdGameService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 第三方游戏
 * @author wxy
 * @create 2018-01-03 10:29
 **/
@Slf4j
@Service
public class ThirdGameServiceImpl implements ThirdGameService{
    @Autowired
    private ThirdGameMapper thirdGameMapper;
    @Autowired
    private ThirdGameInfoMapper thirdGameInfoMapper;
    /**
     * 记录第三方游戏登陆
     * @param bean
     * @throws Exception
     */
    @Override
    public void gameRecordLogin(ActivityBean bean) throws Exception {
        if(StringUtil.isEmpty(bean.getUid()) && StringUtil.isEmpty(bean.getGameId())){
            log.error("记录第三方游戏登陆,参数错误!");
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_THIRD_GAME_PARAM_ERROR));
            bean.setBusiErrDesc("参数异常！");
            return;
        }
        // 查询是当前游戏是否登陆过
        int count = thirdGameMapper.countByUserAndGame(bean.getUid(), bean.getGameId());
        if (count > 0) {
            // 更新最新登录时间
            count = thirdGameMapper.updateLoginTime(bean.getUid(), bean.getGameId());
        } else {
            // 查询第三方游戏的详细信息
            ThirdGameInfoPojo gameInfo = thirdGameInfoMapper.queryGameInfoByGameId(bean.getGameId());
            if (gameInfo == null) {
                bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_THIRD_GAME_GET_INFO_ERROR));
                bean.setBusiErrDesc("获取游戏相关数据失败");
                return;
            }
            // 插入登录第三方游戏的信息
            count = thirdGameMapper.insert(bean.getUid(), gameInfo.getGameId(), gameInfo.getGameName(), gameInfo.getSupplier());
        }

        if (count <= 0) {
            log.info("记录游戏登陆数据失败");
            bean.setBusiErrCode(Integer.parseInt(ErrorCode.ACTIVITY_THIRD_GAME_RECORD_FAIL));
            bean.setBusiErrDesc("记录游戏登陆数据失败");
            return;
        }
        bean.setBusiErrCode(Integer.parseInt(BusiCode.SUCCESS));
        bean.setBusiErrDesc("记录游戏登陆数据成功");
    }
}

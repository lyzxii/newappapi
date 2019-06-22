package com.caiyi.lottery.tradesystem.datacenter.service;

import data.bean.DataBean;
import data.dto.FollowMatchInfoDTO;

/**
 * @author GJ
 * @create 2018-01-18 10:36
 **/
public interface MatchFollowSerivce {

    FollowMatchInfoDTO getMatchFollw(DataBean bean);
}

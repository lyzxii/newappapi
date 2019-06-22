package com.caiyi.lottery.tradesystem.paycenter.service;

import pay.bean.PayBean;
import pay.dto.RechRouteDto;

/**
 * 充值路由
 * @author A-0205
 *
 */
public interface RechRouteService {
	//充值路由
	RechRouteDto distributeRoute(PayBean bean);
	//单卡路由
	RechRouteDto singleCardRoute(PayBean bean);
}

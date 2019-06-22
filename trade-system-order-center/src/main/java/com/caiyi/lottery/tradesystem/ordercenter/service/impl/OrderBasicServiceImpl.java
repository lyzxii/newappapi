package com.caiyi.lottery.tradesystem.ordercenter.service.impl;

import com.caiyi.lottery.tradesystem.ordercenter.dao.ProjMapper;
import com.caiyi.lottery.tradesystem.ordercenter.service.OrderBasicService;
import lombok.extern.slf4j.Slf4j;
import order.bean.OrderBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wxy
 * @create 2018-01-18 13:58
 **/
@Slf4j
@Service
public class OrderBasicServiceImpl implements OrderBasicService {
    @Autowired
    private ProjMapper projMapper;
    @Override
    public Integer queryBetNum(OrderBean bean) {
        return projMapper.queryBetNum(bean.getGid(), bean.getPid());
    }
}

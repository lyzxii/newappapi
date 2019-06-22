package com.caiyi.lottery.trade.system.config.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GJ
 * @create 2018-01-08 16:39
 **/
@RestController
public class Controller {

    @RequestMapping(value = "/config/checklocalhealth.api")
    public String checkLocalHealth() {
        String result = "{\"code\":" + "\"0+\"," + "\"desc\":\"配置中心config启动运行正常\"}";
        return result;
    }
}

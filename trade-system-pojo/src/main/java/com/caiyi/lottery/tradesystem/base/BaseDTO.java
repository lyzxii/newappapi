package com.caiyi.lottery.tradesystem.base;

import com.alibaba.fastjson.JSON;

public class BaseDTO{
	
	public String toJsonString(){
		return JSON.toJSONString(this);
	}
}

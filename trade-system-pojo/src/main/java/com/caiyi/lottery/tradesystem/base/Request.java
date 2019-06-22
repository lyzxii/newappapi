package com.caiyi.lottery.tradesystem.base;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class Request implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6101162736131681479L;
	private String sysCode;
	
	public String getSysCode() {
		return sysCode;
	}

	public void setSysCode(String sysCode) {
		this.sysCode = sysCode;
	}

	public String toJson(){
		return JSON.toJSONString(this);
	}
	
}

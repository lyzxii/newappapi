package com.caiyi.lottery.tradesystem.base;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class Response implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8134847624828668710L;
	
	private String code;
	private String desc;
	
    public Response() {
	}
	
    public Response(String code, String desc) {
       this.code = code;
       this.desc = desc;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String toJson(){
		return JSON.toJSONString(this);
	}
	
}

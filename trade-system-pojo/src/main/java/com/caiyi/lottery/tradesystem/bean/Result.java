package com.caiyi.lottery.tradesystem.bean;

import com.alibaba.fastjson.JSON;
import com.caiyi.lottery.tradesystem.base.Response;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;


/**
 * 统一返回结果类
 *
 * @author GJ
 * @create 2017-11-24 14:01
 **/
public class Result<T> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4291607339837785034L;
	public static final String SUCCESS = BusiCode.SUCCESS;
    public static final String FAIL = BusiCode.FAIL;
    
    private String code;
    private String desc;
    private T data;
    
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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Result() {
    }

    public Result(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Result(String code, String desc, T data) {
    	this.code = code;
    	this.desc = desc;
    	this.data = data;
    }

    public static Result success(String desc, Object data) {
        return new Result<>(SUCCESS, desc, data);
    }

    public static Result success(String desc) {
        return new Result(SUCCESS, desc);
    }

    public static Result fail(String code, String dsec) {
        return new Result(code, dsec);
    }

    public static Result fail(String dsec) {
        return new Result(FAIL, dsec);
    }

    public String toJson(){
        return JSON.toJSONString(this);
    }
}

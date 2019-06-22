package com.caiyi.lottery.tradesystem.base;


public class BaseReq<T> extends Request{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6618017316965805831L;
	private T data;

	public BaseReq(){
	}

	public BaseReq(String syscode){
		setSysCode(syscode);
	}
	
	public BaseReq(T respData,String syscode){
		this.data = respData;
		setSysCode(syscode);
	}
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}

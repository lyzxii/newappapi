package com.caiyi.lottery.tradesystem.base;

public class BaseResp<T> extends Response{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3643756853560427020L;
	//调用成功失败标识,0位失败，1为成功
	private Integer retcode=1;
	
	private T data;
	
	public BaseResp(){
		
	}
	
	public BaseResp(T respData){
		this.data = respData;
	}
	
    public BaseResp(String code, String desc) {
        super(code, desc);
    }

    public BaseResp(String code, String desc, T data) {
       super(code,desc);
       this.data = data;
    }

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getRetcode() {
		return retcode;
	}

	public void setRetcode(Integer retcode) {
		this.retcode = retcode;
	}

}

package com.caiyi.lottery.tradesystem.util.push.bean;



public class PushResult {
	
	public final static String SUCCESS = "1";
	public final static String FAIL = "2";
	
	private String result;
	private String taskId;
	private String reason;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}

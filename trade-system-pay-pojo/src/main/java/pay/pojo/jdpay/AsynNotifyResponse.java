package pay.pojo.jdpay;

import com.jd.jr.pay.gate.signature.vo.JdPayBaseResponse;

import java.util.List;

public class AsynNotifyResponse extends JdPayBaseResponse {

	private static final long serialVersionUID = -4212178959586736782L;

	/**
     * 版本号
     */
    private String version;
    /**
     * 商户号
     */
    private String merchant;
    /**
     * 设备号
     */
    private String device;
    /**
     * 交易流水  数字或字母
     */
    private String tradeNum;
    /**
     * 0:消费,1:退款
     */
    private String tradeType;
    
    /**
     * 交易列表
     */
    private List<PayTradeVo> payList;
    
    
    /** ================= 退款相关字段  =================**/
    private String oTradeNum;
    // 金额，单位分
	private Long amount;
	private String currency;
	private String tradeTime;
	private String note;
	private String status;
	 /** ================= 退款相关字段  =================**/
    
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMerchant() {
		return merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getTradeNum() {
		return tradeNum;
	}
	public void setTradeNum(String tradeNum) {
		this.tradeNum = tradeNum;
	}
	public List<PayTradeVo> getPayList() {
		return payList;
	}
	public void setPayList(List<PayTradeVo> payList) {
		this.payList = payList;
	}
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getoTradeNum() {
		return oTradeNum;
	}
	public void setoTradeNum(String oTradeNum) {
		this.oTradeNum = oTradeNum;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTradeTime() {
		return tradeTime;
	}
	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AsynNotifyResponse [version=");
		builder.append(version);
		builder.append(", merchant=");
		builder.append(merchant);
		builder.append(", device=");
		builder.append(device);
		builder.append(", tradeNum=");
		builder.append(tradeNum);
		builder.append(", tradeType=");
		builder.append(tradeType);
		builder.append(", payList=");
		builder.append(payList);
		builder.append(", oTradeNum=");
		builder.append(oTradeNum);
		builder.append(", amount=");
		builder.append(amount);
		builder.append(", currency=");
		builder.append(currency);
		builder.append(", tradeTime=");
		builder.append(tradeTime);
		builder.append(", note=");
		builder.append(note);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
	
	
    
}

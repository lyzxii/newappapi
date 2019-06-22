package pay.pojo.jdpay;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by gaoronghuan on 2016/3/24.
 */
@XStreamAlias("detail")
public class PayTradeDetail {
    /**
     * 持卡人人姓名  掩码显示（隐去第一位）
     */
    private String cardHolderName;
    /**
     * 持卡人手机号  掩码显示（手机号的前三位与后四位）
     */
    private String cardHolderMobile;
    /**
     * 证件类型   ID("0", "身份证"), PASSPORT("1", "护照"), OFFICER("2", "军官证"), SOLDIER("3", "士兵证"), TWHK_PASSPORT("4", "港奥台通行证"), TEMP_ID("5", "临时身份证"), HOUSEHOLDREGISTER("6", "户口本"), OTHER("7", "其它类型证件")
     */
    private String cardHolderType;
    /**
     * 身份证号
     */
    private String cardHolderId;
    /**
     * 卡号  掩码显示（前六位及后四位）
     */
    private String cardNo;
    /**
     * 银行编码
     */
    private String bankCode;
    /**
     * 银行卡类型   DEBIT_CARD：借记卡CREDIT_CARD：信用卡SEMI_CREDIT_CARD：准贷记卡
     */
    private String cardType;
    /**
     * 支付金额
     */
    private Long payMoney;

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderMobile() {
        return cardHolderMobile;
    }

    public void setCardHolderMobile(String cardHolderMobile) {
        this.cardHolderMobile = cardHolderMobile;
    }

    public String getCardHolderType() {
        return cardHolderType;
    }

    public void setCardHolderType(String cardHolderType) {
        this.cardHolderType = cardHolderType;
    }

    public String getCardHolderId() {
        return cardHolderId;
    }

    public void setCardHolderId(String cardHolderId) {
        this.cardHolderId = cardHolderId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Long getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Long payMoney) {
        this.payMoney = payMoney;
    }

	/**
     * @Title:        toString
	 * @Description:  TODO(这里用一句话描述这个方法的作用) 
	 * @param:        @return    
	 * @throws 
	 * @author       mythling
	 * @Date          2016年4月28日 下午2:14:36 
	 */
	@Override
	public String toString() {
		return "PayTradeDetail [cardHolderName=" + cardHolderName + ", cardHolderMobile=" + cardHolderMobile
				+ ", cardHolderType=" + cardHolderType + ", cardHolderId=" + cardHolderId + ", cardNo=" + cardNo + ", bankCode="
				+ bankCode + ", cardType=" + cardType + ", payMoney=" + payMoney + "]";
	}
    
    
}

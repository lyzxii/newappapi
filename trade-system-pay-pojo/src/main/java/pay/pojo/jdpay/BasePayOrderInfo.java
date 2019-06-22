package pay.pojo.jdpay;

import lombok.Data;

/**
 * 商户加密用的基本类
 * 
 */
@Data
public class BasePayOrderInfo {
    private String version;
	private String sign;
	private String merchant;
	private String device;
	private String tradeNum;
	private String tradeName;
	private String tradeDesc;
	private String tradeTime;
	private String amount;
	private String currency;
	private String note;
	private String callbackUrl;
	private String notifyUrl;
	private String ip;
	private String specCardNo;
	private String specId;
	private String specName;
	private String userType;
	private String userId;
	private String expireTime;
	private String orderType;
	private String industryCategoryCode;
	
	private String vendorId;
	
	private String goodsInfo;
	
	private String orderGoodsNum;
	
	private String receiverInfo;

	private String termInfo;
	private String cert;
	
	private String tradeType;

	
	
}
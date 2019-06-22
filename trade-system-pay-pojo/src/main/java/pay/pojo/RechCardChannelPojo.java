package pay.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class RechCardChannelPojo { 
	private String uid;     //用户名
	private String bankCode;//银行卡编码
	private String bankName;//银行名称
	private int cardtype;   //银行卡类型
	private String cardName;//银行卡类型名称
	private String cardNo;  //银行卡号
	private String lastFourCardNum;//银行卡后四位
	private String merchantId; //商户号
	private String userbusiid; //用户业务协议
	private String userpayid;//用户支付协议
	private Date adddate;//添加时间
	private String safeKey;//充值卡对应的安全中心key
	private String channel;//路由充值的渠道
}

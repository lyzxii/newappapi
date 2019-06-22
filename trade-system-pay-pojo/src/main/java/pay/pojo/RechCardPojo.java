package pay.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class RechCardPojo { 
	private String uid;     //用户名
	private String bankCode;//银行卡编码
	private String bankName;//银行名称
	private int cardtype;   //银行卡类型
	private String cardName;//银行卡类型名称
	private String cardNo;  //银行卡号
	private String lastFourCardNum;//银行卡后四位
	private Date adddate;//添加时间
	private String safeKey;//充值卡对应的安全中心key
	private int status;//银行卡显示状态
	private String authFlag;//鉴权标识
	private String mobileNo;//手机
	private String md5Mobile;//手机Md5
}

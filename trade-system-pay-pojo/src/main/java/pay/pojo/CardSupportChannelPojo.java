package pay.pojo;


import lombok.Data;

@Data
public class CardSupportChannelPojo { 
	private String bankCode;//银行卡编码
	private String cardtype;   //银行卡类型
	private String channel;  //渠道
	private String product; //产品
	private String key; //唯一键
	private String minlimit;//单笔最低限制
	private String maxlimit;//单笔最高限制
	private String daylimit;//单日金额限制
	private String openflag; //开关状态
	private String bindIdCard; //是否帮身份证标识
	private int order;//优先级顺序
}

package pay.pojo;


import lombok.Data;

@Data
public class RouteCardPojo { 
	private String bankCode;//银行卡编码
	private String bankName;//银行名称
	private int cardtype;   //银行卡类型
	private String banStatus; //禁止状态
	private String banContent;//禁止原因
	private String visible; //显示状态
	private String openflag; //开关状态
}

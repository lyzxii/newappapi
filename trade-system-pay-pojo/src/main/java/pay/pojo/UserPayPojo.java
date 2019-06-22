package pay.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class UserPayPojo { 
	private String payid;     //支付编号
	private String uid;       //用户名
	private String applyid;   //订单号
	private double addmoney;  //充值金额
	private String applyinfo;//订单信息
	private Date dApplydate;//订单时间
	private String state;    //状态
	private String bankid; //支付网关编号
	private String applyrate; //手续费
	private String merchantId;//支付商户号
	private Date confdate;//确认时间
	private String confinfo;//确认信息
	private String applyErrCode;//错误号
	private String applyErrDesc;//错误描述
	private String applyMemo;//备注
	private Integer isSuccess;//是否成功 0-未知 1-成功 2-失败
	private double minConsume;//最低消费金额
	private String cardNo;//银行卡号
	private String safeKey;//充值卡对应的安全中心key
	private String confirmid;//第三方返回订单号
}

package pay.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayOrder implements Serializable {

	private static final long serialVersionUID = 1L;
    // 基本参数
	private String oid_partner; // 商户编号是商户在连连钱包支付平台上开设的商户号码，为18位数字，如：201304121000001004
	private String sign_type; // 参与签名
	private String sign; // 参与签名
	// 业务参数
	private String busi_partner; // 虚拟商品销售：101001,实物商品销售：109001
	private String no_order; // 商户系统唯一订单号
	private String dt_order; // 格式：YYYYMMDDH24MISS 14位数字，精确到秒
	private String name_goods;
	private String info_order; // 订单描述
	private String money_order; // 该笔订单的资金总额，单位为RMB-元。大于0的数字，精确到小数点后两位。如：49.65
	private String notify_url; // 连连钱包支付平台在用户支付成功后通知商户服务端的地址
	private String pay_type; // 支付方式(2:借记卡,3:信用卡)
	private String bank_code; // 银行编号
	private String force_bank; // 是否强制使用该银行的银行卡标志(0为不强制，1为强制)
	private String valid_order;
	private String risk_item; // 风控字段
	// 以下字段不参与签名
	private String user_id; // 用户id
	private String no_agree; // 绑定卡协议号
	private String card_no; // 银行卡号
	private String id_type; // 非必须，默认为0 0:身份证
	private String id_no; // 身份证
	private String acct_name;
	private String flag_modify; // 0-可以修改，默认为0 1-不允许修改
								// 与id_type,id_no,acct_name配合使用，如果该用户在商户系统已经实名认证过了，则在绑定银行卡的输入信息不能修改，否则可以修改
	}

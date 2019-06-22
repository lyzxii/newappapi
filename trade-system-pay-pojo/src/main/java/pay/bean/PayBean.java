package pay.bean;

import com.caiyi.lottery.tradesystem.BaseBean;



public class PayBean extends BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2424664212742646618L;
	private String bankid; // 支付网关编号
	private double addmoney = 0.0; // 充值金额
	private double handmoney = 0; // 手续费用
	private double handrate = 0.0;//手续费费率
	private double minConsumeRate = 0.0;//最低消费费率
	private double minConsume = 0.0;//最低消费金额
	private String applyid = ""; // 订单号 
	private String applydate = "";// 申请时间
	private String realName = "";//真实姓名
	private String cardnum = "";//身份证号
	private String dealid="";//支付商号     6.身份证
	private double tkMoney = 0;//取款金额
	private int tkType=0; //提款方式
	private int cardtype=0; //银行卡类型 0 储蓄卡 1 信用卡
	private String cardName; //银行卡类型名称  借记卡或信用卡
	private String bankCode = "";//银行编号
	private String idcard;//证件号
	private String mobileNo;//手机
	private String bankName;//银行卡名称
	private String cardNo;//银行卡号
	private String lastFourCardNum;//银行卡后四位
	private String userbusiid;//用户业务协议号
	private String userpayid;//支付协议号
	private String validDate; //有效日期
	private String cvv;//信用卡cvv
	private String channel;//路由充值的渠道
	private String product;//渠道对应的产品
	private String key;//唯一标识符
	private String rechargeType;//充值类型   0-银行卡 1-微信   2-支付宝 3-QQ支付 4-其他
	private String userid;//用户唯一串
	private String className;//充值对应的className
	private String merchantId;//商户号
	private String merchantKey;//商户密钥           MD5密钥或RSA私钥
	private String rechargeAppid;//商户所需appid
	private String remark;//备注
	private String safeKey;//充值卡对应的安全中心key
	private String cardPass;//该银行卡是否已绑定
	private String authFlag;//鉴权标识
	private String webcallbackurl;//前端回调地址
	private String desKey;//解密key
	private String rsapublickey;//RSA公钥
	private String rsaprivatekey;
	private String verifycode;
	private String contents;
	private String rechargeCode;//充值第三方返回code
	private String rechargeDesc;//充值第三方返回描述
	private String tradeNo;//交易编号
	private String paramString;//回调参数字符串
	private String clientIp;//客户端ip
	private String sessionToken;
	private String bankType;
	private String pro;//省份
	private String city;//城市
	private String bcode;//银行对应的自定义编码
	private String bankbranch;//银行支行


	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public String getSessionToken() {
		return sessionToken;
	}

	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}

	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getParamString() {
		return paramString;
	}

	public void setParamString(String paramString) {
		this.paramString = paramString;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getRechargeCode() {
		return rechargeCode;
	}

	public void setRechargeCode(String rechargeCode) {
		this.rechargeCode = rechargeCode;
	}

	public String getRechargeDesc() {
		return rechargeDesc;
	}

	public void setRechargeDesc(String rechargeDesc) {
		this.rechargeDesc = rechargeDesc;
	}

	public double getTkMoney() {
		return tkMoney;
	}

	public void setTkMoney(double tkMoney) {
		this.tkMoney = tkMoney;
	}

	public String getCardnum() {
		return cardnum;
	}

	public void setCardnum(String cardnum) {
		this.cardnum = cardnum;
	}

	public String getPro() {
		return pro;
	}

	public void setPro(String pro) {
		this.pro = pro;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBcode() {
		return bcode;
	}

	public void setBcode(String bcode) {
		this.bcode = bcode;
	}

	public String getBankbranch() {
		return bankbranch;
	}

	public void setBankbranch(String bankbranch) {
		this.bankbranch = bankbranch;
	}

	public String getVerifycode() {
		return verifycode;
	}
	public void setVerifycode(String verifycode) {
		this.verifycode = verifycode;
	}
	public String getBankid() {
		return bankid;
	}
	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
	public double getAddmoney() {
		return addmoney;
	}
	public void setAddmoney(double addmoney) {
		this.addmoney = addmoney;
	}
	public double getHandmoney() {
		return handmoney;
	}
	public void setHandmoney(double handmoney) {
		this.handmoney = handmoney;
	}
	public double getHandrate() {
		return handrate;
	}
	public void setHandrate(double handrate) {
		this.handrate = handrate;
	}
	public double getMinConsumeRate() {
		return minConsumeRate;
	}
	public void setMinConsumeRate(double minConsumeRate) {
		this.minConsumeRate = minConsumeRate;
	}
	public double getMinConsume() {
		return minConsume;
	}
	public void setMinConsume(double minConsume) {
		this.minConsume = minConsume;
	}
	public String getApplyid() {
		return applyid;
	}
	public void setApplyid(String applyid) {
		this.applyid = applyid;
	}
	public String getApplydate() {
		return applydate;
	}
	public void setApplydate(String applydate) {
		this.applydate = applydate;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getDealid() {
		return dealid;
	}
	public void setDealid(String dealid) {
		this.dealid = dealid;
	}
	public int getTkType() {
		return tkType;
	}
	public void setTkType(int tkType) {
		this.tkType = tkType;
	}
	public int getCardtype() {
		return cardtype;
	}
	public void setCardtype(int cardtype) {
		this.cardtype = cardtype;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getLastFourCardNum() {
		return lastFourCardNum;
	}
	public void setLastFourCardNum(String lastFourCardNum) {
		this.lastFourCardNum = lastFourCardNum;
	}
	public String getUserbusiid() {
		return userbusiid;
	}
	public void setUserbusiid(String userbusiid) {
		this.userbusiid = userbusiid;
	}
	public String getUserpayid() {
		return userpayid;
	}
	public void setUserpayid(String userpayid) {
		this.userpayid = userpayid;
	}
	public String getValidDate() {
		return validDate;
	}
	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}
	public String getCvv() {
		return cvv;
	}
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getRechargeType() {
		return rechargeType;
	}
	public void setRechargeType(String rechargeType) {
		this.rechargeType = rechargeType;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getMerchantKey() {
		return merchantKey;
	}
	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}
	public String getRechargeAppid() {
		return rechargeAppid;
	}
	public void setRechargeAppid(String rechargeAppid) {
		this.rechargeAppid = rechargeAppid;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSafeKey() {
		return safeKey;
	}
	public void setSafeKey(String safeKey) {
		this.safeKey = safeKey;
	}
	public String getCardPass() {
		return cardPass;
	}
	public void setCardPass(String cardPass) {
		this.cardPass = cardPass;
	}
	public String getAuthFlag() {
		return authFlag;
	}
	public void setAuthFlag(String authFlag) {
		this.authFlag = authFlag;
	}
	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getWebcallbackurl() {
		return webcallbackurl;
	}
	public void setWebcallbackurl(String webcallbackurl) {
		this.webcallbackurl = webcallbackurl;
	}
	public String getDesKey() {
		return desKey;
	}
	public void setDesKey(String desKey) {
		this.desKey = desKey;
	}
	public String getRsapublickey() {
		return rsapublickey;
	}
	public void setRsapublickey(String rsapublickey) {
		this.rsapublickey = rsapublickey;
	}

	public String getRsaprivatekey() {
		return rsaprivatekey;
	}

	public void setRsaprivatekey(String rsaprivatekey) {
		this.rsaprivatekey = rsaprivatekey;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
}

package com.caiyi.lottery.tradesystem;
import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class BaseBean implements Serializable {

	private static final long serialVersionUID = -5175532459996605628L;

	public final static String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private int busiErrCode ; //业务处理错误号
	private String busiErrDesc ="";//业务处理错误描叙
	private String busiXml =""; //业务处理后返回的XML
	private int whitelistGrade;//购彩白名单等级
	protected String uid = "";//用户编号
	private String imei="";// 手机imei码
	private String ipAddr = "";//IP地址
	private String mailAddr;
	private String comeFrom = "";//来源
	private int source;//投注来源
	private String osversion;
	private String coupon = "";
	private int logintype = 0 ;//登录类型 0是普通登录 1是token登录
	private String accesstoken = "";//token令牌字符串
	private String appid = "";//token令牌字密钥
	private String appversion="";//app应用版本
	private int mtype ;//移动设备类型  1 android 2 ios
	private String paramJson = "";//token登录中传递的参数,取代之前存放在session中的参数
    private String pwd = "888888"; //密码
    private String newpwd = "";
    private String signmsg = "";  //签名
    private String signtype = ""; //签名方式
    private String privateKey; //加密密钥
    private String merchantacctid=""; //合作的信任ID
    private String memGetNo;//方案编号
    private String func;//功能号
	private String netWork;//手机信号
	private String cuserId;//用户唯一标识
	protected int hztype = 0;
	protected int usertype = 0;
	private String hasVip;

	private String safeMobileId;//安全中心,手机号存储序列号
	private String safeBankCardId;//安全中心，银行卡存储序列号
	private String safeIdCardId;//安全中心,身份证存储序列号
	private String safeRealNameId;//安全中心，真实姓名存储序列号

	private String md5Mobile;//手机号md5
	private String md5BankCard;//银行卡md5
	private String md5IdCard;//身份证md5
	private String md5RealName;//真实姓名md5

	private int pn = 0;//页码
	private int ps = 25;//页面大小
	private int tp = 0;//总页数
	private int tr = 0;//总记录数

	private String gid = "";//彩种
	private String stime = "";//开始时间
	private String etime = "";//结束时间

	public String getGid() {
		return gid;
	}

	public void setGid(String gid) {
		this.gid = gid;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}


	public String getBusiXml() {
		return busiXml;
	}

	public void setBusiXml(String busiXml) {
		this.busiXml = busiXml;
	}

	public String getNetWork() {
		return netWork;
	}

	public void setNetWork(String netWork) {
		this.netWork = netWork;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

	public int getBusiErrCode() {
		return busiErrCode;
	}
	public void setBusiErrCode(int busiErrCode) {
		this.busiErrCode = busiErrCode;
	}
	public String getBusiErrDesc() {
		return busiErrDesc;
	}
	public void setBusiErrDesc(String busiErrDesc) {
		this.busiErrDesc = busiErrDesc;
	}
	public int getWhitelistGrade() {
		return whitelistGrade;
	}
	public void setWhitelistGrade(int whitelistGrade) {
		this.whitelistGrade = whitelistGrade;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getComeFrom() {
		return comeFrom;
	}
	public void setComeFrom(String comeFrom) {
		this.comeFrom = comeFrom;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public String getOsversion() {
		return osversion;
	}
	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}
	public int getLogintype() {
		return logintype;
	}
	public void setLogintype(int logintype) {
		this.logintype = logintype;
	}
	public String getAccesstoken() {
		return accesstoken;
	}
	public void setAccesstoken(String accesstoken) {
		this.accesstoken = accesstoken;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getAppversion() {
		return appversion;
	}
	public void setAppversion(String appversion) {
		this.appversion = appversion;
	}
	public int getMtype() {
		return mtype;
	}
	public void setMtype(int mtype) {
		this.mtype = mtype;
	}
	public int getPn() {
		return pn;
	}
	public void setPn(int pn) {
		this.pn = pn;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public String getParamJson() {
		return paramJson;
	}
	public void setParamJson(String paramJson) {
		this.paramJson = paramJson;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getSignmsg() {
		return signmsg;
	}
	public void setSignmsg(String signmsg) {
		this.signmsg = signmsg;
	}
	public String getSigntype() {
		return signtype;
	}
	public void setSigntype(String signtype) {
		this.signtype = signtype;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getNewpwd() {
		return newpwd;
	}
	public void setNewpwd(String newpwd) {
		this.newpwd = newpwd;
	}
	public String getMerchantacctid() {
		return merchantacctid;
	}
	public void setMerchantacctid(String merchantacctid) {
		this.merchantacctid = merchantacctid;
	}
	public String getMemGetNo() {
		return memGetNo;
	}
	public void setMemGetNo(String memGetNo) {
		this.memGetNo = memGetNo;
	}
	public String getFunc() {
		return func;
	}
	public void setFunc(String func) {
		this.func = func;
	}
	public String toJsonString(){
		return JSON.toJSONString(this);
	}

	public String getCuserId() {
		return cuserId;
	}

	public void setCuserId(String cuserId) {
		this.cuserId = cuserId;
	}

	public int getHztype() {
		return hztype;
	}

	public void setHztype(int hztype) {
		this.hztype = hztype;
	}

	public int getUsertype() {
		return usertype;
	}

	public void setUsertype(int usertype) {
		this.usertype = usertype;
	}

	public String getMailAddr() {
		return mailAddr;
	}

	public void setMailAddr(String mailAddr) {
		this.mailAddr = mailAddr;
	}

	public String getHasVip() {
		return hasVip;
	}

	public void setHasVip(String hasVip) {
		this.hasVip = hasVip;
	}

	public String getSafeMobileId() {
		return safeMobileId;
	}

	public void setSafeMobileId(String safeMobileId) {
		this.safeMobileId = safeMobileId;
	}

	public String getSafeBankCardId() {
		return safeBankCardId;
	}

	public void setSafeBankCardId(String safeBankCardId) {
		this.safeBankCardId = safeBankCardId;
	}

	public String getSafeIdCardId() {
		return safeIdCardId;
	}

	public void setSafeIdCardId(String safeIdCardId) {
		this.safeIdCardId = safeIdCardId;
	}

	public String getSafeRealNameId() {
		return safeRealNameId;
	}

	public void setSafeRealNameId(String safeRealNameId) {
		this.safeRealNameId = safeRealNameId;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(XML_HEAD);
		sb.append("<Resp code=\"" + busiErrCode + "\" desc=\"" + busiErrDesc + "\">");
		sb.append(busiXml);
		sb.append("</Resp>");
		return sb.toString();
	}

	public String getMd5Mobile() {
		return md5Mobile;
	}

	public void setMd5Mobile(String md5Mobile) {
		this.md5Mobile = md5Mobile;
	}

	public String getMd5BankCard() {
		return md5BankCard;
	}

	public void setMd5BankCard(String md5BankCard) {
		this.md5BankCard = md5BankCard;
	}

	public String getMd5IdCard() {
		return md5IdCard;
	}

	public void setMd5IdCard(String md5IdCard) {
		this.md5IdCard = md5IdCard;
	}

	public String getMd5RealName() {
		return md5RealName;
	}

	public void setMd5RealName(String md5RealName) {
		this.md5RealName = md5RealName;
	}
}

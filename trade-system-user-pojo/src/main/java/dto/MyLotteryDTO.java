package dto;

import java.io.Serializable;

import com.caiyi.lottery.tradesystem.base.BaseDTO;


/**
 * 我的彩票页面传输数据
 * @author A-0205
 *
 */
public class MyLotteryDTO extends BaseDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1920301597246372713L;
  
	private String uid = "";       //用户名
    private Double balance = 0.00;   //用户余额
    private Double redpacket = 0.00; //红包余额
    private Integer userpoint = 0;        //用户积分
    private Integer mobbindFlag = 0;   //手机号绑定标识
    private Integer isvip = 0;         //vip标识
    private String realName = "";   //用户真实姓名
    private String idcard = "";     //用户身份证
    private String mobileNo = "";     //用户手机号
    private String drawBankCard = "";   //用户提款银行卡
    private String whitegrade = ""; //用户白名单等级
    private String userImg = "";    //用户头像
	private String levelTitle = ""; //等级名称
	private String relaUserPhoto = ""; //相对用户头像地址
	private String absolutePhoto = ""; //绝对用户头像地址
	private Integer unawardnum = 0;       //未开奖订单数
	private String mydiscount = "";    //vip用户返利金额
	private Integer agentFlag = 0;    //是否是代理商(VIP)标识
	private Integer authSwitch = 0;    //鉴权开关
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public Double getRedpacket() {
		return redpacket;
	}
	public void setRedpacket(Double redpacket) {
		this.redpacket = redpacket;
	}
	public Integer getUserpoint() {
		return userpoint;
	}
	public void setUserpoint(Integer userpoint) {
		this.userpoint = userpoint;
	}
	public Integer getMobbindFlag() {
		return mobbindFlag;
	}
	public void setMobbindFlag(Integer mobbindFlag) {
		this.mobbindFlag = mobbindFlag;
	}
	public Integer getIsvip() {
		return isvip;
	}
	public void setIsvip(Integer isvip) {
		this.isvip = isvip;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
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
	public String getDrawBankCard() {
		return drawBankCard;
	}
	public void setDrawBankCard(String drawBankCard) {
		this.drawBankCard = drawBankCard;
	}
	public String getWhitegrade() {
		return whitegrade;
	}
	public void setWhitegrade(String whitegrade) {
		this.whitegrade = whitegrade;
	}
	public String getUserImg() {
		return userImg;
	}
	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}
	public String getLevelTitle() {
		return levelTitle;
	}
	public void setLevelTitle(String levelTitle) {
		this.levelTitle = levelTitle;
	}
	public String getRelaUserPhoto() {
		return relaUserPhoto;
	}
	public void setRelaUserPhoto(String relaUserPhoto) {
		this.relaUserPhoto = relaUserPhoto;
	}
	public String getAbsolutePhoto() {
		return absolutePhoto;
	}
	public void setAbsolutePhoto(String absolutePhoto) {
		this.absolutePhoto = absolutePhoto;
	}
	public Integer getUnawardnum() {
		return unawardnum;
	}
	public void setUnawardnum(Integer unawardnum) {
		this.unawardnum = unawardnum;
	}
	public String getMydiscount() {
		return mydiscount;
	}
	public void setMydiscount(String mydiscount) {
		this.mydiscount = mydiscount;
	}
	public Integer getAgentFlag() {
		return agentFlag;
	}
	public void setAgentFlag(Integer agentFlag) {
		this.agentFlag = agentFlag;
	}
	public Integer getAuthSwitch() {
		return authSwitch;
	}
	public void setAuthSwitch(Integer authSwitch) {
		this.authSwitch = authSwitch;
	}
}

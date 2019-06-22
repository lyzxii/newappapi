package pojo;

/**
 * tb_user_acct表对应pojo
 * @author A-0205
 *
 */
public class UserAcctPojo {
	private String uid;        //用户名
	private Double balance;    //用户余额
	private Double redpacket;  //红包余额
	private Integer userpoint; //用户积分
	private Double allDrowMoney; // 可全提现金额
	
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

	public Double getAllDrowMoney() {
		return allDrowMoney;
	}

	public void setAllDrowMoney(Double allDrowMoney) {
		this.allDrowMoney = allDrowMoney;
	}
}

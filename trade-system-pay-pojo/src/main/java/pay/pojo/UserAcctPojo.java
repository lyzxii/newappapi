package pay.pojo;

import lombok.Data;

/**
 * tb_user_acct表对应pojo
 * @author A-0205
 *
 */
@Data
public class UserAcctPojo {
	/**用户编号*/
	private String cnickid;
	/**账户余额*/
	private double ibalance;
	/**冻结金额*/
	private double ifreeze;
	/**消费总额*/
	private double idaigou;
	/**合买金额*/
	private double ihemai;
	/**追号投注额*/
	private double izhuihao;
	/**中奖总额*/
	private double iaward;
	/**合买中奖额*/
	private double ihmaward;
	/**追号中奖额*/
	private double izhaward;
	/**合买中奖提成金额*/
	private double ihmget;
	/**总充值*/
	private double ifill;
	/**提现总金额*/
	private double icash;
	/**发单返点金额*/
	private double ihcommiss;
	/**认购金额*/
	private double ijoin;
	/**跟单返点*/
	private double ijcommiss;
	/**所有发单中奖金额*/
	private double iwtotal;
	/**所有红包金额*/
	private double ihbao;
	/**代理商转入总金额*/
	private double iagent;
	/**用户红包余额*/
	private double ipacketmoney;
	/**不可提现金额*/
	private double nodrawmoney;
	/**可全提金额*/
	private double alldrawmoney;
	/**积分购买金额*/
	private double ipointcash;
	/**积分数额*/
	private double ipoint;
	/**经验值数额*/
	private double iexperience;
}

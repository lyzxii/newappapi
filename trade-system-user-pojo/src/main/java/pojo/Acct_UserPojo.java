package pojo;

import lombok.Data;

//我的彩票pojo
@Data
public class Acct_UserPojo {

    private String uid;       //用户名
    private Double balance;   //用户余额
    private Double redpacket; //红包余额
    private Integer userpoint;//用户积分
    private String agentid;   //代理商id
    private Integer mobbindFlag;   //手机号绑定标识
    private Integer isvip;         //vip标识
    private String realName;   //用户真实姓名
    private String idcard;     //用户身份证
    private String mobileNo;     //用户手机号
    private String drawBankCard;   //用户提款银行卡
    private String whitegrade; //用户白名单等级
    private String userImg;    //用户头像

	private String gradeid;//用户等级
	private String expir;//用户经验

	private String amount;//代购+追号
}

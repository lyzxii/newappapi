package redpacket.bean;

import com.caiyi.lottery.tradesystem.BaseBean;
import lombok.Data;


@Data
public class RedPacketBean extends BaseBean{
    private String cupacketid = "";//用户红包关联ID
    private String cnickid;//用户昵称
    private int crpid;//红包id
    private String imoney;//红包总金额
    private String irmoney;//红包余额
    private String cdeaddate = "";//红包过期时间
    private String dispatchtime = "";//红包生效时间
    private String cdispatchtime = "";//红包生效时间
    private String coperator = "";//红包发放人
    private String cadddate;//发放时间
    private int istate ;//红包状态
    private String cmemo = "";//备注
    private String iaddmoney = "0";//9188赠送金额
    private String crpname;//红包名称
    private String scale;//红包使用比例
    private String cgameid;//限制彩种
    private int itid ;//红包使用方式
    private String ctname;//红包使用方式
    private String itype = "";//红包类型
    private String cagent;//可用代理商 值则所有代理商均可用
    private String isource;//可用渠道
    private String trade_gameid;//投注的彩种ID
    private String trade_imoney;//投注总金额(认购份数)
    private String trade_redPacket_money;//投注使用红包金额
    private String trade_agent;//投注时代理商
    private String trade_isource;//投注时渠道
    private String oldAgentId;
    private String igetType = "1"; // 1 系统赠送    2 手动赠送  3 卡密激活
    private int flag;//查询类型
    private String ccardid = "";//卡号
    private String ccardpwd;//卡密
    private String saleid = ""; //销售id
    private int ipoint; //购买红包所需积分
    private int ibuymoney; //购买红包所需金额
    private String remainnums = ""; //红包剩余份数
    private String state = "";
    private String ky_money="";
    private boolean isPaginal;
    private String icardid;

}

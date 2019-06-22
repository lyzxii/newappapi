package redpacket.pojo;

import lombok.Data;

//tb_redpacket 和 tb_user_redpacket
@Data
public class Rp_UserRpPojo {
    private String redpacketId;     //红包唯一id
    private int rpid;            //红包类型id
    private String imoney;          //红包金额
    private String balance;         //红包余额
    private String deaddate;        //红包过期时间
    private int state;              //红包状态
    private String scale;           //使用比例
    private String gameid;          //适用彩种
    private String availableAgent;  //适用代理商
    private String availableSource; //适用渠道
    private int tid;                //使用类型
}

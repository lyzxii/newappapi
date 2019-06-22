package pay.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by XQH on 2017/12/29.
 */
@Data
public class UmpayProtocalPojo {
    /**用户id*/
    private String cnickid;
    /**支付渠道（umpay--联动优势充值）*/
    private String cpaysource;
    /**手机号*/
    private String cmobile;
    /**支付银行*/
    private String cbankid;
    /**银行名称*/
    private String cbankname;
    /**银行卡号*/
    private String ccardno;
    /**银行卡类型*/
    private String ccardtype;
    /**银行卡名称*/
    private String ccardname;
    /**银行卡卡号后四位*/
    private String clastfourcardid;
    /**商户编号*/
    private String cmerid;
    /**用户业务协议号*/
    private String cuserbusiid;
    /**支付协议号*/
    private String cuserpayid;
    /**添加时间*/
    private Date caddtime;
    /**备注*/
    private String cremark;
    /**是否删除标记*/
    private String cstatus;
    /**是否鉴权（0--未鉴权，1--已鉴权）*/
    private String cauthentication;
}

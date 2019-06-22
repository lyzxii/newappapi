package pay.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by XQH on 2017/12/28.
 */
@Data
public class UserCashPojo {
    /**记录编号*/
    private Integer icashid;
    /**用户编号*/
    private String cnickid;
    /**提现金额*/
    private Double imoney;
    /**手续费*/
    private Double irate;
    /**申请时间*/
    private Date ccashdate;
    /**状态(0 已申请 1 已处理 2 处理中)*/
    private String istate;
    /**确认时间(完成时间)*/
    private Date cconfdate;
    /**确认人*/
    private String coperator;
    /**备注*/
    private String cmemo;
    /**是否成功(0 未处理 1 提款成功 2 提款失败 3银行返款给用户充值 11 银行卡批付中12银行卡批付成功13银行卡批付失败)*/
    private Integer isuccess;
    /**失败原因*/
    private String creason;
    /**用户姓名*/
    private String crealname;
    /**银行代码*/
    private String cbankcode;
    /**银行卡号*/
    private String cbankcard;
    /**银行名称*/
    private String cbankname;
    /**银行所在省份*/
    private String cbankpro;
    /**银行所在市*/
    private String cbankcity;
    /**提款方式 (0 提款到银行 1 提款到支付宝)*/
    private String itype;
    /***/
    private String cagentid;
    /***/
    private String sexplain;
    /**银行退款操作之前的确认时间*/
    private Date chisconfdate;
    /**0 其它 1 支付宝 2 盛付通4连连5现在支付6联动优势7京东*/
    private String iinterf;
    /**处理时间*/
    private Date chandledate;
    /**预计到账时间*/
    private String cpredicttime;
    /***/
    private String iabnormal;
}

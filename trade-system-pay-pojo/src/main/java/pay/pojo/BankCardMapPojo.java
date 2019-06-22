package pay.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 对应表 tb_bank_card_map
 * Created by XQH on 2017/12/20.
 */
@Data
public class BankCardMapPojo {
    /**记录唯一id*/
    private String iid;
    /**所属银行名称*/
    private String cbankname;
    /**支行名称*/
    private String cbranchname;
    /**卡类型id*/
    private String icardtype;
    /**卡类型名称*/
    private String ccardtypename;
    /**卡名称*/
    private String ccardname;
    /**银行编号*/
    private String cbankno;
    /**更新时间*/
    private Date ccreatetime;
    /**备注*/
    private String cremark;
    /**卡号所属地id*/
    private String cbinno;
    /**卡号所属地识别位数*/
    private String ibinlen;
    /**卡号位数*/
    private String icardlen;
    /**银行编码*/
    private String cbankcode;
    /**提款编码*/
    private String cbcode;
    /**是否可以鉴权*/
    private String ccauthenticationflag;
}

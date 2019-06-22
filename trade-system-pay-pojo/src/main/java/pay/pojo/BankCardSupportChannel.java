package pay.pojo;

import lombok.Data;

/**
 * Created by XQH on 2017/12/29.
 */
@Data
public class BankCardSupportChannel {
    /**银行编码*/
    private String cbankcode;
    /**银行卡类型 0-借记卡 1-信用卡*/
    private String ccardtype;
    /**充值通道*/
    private String cchannel;
    /**产品类型*/
    private String cproduct;
    /**自定义唯一值*/
    private String ckey;
    /**单笔最小限额*/
    private String cminlimit;
    /**单笔最大限额*/
    private String cmaxlimit;
    /**单日限额*/
    private String cdaylimit;
    /**开关状态*/
    private String copenflag;
    /**绑定身份证状态*/
    private String cbindidcard;
    /**排序顺序*/
    private String iorder;
}

package pojo;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-14 15:40
 **/
@Data
public class UserBankbindPojo {
    private Integer id;         // 逐渐
    private String nickid;      // 用户别名
    private String realName;    // 真实姓名
    private String idcard;      // 身份证号
    private String bankCode;     // 银行卡代码
    private String bankCard;     // 银行卡号
    private String bankName;     // 银行名
    private String subBankName;  // 分行名
    private String bankProvince;  // 银行所在省
    private String bankCity;      // 银行所在市
    private String cardMobile;// 银行卡绑定手机号;

}

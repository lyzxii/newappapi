package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-14 15:15
 **/
@Data
public class IdBankBindingDTO {
    private Integer mobbindFlag;   // 是否绑定手机号标志
    private String mobileNo = "";      // 手机号
    private String idcard = "";        // 身份证号
    private String realName = "";      //用户真实姓名
    private String bankCard = "";      // 银行卡号
    private String bankName = "";      // 银行卡名
    private String bcode = "";      // 银行代码
    private String bankProvince = "";  // 银行所在省份
    private String bankCity = "";      // 银行所在市
    private String cardMobile = "";    // 银行卡绑定手机号
    private Double allDrowMoney = 0.0;  // 可全体现金额
    private Integer safeIndex = 0;    // 安全指数
    private Integer loginPhone = 0;   // 是否手机登录
    private Integer drawingMoneyNum = 0;// 进行中提款笔数
    private String drawMoneyDesc = "";//  提款状态描述
    private Integer todayDrawNum = 0;       // 今日提款次数
    private Integer drawNum = 0;       // 今日剩余提款次数
    private String linkimg = "";        // 图片链接
    private String bankBranch = "";      // 支行名
}

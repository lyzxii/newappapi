package pojo;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-08 14:31
 **/
@Data
public class UserCashPojo {
    private Integer cashId; // 记录编号
    private String uid; // 用户编号
    private Double money; // 提现金额
    private Double rate; // 手续费
    private String cashTime = ""; // 申请时间
    private Integer state; // 状态(0 已申请 1 已处理 2 处理中)
    private String confTime = ""; // 确认时间(完成时间)
    private String operator; // 确认人
    private String memo = ""; // 备注
    private Integer success; // 是否成功(0 未处理 1 提款成功 2 提款失败 3银行返款给用户充值 11 银行卡批付中12银行卡批付成功13银行卡批付失败)
    private String reason; // 失败原因
    private String realName; // 用户姓名
    private String bankCode; // 银行代码
    private String bankCard; // 银行卡号
    private String bankName; // 银行名称
    private String bankPro; // 银行所在省份
    private String bankCity; // 银行所在市
    private Integer type; // 提款方式 (0 提款到银行 1 提款到支付宝)
    private String agentId; // 代理id
    private String explain; // 说明？
    private String hisConfTime; // 银行退款操作之前的确认时间
    private Integer interf; // 0 其它 1 支付宝 2 盛付通4连连5现在支付6联动优势7京东
    private String operatorTime = ""; // 处理时间
    private String predtictTime = ""; // 预计到账时间
    private Integer abnormal; // 异常？
}

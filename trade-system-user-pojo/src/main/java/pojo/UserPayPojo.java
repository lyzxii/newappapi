package pojo;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-07 18:20
 **/
@Data
public class UserPayPojo {
    private Integer payId; // 记录编号
    private String uid; // 用户昵称
    private String applyID; // 订单号
    private Double money; // 交易金额
    private String applyInfo; // 申请信息
    private String applyTime; // 申请时间
    private Integer state; // 状态（0 已申请 1 已确认 2已加款)
    private String bankId; // 支付网关编号(1快钱支付 99手工加款)
    private Double rate; // 手续费
    private String confirmId; // 支付商号
    private String confirmTime; // 确定时间
    private String confirmInfo; // 确认信息
    private String errorCode; // 错误号
    private String errorDesc; // 错误描述
    private String memo; // 备注
    private Integer success; // 是否成功 （0未知 1成功 2失败)
    private Double miniMoney; //
    private String careNo;  // 银行卡号

}

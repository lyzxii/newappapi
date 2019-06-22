package pojo;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-07 18:05
 **/
@Data
public class UserChargePojo {
    private Integer chargeId; // 编号
    private String uid; // 用户昵称
    private Double money; // 金额
    private Integer type; // 类型 (0 进 1 出)
    private Integer tradeType; // 交易类型
// 10 提现      11 代购消费  12 认购消费   13 追号消费
// 20 充值      21 代购撤单  22 认购撤单   23 追号撤单
// 30 返点      31 代购中奖  32 合买中奖   32 追号中奖 33 发起人提成
// 41 补款
    private Double oldMoney; // 变化前余额
    private Double balance; // 余额
    private String memo; // 备注
    private String tradeTime; // 交易日期

}

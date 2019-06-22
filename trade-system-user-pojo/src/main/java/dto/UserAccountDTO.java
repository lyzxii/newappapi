package dto;


import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-06 19:15
 **/
@Data
public class UserAccountDTO {

    private String id; // 充值的订单号，提款、够彩、派奖、返款的流水号
    private Integer type; // 收支的0   收入   1  支出
    private String tradeTime; // 收支的交易时间
    private Integer tradeType; // 收支的交易类型交易类型
// 10 提现      11 代购消费  12 认购消费   13 追号消费
// 20 充值      21 代购撤单  22 认购撤单   23 追号撤单
// 30 返点      31 代购中奖  32 合买中奖   32 追号中奖 33 发起人提成
// 41 补款

    private Double money;
    private Double oldMoney; // 交易前余额
    private Double balance; // 余额

    private String playKind; // 玩法
    private String playId; // 编号

    private Double rate; // 手续费
    private String memo; // 备注
    private String operationTime; // 处理时间
    private String predtictTime; // 预计时间

    private String state; // 提款状态
    private String tradeTypeName;
    private Integer success; // 是否成功(0 未处理 1 提款成功 2 提款失败 3银行返款给用户充值 11 银行卡批付中12银行卡批付成功13银行卡批付失败)
}

package order.pojo;

import lombok.Data;

/**
 * 购彩+追号公用pojo
 * @author 571
 * @create 2018-3-6 15:22:48
 */
@Data
public class ProjZhPojo {
    private int type;//1 自购 2 追号
    private String projid;//方案编号
    private int money =0 ;//购彩金额
    private Double tax;//税后奖金
    private Double bonus;//总奖金
    private String codes;//投注号码
    private Integer ijiesuan;//结算标志（0 未结算 1 正在结算 2 已结算）
    private Integer istate;//状态(0 禁止认购 1 认购中,2 已满员 3 过期未满撤销 4主动撤销 5 出票失败撤销)
    private Integer iaward;//计奖标志（0 未计奖 1 正在计奖 2 已计奖)
    private Integer ireturn;//是否派奖（0 未派奖 1 正在派 1 已派奖）
    private Integer st;
}

package order.pojo;

import lombok.Data;

/**
 * Created by tiankun on 2018/1/2.
 */
@Data
public class ShareUserDetailPojo {

    private Integer allnum;//发单数
    private Integer rednum;//红单数
    private String shootrate;//命中率（红单数/发单数）
    private Double buymoney;//购买金额
    private Double winmoney;//累计奖金
    private String returnrate;//回报率
    private String cstattype;//统计类型（1-当日，3-3天内，7-一周内，15-15天内，30-一个月内）


}

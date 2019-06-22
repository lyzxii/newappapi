package order.pojo;

import lombok.Data;

/**
 * Created by tiankun on 2018/1/3.
 */
@Data
public class ShareUserStatPojo {

    private String creturnrate;//回报率
    private Double iwinmoney;//累计奖金
    private Integer ifollowusers;//跟买人数
    private Double ifollowmoney;//跟买金额
    private Integer icontinurednum;//连续红单数

}

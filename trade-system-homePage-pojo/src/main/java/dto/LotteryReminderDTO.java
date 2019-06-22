package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-18 14:40
 **/
@Data
public class LotteryReminderDTO {
    private String all; //open="1"表示总开关打开，open="0"表示代码失效
    private String ssq; // 双色球
    private String dlt; // 大乐透
    private String fc3d; // 福彩3d
    private String qxc; // 七星彩
    private String qlc; // 七乐彩
    private String pl3; // 排列3
    private String pl5; // 排列5
}

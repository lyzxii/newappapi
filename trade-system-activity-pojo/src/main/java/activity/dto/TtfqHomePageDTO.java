package activity.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-29 14:18
 **/
@Data
public class TtfqHomePageDTO {
    private String date; // 方案日期
    private String projId; // 方案编号
    private String gameId; // 彩种
    private Double money; // 方案所投彩金
    private String awardtime; // 开奖时间
    private Double bonus; // 方案奖金
    private Double myBonus; // 我的奖金
    private Integer status; // 我的参与状态 1000  暂未参与，可立即参与;2000  未登录;3000  等待分钱;4000  领取奖金;5000  已领取奖金;6000  方案已过期，未参与
    private Integer award; // 记奖标志（0 未记奖，1 正在记奖，2 已记奖）
}

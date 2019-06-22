package activity.pojo;

import lombok.Data;

/**
 * @author GJ
 * @create 2018-04-23 10:18
 **/
@Data
public class ForcastPojo {
    private String homeTeamName;
    private String awayTeamName;
    private Integer homeScore;
    private Integer awayScore;
    private Long matchId;
    private Long itemId;
    private String matchName;
    private Integer matchResult;
    // 参与预测人数
    private Integer forcastNum;
    // 领取预测红包人数
    private Integer forcastAwardNum;
    // 使用预测红包人数
    private Integer useForcastAwardNum;
    // 获得的奖励
    private Integer haveAward;
    // 过期的奖励
    private Integer outOfDateAward;
}

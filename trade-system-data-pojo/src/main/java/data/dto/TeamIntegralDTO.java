package data.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TeamIntegralDTO implements Serializable {
    // 球队名
    private String TeamName;
    // 球队id
    private String teamId;
    // 排名
    private String order;
    // 胜场次数
    private String winNum;
    // 平场次数
    private String drawNum;
    // 负场次数
    private String loseNum;
    // 积分
    private String intrgral;
    // 小组名
    private String groupName;
    // 进球数
    private String goalNum;
    // 失球数
    private String fumbleNum;

    private static final long serialVersionUID = 1L;
}

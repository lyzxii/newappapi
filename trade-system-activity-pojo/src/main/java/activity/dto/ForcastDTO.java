package activity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-04-23 10:44
 **/
@Data
public class ForcastDTO {
    private String totaluser;//累计邀请人
    private String totalaward;//累计奖励
    private String totalonway;//在路上奖励
    private String itemid;
    private Long matchId;
    private String homeTeamName;
    private String awayTeamName;
    private String homeTeamId;
    private String awayTeamid;
    private String homeTeamImgUrl;
    private String awayTteamImgUrl;
    private String endTime;
    private String matchTime;
    private String userName;
    private String userImgUrl;
    // 展示状态（0 未开奖,1 有未领取,2 完全领取或无人参与）
    private Integer state;
    // 主队比分
    private Integer homeScore;
    // 客队比分
    private Integer awayScore;
    // 邀请人数
    private Integer invitationNum;
    // 所得奖励
    private Integer award;
    private Integer ungetAward;
    private Integer outOfDateAward;

    List<ForcastUserDTO> userList;
}

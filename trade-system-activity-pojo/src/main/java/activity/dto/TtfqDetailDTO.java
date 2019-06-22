package activity.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-02 14:25
 **/
@Data
public class TtfqDetailDTO {
    private Integer award; // 计奖标志（0 未计奖 1 正在计奖 2 已计奖)
    private String beginTime; // 发单时间
    private Integer cast; // 出票标志 0 未出票 1 可以出票 2 已拆票 3 已出票
    private String projId; // 方案编号
    private String gameId; // 彩种编号
    private String periodId; // 期次
    private Double money; // 方案金额
    private Integer prizes; // 是否派奖（0 未派奖 1 正在派 1 已派奖）
    private String winInfo; // 中奖注数 各等级中奖注数用逗号隔开，如果有大于0的数字则表示方案中奖
    private Integer mulity; // 倍数
    private Integer joinCounts; // 参与总人数
    private String codes; // 投注号码
    private String awardCode; // 开奖号码
    private String awardTime; // 开奖时间
    private Double bonus; // 方案奖金
}

package activity.pojo;

import lombok.Data;

/**
 * tb_proj_qvod表的实体类
 * @author wxy
 * @create 2017-12-29 14:49
 **/
@Data
public class ProjQvodPojo {
    private String projId; // 方案编号
    private String gameId; // 彩种
    private Integer award; // 0 未记奖 1 正在记奖 2 已记奖
    private String activityDate; // 活动日期
    private String endTime; // 截止日期
    private Double money; // 总金额
    private Double bonus; // 总奖金
    private String periodId; // 期次
    private Integer cast; // 出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）
    private String addDate; // 发起时间
    private Integer prizes; // 是否派奖（0 未派奖 1 正在派 1 已派奖）
    private String winInfo = ""; // 中奖信息，中奖柱数用逗号隔开.
    private Integer mulity; // 倍数
    private String codes; // 开奖号码
}

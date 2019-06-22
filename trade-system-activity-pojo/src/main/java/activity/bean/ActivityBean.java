package activity.bean;

import com.caiyi.lottery.tradesystem.BaseBean;
import lombok.Data;

/**
 * 活动中心bean
 * @author wxy
 * @create 2017-12-22 11:42
 **/
@Data
public class ActivityBean extends BaseBean {
    private String mobiletype;//手机型号
    private String projId; // 方案编号
    private String gameId; // 彩种编号
    private Long matchId; // 比赛id
}

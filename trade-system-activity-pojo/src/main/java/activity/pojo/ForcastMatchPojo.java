package activity.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author GJ
 * @create 2018-04-23 10:18
 **/
@Data
public class ForcastMatchPojo {

    /**
     * 竞彩场次编号
     */
    private String itemid;
    /**
     * 主队名
     */
    private String mname;
    /**
     * 客队名
     */
    private String sname;
    /**
     * 主队进球数
     */
    private String mscore;
    /**
     * 比赛时间
     */
    private String matchtime;

    /**
     * 预测截止时间
     */
    private String endTime;
}

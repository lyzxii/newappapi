package activity.pojo;

import lombok.Data;

/**
 * tb_qvod_ttfq表的实体类
 * @author wxy
 * @create 2017-12-29 14:59
 **/
@Data
public class QvodTtfqPojo {
    private String projId; // 方案编号
    private String gameId; // 彩种
    private Double money; // 奖金
    private Integer status; // 3000 等待分钱,4000可领，5000已领

}

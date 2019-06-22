package activity.pojo;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-28 15:58
 **/
@Data
public class CpenginePojo {
    private String uid; // 用户编号
    private String projId; // 方案编号

    private String busiErrCode; // 错误编号
    private String busiErrDesc; // 错误描叙
    private String balance; // 用户余额
    private String status; // 领取状态
    private String bonus; // 领取金额
    private String totalBonus; // 领取总金额

}

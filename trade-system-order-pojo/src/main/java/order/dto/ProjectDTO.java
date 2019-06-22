package order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import order.pojo.QueryProjAppPojo;

/**
 * @author GJ
 * @create 2018-01-12 17:54
 **/
@Data
public class ProjectDTO extends QueryProjAppPojo{
    /**
     * 是否可见
     */
    private String visible="";
    /**
     * 是否显示codes
     */
    private String showCodes="";

    /**
     * 有*的用户名
     */
    private String shieldNickid;

    /**
     * 分享神单类型
     */
    private String shareGod="";

    /**
     * 打赏比率
     */
    private String minRatio;

    /**
     * 分享人名字
     */
    private String sharedNickid;

    /**
     * 分享人加密串，用途用于跳转参数
     */
    private String hideSharedNickid;

    /**
     * 分享人：获得的打赏金额  跟买人:打赏金额  中奖但未盈利  ireward=0
     */
    private String reward;

    /**
     * 最终获取金额 (中奖金额减去打赏金额)
     * 分享人：中奖金额 加上 打赏金额   跟买人：中奖金额减去 打赏金额
     */
    private String dueMoney;

    /**
     * fc 过滤上传删除下面一行 对应原来的<filter
     */
    private String fc;
}

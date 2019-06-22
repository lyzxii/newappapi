package data.bean;

import com.caiyi.lottery.tradesystem.BaseBean;
import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-16 16:58
 **/
@Data
public class DataBean extends BaseBean {
    private String matchId; // 场次id
    private String commentId; // 评论id
    private String intelligenceId; // 情报id
    private String userId;
    private String content; // 内容
    private String idfa; // ios设备号
    private String commentType;
    private String followUserId;
    private String reportType;
    private String optionSPF;//胜平负预测结果，多个预测用逗号分隔
    private String optionRQSPF; // 让球胜平负预测结果，多个预测用逗号分隔
    private String lotteryType; // 彩种，区分篮球足球球评，0，北单，1竞彩，2篮球
    private String rqNum;// 让球数
    private String optionType; // 操作类型 (初始化 1，分页 2)
    private String cmtUserId; // 分页最后一条评论的用户id

    //关注开始
    private Integer gameType;//足球玩法类型 	70竞彩 85北单 94篮球
    private Integer isAll;//是否是外面关注列表  	isall=1 关注列表，isall=0 对阵中的关注

    private String gameId;//游戏id
    private String period;//期次
    private String sort;
    private String roundItemId;
    private Boolean checkLogin;//是否登录
    private Boolean follow;
    private int ftype;//关注类型
    private String teamId; // 球队id

    private String ids; // 场次id，多个id用逗号分隔
    private String flag; // 北单为1

}

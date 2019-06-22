package order.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by tiankun on 2017/12/27.
 * tb_sharelist表pojo
 */
@Data
public class ShareListPojo {

    private String nickid;//分享人
    private String gameid;//游戏编号
    private String period;//期次
    private String codes;//投注号码
    private Integer mulity;//倍数
    private String tmoney;//总金额
    private String matches;//对阵列表(,隔开)
    private Integer followmoney;//跟买方案数
    private Integer extendtype;//分享神单扩展类型
    private Integer open;//是否保密 （0 对所有人公开 1 截止后公开 2 对参与人员公开 3 截止后对参与人公开 4 最后一场比赛开赛后公开）
    private Date endtime;//截止时间
    private Date adddate;//分享时间
    private Double bonus;//总奖金
    private Integer finish;//方案是否结束即是否派奖完成  0 未结束  1  结束
    private Integer follows;//跟买方案数
    private Integer wrate;//跟买人中奖给分享人提成比率 （盈利情况）
    private Integer usernum;//跟买方案人数
    private Date lastdate;//最后一场比赛开赛时间
    private String projid;//方案编号
    private String guoguan;//过关类型
    private Double mintmoney;//起投金额
    private String followrate;
    private Double yhmoney;//优化后的起投金额

}

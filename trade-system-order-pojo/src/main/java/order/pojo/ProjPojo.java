package order.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class ProjPojo {

    private String cprojid;//方案编号
    private Integer itype;//方案类型（0 自购(代购) 1合买  2分享 3跟买 ）
    private Double itmoney;//总金额
    private Double itax;//税后奖金
    private Double ibonus;//总奖金
    private String ccodes;//投注号码
    private Integer ijiesuan;//结算标志（0 未结算 1 正在结算 2 已结算）
    private Integer istate;//状态(0 禁止认购 1 认购中,2 已满员 3 过期未满撤销 4主动撤销 5 出票失败撤销)
    private Integer iaward;//计奖标志（0 未计奖 1 正在计奖 2 已计奖)
    private Integer ireturn;//是否派奖（0 未派奖 1 正在派 1 已派奖）
    private Integer iagnum;//银星个数(发起方案时的银星数)
    private Integer iaunum;//金星个数(发起方案时的银星数)
    private Date cadddate;//发起时间
    private Integer iopen;//是否保密 （0 对所有人公开 1 截止后公开 2 对参与人员公开 3 截止后对参与人公开）
    private String cnickid;//发起人
    private String userid;//用户序列ID
    private Integer icast;//出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）
    private String cwininfo;//中奖信息（中奖注数用逗号隔开）
    private String cgameid;//游戏编号
    private String cperiodid;//期次
    private Integer imulity;//倍数
    private Date cendtime;//截止时间
    private String imoneyrange;//理论奖金范围
    private String cguoguan;//过关类型
    private String cmatchs;//对阵列表(,隔开)
    private String iminrange;//理论最小奖金
    private Integer isource;//投注来源
    private Integer extendtype;
    private Integer st;


}

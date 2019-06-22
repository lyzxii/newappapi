package order.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 混合多个表pojo
 *
 * @author GJ
 * @create 2017-12-27 15:32
 * tb_zh_detail_#gid#
 * tb_zhuihao_#gid#
 * tb_proj_xzjz
 **/
@Data
public class ComplexPojo {

    /**
     * 彩种logo
     */
    private String logo;

    /**
     * 方案编号
     */
    private String CZHID="";

    /**
     * 期号
     */
    private String CPERIODID="";
    /**
     * 投注号码
     */
    private String CCODES="";
    /**
     * 开奖号码
     */
    private String AWARDCODE="";
    /**
     * 投注金额
     */
    private String ICMONEY="";

    /**
     * 投注时间
     */
    private String CCASTDATE="";

    /**
     * 投注状态
     */
    private String ISTATE="";
    /**
     * 是否派奖
     */
    private String ISRETURN="";

    /**
     * 入库时间
     */
    private String CADDDATE="";
    /**
     * 中奖信息
     */
    private String CWININFO="";

    /**
     * 倍数Integer
     */
    private String IMULITY="";
    /**
     * 税后奖金Double
     */
    private String ITAX="";
    /**
     * 结算状态Integer
     */
    private String IJIESUAN="";
    /**
     * 追号类型Integer
     */
    private String ZHTYPE="";

    /**
     * 中奖是否停止(0 不停止 1停止 2 盈利停止)
     */
    private String IZHFLAG="";
    /**
     * 套餐选择类型 0:自选,1机选Integer
     */
    private String SELTYPE="";
    /**
     * 停止原因(0 未完成 1 已投注完成 2 中奖停止 3 用户手工停止)Integer
     */
    private String REASON="";
    /**
     * 追号总期数Integer
     */
    private String PNUMS="";
    /**
     * 来源值 0或者9Integer
     */
    private  String source="";

    /**
     * 累加投注金额
     */
    private String iallmoney = "";

    /**
     * 乐善中奖金额
     */
    private String ilsmoney = "";
    /**
     * 乐善计奖标记
     */
    private String ilsaward = "";

    /**
     * 中奖金额是否包含乐善彩 0不含，1含
     */
    private String iscontainls="0";

    /**
     * 是否是大乐透加奖的单子0不是，1是
     */
    private String isaddreward = "0";
    /**
     * 方案明细
     */
    private String IDETAILID ;
}

package order.dto;

import java.util.Date;

/**
 * 追号
 */
public class ZhuihaoDTO {

    //tb_zhuihao_{gid}
    private String czhid;//追号编号
    private String cnickid;//用户编号
    private String cgameid;//游戏编号
    private Integer ipnums;//追号总期数
    private Integer izhflag;//中奖是否停止(0 不停止 1停止 2 盈利停止)
    private Integer ifinish;//是否完成 0 未完成 1 完成
    private Integer isuccess;//成功数
    private Integer ifailure;//失败数
    private Date cadddate;//添加日期
    private Integer itmoney;//总金额
    private Integer ireason;//停止原因(0 未完成 1 已投注完成 2 中奖停止 3 用户手工停止)
    private Integer ibonus;//总中奖金额
    private Integer icasts;//实际投注金额
    private String ccomefrom;//来源
    private Integer isource;//投注来源( 0 网站 1 客户端 2 手机 3 WAP)
    private Integer ipay;//支付状态 0:未支付 1:支付成功 2:已退款
    private Date ipaydate;//支付到帐时间
    private Date ipayreturndate;//支付退款时间
    private String ipayno;//支付流水号
    private String cdesc;//追号描叙
    private Integer zhtype;//追号类型 0:普通追号,1套餐返点  2套餐不返点
    private Integer seltype;//套餐选择类型 0:自选,1机选
    private Integer combomoney;//套餐金额
    private Integer ihide;//隐藏标识,为0表示不隐藏,为1表示隐藏
    private Date chidedate;//记录隐藏时间


    //tb_zh_detail_{gid}
    private Integer isreturn;//是否派奖,0 未派奖,1 正在处理,2 派奖中,3 已派奖
    private Double icmoney;//投注金额
    private Double itax;//税后奖金
    private Double iamoney;//中奖金额
    private String ccodes;//投注号码
    private Integer ijiesuan;//结算标志（0 未结算 1 正在结算 2 已结算）
    private Integer istate;//状态 ( 0 未投注 1正在处理 2 已投注 3 取消)
    private Integer iaward;//是否算奖 0 未出来 1 正在处理 2 已算奖

    public Integer getIsreturn() {
        return isreturn;
    }

    public void setIsreturn(Integer isreturn) {
        this.isreturn = isreturn;
    }

    public String getCzhid() {
        return czhid;
    }

    public void setCzhid(String czhid) {
        this.czhid = czhid;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getCgameid() {
        return cgameid;
    }

    public void setCgameid(String cgameid) {
        this.cgameid = cgameid;
    }

    public Integer getIpnums() {
        return ipnums;
    }

    public void setIpnums(Integer ipnums) {
        this.ipnums = ipnums;
    }

    public Integer getIzhflag() {
        return izhflag;
    }

    public void setIzhflag(Integer izhflag) {
        this.izhflag = izhflag;
    }

    public Integer getIfinish() {
        return ifinish;
    }

    public void setIfinish(Integer ifinish) {
        this.ifinish = ifinish;
    }

    public Integer getIsuccess() {
        return isuccess;
    }

    public void setIsuccess(Integer isuccess) {
        this.isuccess = isuccess;
    }

    public Integer getIfailure() {
        return ifailure;
    }

    public void setIfailure(Integer ifailure) {
        this.ifailure = ifailure;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public Integer getItmoney() {
        return itmoney;
    }

    public void setItmoney(Integer itmoney) {
        this.itmoney = itmoney;
    }

    public Integer getIreason() {
        return ireason;
    }

    public void setIreason(Integer ireason) {
        this.ireason = ireason;
    }

    public Integer getIbonus() {
        return ibonus;
    }

    public void setIbonus(Integer ibonus) {
        this.ibonus = ibonus;
    }

    public Integer getIcasts() {
        return icasts;
    }

    public void setIcasts(Integer icasts) {
        this.icasts = icasts;
    }

    public String getCcomefrom() {
        return ccomefrom;
    }

    public void setCcomefrom(String ccomefrom) {
        this.ccomefrom = ccomefrom;
    }

    public Integer getIsource() {
        return isource;
    }

    public void setIsource(Integer isource) {
        this.isource = isource;
    }

    public Integer getIpay() {
        return ipay;
    }

    public void setIpay(Integer ipay) {
        this.ipay = ipay;
    }

    public Date getIpaydate() {
        return ipaydate;
    }

    public void setIpaydate(Date ipaydate) {
        this.ipaydate = ipaydate;
    }

    public Date getIpayreturndate() {
        return ipayreturndate;
    }

    public void setIpayreturndate(Date ipayreturndate) {
        this.ipayreturndate = ipayreturndate;
    }

    public String getIpayno() {
        return ipayno;
    }

    public void setIpayno(String ipayno) {
        this.ipayno = ipayno;
    }

    public String getCdesc() {
        return cdesc;
    }

    public void setCdesc(String cdesc) {
        this.cdesc = cdesc;
    }

    public Integer getZhtype() {
        return zhtype;
    }

    public void setZhtype(Integer zhtype) {
        this.zhtype = zhtype;
    }

    public Integer getSeltype() {
        return seltype;
    }

    public void setSeltype(Integer seltype) {
        this.seltype = seltype;
    }

    public Integer getCombomoney() {
        return combomoney;
    }

    public void setCombomoney(Integer combomoney) {
        this.combomoney = combomoney;
    }

    public Integer getIhide() {
        return ihide;
    }

    public void setIhide(Integer ihide) {
        this.ihide = ihide;
    }

    public Date getChidedate() {
        return chidedate;
    }

    public void setChidedate(Date chidedate) {
        this.chidedate = chidedate;
    }

    public Double getIcmoney() {
        return icmoney;
    }

    public void setIcmoney(Double icmoney) {
        this.icmoney = icmoney;
    }

    public Double getItax() {
        return itax;
    }

    public void setItax(Double itax) {
        this.itax = itax;
    }

    public Double getIamoney() {
        return iamoney;
    }

    public void setIamoney(Double iamoney) {
        this.iamoney = iamoney;
    }

    public String getCcodes() {
        return ccodes;
    }

    public void setCcodes(String ccodes) {
        this.ccodes = ccodes;
    }

    public Integer getIjiesuan() {
        return ijiesuan;
    }

    public void setIjiesuan(Integer ijiesuan) {
        this.ijiesuan = ijiesuan;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public Integer getIaward() {
        return iaward;
    }

    public void setIaward(Integer iaward) {
        this.iaward = iaward;
    }

}

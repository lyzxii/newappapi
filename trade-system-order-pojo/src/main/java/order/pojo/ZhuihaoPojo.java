package order.pojo;

import lombok.Data;

@Data
public class ZhuihaoPojo {

    private String czhid;//追号编号
    private String cnickid;//用户编号
    private String cgameid;//游戏编号
    private String ipnums;//追号总期数
    private String izhflag;//中奖是否停止(0 不停止 1停止 2 盈利停止)
    private String ifinish;//是否完成 0 未完成 1 完成
    private String isuccess;//成功数
    private String ifailure;//失败数
    private String cadddate;//添加日期
    private String itmoney;//总金额
    private String ireason;//停止原因(0 未完成 1 已投注完成 2 中奖停止 3 用户手工停止)
    private String ibonus;//总中奖金额
    private String icasts;//实际投注金额
    private String ccomefrom;//来源
    private String isource;//投注来源( 0 网站 1 客户端 2 手机 3 WAP)
    private String ipay;//支付状态 0:未支付 1:支付成功 2:已退款
    private String ipaydate;//支付到帐时间
    private String ipayreturndate;//支付退款时间
    private String ipayno;//支付流水号
    private String cdesc;//追号描叙
    private String zhtype;//追号类型 0:普通追号,1套餐返点  2套餐不返点
    private String seltype;//套餐选择类型 0:自选,1机选
    private String combomoney;//套餐金额
    private String ihide;//隐藏标识,为0表示不隐藏,为1表示隐藏
    private String chidedate;//记录隐藏时间
    private String cperiodid;
    private String ccodes;
    private String icmoney;
    private String ccastdate;
    private String istate;
    private String ibingo;
    private String imulity;
    private String cawarddate;
    private String iaward;
    private String iamoney;
    private String isreturn;
    private String creturndate;
    private String irmoney;
    private String itax;
    private String ijiesuan;
    private String iumoney;
    private String cawardcode;
    private String caddmoney;
    private String idetailid;
    private String isfandian;
    private String igagnum;
    private String igaunum;
    private String izhanji;
    private String cjsdate;
    private String cbgdate;
    private String cwininfo;



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

    public String getIpnums() {
        return ipnums;
    }

    public void setIpnums(String ipnums) {
        this.ipnums = ipnums;
    }

    public String getIzhflag() {
        return izhflag;
    }

    public void setIzhflag(String izhflag) {
        this.izhflag = izhflag;
    }

    public String getIfinish() {
        return ifinish;
    }

    public void setIfinish(String ifinish) {
        this.ifinish = ifinish;
    }

    public String getIsuccess() {
        return isuccess;
    }

    public void setIsuccess(String isuccess) {
        this.isuccess = isuccess;
    }

    public String getIfailure() {
        return ifailure;
    }

    public void setIfailure(String ifailure) {
        this.ifailure = ifailure;
    }

    public String getCadddate() {
        return cadddate;
    }

    public void setCadddate(String cadddate) {
        this.cadddate = cadddate;
    }

    public String getItmoney() {
        return itmoney;
    }

    public void setItmoney(String itmoney) {
        this.itmoney = itmoney;
    }

    public String getIreason() {
        return ireason;
    }

    public void setIreason(String ireason) {
        this.ireason = ireason;
    }

    public String getIbonus() {
        return ibonus;
    }

    public void setIbonus(String ibonus) {
        this.ibonus = ibonus;
    }

    public String getIcasts() {
        return icasts;
    }

    public void setIcasts(String icasts) {
        this.icasts = icasts;
    }

    public String getCcomefrom() {
        return ccomefrom;
    }

    public void setCcomefrom(String ccomefrom) {
        this.ccomefrom = ccomefrom;
    }

    public String getIsource() {
        return isource;
    }

    public void setIsource(String isource) {
        this.isource = isource;
    }

    public String getIpay() {
        return ipay;
    }

    public void setIpay(String ipay) {
        this.ipay = ipay;
    }

    public String getIpaydate() {
        return ipaydate;
    }

    public void setIpaydate(String ipaydate) {
        this.ipaydate = ipaydate;
    }

    public String getIpayreturndate() {
        return ipayreturndate;
    }

    public void setIpayreturndate(String ipayreturndate) {
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

    public String getZhtype() {
        return zhtype;
    }

    public void setZhtype(String zhtype) {
        this.zhtype = zhtype;
    }

    public String getSeltype() {
        return seltype;
    }

    public void setSeltype(String seltype) {
        this.seltype = seltype;
    }

    public String getCombomoney() {
        return combomoney;
    }

    public void setCombomoney(String combomoney) {
        this.combomoney = combomoney;
    }

    public String getIhide() {
        return ihide;
    }

    public void setIhide(String ihide) {
        this.ihide = ihide;
    }

    public String getChidedate() {
        return chidedate;
    }

    public void setChidedate(String chidedate) {
        this.chidedate = chidedate;
    }
}

package order.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by tiankun on 2017/12/27.
 */
public class QueryProjAppPojo implements Serializable {
    /*
     select cprojid hid,itype type,icast cast,cperiodid pid,cnickid,imulity mulity,cgameid gid,ismoney money,ilnum lnum,ipnum pnum, iaunum aunum,cname,inums nums,
             ijindu jindu,iviews views,cendtime endtime,iorder, cdesc ,ccodes ,iplay,cadddate btime,to_char(ccastdate,'MM-dd HH24:mi:ss') ctime,ifile,itmoney tmoney,IBONUS rmoney,itax tax,iaward award,cawarddate awarddate,ireturn,
             cretdate retdate,cwininfo wininfo,iowins owins,iopen ,iwrate wrate, istate ,ijiesuan jiesuan,irpmoney rpmoney,irprgmoney rprgmoney,caddmoney addmoney,decode(extendtype,6,6,7,7,8,8,9,9,11,11,13,13,12,12,14,14,15,15,isource) source,imoneyrange
             ,cmatchs,cguoguan,decode(sign(cendtime-sysdate),-1,0,decode(istate,-1,1,0)) ipay,nvl(iupay,0) upay
     */
    private String upay = "";//方案支付类型: 账户支付 - 0, 订单支付 - 1 ,Integer
    private String ipay = "";//支付状态 0:未支付 1:支付成功 2:退款中 3:已退款,Integer
 //   private String addmoney;//加奖奖金,Double
    private String source = "";//投注类型,Integer
    private String imoneyrange = "";//理论奖金范围
//    private String cmatchs;//对阵列表(,隔开)
 //   private String cguoguan;//过关类型
    private String ireturn;//是否派奖（0 未派奖 1 正在派 1 已派奖）,Integer
    private String retdate;//派奖时间,Date
    private String wininfo;//中奖信息（中奖注数用逗号隔开）
    private String owins;//发起人提成奖金,Double
//    private String wrate;//发起人中奖提成比率 （盈利情况）,Integer
 //   private String jiesuan;//结算标志（0 未结算 1 正在结算 2 已结算）,Integer
    private String rpmoney = "";//红包金额,Double
 //   private String rprgmoney;//红包认购记录总金额,Double
    private String ccodes;//投注号码 *
 //   private String iplay;//玩法（单式 复式）,Integer
    private String btime = "";//发起时间, 认购时间,Date
    private String ifile;//是否文件投注（0不是 1 是）,Integer
    private String tmoney = "";//总金额,Double
    private String rmoney = "";//认购派奖金额,Double
    private String tax = "";//税后奖金,Double
    private String award = "";//计奖标志（0 未计奖 1 正在计奖 2 已计奖),Integer
    private String awarddate;//计奖时间,Date
    private String nums;//总份数,Integer
 //   private String jindu;//进度,Integer
 //   private String views;//方案关注次数,Integer
    private String endtime;//截止时间,Date
//    private String iorder;//置顶标志（0不 1是）,Integer
//    private String cdesc;//合买描叙
    private String hid;//方案编号
    private String mulity = "";//倍数,Integer
 //   private String money;//每份金额,Double
 //   private String lnum;//剩余份数,Integer
//    private String pnum;//发起人保底份数,Integer
//    private String aunum;//金星个数(发起方案时的银星数),Integer
 //   private String cname;//合买名称
    private String pid;//期次
    private String istate = "";//状态(0 禁止认购 1 认购中,2 已满员 3 过期未满撤销 4主动撤销 5 出票失败撤销,Integer
    private String iopen;//是否保密 （0 对所有人公开 1 截止后公开 2 对参与人员公开 3 截止后对参与人公开）,Integer
    private String cnickid;//发起人
    private String type;//方案类型（0 自购(代购) 1合买  2分享 3跟买 ）,Integer
    private String cast = "";//出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）,Integer
    private String gid;//游戏编号
    private String ctime;//出票时间 cast=3 有值 *
    //private Date castdate;//出票时间


    private String lsmoney; //乐善中奖金额
    private String lsaward;//乐善计奖标记

    public String getLsmoney() {
        return lsmoney;
    }

    public void setLsmoney(String lsmoney) {
        this.lsmoney = lsmoney;
    }

    public String getLsaward() {
        return lsaward;
    }

    public void setLsaward(String lsaward) {
        this.lsaward = lsaward;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUpay() {
        return upay;
    }

    public void setUpay(String upay) {
        this.upay = upay;
    }

    public String getIpay() {
        return ipay;
    }

    public void setIpay(String ipay) {
        this.ipay = ipay;
    }



    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImoneyrange() {
        return imoneyrange;
    }

    public void setImoneyrange(String imoneyrange) {
        this.imoneyrange = imoneyrange;
    }



    public String getIreturn() {
        return ireturn;
    }

    public void setIreturn(String ireturn) {
        this.ireturn = ireturn;
    }

    public String getRetdate() {
        return retdate;
    }

    public void setRetdate(String retdate) {
        this.retdate = retdate;
    }

    public String getWininfo() {
        return wininfo;
    }

    public void setWininfo(String wininfo) {
        this.wininfo = wininfo;
    }

    public String getOwins() {
        return owins;
    }

    public void setOwins(String owins) {
        this.owins = owins;
    }



    public String getRpmoney() {
        return rpmoney;
    }

    public void setRpmoney(String rpmoney) {
        this.rpmoney = rpmoney;
    }



    public String getCcodes() {
        return ccodes;
    }

    public void setCcodes(String ccodes) {
        this.ccodes = ccodes;
    }



    public String getBtime() {
        return btime;
    }

    public void setBtime(String btime) {
        this.btime = btime;
    }

    public String getIfile() {
        return ifile;
    }

    public void setIfile(String ifile) {
        this.ifile = ifile;
    }

    public String getTmoney() {
        return tmoney;
    }

    public void setTmoney(String tmoney) {
        this.tmoney = tmoney;
    }

    public String getRmoney() {
        return rmoney;
    }

    public void setRmoney(String rmoney) {
        this.rmoney = rmoney;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getAward() {
        return award;
    }

    public void setAward(String award) {
        this.award = award;
    }

    public String getAwarddate() {
        return awarddate;
    }

    public void setAwarddate(String awarddate) {
        this.awarddate = awarddate;
    }

    public String getNums() {
        return nums;
    }

    public void setNums(String nums) {
        this.nums = nums;
    }


    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }



    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getMulity() {
        return mulity;
    }

    public void setMulity(String mulity) {
        this.mulity = mulity;
    }



    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getIstate() {
        return istate;
    }

    public void setIstate(String istate) {
        this.istate = istate;
    }

    public String getIopen() {
        return iopen;
    }

    public void setIopen(String iopen) {
        this.iopen = iopen;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }
}

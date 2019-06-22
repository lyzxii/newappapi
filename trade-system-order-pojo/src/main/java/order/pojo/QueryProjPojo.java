package order.pojo;

import java.util.Date;

/**
 * Created by tiankun on 2017/12/22.
 */
public class QueryProjPojo {

    private Integer periodid;//期次
    private Integer istate;//状态(0 禁止认购 1 认购中,2 已满员 3 过期未满撤销 4主动撤销 5 出票失败撤销
    private Integer iopen;//是否保密 （0 对所有人公开 1 截止后公开 2 对参与人员公开 3 截止后对参与人公开）
    private String cnickid;//发起人
    private Integer itype;//方案类型（0 自购(代购) 1合买  2分享 3跟买 ）
    private Integer icast;//出票标志（0 未出票 1 可以出票 2 已拆票 3 已出票）
    private String gameid;//游戏编号
    private Date castdate;//出票时间

    public Integer getPeriodid() {
        return periodid;
    }

    public void setPeriodid(Integer periodid) {
        this.periodid = periodid;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public Integer getIopen() {
        return iopen;
    }

    public void setIopen(Integer iopen) {
        this.iopen = iopen;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public Integer getIcast() {
        return icast;
    }

    public void setIcast(Integer icast) {
        this.icast = icast;
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public Date getCastdate() {
        return castdate;
    }

    public void setCastdate(Date castdate) {
        this.castdate = castdate;
    }
}

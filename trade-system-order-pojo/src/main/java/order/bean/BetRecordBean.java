package order.bean;

import com.caiyi.lottery.tradesystem.bean.Page;

import java.util.Date;

public class BetRecordBean {
    private String gid;
    private String pid;
    private String tid;
    private String projid;
    private String buyid;
    private double buyMoney;
    private Date buyDate;
    private int cancelFlag;
    private int awardFlag;
    private int returnFlag;
    private double awardMoney;
    private int jiesuan;
    private int ty;
    private int buyState;
    private int castFlag;
    private int endFlag;
    private String stateDesc;
    private String jindu;
    private Date endtime;
    private int shareGod;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProjid() {
        return projid;
    }

    public void setProjid(String projid) {
        this.projid = projid;
    }

    public String getBuyid() {
        return buyid;
    }

    public void setBuyid(String buyid) {
        this.buyid = buyid;
    }

    public double getBuyMoney() {
        return buyMoney;
    }

    public void setBuyMoney(double buyMoney) {
        this.buyMoney = buyMoney;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public int getCancelFlag() {
        return cancelFlag;
    }

    public void setCancelFlag(int cancelFlag) {
        this.cancelFlag = cancelFlag;
    }

    public int getAwardFlag() {
        return awardFlag;
    }

    public void setAwardFlag(int awardFlag) {
        this.awardFlag = awardFlag;
    }

    public int getReturnFlag() {
        return returnFlag;
    }

    public void setReturnFlag(int returnFlag) {
        this.returnFlag = returnFlag;
    }

    public double getAwardMoney() {
        return awardMoney;
    }

    public void setAwardMoney(double awardMoney) {
        this.awardMoney = awardMoney;
    }

    public int getJiesuan() {
        return jiesuan;
    }

    public void setJiesuan(int jiesuan) {
        this.jiesuan = jiesuan;
    }

    public int getTy() {
        return ty;
    }

    public void setTy(int ty) {
        this.ty = ty;
    }

    public int getBuyState() {
        return buyState;
    }

    public void setBuyState(int buyState) {
        this.buyState = buyState;
    }

    public int getCastFlag() {
        return castFlag;
    }

    public void setCastFlag(int castFlag) {
        this.castFlag = castFlag;
    }

    public int getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(int endFlag) {
        this.endFlag = endFlag;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public String getJindu() {
        return jindu;
    }

    public void setJindu(String jindu) {
        this.jindu = jindu;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public int getShareGod() {
        return shareGod;
    }

    public void setShareGod(int shareGod) {
        this.shareGod = shareGod;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }
}

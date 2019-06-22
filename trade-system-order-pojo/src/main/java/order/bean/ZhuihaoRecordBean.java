package order.bean;

import java.util.Date;

public class ZhuihaoRecordBean{
    private String zhuihaoId;
    private int zhType;
    private String gid;
    private int totalPeriod;
    private int successPeriod;
    private int failPeriod;
    private int zhFlag;
    private int finishFlag;
    private Date addDate;
    private double totalMoney;
    private double totalBonus;
    private double castMoney;
    private int stopReason;

    public String getZhuihaoId() {
        return zhuihaoId;
    }

    public void setZhuihaoId(String zhuihaoId) {
        this.zhuihaoId = zhuihaoId;
    }

    public int getZhType() {
        return zhType;
    }

    public void setZhType(int zhType) {
        this.zhType = zhType;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(int totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public int getSuccessPeriod() {
        return successPeriod;
    }

    public void setSuccessPeriod(int successPeriod) {
        this.successPeriod = successPeriod;
    }

    public int getFailPeriod() {
        return failPeriod;
    }

    public void setFailPeriod(int failPeriod) {
        this.failPeriod = failPeriod;
    }

    public int getZhFlag() {
        return zhFlag;
    }

    public void setZhFlag(int zhFlag) {
        this.zhFlag = zhFlag;
    }

    public int getFinishFlag() {
        return finishFlag;
    }

    public void setFinishFlag(int finishFlag) {
        this.finishFlag = finishFlag;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getTotalBonus() {
        return totalBonus;
    }

    public void setTotalBonus(double totalBonus) {
        this.totalBonus = totalBonus;
    }

    public double getCastMoney() {
        return castMoney;
    }

    public void setCastMoney(double castMoney) {
        this.castMoney = castMoney;
    }

    public int getStopReason() {
        return stopReason;
    }

    public void setStopReason(int stopReason) {
        this.stopReason = stopReason;
    }
}

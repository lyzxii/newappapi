package order.bean;

public class AllRecordBean extends BetRecordBean{
    private int pnum;
    private int zhflag;
    private int finishFlag;
    private int successFlag;
    private int failureFlag;
    private int reason;
    private int casts;
    private int zhtype;
    private int isZh;

    public int getPnum() {
        return pnum;
    }

    public void setPnum(int pnum) {
        this.pnum = pnum;
    }

    public int getZhflag() {
        return zhflag;
    }

    public void setZhflag(int zhflag) {
        this.zhflag = zhflag;
    }

    public int getFinishFlag() {
        return finishFlag;
    }

    public void setFinishFlag(int finishFlag) {
        this.finishFlag = finishFlag;
    }

    public int getSuccessFlag() {
        return successFlag;
    }

    public void setSuccessFlag(int successFlag) {
        this.successFlag = successFlag;
    }

    public int getFailureFlag() {
        return failureFlag;
    }

    public void setFailureFlag(int failureFlag) {
        this.failureFlag = failureFlag;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }

    public int getCasts() {
        return casts;
    }

    public void setCasts(int casts) {
        this.casts = casts;
    }

    public int getZhtype() {
        return zhtype;
    }

    public void setZhtype(int zhtype) {
        this.zhtype = zhtype;
    }

    public int getIsZh() {
        return isZh;
    }

    public void setIsZh(int isZh) {
        this.isZh = isZh;
    }
}

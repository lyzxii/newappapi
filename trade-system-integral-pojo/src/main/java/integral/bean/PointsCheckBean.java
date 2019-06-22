package integral.bean;

public class PointsCheckBean {
    private int busiErrCode;

    private String busiErrDesc;

    private int checkStatus;

    private String checkResult;

    public int getBusiErrCode() {
        return busiErrCode;
    }

    public void setBusiErrCode(int busiErrCode) {
        this.busiErrCode = busiErrCode;
    }

    public String getBusiErrDesc() {
        return busiErrDesc;
    }

    public void setBusiErrDesc(String busiErrDesc) {
        this.busiErrDesc = busiErrDesc;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }
}

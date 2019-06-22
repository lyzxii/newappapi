package integral.pojo;


public class RedPacket {
    private int imoney;
    private int itype;
    private String crpid;
    private String cnickid;
    private String cdeaddate;
    private String coperator;
    private int igetType;
    private String icardid;
    private String cmeomo;
    private String dispatchtime;

    private int busiErrCode;
    private String busiErrDesc;
    private String cupacketid;

    public RedPacket() {
    }

    public RedPacket(int imoney, int itype, String crpid, String cnickid) {
        this.imoney = imoney;
        this.itype = itype;
        this.crpid = crpid;
        this.cnickid = cnickid;
    }

    public int getImoney() {
        return imoney;
    }

    public void setImoney(int imoney) {
        this.imoney = imoney;
    }

    public int getItype() {
        return itype;
    }

    public void setItype(int itype) {
        this.itype = itype;
    }

    public String getCrpid() {
        return crpid;
    }

    public void setCrpid(String crpid) {
        this.crpid = crpid;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getCdeaddate() {
        return cdeaddate;
    }

    public void setCdeaddate(String cdeaddate) {
        this.cdeaddate = cdeaddate;
    }

    public String getCoperator() {
        return coperator;
    }

    public void setCoperator(String coperator) {
        this.coperator = coperator;
    }

    public int getIgetType() {
        return igetType;
    }

    public void setIgetType(int igetType) {
        this.igetType = igetType;
    }

    public String getIcardid() {
        return icardid;
    }

    public void setIcardid(String icardid) {
        this.icardid = icardid;
    }

    public String getCmeomo() {
        return cmeomo;
    }

    public void setCmeomo(String cmeomo) {
        this.cmeomo = cmeomo;
    }

    public String getDispatchtime() {
        return dispatchtime;
    }

    public void setDispatchtime(String dispatchtime) {
        this.dispatchtime = dispatchtime;
    }

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

    public String getCupacketid() {
        return cupacketid;
    }

    public void setCupacketid(String cupacketid) {
        this.cupacketid = cupacketid;
    }
}

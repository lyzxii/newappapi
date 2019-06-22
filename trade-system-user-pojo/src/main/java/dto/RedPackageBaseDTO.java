package dto;

/**
 * @author wxy
 * @create 2017-11-28 10:50
 **/
public class RedPackageBaseDTO {
    private String cnickid;//用户昵称
    private int crpid;//红包id
    private String imoney;//红包总金额
    private String cdeaddate = "";//红包过期时间
    private String igettype = "1"; // 1 系统赠送    2 手动赠送  3 卡密激活
    private String cmemo = "";//备注

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public int getCrpid() {
        return crpid;
    }

    public void setCrpid(int crpid) {
        this.crpid = crpid;
    }

    public String getImoney() {
        return imoney;
    }

    public void setImoney(String imoney) {
        this.imoney = imoney;
    }

    public String getCdeaddate() {
        return cdeaddate;
    }

    public void setCdeaddate(String cdeaddate) {
        this.cdeaddate = cdeaddate;
    }

    public String getIgettype() {
        return igettype;
    }

    public void setIgettype(String igettype) {
        this.igettype = igettype;
    }

    public String getCmemo() {
        return cmemo;
    }

    public void setCmemo(String cmemo) {
        this.cmemo = cmemo;
    }
}

package order.dto;

public class ProjDTO {

    private String ibuyid;//购买编号

    private String cnickid;//认购人

    private Integer ihide;//隐藏

    private Integer icancel;//是否撤销 (0 未撤销 1 本人撤销 2 系统撤销）

    private Integer ireturn;//是否派奖（0 未派奖 1 已派奖）

    public String getIbuyid() {
        return ibuyid;
    }

    public void setIbuyid(String ibuyid) {
        this.ibuyid = ibuyid;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public Integer getIhide() {
        return ihide;
    }

    public void setIhide(Integer ihide) {
        this.ihide = ihide;
    }

    public Integer getIcancel() {
        return icancel;
    }

    public void setIcancel(Integer icancel) {
        this.icancel = icancel;
    }

    public Integer getIreturn() {
        return ireturn;
    }

    public void setIreturn(Integer ireturn) {
        this.ireturn = ireturn;
    }
}

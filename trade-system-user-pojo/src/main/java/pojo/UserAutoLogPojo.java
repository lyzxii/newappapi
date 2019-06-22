package pojo;

/**
 * Created by tiankun on 2017/12/1.
 */
public class UserAutoLogPojo {

    /*cnickid,cgameid,cowner,ctype,ilimit,iminmoney,imaxmoney,ibmoney,itype,irate,ibuy,itimes,ctime,cdes*/
    private String cnickid; //用户编号
    private String cgameid; //游戏编号
    private String cowner;  //发起人编号
    private Integer ctype;  //操作类型：1启用 2禁用 3修改
    private Integer ilimit;
    private Integer iminmoney; //
    private Double imaxmoney; //
    private Double ibmoney; //
    private Integer itype; //按固定or比例认购(0固定金额认购 1按方案比例认购)
    private Integer irate; //认购方案比例
    private Integer ibuy; //方案金额不足是否认购(0否 1 是)
    private Integer itimes; //单日最多跟单次数(0 无限制 其他为值)
    private String cdes;    //备注

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

    public String getCowner() {
        return cowner;
    }

    public void setCowner(String cowner) {
        this.cowner = cowner;
    }

    public Integer getCtype() {
        return ctype;
    }

    public void setCtype(Integer ctype) {
        this.ctype = ctype;
    }

    public Integer getIlimit() {
        return ilimit;
    }

    public void setIlimit(Integer ilimit) {
        this.ilimit = ilimit;
    }

    public Integer getIminmoney() {
        return iminmoney;
    }

    public void setIminmoney(Integer iminmoney) {
        this.iminmoney = iminmoney;
    }

    public Double getImaxmoney() {
        return imaxmoney;
    }

    public void setImaxmoney(Double imaxmoney) {
        this.imaxmoney = imaxmoney;
    }

    public Double getIbmoney() {
        return ibmoney;
    }

    public void setIbmoney(Double ibmoney) {
        this.ibmoney = ibmoney;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public Integer getIrate() {
        return irate;
    }

    public void setIrate(Integer irate) {
        this.irate = irate;
    }

    public Integer getIbuy() {
        return ibuy;
    }

    public void setIbuy(Integer ibuy) {
        this.ibuy = ibuy;
    }

    public Integer getItimes() {
        return itimes;
    }

    public void setItimes(Integer itimes) {
        this.itimes = itimes;
    }

    public String getCdes() {
        return cdes;
    }

    public void setCdes(String cdes) {
        this.cdes = cdes;
    }
}

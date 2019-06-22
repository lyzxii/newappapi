package pojo;

import java.util.Date;

/**
 * 用户手机设备号
 *
 * @author GJ
 * @create 2017-11-27 21:10
 **/
public class UserImeiPojo {
    private String cnickid;
    private String cimei;
    private String cagentid;
    private Date cadddate;
    private Integer itype;
    private String cuserid;
    private Integer isource;

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getCimei() {
        return cimei;
    }

    public void setCimei(String cimei) {
        this.cimei = cimei;
    }

    public String getCagentid() {
        return cagentid;
    }

    public void setCagentid(String cagentid) {
        this.cagentid = cagentid;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public Integer getIsource() {
        return isource;
    }

    public void setIsource(Integer isource) {
        this.isource = isource;
    }
}

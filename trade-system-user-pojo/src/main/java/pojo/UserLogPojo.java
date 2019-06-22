package pojo;

import java.util.Date;

/**
 * 用户日志表对应的pojo
 *
 * @author GJ
 * @create 2017-11-24 18:44
 **/
public class UserLogPojo {
    //记录编号
    private Long irecid;
    //用户昵称
    private String cnickid;
    //记录时间
    private Date cadddate;
    //操作内容
    private String cmemo;
    //IP地址
    private String cipaddr;
    //操作类型
    private String ctype;

    public Long getIrecid() {
        return irecid;
    }

    public void setIrecid(Long irecid) {
        this.irecid = irecid;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCmemo() {
        return cmemo;
    }

    public void setCmemo(String cmemo) {
        this.cmemo = cmemo;
    }

    public String getCipaddr() {
        return cipaddr;
    }

    public void setCipaddr(String cipaddr) {
        this.cipaddr = cipaddr;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }
}

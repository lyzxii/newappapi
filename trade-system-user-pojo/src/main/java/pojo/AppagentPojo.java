package pojo;

import java.util.Date;

/**
 * 代理商pojo
 * @create 2017-11-27 20:18:29
 */
public class AppagentPojo {

    private String agentid;
    private Integer isource;
    private String cdownfile;
    private Date cadddate;
    private String cname;
    private String cimg;
    private String cdomain;
    private Integer type;
    private String logo;
    private String lottername;
    private String download;

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public Integer getIsource() {
        return isource;
    }

    public void setIsource(Integer isource) {
        this.isource = isource;
    }

    public String getCdownfile() {
        return cdownfile;
    }

    public void setCdownfile(String cdownfile) {
        this.cdownfile = cdownfile;
    }

    public Date getCadddate() {
        return cadddate;
    }

    public void setCadddate(Date cadddate) {
        this.cadddate = cadddate;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCimg() {
        return cimg;
    }

    public void setCimg(String cimg) {
        this.cimg = cimg;
    }

    public String getCdomain() {
        return cdomain;
    }

    public void setCdomain(String cdomain) {
        this.cdomain = cdomain;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLottername() {
        return lottername;
    }

    public void setLottername(String lottername) {
        this.lottername = lottername;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}

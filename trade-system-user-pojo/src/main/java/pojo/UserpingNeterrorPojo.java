package pojo;

import java.util.Date;

/**
 * Created by tiankun on 2017/12/6.
 */
public class UserpingNeterrorPojo {
    private String errordesc;   //错误提示
    private String nettype; //网络类型
    private String operatortype;//运营商
    private String osversion;//系统版本号
    private String dnsresolution;//DNS解析结果
    private String username;//用户名
    private String errortime;//错误时间
    private String appname;//APP名称
    private String appplatform;//APP平台
    private String appversion;//APP版本号
    private String localip;//本地IP
    private String localdns;//本地DNS
    private String pingresult;

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrordesc(String errordesc) {
        this.errordesc = errordesc;
    }

    public String getNettype() {
        return nettype;
    }

    public void setNettype(String nettype) {
        this.nettype = nettype;
    }

    public String getOperatortype() {
        return operatortype;
    }

    public void setOperatortype(String operatortype) {
        this.operatortype = operatortype;
    }

    public String getOsversion() {
        return osversion;
    }

    public void setOsversion(String osversion) {
        this.osversion = osversion;
    }

    public String getDnsresolution() {
        return dnsresolution;
    }

    public void setDnsresolution(String dnsresolution) {
        this.dnsresolution = dnsresolution;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getErrortime() {
        return errortime;
    }

    public void setErrortime(String errortime) {
        this.errortime = errortime;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppplatform() {
        return appplatform;
    }

    public void setAppplatform(String appplatform) {
        this.appplatform = appplatform;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getLocalip() {
        return localip;
    }

    public void setLocalip(String localip) {
        this.localip = localip;
    }

    public String getLocaldns() {
        return localdns;
    }

    public void setLocaldns(String localdns) {
        this.localdns = localdns;
    }

    public String getPingresult() {
        return pingresult;
    }

    public void setPingresult(String pingresult) {
        this.pingresult = pingresult;
    }
}

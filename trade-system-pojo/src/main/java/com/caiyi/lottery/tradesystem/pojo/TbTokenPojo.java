package com.caiyi.lottery.tradesystem.pojo;

import java.util.Date;

public class TbTokenPojo {

    private Date lasttime;
    private Integer expiresin;
    private Integer istate;
    private String cnickid;
    private String cpassword;
    private String paramjson;
    private String accesstoken;
    private Integer mobiletype;
    private String  appid;

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public Integer getMobiletype() {
        return mobiletype;
    }

    public void setMobiletype(Integer mobiletype) {
        this.mobiletype = mobiletype;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public Date getLasttime() {
        return lasttime;
    }

    public void setLasttime(Date lasttime) {
        this.lasttime = lasttime;
    }

    public Integer getExpiresin() {
        return expiresin;
    }

    public void setExpiresin(Integer expiresin) {
        this.expiresin = expiresin;
    }

    public Integer getIstate() {
        return istate;
    }

    public void setIstate(Integer istate) {
        this.istate = istate;
    }

    public String getCnickid() {
        return cnickid;
    }

    public void setCnickid(String cnickid) {
        this.cnickid = cnickid;
    }

    public String getCpassword() {
        return cpassword;
    }

    public void setCpassword(String cpassword) {
        this.cpassword = cpassword;
    }

    public String getParamjson() {
        return paramjson;
    }

    public void setParamjson(String paramjson) {
        this.paramjson = paramjson;
    }
}

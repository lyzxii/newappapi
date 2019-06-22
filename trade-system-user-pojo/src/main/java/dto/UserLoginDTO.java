package dto;

/**
 * 用户登入返回信息
 *
 * @author GJ
 * @create 2017-11-30 9:48
 **/
public class UserLoginDTO {
    private String notice;
    private String uid;
    private String userid;
    private String appid;
    private String accesstoken;
    private String whitelist;
    private String hasVip;

    public String getHasVip() {
        return hasVip;
    }

    public void setHasVip(String hasVip) {
        this.hasVip = hasVip;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }
}

package bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

public class TokenBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uid; //用户信息
    private String pwd; //用户密码
    private String mtype; //客户端类型

    private String accessToken; //令牌字符串
    private String appid; //令牌密钥

    private String paramJson; //token中传递的参数,取代之前存放在session中的额外参数


    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getMtype() {
        return mtype;
    }
    public void setMtype(String mtype) {
        this.mtype = mtype;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getAppid() {
        return appid;
    }
    public void setAppid(String appid) {
        this.appid = appid;
    }
    public String getParamJson() {
        return paramJson;
    }
    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
	public String toJson(){
		return JSON.toJSONString(this);
	}
}

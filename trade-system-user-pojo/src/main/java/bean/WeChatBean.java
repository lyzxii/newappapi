package bean;

import com.caiyi.lottery.tradesystem.BaseBean;

public class WeChatBean extends BaseBean{
    private String mphone;
    private String verycode; // 验证码
    private String requestURI;
    private String wechatAppid; // 公众号的唯一标识
    private String unionid;//用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。
    private String openid;//关注者openid
    private String weChatToken;//微信授权信息

    private String nickName;//关注者微信昵称
    private String sex;//关注者性别
    private String province;// 关注者省份
    private String city;// 关注者
    private String country;
    private String headImgUrl;// 关注者头像地址

    private String appAgentId;
    private String pwdflag;// 是否默认密码

    private String code; // 换取access_token的微信授权票据
    private String secret; // 公众号的appsecret

    public String getMphone() {
        return mphone;
    }

    public void setMphone(String mphone) {
        this.mphone = mphone;
    }

    public String getVerycode() {
        return verycode;
    }

    public void setVerycode(String verycode) {
        this.verycode = verycode;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getWeChatToken() {
        return weChatToken;
    }

    public void setWeChatToken(String weChatToken) {
        this.weChatToken = weChatToken;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getAppAgentId() {
        return appAgentId;
    }

    public void setAppAgentId(String appAgentId) {
        this.appAgentId = appAgentId;
    }

    public String getPwdflag() {
        return pwdflag;
    }

    public void setPwdflag(String pwdflag) {
        this.pwdflag = pwdflag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getWechatAppid() {
        return wechatAppid;
    }

    public void setWechatAppid(String wechatAppid) {
        this.wechatAppid = wechatAppid;
    }
}

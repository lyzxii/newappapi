package bean;

import com.caiyi.lottery.tradesystem.BaseBean;

/**
 * 支付宝快捷登入bean
 *
 * @author GJ
 * @create 2017-12-14 16:21
 **/
public class AlipayLoginBean extends BaseBean {
    /**
     * 支付宝授权code
     */
    private String authcode;

    /**
     * 联合登录类型
     */
    private int type;
    /**
     * 支付宝唯一id
     */
    private String aliypayid;


    /**
     * 身份证号码
     */
    private String certNo;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String mobileNo;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 性别
     */
    private String gender;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 合作网站用户类型
     */
    private int allyType;
    /**
     * 合作网站用户id
     */
    private String userId;
    /**
     * userbean对应的字段
     * 0:可以直接修改密码  1:不可以直接修改密码
     */
    private String pwdflag;

    /**
     * 0 新 1老
     */
    private int isNew;

    private String yzm;

    private String partner;
    /**
     * 安卓或者ios
     */
    private String referer;

    /**
     * 是否参与活动
     */
    private int ishuodong;

    private String returnInfo;


    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public int getIshuodong() {
        return ishuodong;
    }

    public void setIshuodong(int ishuodong) {
        this.ishuodong = ishuodong;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getAllyType() {
        return allyType;
    }

    public void setAllyType(int allyType) {
        this.allyType = allyType;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getAliypayid() {
        return aliypayid;
    }

    public void setAliypayid(String aliypayid) {
        this.aliypayid = aliypayid;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPwdflag() {
        return pwdflag;
    }

    public void setPwdflag(String pwdflag) {
        this.pwdflag = pwdflag;
    }

    public int getIsNew() {
        return isNew;
    }

    public void setIsNew(int isNew) {
        this.isNew = isNew;
    }
}

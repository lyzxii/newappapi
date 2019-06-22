package pojo;

/**
 * 支付宝快登和用户
 *
 * @author GJ
 * @create 2017-12-14 20:58
 **/
public class Ally_UserPojo {

    /**
     * 用户名
     */
    private String uid ;

    /**
     * 密码
     */
    private String pwd;


    /**
     * 手机号
     */
    private String mobileNo;

    /**
     * 手机号是否验证绑定
     */
    private Integer mobileBind;
    /**
     * 手机MD5
     */
    private String mobilenomd5;

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

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public Integer getMobileBind() {
        return mobileBind;
    }

    public void setMobileBind(Integer mobileBind) {
        this.mobileBind = mobileBind;
    }

    public String getMobilenomd5() {
        return mobilenomd5;
    }

    public void setMobilenomd5(String mobilenomd5) {
        this.mobilenomd5 = mobilenomd5;
    }
}

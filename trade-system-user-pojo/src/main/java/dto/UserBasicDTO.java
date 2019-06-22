package dto;

/**
 * 用户基本信息DTO
 *
 * @author GJ
 * @create 2017-11-27 16:08
 **/
public class UserBasicDTO {
    //用户id
   private String uid;
    //手机号
   private String mobileNo;
   //密码
   private String pwd;
   //是否清除
   private Boolean clear;
   //标识
   private String flag;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Boolean getClear() {
        return clear;
    }

    public void setClear(Boolean clear) {
        this.clear = clear;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}

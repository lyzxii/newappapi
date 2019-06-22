package bean;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SafeBean /*extends BaseBean*/ implements Serializable {

    private static final long serialVersionUID = -3486405697231875014L;

    private String realname ;//真实姓名
    private String idcard ;//身份证号码
    private String mobileno ;//手机号码
    private String bankcard ;//银行卡号

    private String cardmobile; // 提款银行卡对应的手机号

    private String usersource ; // 用户来源
    private String adddate; // 加入时间

    private String nickid;// 用户别名
    private String cashid; // 提现记录编号
    private String smsid; // 短信表编号
    private String cid ; // 充值银行卡表编号

    private String mobileId; // 手机号表序列号
    private String bankcardId; // 银行卡表序列号
    private String idCardId; // 身份证表序列号
    private String realnameId; // 真实姓名序列号
    private String rechargeCardId;// 充值银行卡副表序列号

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getBankcard() {
        return bankcard;
    }

    public void setBankcard(String bankcard) {
        this.bankcard = bankcard;
    }

    public String getUsersource() {
        return usersource;
    }

    public void setUsersource(String usersource) {
        this.usersource = usersource;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public String getNickid() {
        return nickid;
    }

    public void setNickid(String nickid) {
        this.nickid = nickid;
    }

    public String getCashid() {
        return cashid;
    }

    public void setCashid(String cashid) {
        this.cashid = cashid;
    }

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getMobileId() {
        return mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getBankcardId() {
        return bankcardId;
    }

    public void setBankcardId(String bankcardId) {
        this.bankcardId = bankcardId;
    }

    public String getRechargeCardId() {
        return rechargeCardId;
    }

    public void setRechargeCardId(String rechargeCardId) {
        this.rechargeCardId = rechargeCardId;
    }

    public String getIdCardId() {
        return idCardId;
    }

    public void setIdCardId(String idCardId) {
        this.idCardId = idCardId;
    }

    public String getRealnameId() {
        return realnameId;
    }

    public void setRealnameId(String realnameId) {
        this.realnameId = realnameId;
    }

    public String getCardmobile() {
        return cardmobile;
    }

    public void setCardmobile(String cardmobile) {
        this.cardmobile = cardmobile;
    }
}

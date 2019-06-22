package pojo;

public class UserPojo {

    //用户编号
    private String uid;
    private String flag;//TODO
    private String yzm;

    //登录密码
    private String pwd;
    //0:可以直接修改密码  1:不可以直接修改密码
    private String pwdflag;
    //手机号码
    private String mobileNo;
    //电子邮件
    private String mailAddr;
    //用户来源
    private String comeFrom;
    //注册IP
    private String ipAddr;
    //用户惟一序列ID
    private String cuserId;
    //渠道值
    private String source;
    //手机IMEI
    private String imei;
    //加密私钥
    private String privateKey;
    //手机号MD5
    private String mobileMd5;
    private String agentid;       //代理商id
    private Integer mobbindFlag;  //手机号是否绑定标识
    private Integer isvip;        //是否是vip标识
    private String realName;      //用户真实姓名
    private String idcard;        //用户身份证号码
    private String drawBankCard;      //用户提款银行卡
    private String whitegrade;    //用户白名单等级
    private String userImg;       //用户头像路径
    private Integer state;        //用户账号状态

    private Integer type;//用户类型 0普通用户 1支付宝生活助手

    private Integer lgphone;//是否手机登陆

    private Integer open;//1 开发白名单 0 冻结白名单

    private String bankCard;      // 银行卡号
    private String bankName;      // 银行卡名
    private String bankCode;      // 银行代码
    private String bankProvince;  // 银行所在省份
    private String bankCity;      // 银行所在市
    private Integer loginPhone;   // 是否手机登录
    private String cardMobile;    // 银行卡绑定手机号
    private Integer gradeid; //用户等级

    // 添加日期
    private String addDate;
    // 是否绑定手机号
    private Integer mobileBind;

    private String mobileNoMD5; // 手机号MD5值
    private String idCardMD5; // 身份证MD5值
    private String realNameMD5; // 真实姓名的MD5值

    private Integer busiErrCode;
    private String busiErrDesc;

    public String getTaskInit() {
        return taskInit;
    }

    public void setTaskInit(String taskInit) {
        this.taskInit = taskInit;
    }

    private String taskInit;

    public String getMobileMd5() {
        return mobileMd5;
    }

    public void setMobileMd5(String mobileMd5) {
        this.mobileMd5 = mobileMd5;
    }

    public String getPwdflag() {
        return pwdflag;
    }

    public void setPwdflag(String pwdflag) {
        this.pwdflag = pwdflag;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLgphone() {
        return lgphone;
    }

    public void setLgphone(Integer lgphone) {
        this.lgphone = lgphone;
    }


    public Integer getState() {
        return state;
    }

    public void setState(Integer istate) {
        this.state = istate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
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

    public String getMailAddr() {
        return mailAddr;
    }

    public void setMailAddr(String mailAddr) {
        this.mailAddr = mailAddr;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public Integer getMobbindFlag() {
		return mobbindFlag;
	}

	public void setMobbindFlag(Integer mobbindFlag) {
		this.mobbindFlag = mobbindFlag;
	}

	public Integer getIsvip() {
		return isvip;
	}

	public void setIsvip(Integer isvip) {
		this.isvip = isvip;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}


	public String getWhitegrade() {
		return whitegrade;
	}

	public void setWhitegrade(String whitegrade) {
		this.whitegrade = whitegrade;
	}

	public String getUserImg() {
		return userImg;
	}

	public void setUserImg(String userImg) {
		this.userImg = userImg;
	}

	public Integer getBusiErrCode() {
        return busiErrCode;
    }

    public void setBusiErrCode(Integer busiErrCode) {
        this.busiErrCode = busiErrCode;
    }

    public String getBusiErrDesc() {
        return busiErrDesc;
    }

    public void setBusiErrDesc(String busiErrDesc) {
        this.busiErrDesc = busiErrDesc;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public Integer getMobileBind() {
        return mobileBind;
    }

    public void setMobileBind(Integer mobileBind) {
        this.mobileBind = mobileBind;
    }

	public String getDrawBankCard() {
		return drawBankCard;
	}

	public void setDrawBankCard(String drawBankCard) {
		this.drawBankCard = drawBankCard;
	}

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public Integer getLoginPhone() {
        return loginPhone;
    }

    public void setLoginPhone(Integer loginPhone) {
        this.loginPhone = loginPhone;
    }

    public String getCardMobile() {
        return cardMobile;
    }

    public void setCardMobile(String cardMobile) {
        this.cardMobile = cardMobile;
    }

    public Integer getGradeid() {
        return gradeid;
    }

    public void setGradeid(Integer gradeid) {
        this.gradeid = gradeid;
    }

    public String getMobileNoMD5() {
        return mobileNoMD5;
    }

    public void setMobileNoMD5(String mobileNoMD5) {
        this.mobileNoMD5 = mobileNoMD5;
    }

    public String getIdCardMD5() {
        return idCardMD5;
    }

    public void setIdCardMD5(String idCardMD5) {
        this.idCardMD5 = idCardMD5;
    }

    public String getRealNameMD5() {
        return realNameMD5;
    }

    public void setRealNameMD5(String realNameMD5) {
        this.realNameMD5 = realNameMD5;
    }
}

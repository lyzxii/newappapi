package bean;




import com.caiyi.lottery.tradesystem.BaseBean;

import java.util.Set;


public class UserBean extends BaseBean {

    private static final long serialVersionUID = -3486405697231875014L;

    private String realName = "";//真实姓名
    private String idCardNo = "";//身份证号码
    private String mailAddr = "";//邮件地址
    private String mobileNo = "";//手机号码

    private String bankCode = "";//银行代码
    private String bankCard = "";//银行卡号
    private String bankName = "";//银行名称
    private String drawBankCode = ""; //提款银行码(我们定义的提款银行卡Code)

    private int flag;//类型

    private String newValue = "";//新的值
    private String upwd = "";//用户登录后输入的密码
    
    private String provid = "";//省份编号
    private String cityid = "";//地市编号

    private String imNo = "";//即时通信号码



    private String rid = "";//问题编号
    private String aid = "";//答案
    private String tid = "";//交易

    private String yzm = "";//验证码
    private String rand ="";//服务端的验证码
    private String desc = "";//手机描述信息
    private Boolean phoneLoginFlag;// 是否手机登录

    private String cuserId = "";
    private String appversion = "";//版本
    private String confupwd = "";
    private String clientName;//客户端名称
    private String MemBasicVal;
    private String phoneModel; // 手机型号
    private String phoneSys; // 手机系统
    private String operator; // 运营商
    private String feedContent; //  反馈信息
    private String comeFrom; //  IP地址
    private String picone; //  图片1
    private String pictwo; //  图片2
    private String picthree; //  图片3
    private String banginginfo; //  是否绑定
    private Integer point;//用户积分
    private String viplevel;
    private String whitegrade;

    //自动跟单
    private String owner = "";//发起人
    //混合数据
    private String data;
    private String idfa;
    private String code;
    private String lstime;


    private String getPointForm;//获取积分来源
    private String hasSignDays;//已签到天数
    private String userLevel;//usercenter level

    private String userImgPath;//头像地址

    private String userInputs = "";//网站用户输入内容

    private String miTag;//小米tag

    private String normalTag;//个推tag
    private String packageName;//包名
    private String cashId; // 流水号
    private Set<String> set;

    private String temporaryId; //临时用户名 ，只用于短信验证流程，不是最终默认用户名

    private int mobileId;//标识手机号id
    private String subbankName;//支行名称

    private String idCardFrontUrl;//身份证正面url
    private String idCardBackUrl;//身份证反面url
    private String bankCardFrontUrl;//银行卡正面url
    private String bankCardBackUrl;//银行卡反面url
    private String realBankCode;
    private String bankType;
    private String cardAuthFlag;//鉴权标记


    protected int hztype = 0;  //联合登录类型值为1  支付宝用
    private long auth;
    private String backurl;

    private String agentid;

    public String getViplevel() {
        return viplevel;
    }

    public void setViplevel(String viplevel) {
        this.viplevel = viplevel;
    }

    public String getWhitegrade() {
        return whitegrade;
    }

    public void setWhitegrade(String whitegrade) {
        this.whitegrade = whitegrade;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public String getFeedContent() {
        return feedContent;
    }

    public void setFeedContent(String feedContent) {
        this.feedContent = feedContent;
    }

    @Override
    public String getComeFrom() {
        return comeFrom;
    }

    @Override
    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public int getHztype() {
        return hztype;
    }

    public void setHztype(int hztype) {
        this.hztype = hztype;
    }

    public String getTemporaryId() {
        return temporaryId;
    }

    public void setTemporaryId(String temporaryId) {
        this.temporaryId = temporaryId;
    }

    public String getMiTag() {
        return miTag;
    }

    public void setMiTag(String miTag) {
        this.miTag = miTag;
    }

    public String getNormalTag() {
        return normalTag;
    }

    public void setNormalTag(String normalTag) {
        this.normalTag = normalTag;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getSet() {
        return set;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public String getUserInputs() {
        return userInputs;
    }

    public void setUserInputs(String userInputs) {
        this.userInputs = userInputs;
    }

    public String getUpwd() {
        return upwd;
    }

    public void setUpwd(String upwd) {
        this.upwd = upwd;
    }

    private int gender ;//性别

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUserImgPath() {
        return userImgPath;
    }

    public void setUserImgPath(String userImgPath) {
        this.userImgPath = userImgPath;
    }
    private Integer winSwitch; // 中奖推送开过，1：开，0：关
    private Integer chaseSwitch; // 追号推送开关，1：开，0：关

    public String getUserLevel() {
        return userLevel;
    }

    public String getPicone() {
        return picone;
    }

    public void setPicone(String picone) {
        this.picone = picone;
    }

    public String getPictwo() {
        return pictwo;
    }

    public void setPictwo(String pictwo) {
        this.pictwo = pictwo;
    }

    public String getPicthree() {
        return picthree;
    }

    public void setPicthree(String picthree) {
        this.picthree = picthree;
    }

    public String getBanginginfo() {
        return banginginfo;
    }

    public void setBanginginfo(String banginginfo) {
        this.banginginfo = banginginfo;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getGetPointForm() {
        return getPointForm;
    }

    public void setGetPointForm(String getPointForm) {
        this.getPointForm = getPointForm;
    }

    public String getHasSignDays() {
        return hasSignDays;
    }

    public void setHasSignDays(String hasSignDays) {
        this.hasSignDays = hasSignDays;
    }



    public String getLstime() {
        return lstime;
    }

    public void setLstime(String lstime) {
        this.lstime = lstime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCuserId() {
        return cuserId;
    }

    public void setCuserId(String cuserId) {
        this.cuserId = cuserId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }


    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getImNo() {
        return imNo;
    }

    public void setImNo(String imNo) {
        this.imNo = imNo;
    }

    public String getProvid() {
        return provid;
    }

    public void setProvid(String provid) {
        this.provid = provid;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }

    public String getMailAddr() {
        return mailAddr;
    }

    public void setMailAddr(String mailAddr) {
        this.mailAddr = mailAddr;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }


    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getYzm() {
        return yzm;
    }

    public void setYzm(String yzm) {
        this.yzm = yzm;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getConfupwd() {
        return confupwd;
    }

    public void setConfupwd(String confupwd) {
        this.confupwd = confupwd;
    }

    public String getData(String encryptData) {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getWinSwitch() {
        return winSwitch;
    }

    public void setWinSwitch(Integer winSwitch) {
        this.winSwitch = winSwitch;
    }

    public Integer getChaseSwitch() {
        return chaseSwitch;
    }

    public void setChaseSwitch(Integer chaseSwitch) {
        this.chaseSwitch = chaseSwitch;
    }

	public String getMemBasicVal() {
		return MemBasicVal;
	}

	public void setMemBasicVal(String memBasicVal) {
		MemBasicVal = memBasicVal;
	}

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneSys() {
        return phoneSys;
    }

    public void setPhoneSys(String phoneSys) {
        this.phoneSys = phoneSys;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

	public String getDrawBankCode() {
		return drawBankCode;
	}

	public void setDrawBankCode(String drawBankCode) {
		this.drawBankCode = drawBankCode;
	}

    public String getCashId() {
        return cashId;
    }

    public void setCashId(String cashId) {
        this.cashId = cashId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getMobileId() {
        return mobileId;
    }

    public void setMobileId(int mobileId) {
        this.mobileId = mobileId;
    }

    public String getSubbankName() {
        return subbankName;
    }

    public void setSubbankName(String subbankName) {
        this.subbankName = subbankName;
    }

    public String getIdCardFrontUrl() {
        return idCardFrontUrl;
    }

    public void setIdCardFrontUrl(String idCardFrontUrl) {
        this.idCardFrontUrl = idCardFrontUrl;
    }

    public String getIdCardBackUrl() {
        return idCardBackUrl;
    }

    public void setIdCardBackUrl(String idCardBackUrl) {
        this.idCardBackUrl = idCardBackUrl;
    }

    public String getBankCardFrontUrl() {
        return bankCardFrontUrl;
    }

    public void setBankCardFrontUrl(String bankCardFrontUrl) {
        this.bankCardFrontUrl = bankCardFrontUrl;
    }

    public String getBankCardBackUrl() {
        return bankCardBackUrl;
    }

    public void setBankCardBackUrl(String bankCardBackUrl) {
        this.bankCardBackUrl = bankCardBackUrl;
    }

	public String getRand() {
		return rand;
	}

	public void setRand(String rand) {
		this.rand = rand;
	}

	public Boolean getPhoneLoginFlag() {
		return phoneLoginFlag;
	}

	public void setPhoneLoginFlag(Boolean phoneLoginFlag) {
		this.phoneLoginFlag = phoneLoginFlag;
	}

	public long getAuth() {
		return auth;
	}

	public void setAuth(long auth) {
		this.auth = auth;
	}

	public String getBackurl() {
		return backurl;
	}

	public void setBackurl(String backurl) {
		this.backurl = backurl;
	}

    public String getRealBankCode() {
        return realBankCode;
    }

    public void setRealBankCode(String realBankCode) {
        this.realBankCode = realBankCode;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getCardAuthFlag() {
        return cardAuthFlag;
    }

    public void setCardAuthFlag(String cardAuthFlag) {
        this.cardAuthFlag = cardAuthFlag;
    }
}

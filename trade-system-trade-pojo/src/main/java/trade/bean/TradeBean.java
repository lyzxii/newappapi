package trade.bean;

import com.caiyi.lottery.tradesystem.BaseBean;

import java.util.Map;

public class TradeBean extends BaseBean {

    private static final long serialVersionUID = 4797478681976350781L;

    private String balance = "";//用户余额
    private String bid = "";//认购编号
    private String gid = "";//游戏编号
    private String did = "";//明细编号
    private String pid = "";//期次编号
    private String zid = "";//选择场次
    private String hid = "";//合买编号
    private int bnum;//认购份数
    private String codes = "";//投注号码（文件投注的文件名）
    private String newcodes; //奖金优化使用codes
    private String endTime = "";//方按截止时间
    private int fflag;//文件标志（0 是号码 1 是文件）
    private int money;//方案总金额
    private int muli;//投注倍数
    private int oflag;//公开标志
    private int play;//玩法
    private int pnum;//保底份数
    private int tnum;//总份数
    private String name = "";//方案名称
    private String desc = "";//方案描叙
    private int type;//方案类型(0代购 1合买)
    private int wrate;//中奖提成比率
    private String cupacketid;//红包id
    private String redpacket_money;//使用红包金额
    private int zflag;//追号标志
    private String mpRemider;//慢频提示标志
    private String mpAgree;//慢频看到截止日期至开奖日期提示后统一投注
    private String guoguan = "";//过关方式
    private int upay = 0;//支付标识
    private int extendtype;    //投注扩展类型 6标识奖金优化,13表示竞彩足球-单关固赔
    private String imoneyrange = ""; //理论奖金范围
    private String iminrange = ""; //理论奖金最小值  默认为0
    private int gopaymoney = 0;
    private int xzflag;        //旋转矩阵-投注标识,0表示非旋转矩阵,1表示旋转矩阵投注
    private String ischase; //追号标志
    private String mulitys = "";//追号倍数列表
	private String payorderid = "";
	private String items;
	private String yhfs;//优化方式

    private String sessionId1 = "";
    private String sessionId2 = "";
    private String requestUrl = "";

    private String cType = "";//购买方式
    private int bdMoney = 0;//保底金额(认购)
    private int views = 0;//参与人数(认购)

    private int comboid = 0;//套餐编号
    private String find = "";//查询字符串
    private String appScheme = "";

    private int izhflag = 0;    //中奖是否停止(0 不停止 1停止 2 盈利停止)

    private int grade = 0;//等级

    // 活动投注标识
    private int activityflag;

    private String trade_isource;
    private String trade_imoney;
    private String trade_gameid;
    private String itemid;
    private String appkey;
    private String playid;
    private String tzyh;
    private String ggtype;
    private String zxcodes;
    private String ppcodes;
    private int isfollow;
    private String fuid;

    private String jType;

    private String checkor;
    private String message;
    private String expect;
    private String initems;
    private String isshow;
    private String lotid;
    private String tcbili;
    private String title;
    private String content;

    public String getExpect() {
        return expect;
    }

    public void setExpect(String expect) {
        this.expect = expect;
    }

    public String getInitems() {
        return initems;
    }

    public void setInitems(String initems) {
        this.initems = initems;
    }

    public String getIsshow() {
        return isshow;
    }

    public void setIsshow(String isshow) {
        this.isshow = isshow;
    }

    public String getLotid() {
        return lotid;
    }

    public void setLotid(String lotid) {
        this.lotid = lotid;
    }

    public String getTcbili() {
        return tcbili;
    }

    public void setTcbili(String tcbili) {
        this.tcbili = tcbili;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public String getCheckor() {
        return checkor;
    }

    public void setCheckor(String checkor) {
        this.checkor = checkor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTzyh() {
        return tzyh;
    }

    public void setTzyh(String tzyh) {
        this.tzyh = tzyh;
    }

    public String getjType() {
        return jType;
    }

    public void setjType(String jType) {
        this.jType = jType;
    }

    public int getIsfollow() {
        return isfollow;
    }

    public String getFuid() {
        return fuid;
    }

    public void setFuid(String fuid) {
        this.fuid = fuid;
    }

    public void setIsfollow(int isfollow) {
        this.isfollow = isfollow;
    }

    public String getGgtype() {
        return ggtype;
    }

    public void setGgtype(String ggtype) {
        this.ggtype = ggtype;
    }

    public String getZxcodes() {
        return zxcodes;
    }

    public void setZxcodes(String zxcodes) {
        this.zxcodes = zxcodes;
    }

    public String getPpcodes() {
        return ppcodes;
    }

    public void setPpcodes(String ppcodes) {
        this.ppcodes = ppcodes;
    }

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getActivityflag() {
        return activityflag;
    }

    public void setActivityflag(int activityflag) {
        this.activityflag = activityflag;
    }

    public int getIzhflag() {
        return izhflag;
    }

    public void setIzhflag(int izhflag) {
        this.izhflag = izhflag;
    }

    public String getAppScheme() {
        return appScheme;
    }

    public void setAppScheme(String appScheme) {
        this.appScheme = appScheme;
    }

    public int getComboid() {
        return comboid;
    }

    public void setComboid(int comboid) {
        this.comboid = comboid;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public int getBdMoney() {
        return bdMoney;
    }

    public void setBdMoney(int bdMoney) {
        this.bdMoney = bdMoney;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getcType() {
        return cType;
    }

    public void setcType(String cType) {
        this.cType = cType;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getSessionId1() {
        return sessionId1;
    }

    public void setSessionId1(String sessionId1) {
        this.sessionId1 = sessionId1;
    }

    public String getSessionId2() {
        return sessionId2;
    }

    public void setSessionId2(String sessionId2) {
        this.sessionId2 = sessionId2;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getBnum() {
        return bnum;
    }

    public void setBnum(int bnum) {
        this.bnum = bnum;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getFflag() {
        return fflag;
    }

    public void setFflag(int fflag) {
        this.fflag = fflag;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMuli() {
        return muli;
    }

    public void setMuli(int muli) {
        this.muli = muli;
    }

    public int getOflag() {
        return oflag;
    }

    public void setOflag(int oflag) {
        this.oflag = oflag;
    }

    public int getPlay() {
        return play;
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public int getPnum() {
        return pnum;
    }

    public void setPnum(int pnum) {
        this.pnum = pnum;
    }

    public int getTnum() {
        return tnum;
    }

    public void setTnum(int tnum) {
        this.tnum = tnum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getWrate() {
        return wrate;
    }

    public void setWrate(int wrate) {
        this.wrate = wrate;
    }

    public String getRedpacket_money() {
        return redpacket_money;
    }

    public void setRedpacket_money(String redpacket_money) {
        this.redpacket_money = redpacket_money;
    }

    public int getZflag() {
        return zflag;
    }

    public void setZflag(int zflag) {
        this.zflag = zflag;
    }

    public String getMpRemider() {
        return mpRemider;
    }

    public void setMpRemider(String mpRemider) {
        this.mpRemider = mpRemider;
    }

    public String getMpAgree() {
        return mpAgree;
    }

    public void setMpAgree(String mpAgree) {
        this.mpAgree = mpAgree;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCupacketid() {
        return cupacketid;
    }

    public void setCupacketid(String cupacketid) {
        this.cupacketid = cupacketid;
    }

    public String getGuoguan() {
        return guoguan;
    }

    public void setGuoguan(String guoguan) {
        this.guoguan = guoguan;
    }

    public int getUpay() {
        return upay;
    }

    public void setUpay(int upay) {
        this.upay = upay;
    }

    public int getExtendtype() {
        return extendtype;
    }

    public void setExtendtype(int extendtype) {
        this.extendtype = extendtype;
    }

    public String getImoneyrange() {
        return imoneyrange;
    }

    public void setImoneyrange(String imoneyrange) {
        this.imoneyrange = imoneyrange;
    }

    public String getIminrange() {
        return iminrange;
    }

    public void setIminrange(String iminrange) {
        this.iminrange = iminrange;
    }

    public int getGopaymoney() {
        return gopaymoney;
    }

    public void setGopaymoney(int gopaymoney) {
        this.gopaymoney = gopaymoney;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public int getXzflag() {
        return xzflag;
    }

    public void setXzflag(int xzflag) {
        this.xzflag = xzflag;
    }

    public String getIschase() {
        return ischase;
    }

    public void setIschase(String ischase) {
        this.ischase = ischase;
    }

    public String getMulitys() {
        return mulitys;
    }

    public void setMulitys(String mulitys) {
        this.mulitys = mulitys;
    }

	public String getPayorderid() {
		return payorderid;
	}

	public void setPayorderid(String payorderid) {
		this.payorderid = payorderid;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getYhfs() {
		return yhfs;
	}

	public void setYhfs(String yhfs) {
		this.yhfs = yhfs;
	}

	public String getNewcodes() {
		return newcodes;
	}

	public void setNewcodes(String newcodes) {
		this.newcodes = newcodes;
	}

    public String getTrade_isource() {
        return trade_isource;
    }

    public void setTrade_isource(String trade_isource) {
        this.trade_isource = trade_isource;
    }

    public String getTrade_imoney() {
        return trade_imoney;
    }

    public void setTrade_imoney(String trade_imoney) {
        this.trade_imoney = trade_imoney;
    }

    public String getTrade_gameid() {
        return trade_gameid;
    }

    public void setTrade_gameid(String trade_gameid) {
        this.trade_gameid = trade_gameid;
    }

    public String getPlayid() {
        return playid;
    }

    public void setPlayid(String playid) {
        this.playid = playid;
    }
}
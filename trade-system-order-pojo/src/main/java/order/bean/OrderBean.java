package order.bean;

import com.caiyi.lottery.tradesystem.BaseBean;

public class OrderBean extends BaseBean {

    private String aid = "";//是否中奖
    private String rid = "";//是否中奖
    private String pid = "";//期次编号
    private String gid = "";//游戏编号
    private String hid = "";//方案编号
    private String bid = "";//认购编号
    private String tid = "";//期次编号
    private String did = "";//明细编号
    private String find = "";//查询字符串
    private int xzflag;		//旋转矩阵-投注标识,0表示非旋转矩阵,1表示旋转矩阵投注
    private int fflag ;//文件标志（0 是号码 1 是文件）
    private Integer flag;//标志
    private String name = "";//方案名称
    private Integer offlag;//方案公开标志
    private String rversion="";//app应用版本

    private String cType;//购买方式
    private String fsort = "";//排序字段
    private String dsort = "";//排序方向
    private Integer oflag;//方案公开标志
    private String newValue = "";//新的值
    private String qtype="";//查询参数
    private String sort;
    private String guoguan;
    private String iwrate;//分享人设置的打赏比例
    private String codes;//投注号码
    private int extendType;

    private String worldCup;//是否是世界杯

    public int getExtendType() {
        return extendType;
    }

    public void setExtendType(int extendType) {
        this.extendType = extendType;
    }

    public String getWorldCup() {
        return worldCup;
    }

    public void setWorldCup(String worldCup) {
        this.worldCup = worldCup;
    }

    public String getCodes() {
        return codes;
    }

    public void setCodes(String codes) {
        this.codes = codes;
    }

    public Integer getOfflag() {
        return offlag;
    }

    public void setOfflag(Integer offlag) {
        this.offlag = offlag;
    }

    public String getRversion() {
        return rversion;
    }

    public void setRversion(String rversion) {
        this.rversion = rversion;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    @Override
    public String getGid() {
        return gid;
    }

    @Override
    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getFind() {
        return find;
    }

    public void setFind(String find) {
        this.find = find;
    }

    public int getXzflag() {
        return xzflag;
    }

    public void setXzflag(int xzflag) {
        this.xzflag = xzflag;
    }

    public int getFflag() {
        return fflag;
    }

    public void setFflag(int fflag) {
        this.fflag = fflag;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getcType() {
        return cType;
    }

    public void setcType(String cType) {
        this.cType = cType;
    }

    public String getFsort() {
        return fsort;
    }

    public void setFsort(String fsort) {
        this.fsort = fsort;
    }

    public String getDsort() {
        return dsort;
    }

    public void setDsort(String dsort) {
        this.dsort = dsort;
    }

    public Integer getOflag() {
        return oflag;
    }

    public void setOflag(Integer oflag) {
        this.oflag = oflag;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getQtype() {
        return qtype;
    }

    public void setQtype(String qtype) {
        this.qtype = qtype;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
	}
       
    public String getGuoguan() {
        return guoguan;
    }

    public void setGuoguan(String guoguan) {
        this.guoguan = guoguan;
    }

    public String getIwrate() {
        return iwrate;
    }

    public void setIwrate(String iwrate) {
        this.iwrate = iwrate;
    }
}

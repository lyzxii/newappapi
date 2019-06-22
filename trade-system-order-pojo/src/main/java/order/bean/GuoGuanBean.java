package order.bean;

import com.util.comparable.ComparableBean;

import java.io.Serializable;
import java.util.Date;

/**
 * 过关统计对象
 */
public class GuoGuanBean extends ComparableBean<GuoGuanBean> implements Serializable {
    private double bonus;// 总奖金
    private String nickID;// 昵称
    private String numInfo; // 中奖信息
    private Double betNum;// 总金额（原int类型）------------
    private int type; // 0是代购 1是合买 2 追号
    private int state; // 0是成功 1是流产
    private int file; // 0是文件 1不是文件
    private int agNum;// 银星个数
    private int auNum;// 金星个数
    private int mnums = 0;// 选择场数
    private int bnums = 0;// 命中场数
    private String gid;// 彩种
    private String gupguans = "未过关";// 过关类型
    private String projID;// 方案编号
    private Date addDate;// 发起时间(原String类型)------------
    private String cuserid; //用户惟一标识
    private int rrate; // 回报率

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public String getNickID() {
        return nickID;
    }

    public void setNickID(String nickID) {
        this.nickID = nickID;
    }

    public String getNumInfo() {
        return numInfo;
    }

    public void setNumInfo(String numInfo) {
        this.numInfo = numInfo;
    }

    public Double getBetNum() {
        return betNum;
    }

    public void setBetNum(Double betNum) {
        this.betNum = betNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }

    public int getAgNum() {
        return agNum;
    }

    public void setAgNum(int agNum) {
        this.agNum = agNum;
    }

    public int getAuNum() {
        return auNum;
    }

    public void setAuNum(int auNum) {
        this.auNum = auNum;
    }

    public int getMnums() {
        return mnums;
    }

    public void setMnums(int mnums) {
        this.mnums = mnums;
    }

    public int getBnums() {
        return bnums;
    }

    public void setBnums(int bnums) {
        this.bnums = bnums;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getGupguans() {
        return gupguans;
    }

    public void setGupguans(String gupguans) {
        this.gupguans = gupguans;
    }

    public String getProjID() {
        return projID;
    }

    public void setProjID(String projID) {
        this.projID = projID;
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getCuserid() {
        return cuserid;
    }

    public void setCuserid(String cuserid) {
        this.cuserid = cuserid;
    }

    public int getRrate() {
        return rrate;
    }

    public void setRrate(int rrate) {
        this.rrate = rrate;
    }
}

package pojo;

import java.util.Date;

/**
 * 用户代理pojo
 *
 * @author GJ
 * @create 2017-11-29 13:41
 **/
public class UserAgentPojo {
    private String cagentid;//代理商编号
    private String cagentname;//代理商名称
    private String cparentid;//上级代理商编号
    private Date ccreatedate;//添加日前
    private String isdaili;//是否代理
    private Integer iscaptain;//是否是队长
    private Integer teamid;//所属团队ID

    public String getCagentid() {
        return cagentid;
    }

    public void setCagentid(String cagentid) {
        this.cagentid = cagentid;
    }

    public String getCagentname() {
        return cagentname;
    }

    public void setCagentname(String cagentname) {
        this.cagentname = cagentname;
    }

    public String getCparentid() {
        return cparentid;
    }

    public void setCparentid(String cparentid) {
        this.cparentid = cparentid;
    }

    public Date getCcreatedate() {
        return ccreatedate;
    }

    public void setCcreatedate(Date ccreatedate) {
        this.ccreatedate = ccreatedate;
    }

    public String getIsdaili() {
        return isdaili;
    }

    public void setIsdaili(String isdaili) {
        this.isdaili = isdaili;
    }

    public Integer getIscaptain() {
        return iscaptain;
    }

    public void setIscaptain(Integer iscaptain) {
        this.iscaptain = iscaptain;
    }

    public Integer getTeamid() {
        return teamid;
    }

    public void setTeamid(Integer teamid) {
        this.teamid = teamid;
    }
}

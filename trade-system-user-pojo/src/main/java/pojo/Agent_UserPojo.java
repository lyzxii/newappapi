package pojo;

import java.io.Serializable;

/**
 * @author wxy
 * @create 2017-12-01 10:51
 **/
public class Agent_UserPojo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idCard; // 身份证号
    private String mobileNumber; // 手机号
    private Integer mobileBind; // 手机号是否绑定
    private String agentId; // 代理id
    private String agentDate; // 代理更新时间
    private String parentId; // 父id
    private Integer isAgent; // 是否代理
    private String mobilenoMD5; // 手机号MD5值
    private String idCardMD5; // 身份证MD5值

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Integer getMobileBind() {
        return mobileBind;
    }

    public void setMobileBind(Integer mobileBind) {
        this.mobileBind = mobileBind;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentDate() {
        return agentDate;
    }

    public void setAgentDate(String agentDate) {
        this.agentDate = agentDate;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getIsAgent() {
        return isAgent;
    }

    public void setIsAgent(Integer isAgent) {
        this.isAgent = isAgent;
    }

    public String getMobilenoMD5() {
        return mobilenoMD5;
    }

    public void setMobilenoMD5(String mobilenoMD5) {
        this.mobilenoMD5 = mobilenoMD5;
    }

    public String getIdCardMD5() {
        return idCardMD5;
    }

    public void setIdCardMD5(String idCardMD5) {
        this.idCardMD5 = idCardMD5;
    }
}

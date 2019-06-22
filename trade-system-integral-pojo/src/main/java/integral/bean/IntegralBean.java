package integral.bean;


import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 积分中心bean
 */
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class IntegralBean {

    private String userPhoto = "";//头像
    private String currentPoint = "";//当前积分
    private String currentLevel = "";//当前等级
    private String differ = "";//签到间隔天数
    private String signed = "";//是否签到
    private String status = "";//签到状态
    private String signDays = "";//签到天数
    private String code = "";//状态
    private String totalPoints = "";//每日获取总积分

    private String bindIdCard = "";//是否绑定身份证
    private String bindBankCard = "";//是否绑定银行卡

    private String isGetPoint = "";//二进制值
    private String task = "";//
    private String flag = "";

    private String currentExp = "";//当前经验值
    private String currentLevelExp = "";//当前等级所需经验值
    private String levelName = "";//等级称谓

    private String nextLevel = "";//下一等级
    private String nextLevelExp = "";//下一级需所需经验值

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(String nextLevel) {
        this.nextLevel = nextLevel;
    }

    public String getNextLevelExp() {
        return nextLevelExp;
    }

    public void setNextLevelExp(String nextLevelExp) {
        this.nextLevelExp = nextLevelExp;
    }

    public String getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(String currentExp) {
        this.currentExp = currentExp;
    }

    public String getCurrentLevelExp() {
        return currentLevelExp;
    }

    public void setCurrentLevelExp(String currentLevelExp) {
        this.currentLevelExp = currentLevelExp;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIsGetPoint() {
        return isGetPoint;
    }

    public void setIsGetPoint(String isGetPoint) {
        this.isGetPoint = isGetPoint;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getBindBankCard() {
        return bindBankCard;
    }

    public void setBindBankCard(String bindBankCard) {
        this.bindBankCard = bindBankCard;
    }

    public String getBindIdCard() {
        return bindIdCard;
    }

    public void setBindIdCard(String bindIdCard) {
        this.bindIdCard = bindIdCard;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiffer() {
        return differ;
    }

    public void setDiffer(String differ) {
        this.differ = differ;
    }

    public String getSigned() {
        return signed;
    }

    public void setSigned(String signed) {
        this.signed = signed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignDays() {
        return signDays;
    }

    public void setSignDays(String signDays) {
        this.signDays = signDays;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(String currentPoint) {
        this.currentPoint = currentPoint;
    }

    public String getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(String currentLevel) {
        this.currentLevel = currentLevel;
    }
}

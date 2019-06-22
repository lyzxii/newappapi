package pojo;

import java.io.Serializable;

/**
 * @author wxy
 * @create 2017-12-04 15:06
 **/
public class ActiveDataPojo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idfa; // ios标识码
    private String imei; // 设备码
    private String phoneSys; // 手机系统
    private String cityid; // 地理位置
    private String phoneModel; // 手机型号
    private String ipAddr; // ip地址
    private String appversion; // 应用版本
    private Integer source; // source值
    private String clientName; // app名
    private String operator; // 运营商

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getPhoneSys() {
        return phoneSys;
    }

    public void setPhoneSys(String phoneSys) {
        this.phoneSys = phoneSys;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}

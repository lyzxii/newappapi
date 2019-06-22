package dto;

import java.util.List;

/**
 * Created by Administrator on 2017/12/19.
 */
public class RechargeCardDTO {
    private String nickid;
    private String usersource;
    private List<String> rechargeList;

    public String getNickid() {
        return nickid;
    }

    public void setNickid(String nickid) {
        this.nickid = nickid;
    }

    public String getUsersource() {
        return usersource;
    }

    public void setUsersource(String usersource) {
        this.usersource = usersource;
    }

    public List<String> getRechargeList() {
        return rechargeList;
    }

    public void setRechargeList(List<String> rechargeList) {
        this.rechargeList = rechargeList;
    }
}

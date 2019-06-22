package pay.pojo;

import lombok.Data;

import java.util.Date;

/**
 * Created by XQH on 2017/12/26.
 */
@Data
public class UserLogPojo {
    /**记录编号*/
    private String irecid;
    /**用户昵称*/
    private String cnickid;
    /**操作时间*/
    private Date cadddate;
    /**操作内容*/
    private String cmemo;
    /**ip地址*/
    private String cipaddr;
    /**操作标题*/
    private String ctype;
    public UserLogPojo(){}
    public UserLogPojo(String cnickid,String cmemo,String cipaddr,String ctype){
        this.cnickid = cnickid;
        this.cmemo = cmemo;
        this.cipaddr = cipaddr;
        this.ctype = ctype;
    }
}

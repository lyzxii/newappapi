package activity.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 分享人pojo
 *
 * @author GJ
 * @create 2018-04-23 19:59
 **/
@Data
public class ForcastShareUserPojo {
    private String nickid;
    private String appclient;
    private String source;
    private String ipaddress;
    private String mobiletype;
    private Date addtime;

}

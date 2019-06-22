package pojo;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-21 10:53
 **/
@Data
public class WxUserBindPojo {
    private String openid;
    private String uid;
    private String mobileNo;
    private String unionid;
    private String returnInfo;
    private String mobilenoMD5;
}

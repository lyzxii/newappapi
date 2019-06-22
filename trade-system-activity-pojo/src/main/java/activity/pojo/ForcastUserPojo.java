package activity.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author wxy
 * @create 2018-04-23 18:46
 **/
@Data
public class ForcastUserPojo {
    private String forcastContent;
    private String openId;
    private String nickName;
    private String mobile;
    private String userImgUrl;
    private Integer isRegist;
    private Long matchId;
    private String addTime;
    private String itemId;
    private int isNew;
    private int isLogin;
}

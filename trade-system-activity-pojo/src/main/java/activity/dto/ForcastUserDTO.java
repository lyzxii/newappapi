package activity.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-04-23 18:28
 **/
@Data
public class ForcastUserDTO {
    private String forcastcontent;
    private String forcast;
    private String nickName;
    private String userImgUrl;
    private String addTime;
    // 0 成功，1 未登录，2 非新用户，3 过期
    private Integer state;
    private String result;
}

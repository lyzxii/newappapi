package order.pojo;

import lombok.Data;

/**
 * Created by tiankun on 2018/1/2.
 */
@Data
public class ShareUserListPojo {

    private String usertype;//0-普通分享用户，1-大神用户
    private Integer allnum;//分享单数
    private Integer rednum;//分享红单数
    private String uptype;//上榜类型
    private Integer rank;//综合排名
    private String cnickid;

}

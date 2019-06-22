package order.pojo;

import lombok.Data;

/**
 * Created by tiankun on 2018/1/8.
 */
@Data
public class ShareGodUserPojo {
    public int ranking;
    public String nickid;
    public String userphoto;
    public String uptype;
    public int projallnum;
    public int projrednum;
    public String shootrate;
    public double buymonry;
    public double winmoney;
    public String returnrate;
    public int unfinishnum;
    public int followusers;
    public double followmoney;
    public int continurednum;

    public double rewardall;//累计打赏
    public double overrate;//战胜比例

    private String newuserphoto;
    private String realuid;
}

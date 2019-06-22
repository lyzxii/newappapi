package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-11 13:54
 **/
@Data
public class HotBetsDTO extends BaseHomePageDTO{
    private String haveTitle;
    private String titleAdSrc;
    private String titleIOSsrc;
    private String titleIOSsrc2X;
    private String titleIOSsrc3X;
    private String titleIOSsrc6;
    private String titleIOSsrc6p;
    private String titleHeightAD;
    private String titleHeightIOS;
    private String gid;
    private String lotteryName;
    private String pools;
    private String endTime;
    private String imgSrc;
}

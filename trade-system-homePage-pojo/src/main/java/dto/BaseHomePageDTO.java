package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-10 14:57
 **/
@Data
public class BaseHomePageDTO {
    private String order;
    private String evid;
    private String src;
    private String link;
    private String linkUrl;
    private String newsrc;
    private String adsrc;
    private String adlink;
    private String iOSsrc;
    private String iOSsrc2X;
    private String iOSsrc3X;
    private String iOSlink;
    private String newadsrc;
    private String newadlink;
    private String newiOSsrc;
    private String newiOSlink;
    private String haveSpace; // 和下面的块是否有间距，0没有，1有
}

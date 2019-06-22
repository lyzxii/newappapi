package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-03-19 10:22
 **/
@Data
public class TopicListDTO extends BaseHomePageDTO {
    private String adImgHeight;
    private String adImgWidth;
    private String iOSImgHeight;
    private String haveTitle;
    private String titleAdSrc;
    private String titleIOSsrc;
    private String titleIOSsrc2X;
    private String titleIOSsrc3X;
    private String titleHeightAD;
    private String titleHeightIOS;
    private List<BannerDTO> topic;
}

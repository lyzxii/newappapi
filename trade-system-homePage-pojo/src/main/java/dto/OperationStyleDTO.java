package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-10 19:28
 **/
@Data
public class OperationStyleDTO extends BaseHomePageDTO{
    private String style;
    private String adImgHeight;
    private String adImgWidth;
    private String iOSImgHeight;
    private String haveTitle;
    private String titleAdSrc;
    private String titleIOSsrc;
    private String titleIOSsrc2X;
    private String titleIOSsrc3X;
    private String titleIOSsrc6;
    private String titleIOSsrc6p;
    private String titleHeightAD;
    private String titleHeightIOS;
    private List<OperationDTO> operation;
}

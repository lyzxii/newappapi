package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-11 19:54
 **/
@Data
public class FocusEventListDTO extends BaseHomePageDTO {
    private String gameType;
    private String haveTitle;
    private String titleAdSrc;
    private String titleIOSsrc;
    private String titleIOSsrc2X;
    private String titleIOSsrc3X;
    private String titleIOSsrc6;
    private String titleIOSsrc6p;
    private String titleHeightAD;
    private String titleHeightIOS;
    private List<FocusEventDTO> focusEvent;
}

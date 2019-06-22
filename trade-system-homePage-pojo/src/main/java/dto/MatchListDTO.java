package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-18 11:32
 **/
@Data
public class MatchListDTO {
    private String order;
    private String haveSpace;
    private String evid;
    private String haveTitle;
    private String titleAdSrc;
    private String titleIOSsrc;
    private String titleIOSsrc2X;
    private String titleIOSsrc3X;
    private String titleIOSsrc6;
    private String titleIOSsrc6p;
    private String titleHeightAD;
    private String titleHeightIOS;
    private String path;
    private String logoUrl;
    private List<MatchDTO> matchs;
}

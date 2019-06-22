package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-11 19:47
 **/
@Data
public class FocusEventDTO extends BaseHomePageDTO{
    private String leagueName;
    private String jcName;
    private String homeName;
    private String awayName;
    private String homeId;
    private String awayId;
    private String homeLogo;
    private String awayLogo;
    private String endTime;
    private String desc;
    private String itemId;
    private String endDesc;
}

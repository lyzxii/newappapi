package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-10 14:54
 **/
@Data
public class QuickEnterDTO extends BaseHomePageDTO{
    private String title;
    private String titleColor;
    private String desc;
    private String descColor;
    private String descBgColor;
    private String color;

}

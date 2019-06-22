package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-15 15:56
 **/
@Data
public class DiscoverContentDTO {
    private String itemId;
    private String title;
    private String subTitle;
    private String content;
    private String logoUrl;
    private String newlogoUrl;
    private String link;
    private String newlink;
    private String evid;
    private String flag;
    private String time;
}

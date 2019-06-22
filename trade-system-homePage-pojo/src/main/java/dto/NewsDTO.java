package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-15 20:33
 **/
@Data
public class NewsDTO {
    private String articleId;
    private String articleUrl;
    private String title;
    private String description;
    private Integer praiseNum;
    private String publishDate;
    private String gid;
    private String litpic;
}

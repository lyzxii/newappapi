package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-18 14:57
 **/
@Data
public class HotlineDTO {
    private String desc;
    private String phoneNo;
    private String url;
    private String newurl;
    private String stopsale;
    private String inweb;
    private List<BaseConfigDTO> hotLine;
}

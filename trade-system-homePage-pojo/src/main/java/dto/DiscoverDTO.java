package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-15 15:54
 **/
@Data
public class DiscoverDTO {
    private String group;
    private String userNames;
    private String contentAPI;
    private List<DiscoverContentDTO> discoverContent;
}

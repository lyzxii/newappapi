package data.dto;

import lombok.Data;

import java.util.List;

/**
 * @author GJ
 * @create 2018-01-18 11:42
 **/
@Data
public class FollowMatchInfoDTO {
    private String title;
    private String time;
    private String logourl;
    private List<MatchDetailDTO> matchdetail;

}

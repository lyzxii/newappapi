package data.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-02-03 17:11
 **/
@Data
public class RoundInfoDTO {
    private String url;
    private String newUrl;
    List<MatchDTO> matches;
}

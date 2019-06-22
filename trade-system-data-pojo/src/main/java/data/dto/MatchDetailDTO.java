package data.dto;

import lombok.Data;

import java.util.List;

/**
 * @author GJ
 * @create 2018-01-18 14:04
 **/
@Data
public class MatchDetailDTO {
    private String desc;
    private List<MatchDTO> matchs;
}

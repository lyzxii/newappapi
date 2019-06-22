package data.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-02-03 10:55
 **/
@Data
public class QueryTeamDTO {
    private String leagueName;
    private String homeTeamName;
    private String awayTeamName;
    private String homeTeamId;
    private String awayTeamId;
    private String homeTeamScore;
    private String awayTeamScore;
    private String matchTime;
}

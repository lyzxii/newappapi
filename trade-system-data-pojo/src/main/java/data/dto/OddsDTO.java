package data.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-28 11:53
 **/
@Data
public class OddsDTO {
    private String homeTeamName;
    private String guestTeamName;
    private String score;
    private String halfScore;
    private String jcsg;
    private String jcodds;
    private String ocOldOdds;
    private String ocNewOdds;
    private String avgOld;
    private String avgNew;
    private String profit;
    private String volume;
}

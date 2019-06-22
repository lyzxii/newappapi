package order.dto;

import lombok.Data;

@Data
public class BetRecordDTO {
    private String gid = "";
    private String betType = ""; //投注类型,1是普通投注,2追号
    private String pid = "";
    private double bonus = 0d;
    private int result = 0;
}

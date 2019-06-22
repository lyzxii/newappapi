package trade.dto;


import lombok.Data;

@Data
public class SelectMatchDto {
    private String zxitemid="";
    private SelectMatch match=new SelectMatch();
}

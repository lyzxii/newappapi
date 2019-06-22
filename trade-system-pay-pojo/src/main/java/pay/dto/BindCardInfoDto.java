package pay.dto;


import lombok.Data;

@Data
public class BindCardInfoDto {
    private String channel="";
    private String userPayId="";
    private String userId="";
}

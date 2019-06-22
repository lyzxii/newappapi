package trade.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PrepareCastDto {
    private Object usermoney="";//账户余额
    private Object redpacket="";//红包
    private Object buycount="";
    private Object ioswebpay="";//ios白名单
    private Object szc;
}

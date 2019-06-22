package trade.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class JcCastDto {
    private String projid;
    private String gid;
    private String balance;
}

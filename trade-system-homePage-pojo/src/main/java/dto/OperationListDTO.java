package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-22 11:41
 **/
@Data
public class OperationListDTO extends BaseHomePageDTO {
    private List<OperationStyleDTO> OperationStyle;
}

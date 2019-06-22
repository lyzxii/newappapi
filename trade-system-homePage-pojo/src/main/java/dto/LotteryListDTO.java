package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-10 20:47
 **/
@Data
public class LotteryListDTO extends BaseHomePageDTO{
    private String showNum;
    private List<LotteryDTO> lottery;
}

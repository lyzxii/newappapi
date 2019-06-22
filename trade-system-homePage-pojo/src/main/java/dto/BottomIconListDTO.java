package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-10 17:31
 **/
@Data
public class BottomIconListDTO extends BaseHomePageDTO{
    private List<BottomIconDTO> icon;
}

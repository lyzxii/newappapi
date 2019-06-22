package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-09 20:01
 **/
@Data
public class BannerListDTO extends BaseHomePageDTO{
    private List<BannerDTO> banner;
}

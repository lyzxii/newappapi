package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-18 11:14
 **/
@Data
public class ThemDTO {
    private List<BannerDTO> banners;
    private List<QuickEnterDTO> quickEnters;
    private MatchListDTO matches;
    private List<LotteryDTO> lotteries;
}

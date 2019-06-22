package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-10 20:49
 **/
@Data
public class LotteryDTO extends BaseHomePageDTO{
    private String evid;
    private String gid;
    private String pid;
    private String lotteryName;
    private String desc;
    private String imgUrl;
    private String imgUrl2X;
    private String imgUrl3X;
    private String newimgUrl;
    private String showSale;
    private String addAward;
    private String style;
    private String streamer;
    private String day;
    private String pools;
    private String remainMatch;
    private String tryCode;
    private String awardTime;
    private String code;
    private String endTime;
    private String number;
    private List<LotteryDTO> childLottery;
}

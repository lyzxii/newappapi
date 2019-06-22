package dto;

import lombok.Data;

import java.util.List;


/**
 * @author wxy
 * @create 2018-01-09 18:41
 **/
@Data
public class LotteryHomePageDTO {
    private BannerListDTO bannerList;
    private NoticeListDTO noticeList;
    private List<OperationStyleDTO> operationList;
    private List<BottomIconDTO> bottomIconList;
    private LotteryListDTO lotteryList;
    private HotBetsDTO hotBets;
    private FocusEventListDTO focusEventList;
    private TopicListDTO worldCupTopic;
    private MatchListDTO worldCupMatches;
    private FloatImgDTO floatImg;
    private FloatImgDTO floatWindow;
}

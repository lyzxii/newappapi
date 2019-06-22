package dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-18 14:38
 **/
@Data
public class StartUpDTO {
    private String godItemControl;
    private String readMode;
    private String whiteGrade;
    private String rechargeControl;
    private String banWord;
    private String imageCaptchaControl;
    private String worldCupFlag;
    private String leShanFlag;

    private LotteryReminderDTO lotteryReminder;
    private HotlineDTO hotline;
    private RechargeMessageDTO rechargeMessage;
    private StartImgDTO startImg;
    private RecordBottomHintDTO recordBottomHint;
    private ReviewStateDTO reviewState;
    private RedpacketRemindDTO rpRemind;
}

package activity.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-03 15:04
 **/
@Data
public class ThirdGameGiftDTO {
    private String cdKey; // 兑换码
    private String giftType; // 礼包类型
    private String gameId; // 游戏编号
    private String gameName; // 游戏名
    private String supplier; // 代理商名
    private String startTime; // 游戏合作开始时间
    private String endTime; // 游戏合作结束时间
    private String describe; // 简介
    private String photoUrl; // 图片地址
    private String downloadUrl; // 下载地址
    private String thridUrl; // 第三方游戏地址
    private Integer state; // 游戏显示状态 0-不显示 1-显示
    private Integer prio; // 优先级
    private String memo; // 备注
}

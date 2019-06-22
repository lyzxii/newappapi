package activity.pojo;

import lombok.Data;

/**
 * tb_third_ginfo表的实体类
 * @author wxy
 * @create 2018-01-03 11:28
 **/
@Data
public class ThirdGameInfoPojo {
    private String gameId; // 游戏编号
    private String gameName; // 游戏名
    private String supplier; // 游戏提供商
    private String startTime; // 游戏合作开始时间
    private String endTiem; // 游戏合作结束时间
    private String describe; // 游戏介绍
    private String photoUrl; // 游戏图片url
    private String downloadUrl; // 游戏下载url
    private String thridUrl; // 第三方游戏界面url
    private Integer state; // 游戏显示状态 0-不显示 1-显示
    private Integer prio; // 优先级
    private String memo; // 备注

}

package dto;

import lombok.Data;

/**
 * 用户个人中心DTO
 *
 * @author GJ
 * @create 2017-12-04 11:44
 **/
@Data
public class UserPersonalInfoDTO {
    private String status;//审核状态
    private String userpoint;//积分
    private String userImg;//用户头像地址
    private String flag;//是否可以修改
    private String grade;//用户等级
    private String desc;//描述
    private String whitelistGrade;//白名单等级
}

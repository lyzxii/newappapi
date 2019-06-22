package activity.dto;

import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-28 15:34
 **/
@Data
public class GetBonusDTO {
    private String status; // 参与状态
    private String bonus; // 我的奖金
    private String totalBonus; // 我的累计总奖金
    private String balance; // 账户余额
}

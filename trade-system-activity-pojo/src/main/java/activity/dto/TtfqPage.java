package activity.dto;

import com.caiyi.lottery.tradesystem.bean.Page;
import lombok.Data;

/**
 * @author wxy
 * @create 2017-12-29 14:12
 **/
@Data
public class TtfqPage<T> extends Page<T> {
    private Double totalBonus; // 奖金总数
    private Double totalMyBonus; // 我的奖金总数
    private Integer isLogin; // 是否登录1 登录，0 未登录
}

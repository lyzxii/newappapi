package bean;

import com.caiyi.lottery.tradesystem.BaseBean;
import lombok.Data;

/**
 * @author wxy
 * @create 2018-01-15 10:07
 **/
@Data
public class HomePageBean extends BaseBean {
    private String name;
    private String themeType; // 主题版本选择 1-足球 2-篮球 3-福利彩票
    private String rversion;
    private String channelId;
}

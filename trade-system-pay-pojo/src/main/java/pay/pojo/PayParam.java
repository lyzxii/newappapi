package pay.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 *支付参数
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PayParam {
    private String prepayUrl;//支付url
    private Object prepayContent;//返回下单信息
    private String prepayHtml;//返回html网页
    private String prepayString;//字符串
    private String sessionToken;//盛付通api
    private String tradeNo;//第三方订单号
}

package pay.bean;

import lombok.Data;

@Data
public class PaySftBean extends PayBean {
    private String sftOrderNo;
    private String orderCreateTime;
    private String amount;
    private String desc;
}

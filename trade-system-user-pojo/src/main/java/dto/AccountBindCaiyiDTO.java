package dto;

import lombok.Data;

/**
 * 支付宝手机号绑定彩亿账号
 *
 * @author GJ
 * @create 2017-12-15 11:52
 **/
@Data
public class AccountBindCaiyiDTO {
    /**
     * 彩亿账号名
     */
    private String uid;
    /**
     * 不同产品logo
     */
    private String logo;

}

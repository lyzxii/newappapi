package pay.pojo;

import lombok.Data;

/**
 * Created by XQH on 2017/12/21.
 */
@Data
public class BankBranchPojo {
    /**银行名称*/
    private String bankname;
    /**银行编码*/
    private String bankcode;
    /**省份*/
    private String pro;
    /**城市*/
    private String city;
    /**银行支行*/
    private String bankbranch;
    /**联行号*/
    private String banknum;
    /**银行对应的自定义编码*/
    private String bcode;
}

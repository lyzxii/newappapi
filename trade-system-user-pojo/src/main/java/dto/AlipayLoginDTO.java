package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 支付宝快捷登入DTO
 *
 * @author GJ
 * @create 2017-12-14 16:57
 **/
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AlipayLoginDTO {

    /**
     * 签名原串
     */
    private String authInfo = "";
    /**
     * 签名串
     */
    private String sign = "";
    /**
     *  签名加密类型
     */
    private String signType = "";
    /**
     * 是否首次登入，0是，1不是
     */
    private String isfirst = "";
    /**
     * 是否需要用户输入手机号码，需要为1，不需要为0
     */
    private String inputmobileno = "";
    /**
     * 支付宝的手机号
     */
    private String alipayMobileno = "";
    /**
     * 支付宝唯一id
     */
    private String aliypayid = "";
    /**
     * 支付宝token
     */
    private String alipayaccesstoken = "";

    /**
     * 对应彩亿用户名
     */
    private String uid = "";

    /**
     * 对应彩亿appid
     */
    private String appid = "";
    /**
     * 对应彩亿accesstoken
     */
    private String accesstoken = "";
    /**
     * 	 登录密码是否是默认密码
     * 	 0       是默认密码
     *   1       非默认密码
     */
    private String pwdflag = "";

    /**
     * 支付宝手机号绑定的彩亿账号
     */
    private List<AccountBindCaiyiDTO> accounts;

    private String logo = "";

    private String nickid = "";


    public String getNickid() {
        return nickid;
    }

    public void setNickid(String nickid) {
        this.nickid = nickid;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

}

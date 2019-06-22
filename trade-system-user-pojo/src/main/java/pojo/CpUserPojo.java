package pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wxy
 * @create 2017-12-01 10:24
 **/
@Data
public class CpUserPojo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uid; // 用户名
    private Integer flag; // 验证标志
    private String verificationCode; // 验证码
    private Integer busiErrCode; // 返回码
    private String busiErrDesc; // 返回描述
    private String mobileNo;//手机号
    private String yzm;//验证码
    private String ipAddr;//ip地址
    private String source;//source值
    private String temporaryId;//临时用户名
    /**
     *  0 新 1老
     */
    private Integer isNew;

    /**
     * 支付宝唯一id
     */
    private String aliypayid;
    /**
     * 联合登录类型
     */
    private Integer type;

    /**
     * 用户类型
     */
    private Integer usertype = 0;
    /**
     * 密码
     */
    private String pwd;
    /**
     * 来源
     */
    private String comeFrom;
    /**
     * 邮箱
     */
    private String mailAddr;
    /**
     * 域名
     */
    private String host;

    /**
     * 合作网站用户id
     */
    private String userId;
    /**
     * 合作类型
     *
     */
    private Integer hztype = 0;
    /**
     * 用户唯一标识uid生成
     */
    private String cuserId;

    private String partner;
    /**
     * 手机imei
     */
    private String imei;
    /**
     * 密钥
     */
    private String privateKey;

    /**
     * 微信账户unionid
     */
    private String unionid;

    /**
     * 微信账户唯一标识
     */
    private String openid;

}

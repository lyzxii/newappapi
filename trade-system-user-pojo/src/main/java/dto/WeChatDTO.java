package dto;

import lombok.Data;

import java.util.List;

@Data
public class WeChatDTO {
    private String appid; // 9188token登录校验信息
    private String accesstoken; // 9188token登录校验信息
    private String uid; // 9188账户昵称
    private String pwdflag; // 密码是否是默认密码

    private String WeChatToken; // 微信授权access token
    private String unionid; // 微信账户unionid
    private String openid; // 微信账户唯一标识
    private String bind; //  微信账户是否绑定9188账户-1    未绑定（只有唯一值 -1）
    private String nickname; // 微信账户昵称
    private String headimgurl; // 微信头像url
    private List<AccountBindCaiyiDTO> accounts;
}

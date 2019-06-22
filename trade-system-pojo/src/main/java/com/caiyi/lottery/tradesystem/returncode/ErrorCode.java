package com.caiyi.lottery.tradesystem.returncode;

/**
 *  客户端返回系统code定义
 *	0:成功        大于0:业务code  小于0:系统异常  从-1000开始
 *  用户中心:    10000~19999       -10000~-19999
 *  支付中心:    20000~29999       -20000~-29999
 *  交易中心:    30000~39999       -30000~-39999
 *  活动中心:    40000~49999       -40000~-49999
 *  会员积分中心: 50000~59999       -50000~-59999
 *  安全中心：         60000~69999       -60000~-69999
 *  红包中心:    70000~79999       -70000~-79999
 *  订单中心:    80000~89999       -80000~-89999
 *  命名规则：中心名称_业务名称_错误描述
 *  例:USER_LOGIN_SQL_ERROR  用户登录sql执行错误
 */
public class ErrorCode {
	/*************************************用户中心START*********************************************/
	public static final String USER_MYLOTTERY_REMOTE_ERROR  = "-10000";  //我的彩票远程调用失败
	public static final String USER_SAVEACTIVE_REMOTE_ERROR = "-10001"; // 保存激活数据远程调用失败
	public static final String USER_SAVEACTIVE_PROCESS_ERROR = "-10002"; // 保存激活数据处理过程异常
	public static final String USER_SAVEACTIVE_SAVE_ERROR = "-10003"; // 数据保存失败
	public static final String USER_UPLOADPHOTO_REMOTE_ERROR = "-10004"; // 用户上传头像远程调用异常
	public static final String USER_UPLOADPHOTO_FILE_ERROR = "-10005"; // 上传文件错误
	public static final String USER_UPLOADPHOTO_PROCESS_ERROR = "-10006"; // 上传头像处理过程异常
	public static final String USER_BINDCHECK_REMOTE_ERROR = "-10007"; // 绑定验证服务异常
	public static final String USER_BINDCHECK_PROCESS_ERROR = "-10008"; // 绑定验证处理异常
	public static final String USER_TOKEN_QUERY_ERROR  = "-10009";       //token查询出错
	public static final String USER_WINANDCHASENUMBERSWITCH_REMOTE_ERROR = "-10010"; // 中奖追号推送开关更新服务异常
	public static final String USER_WINANDCHASENUMBERSWITCH_PROCESS_ERROR = "-10011"; // 中奖追号推送开关设置异常
	public static final String USER_LOGINOUT_REMOTE_ERROR = "-10012"; // 退出服务异常
	public static final String USER_LOGINOUT_PROCESS_ERROR = "-10013"; // 退出处理异常
	public static final String USER_LOGIN_ERROR = "-10014";//登入异常
	public static final String USER_CHECKMSM_ERROR = "-10015";//验证手机短信号码失败
	public static final String USER_NAMEPHONENO_MATCH_ERROR = "-10016";//用户名和手机号不匹配
	public static final String USER_NAMEPHONENO_MATCH_EXCEPTION = "-10017";//用户名手机号匹配异常
	public static final String USER_FORGETPWD_EXCEPTION = "-10018";//用户忘记密码异常
	public static final String USER_USERNAMENULL_ERROR = "-10019";//用户名为空
	public static final String USER_PHONENOFORMAT_ERROR = "-10020";//手机号格式不正确
	public static final String USER_PHONENONULL_ERROR = "-10021";//手机号号为空
	public static final String USER_SMSNULL_ERROR = "-10022";//短信验证码为空
	public static final String USER_PICAUTH_ERROR = "-10024";//图形验证码错误
	public static final String USER_REGIST_PHONENOREPET_ERROR= "-10025";//注册手机号已重复
	public static final String USER_SURPASSMAXBINDNO_ERROR= "-10026";//手机号绑定超过最大值
	public static final String USER_CHECKPHONESTATUS_EXCEPTION= "-10027";//检测手机号绑定状态出错
	public static final String USER_APPAGENT_EXCEPTION= "-10028";//查询APP代理商异常
	public static final String USER_CHECKSMS_ERROR = "-10029"; //发送短信校验异常
	public static final String USER_PICAUTHNULL_ERROR = "-10030";//图形验证码为空
	public static final String USER_SIGNFAIL_ERROR = "-10031";//验签失败
	public static final String USER_REQUESTSUPASS_ERROR = "-10032";//请求频繁
	public static final String USER_AUTHCREATEFAIL_ERROR = "-10033";//验证码生成失败
	public static final String USER_SENDSMS_ERROR = "-10034";//发送手机验证码异常
	public static final String USER_IMEINULL_ERROR = "-10035";//imei不存在
	public static final String USER_CHECKIMEI_ERROR = "-10036";//检测手机Imei出错
	public static final String USER_CHECKIDFA_ERROR = "-10037";//检测手机idfa出错
	public static final String USER_SENDSMSSUPASS_ERROR = "-10038";//发送短信超过次数
	public static final String USER_AUTHCODE_ERROR = "-10039";//验证码错误
	public static final String USER_SOURCERANGE_ERROR = "-10040";//错误的用户来源值
	public static final String USER_REGISTFAIL_ERROR = "-10041";//注册失败
	public static final String USER_NAME_FOMAT_ERROR = "-10042";	//用户名格式错误
	public static final String USER_NAMELENGTH_ERROR = "-10043";	//用户名长度不合法
	public static final String USER_REPET_ERROR = "-10044";	//用户名已存在
	public static final String USER_GETIDFA_ERROR = "-10045";	//idfa获取不正确
	public static final String USER_IDFANULL_ERROR = "-10046";	//idfa不存在
	public static final String USER_UPDATE_DRAWCARD_ERROR = "-10047"; // 更新提款银行卡错误
	public static final String USER_ADDLOG_ERROR = "-10048"; // 添加用户日志错误
	public static final String USER_QUERY_SAFEINFO_ERROR = "-10049"; // 调用安全中心查询错误
	public static final String USER_CHECKAPPLYELIGIBLE_REMOTE_ERROR = "-10050"; // 提交银行卡申请验证服务调用异常
	public static final String USER_CHECKAPPLYELIGIBLE_PROCESS_ERROR = "-10051"; // 提交银行卡申请验证服务异常
	public static final String USER_DIFFPWD_ERROR = "-10052";	//两次输入的密码不一致
	public static final String USER_PWDREPET_ERROR = "-10053";	//密码重复
	public static final String USER_SETPWDFAIL_ERROR = "-10054";  //设置密码失败
	public static final String USER_ADD_SAFEINFO_ERROR = "-10055"; // 调用安全中心添加错误
	public static final String USER_QUERYUSERPWD_ERROR = "-10056"; // 查询用户密码出错
	public static final String USER_QUERYACCOUNT_REMOTE_ERROR = "-10057"; // 账户明细查询服务调用失败
	public static final String USER_QUERY_USER_WHITEGRADE_ERROR = "-10058"; // 查询用户白名单错误
	public static final String USER_QUERYACCOUNT_PROCESS_ERROR = "-10059"; // 账户明细查询失败
	public static final String USER_QUERY_USER_INFO_ERROR = "-10060"; // 查询用户基础信息错误
	public static final String USER_PARAMDECODE_ERROR = "-10061"; // 参数解密失败
	public static final String USER_REGISTEXCEPTION_ERROR = "-10062";//注册异常
	public static final String USER_PWDLENGTH_ERROR = "-10063";//密码长度不正确
	public static final String USER_USERNAMEBLANK_ERROR = "-10064";//用户昵称不能包含空格
	public static final String USER_USERNAMEILLEGAL_ERROR = "-10065";	//用户名不合法
	public static final String USER_IPFORBID_ERROR = "-10066";	//ip禁止
	public static final String USER_MOBILEREGISTER_FAIL="-10067";//签名验证不通过
	public static final String USER_CHANGE_MOBILE_CHECK_REMOTE_ERROR = "-10068"; // 更换手机号服务调用失败
	public static final String USER_CHANGE_MOBILE_CHECK_PROCESS_ERROR = "-10069"; // 更换手机号服务调用失败
	public static final String USER_GET_SAFEINFO_ERROR="-10070";//获取安全中心用户表解密数据错误
	public static final String USER_IDBANK_BINDING_REMOTE_ERROR = "-10071"; // 查询身份证银行卡绑定信息异常
	public static final String USER_IDBANK_BINDING_PROCESS_ERROR = "-10072"; // 查询身份证银行卡绑定信息异常
	public static final String USER_IDBANK_BINDING_SEARCH_FAIL = "-10073"; // 查询身份证银行卡绑定信息异常
	public static final String USER_INPUTPWD_ERROR = "-10071";//输入密码错误
	public static final String USER_FORGETPWD_OLDVERSION_ERROR="-10090";//检测到您的客户端版本较低，无法获取到短信验证码，请升级客户端！
	public static final String USER_FORGETPWD_NOSUPPORT_EDIT_ERROR="-10091";//不支持的找回密码方式
	public static final String USER_FORGETPWD_USERNOEXIST_ERROR="-10092";//忘记密码：用户名不存在
	public static final String USER_FORGETPWD_MOBILENOBIND_ERROR="-10093";//忘记密码：手机号未绑定
	public static final String USER_FORGETPWD_MOBILENOREGISTER_ERROR="-10094";//忘记密码：您输入的手机号还未注册哦
	public static final String USER_CAIYI_ACCOUNT_BINDING_QUERY_MOBILENO_ERROR="-10096";
	public static final String USER_CAIYI_ACCOUNT_BINDING_QUERY_SMS_ERROR="-10097";
	public static final String USER_CARD_EXIST_ERROR="-10095";//此账户已经绑定过身份证和真实姓名
	public static final String USER_INVOKE_MATCH_FAIL = "-10098";//回调激活匹配失败
	public static final String USER_YYDBBINDING_QUERY_ERROR="-10099";
	public static final String USER_ALLY_WECHAT_PARAM_ERROR = "-10101"; // 微信授权参数未传递
	public static final String USER_ALLY_WECHAT_GETINFO_FAIL = "-10102"; // 获取微信用户信息失败
	public static final String USER_ALLY_WECHAT_REGIST_FAIL = "-10103"; // 获取微信用户信息失败
	public static final String USER_ALLY_PWD_EXIST_FAIL ="-10104";//您已经设置过首次密码,不可重复设置
	public static final String USER_ALLY_WECHAT_GETACCESSTOKEN_FAIL = "-10105"; // 获取微信accessToken失败
	public static final String USER_ALLY_WECHAT_GETUSERINFO_FAIL = "-10106"; // 获取微信用户个人信息失败
	public static final String USER_ALLY_WECHAT_LOGIN_FAIL = "-10107"; // 登陆失败
	public static final String USER_ALLY_WECHAT_QUERY_BIND_FAIL = "-10108"; // 查询手机号码绑定状态失败
	public static final String USER_AUTOSTART_FAIL = "-10109";//自动开启失败
	public static final String USER_ALLY_WECHAT_PARAM_CHECK_FAIL = "-10110";
	public static final String USER_ALLY_WECHAT_VERIFY_FAIL = "-10111"; // 校验短信验证码失败
	public static final String USER_ALLY_WECHAT_MOBILE_BIND_FAIL = "-10112"; // 绑定手机号至彩亿账户出错
	public static final String USER_POINT_UPDATE_ERROR="-10113";//更新用户积分失败、
	public static final String USER_BANKNO_INPUT_ERROR = "-10114";//银行卡号输入错误
	public static final String USER_BANKNO_CHECK_ERROR = "-10115";//银行卡号错误
	public static final String USER_SENDMSG_NULLORFAIL = "-10116";//报文发送失败或应答消息为空
	public static final String USER_CARDAUTH_CHECK_ERROR = "-10117";//卡鉴权出错
	public static final String USER_CARDAUTH_CHECK_FAIL = "-10118";//卡鉴权失败
	public static final String USER_CREATEMD5_FAIL = "-10119";//生成md5序列号失败
	public static final String USER_IMEICACHE_OVERFOLLOW = "-10120";//手机设备号缓存已存在且已超过次数
	public static final String USER_FOUND_PWD_FIND_CNT = "-10121";//手机设备号缓存已存在且已超过次数
	public static final String USER_ID_BINDING_OUTLIMIT_ERROR  = "-18001";//您的身份证绑定账户超过限制，如有疑问请联系客服

	public static final String USER_REMOTE_INVOKE_ERROR = "-11000";//用户中心远程调用失败
	
	/*************************************用户中心END***********************************************/



	/*************************************支付中心START*********************************************/
	public static final String PAY_RECHARGE_CREATE_ORDER_FAIL  = "-20000";  //创建充值支付订单失败
	public static final String PAY_RECHARGE_CENTER_SAFE_FAIL  = "-20001";  //保存用户充值卡信息至安全中心失败
	public static final String PAY_RECHARGE_SAVE_PAY_SAFEKEY_FAIL  = "-20002";  //更新安全中心对应的key至用户充值表失败
	public static final String PAY_RECHARGE_QUERY_SAFE_ALLCARD_FAIL  = "-20003";  //获取安全中心用户所有银行卡失败
	public static final String PAY_RECHARGE_SAFE_CARD_FAIL  = "-20004";  //保存用户充值银行卡失败
	public static final String PAY_RECHARGE_CLASS_REFLECT_FAIL  = "-20005";  //充值渠道反射实例化失败
	public static final String PAY_RECHARGE_ADDMONEY_EXCEPTION  = "-20006";  //用户充值异常
	public static final String PAY_RECHARGE_UPDATE_RECH_MOBILE_EXCEPTION  = "-20007";  //更新充值用户手机异常
	public static final String PAY_RECHARGE_QUERY_BANKID_ERROR="-20008";//根据订单号查询bankid异常
	public static final String PAY_RECHARGE_ADDMONEYSUC_ERROR="-20009";//调用addmoney_suc存储过程异常
	public static final String PAY_UPDATE_DEALID_ERROR="-20010";//更新第三方单号异常
	public static final String PAY_RECHARGE_UPDATE_RECH_CARD_EXCEPTION  = "-20011";  //更新充值银行卡信息失败

	public static final String PAY_REMOTE_INVOKE_ERROR = "-21000";//支付中心远程调用失败
	/*************************************支付中心END***********************************************/


	/*************************************交易中心START*********************************************/
	public static final String TRADE_PCAST_ERROR = "-30001"; //数字彩投注异常
	public static final String TRADE_JCAST_ERROR = "-30002"; //竞技彩投注异常
	public static final String TRADE_CHECK_ZQ_CODE_ERROR = "-30003"; //检测竞彩足球code值异常
	public static final String TRADE_QUERYPROJ_ERROR = "-30004"; //查询方案信息异常
	public static final String TRADE_CHECK_FOLLOWBUY_ONEMIN_ERROR = "-30005"; //检测是否一分钟内重复下单失败
	public static final String TRADE_CAST_ITEM_MATCH_ERROR = "-30006"; //神单跟买投注选项不正确
	public static final String TRADE_GODORDER_CAST_ERROR = "-30007"; //神单跟买投注异常

	public static final String TRADE_REMOTE_INVOKE_ERROR = "-31000";//支付中心远程调用失败
	/*************************************交易中心END***********************************************/


	/*************************************活动中心START*********************************************/
	public static final String ACTIVITY_TTFQ_NOT_PROJID = "-40001"; // 非法访问,未传入方案编号
	public static final String ACTIVITY_TTFQ_GET_BONUS_FAIL = "-40002"; // 天天分钱用户领奖失败
	public static final String ACTIVITY_TTFQ_JOIN_FAIL = "-40003"; // 参与天天分钱失败
	public static final String ACTIVITY_THIRD_GAME_PARAM_ERROR = "-40004"; // 参数错误
	public static final String ACTIVITY_THIRD_GAME_GET_INFO_ERROR = "-40005"; // 获取游戏信息失败
	public static final String ACTIVITY_THIRD_GAME_RECORD_FAIL = "-40006"; // 记录登录数据失败
	public static final String ACTIVITY_FORCAST_QUERY_DETAIL_FAIL = "-40007"; // 查询详细记录失败

	public static final String ACTIVITY_REMOTE_INVOKE_ERROR = "-41000";//活动中心远程调用失败
	/*************************************活动中心END***********************************************/


	/*************************************会员积分中心START*********************************************/
	public static final String INTEGRAL_EXGOOD_USERPOINT_ERROR="-50001";//积分商城兑换扣除积分错误
	public static final String INTEGRAL_EXGOOD_SYS_ERROR="-50002";//积分商城兑换物品系统未知内部错误
	public static final String INTEGRAL_POINTS_DRAW_SYS_ERROR="-50003";//积分抽奖系统内部错误

	public static final String INTEGRAL_REMOTE_INVOKE_ERROR = "-51000";//积分中心远程调用失败
	/*************************************会员积分中心END***********************************************/


	/*************************************安全中心START*********************************************/
	public static final String SAFE_REMOTE_INVOKE_ERROR = "-61000";//订单中心远程调用失败
	/*************************************安全中心END***********************************************/


	/*************************************红包中心START*********************************************/
	public static final String REDPACKET_REMOTE_INVOKE_ERROR = "-71000";//订单中心远程调用失败
	/*************************************红包中心END***********************************************/

	/*************************************订单中心START*********************************************/
	public static final String ORDER_AFFILIATION_FAIL = "-80000";//归属查询追号记录所属用户昵称失败
	public static final String ORDER_GETXML_FAIL = "-80001";//获取xml内容失败
	public static final String ORDER_PARAMETER_ERROR = "-80002";//传入参数不正确
	public static final String ORDER_GETCURRENTISSUE_FAIL = "-80003";//获取当前期次失败

	public static final String ORDER_REMOTE_INVOKE_ERROR = "-81000";//订单中心远程调用失败
	/*************************************订单中心END***********************************************/



	/*************************************缓存中心START***********************************************/
	public static final String CACHE_EXCEPTION_ERROR = "-9501";// 缓存服务异常
	public static final String CACHE_PARAM_KEY_NULL_ERROR = "-9502";// 传入参数键为空
	public static final String CACHE_PARAM_VALUE_NULL_ERROR = "-9503";// 传入参数值为空
	public static final String CACHE_SERVER_KEY_NULL_ERROR = "-9504";// 缓存服务器中键不存在
	public static final String CACHE_SERVER_VALUE_NULL_ERROR = "-9505";// 缓存服务器中键对应的值不存在

	public static final String CACHE_REMOTE_INVOKE_ERROR = "-91000";//缓存中心远程调用失败
	/*************************************缓存中心END***********************************************/

	/*************************************数据中心START***********************************************/
	public static final String DATA_REMOTE_INVOKE_ERROR = "-101000";//缓存中心远程调用失败
	/*************************************数据中心END***********************************************/
}

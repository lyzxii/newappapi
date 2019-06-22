package com.caiyi.lottery.tradesystem.returncode;


/**
 *  客户端返回业务code定义
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
 *  例:USER_LOGIN_PASSWORD_ERROR  用户登录密码错误
 */


public class BusiCode {
	//成功
	public static final String SUCCESS = "0";
	//通用失败
	public static final String FAIL = "-1";
	//未查到数据
	public static final String NOT_EXIST = "1";
	
	/*************************************用户中心(10000 ~ 19999)START*********************************************/
	public static final String USER_LOGIN_PASSWORD_ERROR  = "10000";  //用户登录密码错误
	public static final String USER_ACTIVEDATA_DIVIDE_DATA_EXIST = "10001"; // 设备信息已存在
	public static final String USER_UPLOADPHOTO_AUDITING = "10002"; // 头像审核中
	public static final String USER_UPLOADPHOTO_DURING_15DAYS = "10003"; // 十五日之内仅可修改一次头像
	public static final String USER_UPLOADPHOTO_MORE_THEN_THREE = "10004"; // 三次审核未通过，修改头像功能暂不可用
	public static final String USER_UPLOADPHOTO_IN_FIVE_SECONDS = "10005"; // 用户5秒内头像重复上传
	public static final String USER_BINDCHECK_FLAG_ERROR = "10006"; // 绑定类型不支持
	public static final String USER_TOKEN_UNFIND = "10007";           //未查询到TOKEN信息
	public static final String USER_PWD_CHANGE = "10008";             //用户密码已修改
	public static final String USER_FORBID_ACCT = "10009";            //用户账户已禁用
	public static final String USER_TOKEN_AUTH_FAIL = "10010";        //TOKEN验证失败
	public static final String USER_TOKEN_DISABLE = "10011";          //TOKEN已注销
	public static final String USER_WINANDCHASENUMBERSWITCH_PARAM_NULL = "10012"; // 开关设置参数不可全为空
	public static final String USER_WINANDCHASENUMBERSWITCH_SAVE_ERROR = "10013"; // 开关设置参数保存出错
	public static final String USER_LOGINOUT_FAIL = "10014"; // 退出失败
	public static final String USER_UNLOGIN = "10015"; // 用户未登录
	public static final String USER_BANKCODE_QUERY_ERROR = "10016"; // 用户银行卡真实bankcode查询失败
	public static final String USER_BANKCARD_ERROR = "10017"; // 用户银行卡不正确
	public static final String USER_MOBILE_ERROR = "10018"; // 用户手机号错误
	public static final String USER_BANKCARD_AUTH_ERROR = "10019"; // 银行卡鉴权错误
	public static final String USER_BANKCARD_SUBBANK_ERROR = "10020"; // 银行卡支行错误
	public static final String USER_BANKCARD_LOCATION_ERROR = "10021"; // 提款银行卡所属未知错误
	public static final String USER_DRAWCARD_CODE_ERROR = "10022"; // 提款银行卡code错误
	public static final String USER_ID_BINDING_FAIL_ERROR = "10023";  //绑定身份证失败
	public static final String USER_LOGIN_NICKORPWD_ILLEGAl="10024"; //用户名或密码不合法
	public static final String USER_LOGIN_NAME_NOTEXIST="10025";//用户不存在
	public static final String USER_LOGIN_BINDLOTNAME="10026";//同一号码绑定多个账号
	public static final String USER_LOGIN_ALIPAYUSER="10027";//支付宝用户
	public static final String USER_REGISTER_VERIFICATION_EMPTY="10028";//验证码已过期或者不存在
	public static final String USER_CHECKAPPLYELIGIBLE_APPLY_DENDING = "10029"; // 已经有待审核的申请
	public static final String USER_CHECKAPPLYELIGIBLE_APPLY_MORETHEN3_INDAY = "10030"; // 每天最多只能申请3次
	public static final String USER_CHECKAPPLYELIGIBLE_APPLY_IN15DAYS = "10031"; // 每15天只能有一条待审核和已审核通过的变更申请
	public static final String USER_UPLOADPHOTO_FAIL = "10032"; // 头像上传失败
	public static final String USER_UPLOADPHOTO_NULL = "10033"; // 文件为空
	public static final String USER_PHONEREGIST_EXIST = "10034"; // 手机号已经注册
	public static final String USER_TOKEN_ERROR="10035";//查询token信息出错
	public static final String USER_TOKEN_EXPIRE="10036";//token过期
	public static final String USER_REGISTER_GT_MOBILE="10037";//该手机号已经注册多个账号,请更换手机号
	public static final String USER_LOGIN_PARAM_ERROR="10038";//登入参数不正确
	public static final String USER_QUERYACCOUNT_NOT_QUERY = "10039"; // 触屏不显示账户收支明细,购彩明细,派奖明细,返款明细
	public static final String USER_QUERYACCOUNT_UNDEFINE_FLAG = "10040"; // 未知的查询类型
	public static final String USER_QUERYACCOUNT_NODATA = "10041"; // 暂无数据
	public static final String USER_MOBILEREGISTER_LIMIT="10042";//同一IP地址绑定已超过每天限制次数(1000次)/同一手机号绑定已超过每天限制次数(3次)
	public static final String USER_UPLOADPHOTO_FORMAT_ERROR = "10043"; // 文件格式错误
	public static final String USER_BINDCHECK_CHECK_FAIL = "10044"; // 绑定验证失败
	public static final String USER_CHANGE_MOBILE_CHECK_FORMAT_OLD_ERROR = "10045"; // 旧手机号格式错误
	public static final String USER_CHANGE_MOBILE_CHECK_FORMAT_NEW_ERROR = "10046"; // 新手机号格式错误
	public static final String USER_CHANGE_MOBILE_CHECK_SAME = "10047"; // 新旧手机号相同
	public static final String USER_CHANGE_MOBILE_CHECK_MORE = "10048"; // 新手机号已绑定过多账户,请重新选择手机号
	public static final String USER_CHANGE_MOBILE_CHECK_GET_FAIL = "10049"; // 获取原手机号失败
	public static final String USER_CHANGE_MOBILE_CHECK_FAIL = "10050"; // 原手机号验证失败
	public static final String USER_CHANGE_MOBILE_CHECK_NONE = "10051"; // 未查询到您的原有手机号
	public static final String USER_COMMIT_AUTH = "10052"; //已存在待审核的申请
	public static final String USER_EXCEEDSUPPLY_AUTH = "10053"; //超过申请限制
	public static final String USER_ALTERSUPPLY_AUTH = "10054"; //超过修改限制
	public static final String USER_SUBBANK_NULL = "10055";//支行名称为空
	public static final String USER_APPLY_FAIL = "10056";//申请失败
	public static final String USER_ALLY_ALIYCODE_NULL = "10057";//支付宝授权码为空
	public static final String USER_ALLY_TYPE_ERROR = "10058";//快登类型错误
	public static final String USER_IDBANK_BINDING_EXIST_APPLY = "10059"; // 有待审核的申请，不能重复申请
	public static final String USER_IDBANK_BINDING_NOBINDING = "10060"; // 未绑定银行卡
	public static final String USER_ALLY_FIRSTLOGIN = "10061";//首次登陆，请输入用户的手机号码
	public static final String USER_ALLY_NOTFIRSTLOGIN = "10062";//非首次登录，请输入手机号码
	public static final String USER_ALLY_CHECK_SUCCESS = "10063";//检测支付宝授权绑定彩亿账号成功
	public static final String USER_ALLY_ALIYID_NULL = "10064";//支付宝唯一id为空
	public static final String USER_PARAM_NULL = "10065";//获取参数为空
	public static final String USER_INVOKE_REPEAT = "10066";//重复匹配idfa或ip信息
	public static final String USER_INVOKE_MATCH_SUCCESS = "10067";//回调激活匹配成功
	public static final String USER_INVOKE_MATCH_NOFOUND = "10068";//没有找到匹配
	public static final String USER_ALLY_WECHAT_UID_ERROR = "10069"; // 微信用户输入的昵称不正确
	public static final String USER_ALLY_WECHAT_PWD_ERROR = "10070"; // 微信用户输入的登录密码不正确
	public static final String USER_ALLY_WECHAT_PHONE_ERROR = "10071"; // 用户输入的手机号不正确
	public static final String USER_ALLY_WEBCHAR_OPENID_ERROR = "10072"; // 微信openid不能为空
	public static final String USER_ALLY_WECHAT_UNIOID_ERROR = "10073"; // 微信unionid不能为空
	public static final String USER_ALLY_WECHAT_USERNAME_EXIST = "10074"; // 用户名已经存在
	public static final String USER_ALLY_BIND_FAIL = "10075";//绑定失败
	public static final String USER_ALLY_WECHAT_NOT_BIND = "10076"; // 未绑定
	public static final String USER_ALLY_WECHAT_LOGIN_FAIL = "10077"; // 微信登录失败
	public static final String USER_ALLY_WECHAT_MOBILE_BIND_YES = "10078"; // 已绑定手机号
	public static final String USER_ALLY_WECHAT_MOBILE_BIND_NO = "10079"; // 未绑定手机号
	public static final String USER_OCCUPY = "10080";//占用
	public static final String USER_ALLY_WECHAT_BIND_FAIL = "10081"; // 微信关注ID与9188账户ID绑定失败
	public static final String USER_CHECK_UNKNOW = "10082";//未知检查类型
	public static final String USER_SIGNSMS_NULL = "10083";//验签为空
	public static final String USER_SIGNSMS_REAPET = "10084";//验签组合重复
	public static final String USER_ALLY_WECHAT_UID_MOBILE_NOT_MATCH = "10085"; // 用户名与手机号不匹配
	public static final String USER_ALLY_WECHAT_UID_PWD_NOT_MATCH = "10086";  // 用户名或密码不正确
	public static final String USER_ALLY_WECHAT_TYPE_NOT_MATCH = "10087"; // 用户类型不匹配
	public static final String USER_ALLY_WECHAT_USER_NOT_EXIT = "10088"; // 用户名不存在
	public static final String USER_ALLY_WECHAT_USER_EXIT = "10089"; // 用户名不存在
	public static final String USER_MOBILENO_NULL = "10090";//手机号为空
	public static final String USER_NAME_NULL = "10091";//用户名为空
	public static final String USER_BANKCARD_NULL = "10092";//银行卡为空
	public static final String USER_DATA_MISMATCH = "10093";//数据不匹配
	public static final String USER_REGISTER_YZM_20MIN = "10093"; // 短信验证码校验出错，请稍后重试
	public static final String USER_REGISTER_REPEAT_1MIN = "10094"; // 账户已注册成功，请直接登录
	public static final String USER_REALNANMEORIDCARD_NULL = "10095";//用户名或身份证不存在


	/*************************************用户中心END***********************************************/
	
	/*************************************支付中心(20000 ~ 29999)START*********************************************/
	public static final String PAY_RECHARGE_CONFIG_ERROR  = "20000";  //渠道配置错误
	public static final String PAY_RECHARGE_CHANNEL_CLOSE  = "20001";  //该渠道已关闭
	public static final String PAY_RECHARGE_NEED_IDCARD  = "20002";  //充值需要绑定身份证
	public static final String PAY_RECHARGE_NOT_FIND  = "20003";  //未找到相应的充值渠道
	public static final String PAY_RECHARGE_AMONEY_AMOUNT_ERROR  = "20004";  //充值金额错误
	public static final String PAY_RECHARGE_HANDMONEY_ERROR  = "20005";  //手续费金额错误
	public static final String PAY_RECHARGE_APPLYID_ERROR  = "20006";  //充值订单号错误
	public static final String PAY_RECHARGE_APPLYDATE_ERROR  = "20007";  //充值日期错误
	public static final String PAY_RECHARGE_CLASS_CONFIG_ERROR  = "20008";  //充值渠道类名配置错误
	public static final String PAY_RECHARGE_CHANNEL_MATCH_ERROR  = "20009";  //客户端上传channel和配置文件channel不匹配
	public static final String PAY_RECHARGE_CARDNO_ERROR  = "20010";  //银行卡号错误
	public static final String PAY_RECHARGE_MOBILE_ERROR  = "20011";  //银行卡手机号错误
	public static final String PAY_RECHARGE_CARD_SHOW  = "20012";  //用户充值银行卡已显示
	public static final String PAY_RECHARGE_CARD_SIMILAR  = "20013";  //存在前六后四相同中间不同的银行卡
	public static final String PAY_RECHARGE_REQUEST_FAIL  = "20014";  //请求三方支付请求失败
	public static final String PAY_RECHARGE_ADDMONEY_FAIL  = "20015";  //请求三方支付请求失败
	public static final String PAY_RECHARGE_SIGN_ERROR = "20016"; //验签校验失败
	public static final String PAY_RECHARGE_QUERY_BANKID_NULL_APPLYID="20017";//查询bankid传入订单号为空
	public static final String PAY_RECHARGE_QUERY_BANKID_NO="20018";//根据订单号没有查询到bankid
	public static final String PAY_RECHARGE_QUERY_USER_ID="20019";//用户信息失败
	public static final String PAY_RECHARGE_PARAM_ERROR = "20020";//充值参数错误
	public static final String PAY_RECHARGE_NO_USEFUL_CHANNEL = "20021";//没有可用渠道
	public static final String PAY_RECHARGE_OUT_MONEY_LIMIT="20022";//充值金额超过限制
	public static final String PAY_RECHARGE_NOT_EXIST_APPLYID="20023";//不存在该订单号
	public static final String PAY_RECHARGE_WRONG_BUSI_CODE="20024";//验证码错误，对应原来 10
	public static final String PAY_RECHARGE_WRONG_BUSI_UNREPAY="20025";//不重新支付，对应原来 20
	public static final String PAY_RECHARGE_WRONG_BUSI_REPAY="20026";//重新支付，，对应原来 30
	
	/*************************************支付中心END***********************************************/

	/*************************************交易中心(30000 ~ 39999)START*********************************************/
	public static final String TRADE_PARAM_NULL = "30000";    //参数为空
 	public static final String TRADE_BAN_ACTIVITY = "30001";  //禁止交易活动
 	public static final String TRADE_ERROR_CODE = "30002";  //投注号码错误
	public static final String TRADE_MP_CAST_NEXT_PID = "30003";  //慢频投注提示预约下一期
	public static final String TRADE_PARAM_ERROR_CHECK = "30004";  //参数校验错误
	public static final String TRADE_NOT_FIND_USER = "30005";  //未查询到用户信息
	public static final String TRADE_OUT_OF_ENDTIME = "30006";  //投注按单式投注截止时间截止
	public static final String TRADE_CAST_MONEY_NOT_MATCH = "30007";  //投注金额不匹配
	public static final String TRADE_KP_PROJNUM_OUT_OF_LIMIT = "30008";  //快频彩种方案条数超过限制
	public static final String TRADE_SINGLE_MONEY_OUT_OF_LIMIT = "30009";  //单注金额超过限制
	public static final String TRADE_GAME_NOT_SUPPORT = "30010";  //不支持该类彩种
	public static final String TRADE_NOT_CAST_REPEAT = "30011";  //30s内不允许重复投注
	public static final String TRADE_PERIOD_NOTSURE = "30012";   //对阵期次比赛未确定
	public static final String TRADE_MONEY_OUT_OF_LIMIT = "30010";  //方案投注金额超过限制
	public static final String TRADE_UPAY_FORBID = "30011";  //禁止订单支付
	public static final String TRADE_CAST_FILE_NOT_FIND = "30012";  //用户投注文件未找到
	public static final String TRADE_SINGLE_TICKET_NUM_OUT_OF_LIMIT = "30013";  //单倍票数超过限制
	public static final String TRADE_CAST_MULI_ERROR = "30014";  //投注格式倍数异常
	public static final String TRADE_CAST_FORMAT_ERROR = "30015";  //投注格式异常
	public static final String TRADE_PLAY_NOT_SUPPORT = "30016";  //该玩法暂不支持
	public static final String TRADE_UPLOADFILE_CAST_OUT_OF_LIMIT = "30017"; //上传文件注数超过限制
	public static final String TRADE_UPLOADFILE_MONEY_NOT_MATCH = "30018"; //上传文件中检测到注数与实际金额不相符
	public static final String TRADE_UPLOADFILE_CAST_ERROR = "30019"; //上传文件格式错误
	public static final String TRADE_OPTIMIZE_FILE_SAVE_FAIL = "30020"; //奖金优化文件存储失败
	public static final String TRADE_CANNOT_GET_MATCH_TIME = "30021"; //无法获得方案截止时间
	public static final String TRADE_FOLLOWBUY_SELFORDER = "30022"; //跟买自己的神单
	public static final String TRADE_ORDER_NO_CAST = "30023"; //方案未出票
	public static final String TRADE_GODORDER_OUT_OF_ENDTIME = "30024"; //神单跟买已达上限
	public static final String TRADE_FOLLOWBUY_AGAIN_IN_ONE_MIN = "30025"; //神单跟买一分钟内重复下单

	public static final String TRADE_EMPTY_GID_AND_PID = "30026";//期次或彩种编号不能为空,原来-2
	public static final String TRADE_NOT_EXITS_PID = "30027";//未找到符合条件的彩种信息，原来114
	public static final String TRADE_CURRENT_PID_DISABLE = "30028";// aa期已截至，是否从bb期开始追号，原来111
	public static final String TRADE_AUTO_CHECK_PID = "30029";//智能追号期次检测出错，原来113
	public static final String TRADE_PID_PAUSE_SALE = "30030";//系统升级中，暂停销售，原来7
	public static final String TRADE_PID_CANNOT_ZHUIHAO = "30031";//系统升级中，暂不能追号，原来4
	public static final String TRADE_KP_OVERTOOP_ITEMS_LIMIT = "30032";//快频彩种方案条数不能够超过500条!,原来13
	public static final String TRADE_KP_OVERTOOP_SINGLE_DAY_MONEY_LIMIT = "30033";//单注金额不能超过2万!,原来12
	public static final String TRADE_LOW_VERSION_CLIENT = "30034";//版本低，原来5
	public static final String TRADE_UNSALE_IN_SPRING_FESTIVAL = "30035";//春节期间休市，原来2111
	public static final String TRADE_BALANCE_IS_NOT_ENOUGH = "30036";//余额不足，原来2
	public static final String TRADE_QICI_EXCEPTION = "30037";//期次异常，原来3
//	public static final String TRADE_UNKNOWN_EXCEPTION = "30038";//其它异常，原来4

	/*************************************交易中心END***********************************************/
	
	
	/*************************************活动中心(40000 ~ 49999)START*********************************************/
	public static final String ACTIVITY_CHECK_REDPACKET_NOT_FIT = "40001"; // 不满足活动条件
	public static final String ACTIVITY_CHECK_REDPACKET_NOT_AGENT = "40002"; // 该渠道不参与活动
	public static final String ACTIVITY_CHECK_REDPACKET_NOT_NEW = "40003"; // 不是新用户
	public static final String ACTIVITY_CHECK_REDPACKET_NOT_BIND_MOBILE = "40004"; // 未绑定手机号
	public static final String ACTIVITY_CHECK_REDPACKET_NOT_BIND_IDCARD = "40005"; // 未绑定身份证号
	public static final String ACTIVITY_CHECK_REDPACKET_NOT_NEW_MOBILE_IDCARD = "40006"; // 手机号和身份证检测不是新用户
	public static final String ACTIVITY_CHECK_REDPACKET_PAY_LOWER20 = "40007"; // 单笔充值订单金额小于20元
	public static final String ACTIVITY_CHECK_REDPACKET_PAY_UNACCOUNT = "40008"; // 未充值或者充值未到账,请等待
	public static final String ACTIVITY_TTFQ_NOT_BIND_IDCARD = "40009"; // 绑定身份证后才能参与该活动
	public static final String ACTIVITY_TTFQ_QUERY_FAIL = "40010";  // 方案详情查询失败
	public static final String ACTIVITY_TTFQ_UNBIND_IDCARD = "40011"; // 未绑定身份证
	public static final String ACTIVITY_TTFQ_OUTOFDATE = "40012"; // 方案已过期，请参加下一期
	public static final String ACTIVITY_TTFQ_HAVEN_JOIN = "40013"; // 不能重复参与哦
	public static final String ACTIVITY_FORCAST_GET_TEAMID_FAIL = "40014"; // 获取球队id失败
	public static final String ACTIVITY_FORCAST_UID_EMPTY = "40015"; // 预测拉新活动，用户名为空
	/*************************************活动中心END***********************************************/
	
	
	/*************************************会员积分中心(50000 ~ 59999)START*********************************************/
	public static final String INTEGRAL_EXGOOD_STATUS_HAVE_EXCHANGED="50000";//积分商城此物品用户已经兑换
	public static final String INTEGRAL_EXGOOD_STATUS_NO_LEFT="50001";//积分商城此物品已经兑换完
	public static final String INTEGRAL_EXGOOD_STATUS_NOT_ENOUGH_POINT="50002";//积分商城兑换物品积分不足
	public static final String INTEGRAL_EXGOOD_STATUS_CAN_BEEXCHANGED="50003";//积分商城此物品用户可以兑换
	public static final String INTEGRAL_EXGOOD_STATUS_SUCCESS="50004";//积分商城兑换物品成功
	public static final String INTEGRAL_POINTS_DRAW_NO_LEFT_CNT="500005";//积分商城抽奖次数已用完
	public static final String INTEGRAL_POINTS_DRAW_NO_ENOUGH_POINT="500006";//积分商城抽奖次数已用完
	public static final String INTEGRAL_POINTS_DRAW_SUCCESS="500007";//积分商城抽奖次数已用完
	/*************************************会员积分中心END***********************************************/
	
	
	/*************************************安全中心START*********************************************/
	
	/*************************************安全中心END***********************************************/
	
	
	/*************************************红包中心START*********************************************/
	public static final String REDPACKET_NOTFOUND_RP = "70000"; //未找到用户相关红包
	public static final String REDPACKET_RP_CANNOT_USE = "70001"; //红包不能使用
	/*************************************红包中心END***********************************************/

	/************************************订单中心START*********************************************/
	public static final String ORDER_SCHEME_NOT_EXITS="80000";//方案不存在
	public static final String ORDER_PARAMETER_ERROR="80001";//输入参数不正确
	public static final String ORDER_NO_TICKET_DETAIL="80002";//该彩种无出票明细
	public static final String ORDER_GENMAI_NO_CHECK="80003";//该方案为跟买方案,暂不能查看
	public static final String ORDER_ERR_DAIGOU_VIEW="80004";//抱歉，该方案是代购方案，您不是该方案的发起人，不能查看。
	public static final String ORDER_NO_RIGHT="80005";//出票明细无权查看
	public static final String ORDER_NO_TICKET_OUT="80006";//尚未出票，请稍候
	public static final String ORDER_NOT_CHECK_SP="80007";//该彩种不支持查看出票sp值
	public static final String ORDER_HISTORY_NO_CHECK="80008";//出票明细为新功能,历史方案不支持查看
	public static final String ORDER_GUOGUAN_NO_CREATE="80009";//过关文件尚未生成，请稍候
	public static final String ORDER_REPEAT_OPERATION = "80010";//重复操作
	public static final String ORDER_DATA_MISMATCHING = "80011";//数据不匹配
	public static final String ORDER_UNFINISH_OPERATION = "80012";//操作未完成
	public static final String ORDER_PARAM_NULL = "80013";//参数为空
	public static final String ORDER_INVALID_RECODE = "80014";//无效记录
	public static final String ORDER_BIN_OPERATION = "80015";//禁止操作
	public static final String ORDER_FINISH_OPERATION = "80016";//操作已完成
	public static final String ORDER_FANGAN_NOEMPTY = "80017";//方案详情编号不可为空
	public static final String ORDER_FANGAN_NOEXIST = "80018";//未查询到神单信息
	public static final String ORDER_NONSUPPORT_LOTTER = "80019";//不支持的彩种
	public static final String ORDER_NODATA = "80020";//无数据
	public static final String ORDRE_UNAUTHORIZED_ACCESS = "80021";//权限未开放
	public static final String ORDRE_MATRIX = "80022";//非旋转矩阵方式投注
	public static final String ORDRE_UNSUPPORT_LOTTERYTYPE = "80023";//不支持彩种
	
	/**************************************订单中心END***********************************************/

	/************************************首页中心START*********************************************/
	public static final String ACTIVITY_BAN = "90001"; // 活动禁止
	/**************************************首页中心END***********************************************/

	/************************************数据资料中心START*********************************************/
	public static final String MATCH_FOLLOW = "100001";
	public static final String MATCH_UNFOLLOW = "100002";
	/**************************************数据资料中心END***********************************************/

}

package trade.bean;

public class TradeErrCode {
	
	
	public final static int ERR_CHECK = 1000;
	public final static int ERR_CALL_SP = 1001;
	public final static int ERR_EXCEPTION = 1002;
	
	  
	public final static int ERR_USER_NOT_EXITS = 2000;
	public final static int ERR_DAIGOU_VIEW = 2001;
	
	
	
	public final static String getErrDesc(int errCode) {
		String errDesc = "未知错误[" + errCode + "]";
		switch ( errCode ) {
		case ERR_CHECK: {
			errDesc = "验证参数失败";
			break ;
		}
		case ERR_CALL_SP: {
			errDesc = "调用存储过程失败";
			break ;
		}		
		case ERR_EXCEPTION: {
			errDesc = "调用出现异常";
			break ;
		}
		
		case ERR_USER_NOT_EXITS: {
			errDesc = "用户不存在";
			break ;
		}
		 
		case ERR_DAIGOU_VIEW:{
			errDesc = "抱歉，该方案是代购方案，您不是该方案的发起人，不能查看。";
			break;
		}
		default: {
			errDesc = "未知错误[" + errCode + "]";
			break ;
		}
		}
		return errDesc ;
	}
}
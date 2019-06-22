package trade.util;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.util.CheckUtil;

import trade.bean.TradeBean;

public class TradeUtil {
	
	public final static int CAST_HM = 1;
	public final static int CAST_BUY = 2;
	public final static int CAST_ZH = 3;
	public final static int CANCEL_HM = 4;
	public final static int CANCEL_BUY = 5;
	public final static int CANCEL_ZH = 6;
	public final static int UPLOAD = 7;
	
	public static boolean check(int flag, TradeBean bean) {
		if(GameContains.canNotUse(bean.getGid())){
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
			bean.setBusiErrDesc("不支持的彩种");
			return false;
		}
		if(flag == CAST_HM){
			if(!(GameContains.isFootball(bean.getGid()) || GameContains.isBasket(bean.getGid()) || GameContains.isGYJ(bean.getGid()))){
				if(CheckUtil.isNullString(bean.getPid())){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
					bean.setBusiErrDesc("期号指定不明确");
					return false;
				}
			}
			if(bean.getBnum() < 0){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("认购份数不符合要求");
				return false;
			}
			
			if(bean.getBnum() > bean.getTnum()){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("认购份数不能超过总份数");
				return false;
			}
			
			if(bean.getBnum() + bean.getPnum() > bean.getTnum()){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("购买份数和保底份数不能超过总份数");
				return false;
			}
			
			if(bean.getType() == 0 || bean.getType() == 3){
				if(CheckUtil.isNullString(bean.getCodes())){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
					bean.setBusiErrDesc("代购必须有投注号码");
					return false;
				}
			}else{
				if(bean.getMoney() / bean.getTnum() != 1){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
					bean.setBusiErrDesc("每份必须是1元");
					return false;
				}

				if(bean.getBnum() > 0 && (bean.getBnum() * 100.0 / bean.getTnum() < 5)){
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
					bean.setBusiErrDesc("自购比率必须超过5%");
					return false;
				}
			}
			
			if(bean.getWrate() < 0 || bean.getWrate() > 10){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("提成比率不能超过10%");
				return false;
			}
			
		} else if(flag == CAST_BUY){
			if(CheckUtil.isNullString(bean.getGid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("彩种指定不明确");
				return false;
			}
			if(bean.getBnum() <= 0){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("认购份数不符合要求");
				return false;
			}
		} else if(flag == CAST_ZH){
			if(CheckUtil.isNullString(bean.getGid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("彩种指定不明确");
				return false;
			}
			if(bean.getMoney() <= 0){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("追号金额不符合要求");
				return false;
			}
		} else if(flag == CANCEL_HM){
			if(CheckUtil.isNullString(bean.getGid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("彩种指定不明确");
				return false;
			}
			if(CheckUtil.isNullString(bean.getHid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("撤销方案指定不明");
				return false;
			}
		} else if(flag == CANCEL_BUY){
			if(CheckUtil.isNullString(bean.getGid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("彩种指定不明确");
				return false;
			}
			if(CheckUtil.isNullString(bean.getBid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("撤销认购指定不明");
				return false;
			}
		} else if(flag == CANCEL_ZH){
			if(CheckUtil.isNullString(bean.getGid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("彩种指定不明确");
				return false;
			}
			if(CheckUtil.isNullString(bean.getZid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("撤销追号指定不明");
				return false;
			}
		} else if(flag == UPLOAD) {
			if(CheckUtil.isNullString(bean.getGid())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("彩种指定不明确");
				return false;
			}
			if(CheckUtil.isNullString(bean.getCodes())){
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_ERROR_CHECK));
				bean.setBusiErrDesc("上传号码不能为空");
				return false;
			}
		}
		return true;
	}	
}

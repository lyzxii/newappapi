package com.caiyi.lottery.tradesystem.tradecenter.util.code;

import com.caipiao.plugin.helper.CodeFormatException;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.bj.FilterBQC;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.bj.FilterCBF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.bj.FilterJQS;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.bj.FilterSPF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.bj.FilterSXP;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.jc.FilterJcBQC;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.jc.FilterJcCBF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.jc.FilterJcHH;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.jc.FilterJcJQS;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.jc.FilterJcRQSPF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.jc.FilterJcSPF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.lc.FilterLcDXF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.lc.FilterLcHH;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.lc.FilterLcRFSF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.lc.FilterLcSF;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.lc.FilterLcSFC;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;

import trade.bean.CodeBean;

public abstract class FilterBase {
	public abstract void filter(CodeBean bean, FilterResult result) throws CodeFormatException;
	public static void doFilter(CodeBean bean, FilterResult result) throws CodeFormatException{
		if(bean.getLottype() == 85){
			new FilterSPF().filter(bean, result);
		}else if(bean.getLottype()==86){
			new FilterCBF().filter(bean, result);
		}else if(bean.getLottype()==87){
			new FilterBQC().filter(bean, result);
		}else if(bean.getLottype()==88){
			new FilterSXP().filter(bean, result);
		}else if(bean.getLottype()==89){
			new FilterJQS().filter(bean, result);
		}
	}
	
	public static void doFilterJc(CodeBean bean, FilterResult result) throws CodeFormatException{
		if(bean.getLottype() == 90){
			new FilterJcRQSPF().filter(bean, result);
		} else if(bean.getLottype() == 91){
			new FilterJcCBF().filter(bean, result);
		} else if(bean.getLottype() == 92){
			new FilterJcBQC().filter(bean, result);
		}else if(bean.getLottype() == 93){
			new FilterJcJQS().filter(bean, result);
		}else if(bean.getLottype() == 72){
			new FilterJcSPF().filter(bean, result);
		}else if (bean.getLottype()==70) {
			new FilterJcHH().filter(bean, result);
		}
	}
	
    public static void doFilterLc(CodeBean bean, FilterResult result) throws CodeFormatException{
        if (bean.getLottype() == 94){
            new FilterLcSF().filter(bean, result);
        } else if (bean.getLottype() == 95){
            new FilterLcRFSF().filter(bean, result);
        } else if (bean.getLottype() == 96){
            new FilterLcSFC().filter(bean, result);
        } else if (bean.getLottype() == 97){
            new FilterLcDXF().filter(bean, result);
        } else if (bean.getLottype() == 71){
            new FilterLcHH().filter(bean, result);
        }
    }
}

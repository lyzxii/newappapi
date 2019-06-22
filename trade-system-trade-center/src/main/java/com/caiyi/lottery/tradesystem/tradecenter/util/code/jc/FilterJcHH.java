package com.caiyi.lottery.tradesystem.tradecenter.util.code.jc;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.caipiao.plugin.helper.CodeFormatException;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.mina.rbc.util.StringUtil;

import trade.bean.CodeBean;

public class FilterJcHH extends FilterBase{
	
	public static final HashMap<String, String> hhMaps = new HashMap<String, String>();
	
	static{
		//半全场
		hhMaps.put("3-3", "3-3");
		hhMaps.put("3-1", "3-1");
		hhMaps.put("3-0", "3-0");
		hhMaps.put("1-3", "1-3");
		hhMaps.put("1-1", "1-1");
		hhMaps.put("1-0", "1-0");
		hhMaps.put("0-3", "0-3");
		hhMaps.put("0-1", "0-1");
		hhMaps.put("0-0", "0-0");
		//猜比分
		hhMaps.put("90", "9:0");
		hhMaps.put("10", "1:0");
		hhMaps.put("20", "2:0");
		hhMaps.put("21", "2:1");
		hhMaps.put("30", "3:0");
		hhMaps.put("31", "3:1");
		hhMaps.put("32", "3:2");
		hhMaps.put("40", "4:0");
		hhMaps.put("41", "4:1");
		hhMaps.put("50", "5:0");
		hhMaps.put("51", "5:1");
		hhMaps.put("52", "5:2");
		hhMaps.put("42", "4:2");
		hhMaps.put("99", "9:9");
		hhMaps.put("00", "0:0");
		hhMaps.put("11", "1:1");
		hhMaps.put("22", "2:2");
		hhMaps.put("33", "3:3");
		hhMaps.put("09", "0:9");
		hhMaps.put("01", "0:1");
		hhMaps.put("02", "0:2");
		hhMaps.put("12", "1:2");
		hhMaps.put("03", "0:3");
		hhMaps.put("13", "1:3");
		hhMaps.put("23", "2:3");
		hhMaps.put("04", "0:4");
		hhMaps.put("14", "1:4");
		hhMaps.put("24", "2:4");
		hhMaps.put("05", "0:5");
		hhMaps.put("15", "1:5");
		hhMaps.put("25", "2:5");
		//进球数
		hhMaps.put("0", "0");
		hhMaps.put("1", "1");
		hhMaps.put("2", "2");
		hhMaps.put("3", "3");
		hhMaps.put("4", "4");
		hhMaps.put("5", "5");
		hhMaps.put("6", "6");
		hhMaps.put("7", "7");
	}

	@Override
	public void filter(CodeBean bean, FilterResult result) throws CodeFormatException {
		if(bean.getItemType() == CodeBean.NOITEM){
			if ("1".equals(bean.getHhtype())) {
				doSimpleForThePublic(bean, result);
			}else {
				doSimple(bean, result);
			}
		}else if(bean.getItemType() == CodeBean.HAVEITEM){
			//检测投注选项
			String codeString = bean.getCodeitems();
			HashMap<String, String> codeMaps = new HashMap<String, String>();
			if(codeString != null){
				codeString = codeString.replaceAll("\\s+", "");
				String [] codeitems = codeString.split(",");
				for(int i = 0; i < codeitems.length; i++){
					String [] ccs = codeitems[i].split("=");
					if(ccs.length != 2){
						throw new CodeFormatException(-1, "投注选项替换格式不符合要求", bean.getCode());
					}
					codeMaps.put(ccs[1].trim(), ccs[0].trim());
				}
			}
			
			/**
			 * 混合投注
			 */
			Pattern pattern = Pattern.compile("\\s*\\[混合过关\\](.+)\\|((\\s*\\d+\\s*\\*\\s*\\d+\\s*,\\s*)*(\\s*\\d+\\s*\\*\\s*\\d+\\s*))");
			Matcher matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				if ("1".equals(bean.getHhtype())) {
					doDyjWebForThePublic(bean, result, matcher.group(1),matcher.group(2), codeMaps);
				}else {
					doDyjWeb(bean, result, matcher.group(1),matcher.group(2), codeMaps);
				}
				return;
			}
			pattern = Pattern.compile("\\s*^HH\\s*\\|(.+)\\|((\\s*\\d+\\s*\\*\\s*\\d+\\s*,\\s*)*(\\s*\\d+\\s*\\*\\s*\\d+\\s*))");
			matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				if ("1".equals(bean.getHhtype())) {
					doDyjWebForThePublic(bean, result, matcher.group(1),matcher.group(2), codeMaps);
				}else {
					doDyjWeb(bean, result, matcher.group(1),matcher.group(2), codeMaps);
				}
				return;
			}
			pattern = Pattern.compile("\\s*^hh\\s*\\|(.+)\\|((\\s*\\d+\\s*\\*\\s*\\d+\\s*,\\s*)*(\\s*\\d+\\s*\\*\\s*\\d+\\s*))");
			matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				if ("1".equals(bean.getHhtype())) {
					doDyjWebForThePublic(bean, result, matcher.group(1),matcher.group(2), codeMaps);
				}else {
					doDyjWeb(bean, result, matcher.group(1),matcher.group(2), codeMaps);
				}
				return;
			}
			
			result.setCurrentCode("");
		}
	}
	
	/**
	 * 兼容格式（带场次）
	 * @param bean
	 * @param result
	 * @param code
	 * @param codeMaps
	 * @throws CodeFormatException
	 */
	private void doDyjWeb(CodeBean bean, FilterResult result, String code, String guoguan, HashMap<String, String> codeMaps) throws CodeFormatException{
		String tmpcode = code.replaceAll("\\s*", "").replaceAll(":", "")
				.replaceAll("：", "").replace("——", "");
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		String [] cs = tmpcode.split(",");
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		int len = cs.length;
		String gg = guoguan;
		
//		if(bean.getGuoguan().equals("1*1") && !(bean.getGuoguan().equals(gg))){
//			throw new CodeFormatException(-1, "浮动奖金玩法仅支持单关投注", bean.getCode());
//		}
//		
//		if(gg.equals("1*1") && !(bean.getGuoguan().equals("1*1"))){
//			throw new CodeFormatException(-1, "固定奖金玩法不支持单关投注", bean.getCode());
//		}
		
		//检查玩法和过关方式是否匹配
		if(!JcUtil.check(bean.getPlaytype(), gg)){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		
		bean.setGuoguan(gg);
		
		for(int i = 0; i < len; i++){
			
			String [] ccs = StringUtil.splitter(cs[i], ">");
			if(ccs.length != 2){
				throw new CodeFormatException(-1, "投注格式不符合要求", bean.getCode());
			}
	
			sb.append(ccs[0]);
			teamsMaps.put(ccs[0], ccs[0]);
			sb.append(">");
			
			String [] ps = StringUtil.splitter(ccs[1], "+");
			for(int k=0;k<ps.length;k++){
				String [] _s = StringUtil.splitter(ps[k], "=");
				if(_s.length != 2){
					throw new CodeFormatException(-1, "投注场次不符合要求", bean.getCode());
				}
				sb.append(_s[0]+"=");
				String [] csc = _s[1].split("/");
				int clen = csc.length;
				HashMap<String, String> tmpMaps = new HashMap<String, String>();
				for(int j = 0; j < clen; j++){
					String value = codeMaps.get(csc[j]);
					if(value == null){
						sb.append(getCodeItem(csc[j], bean));
						tmpMaps.put(csc[j], csc[j]);
					} else {
						sb.append(getCodeItem(value, bean));
						tmpMaps.put(value, value);
					}
					if(j != clen - 1){
						sb.append("/");
					}
				}
				if(tmpMaps.size() != clen){
					throw new CodeFormatException(-1, "投注选项处理后存在重复", bean.getCode());
				}
				if (k<(ps.length-1)) {
					sb.append("+");
				}else {
					sb.append(",");
				}
				
			}
			
		}
		
		if(teamsMaps.size() != len){
			throw new CodeFormatException(-1, "投注场次存在重复", bean.getCode());
		}
		code = sb.toString();
		if(code.endsWith(",")){
			code = code.substring(0, code.lastIndexOf(","));
		}
		
		code += "|" + bean.getGuoguan();
		
		result.putGglist(gg);
		result.putItems(teamsMaps);
		result.addCode(code);
	}
	
	/**
	 * 兼容格式（不带场次）
	 * 11,31,30,13,33,30
	 * 11-31-30-13-33-30 
	 * 113130133330
	 * 11 31 30 13 33 30
	 * 11,31,30,**,13,33,**,**,30,**,** 
	 * 113130##1333####30 
	 * @param bean
	 * @param result
	 * @throws CodeFormatException
	 */
	private void doSimple(CodeBean bean, FilterResult result) throws CodeFormatException{
		//检查玩法和过关方式是否匹配
		if(!JcUtil.check(bean.getPlaytype(), bean.getGuoguan())){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		//兼容各种投注分割符号
		String code = bean.getCode();
		int len = code.split(",").length;
	
		//检测投注场次
		String itemString = bean.getTeamitems(); 
		String [] teamitems = itemString.split(",");
		int teamlen = teamitems.length;
		if(teamlen < len){
			throw new CodeFormatException(-1, "所选场次数量不能少于实际投注场次数量", bean.getCode());
    	}
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		for(String s: teamitems){
			try {
				Integer.parseInt(s);
			} catch (Exception e) {
				throw new CodeFormatException(-1, "所选场次不符合要求", bean.getCode());
			}
			teamsMaps.put(s, s);
		}
		if(teamsMaps.size() != teamlen){
			throw new CodeFormatException(-1, "所选场次存在重复场次", bean.getCode());
		}
		
		//检测投注选项
		String codeString = bean.getCodeitems();
		HashMap<String, String> codeMaps = new HashMap<String, String>();
		if(codeString != null){
			codeString = codeString.replaceAll("\\s+", "");
			String [] codeitems = codeString.split(",");
			for(int i = 0; i < codeitems.length; i++){
				String [] ccs = codeitems[i].split("=");
				if(ccs.length != 2){
					throw new CodeFormatException(-1, "投注选项替换格式不符合要求", bean.getCode());
				}
				codeMaps.put(ccs[1].trim(), ccs[0].trim());
			}
		}
		
		//生成标准格式
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		int count = 0;
        String [] cd =code.split(",");
		for(int i = 0; i < teamlen ; i++){
			String tmp = code.substring(2 * i, 2 * (i+1));
			if("##".equals(tmp)){
				continue;
			}
			sb.append(teamitems[i]);
			sb.append(">");
			sb.append(cd[i]);
			sb.append(",");
			count++;
		}
			
		if(count < JcUtil.getType(bean.getGuoguan())){
			throw new CodeFormatException(-1, "场次不足支持过关方式", bean.getCode());
		}
		
		code = sb.toString();
		if(code.endsWith(",")){
			code = code.substring(0, code.lastIndexOf(","));
		}
		
		code += "|" + bean.getGuoguan();
		
		result.putItems(teamsMaps);
		result.addCode(code);
	}
	
	
	/**
	 * 兼容格式（带场次）公众版
	 * @param bean
	 * @param result
	 * @param code
	 * @param codeMaps
	 * @throws CodeFormatException
	 */
	private void doDyjWebForThePublic(CodeBean bean, FilterResult result, String code, String guoguan, HashMap<String, String> codeMaps) throws CodeFormatException{
		String tmpcode = code.replaceAll("\\s*", "").replaceAll(":", "")
				.replaceAll("：", "").replace("——", "");
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		HashMap<String, String> ccMaps = new HashMap<String, String>();
		String [] cs = tmpcode.split(",");
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		int len = cs.length;
		String gg = guoguan;
		
//		if(bean.getGuoguan().equals("1*1") && !(bean.getGuoguan().equals(gg))){
//			throw new CodeFormatException(-1, "浮动奖金玩法仅支持单关投注", bean.getCode());
//		}
//		
//		if(gg.equals("1*1") && !(bean.getGuoguan().equals("1*1"))){
//			throw new CodeFormatException(-1, "固定奖金玩法不支持单关投注", bean.getCode());
//		}
		
		//检查玩法和过关方式是否匹配
		if(!JcUtil.check(bean.getPlaytype(), gg)){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		
		bean.setGuoguan(gg);
		
		for(int i = 0; i < len; i++){
			
			String [] ccs = StringUtil.splitter(cs[i], ">");
			if(ccs.length != 2){
				throw new CodeFormatException(-1, "投注格式不符合要求", bean.getCode());
			}
	
			sb.append(ccs[0]);
			teamsMaps.put(ccs[0], ccs[0]);
			sb.append(">");
			
			String [] ps = StringUtil.splitter(ccs[1], "+");
			
			for(int k=0;k<ps.length;k++){
				String [] _s = StringUtil.splitter(ps[k], "=");
				if(_s.length != 2){
					_s=new String[2];
					String tempStr=ps[k];
					if (tempStr.indexOf("[")!=-1&&tempStr.indexOf("]")!=-1) {
						_s[0]="RQSPF";
						try {
							_s[1]=tempStr.substring(tempStr.indexOf("]")+1, tempStr.length());
						} catch (Exception e) {
							throw new CodeFormatException(-1, "投注场次不符合要求", bean.getCode());
						}
					}else {
						_s[0]="SPF";
						_s[1]=tempStr;
					}
				}else {
					if (!_s[0].toUpperCase().equals("SPF")&&!_s[0].toUpperCase().equals("RQSPF")) {
						throw new CodeFormatException(-1, "暂不支持的投注种类:"+_s[0], bean.getCode());
					}
				}
				
				if (ccMaps.get(ccs[0])==null) {
					ccMaps.put(ccs[0], _s[0]);
				}else {
					String wanf=ccMaps.get(ccs[0]);
					if (!wanf.equals(_s[0])) {
						ccMaps.put(ccs[0], "SPFRQSPF");
					}else {
						throw new CodeFormatException(-1, "投注场次不符合要求", bean.getCode());
					}
				}
				
				sb.append(_s[0]+"=");
				
				
				String [] csc = _s[1].split("/");
				int clen = csc.length;

				if (clen==1&&_s[1].length()>1) {
					//无斜杠的复选
					csc=new String[_s[1].length()];
					for (int p = 0; p < _s[1].length(); p++) {
						csc[p]=_s[1].substring(p,p+1);
					}
					clen=csc.length;
				}
				
				HashMap<String, String> tmpMaps = new HashMap<String, String>();
				for(int j = 0; j < clen; j++){
					String value = codeMaps.get(csc[j]);
					if(value == null){
						sb.append(getCodeItem(csc[j], bean));
						tmpMaps.put(csc[j], csc[j]);
					} else {
						sb.append(getCodeItem(value, bean));
						tmpMaps.put(value, value);
					}
					if(j != clen - 1){
						sb.append("/");
					}
				}
				if(tmpMaps.size() != clen){
					throw new CodeFormatException(-1, "投注选项处理后存在重复", bean.getCode());
				}
				if (k<(ps.length-1)) {
					sb.append("+");
				}else {
					sb.append(",");
				}
				
			}
			
		}
		
		if(teamsMaps.size() != len){
			throw new CodeFormatException(-1, "投注场次存在重复", bean.getCode());
		}
		code = sb.toString();
		if(code.endsWith(",")){
			code = code.substring(0, code.lastIndexOf(","));
		}
		
		code += "|" + bean.getGuoguan();
		bean.setCcitems(ccMaps);
		result.putGglist(gg);
		result.putItems(teamsMaps);
		result.addCode(code);
	}
	
	
	
	/**
	 * 兼容格式（不带场次）公众版
	 * 3,[-1]3,1,0,3,1
	 * 3-3-1-[-1]0-3-1
	 * 33[1]1303
	 * 3 [1]3 1 0 3 1
	 * 3,3,1,*,[-1]0,3,*,*,1,*,* 
	 * 33[-1]1#03##1
	 * @param bean
	 * @param result
	 * @throws CodeFormatException
	 */
	private void doSimpleForThePublic(CodeBean bean, FilterResult result) throws CodeFormatException{
		//检查玩法和过关方式是否匹配
		if(!JcUtil.check(bean.getPlaytype(), bean.getGuoguan())){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		//兼容各种投注分割符号
		String code = bean.getCode();
//		code = code.replaceAll(",|-|\\s+|\\*|\\#", "");
//		//code = code.replaceAll("\\*", "#");
		code = code.replaceAll(",|-|\\s+", "");
		code = code.replaceAll("\\*", "#");
		if(code.indexOf("_")!=-1){
			code = code.split("_")[0];
		}
		
		code=formatcodeStr(code,bean);

		int len = code.split(",").length;
		
		//检测投注场次
		String itemString = bean.getTeamitems(); 
		String [] teamitems = itemString.split(",");
		int teamlen = teamitems.length;
		if(teamlen < len){
			throw new CodeFormatException(-1, "所选场次数量不能少于实际投注场次数量", bean.getCode());
    	}
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		for(String s: teamitems){
			try {
				Integer.parseInt(s);
			} catch (Exception e) {
				throw new CodeFormatException(-1, "所选场次不符合要求", bean.getCode());
			}
			teamsMaps.put(s, s);
		}
		if(teamsMaps.size() != teamlen){
			throw new CodeFormatException(-1, "所选场次存在重复场次", bean.getCode());
		}
		
		//检测投注选项
		String codeString = bean.getCodeitems();
		HashMap<String, String> codeMaps = new HashMap<String, String>();
		if(codeString != null){
			codeString = codeString.replaceAll("\\s+", "");
			String [] codeitems = codeString.split(",");
			for(int i = 0; i < codeitems.length; i++){
				String [] ccs = codeitems[i].split("=");
				if(ccs.length != 2){
					throw new CodeFormatException(-1, "投注选项替换格式不符合要求", bean.getCode());
				}
				codeMaps.put(ccs[1].trim(), ccs[0].trim());
			}
		}
		
		//生成标准格式
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		int count = 0;
        String [] cd =code.split(",");
		HashMap<String, String> ccMaps = new HashMap<String, String>();
		for(int i = 0; i < len ; i++){
			if("#".equals(cd[i])){
				continue;
			}
			
			String cdtemp=cd[i];
			sb.append(teamitems[i]);
			sb.append(">");
			if (cd[i].indexOf("[")!=-1&&cd[i].indexOf("]")!=-1) {
				sb.append("RQSPF=");
				cdtemp=cdtemp.substring(cdtemp.indexOf("]")+1, cdtemp.length());
				ccMaps.put(teamitems[i], "RQSPF");
			}else {
				sb.append("SPF=");
				ccMaps.put(teamitems[i], "SPF");
			}
			sb.append(cdtemp);
			sb.append(",");
			count++;
		}
			
		if(count < JcUtil.getType(bean.getGuoguan())){
			throw new CodeFormatException(-1, "场次不足支持过关方式", bean.getCode());
		}
		
		code = sb.toString();
		if(code.endsWith(",")){
			code = code.substring(0, code.lastIndexOf(","));
		}
		
		code += "|" + bean.getGuoguan();
		bean.setCcitems(ccMaps);
		result.putItems(teamsMaps);
		result.addCode(code);
	}
	

	/**
	 * 转化成“,”连接格式
	 * @param code
	 * @param bean
	 * @return
	 * @throws CodeFormatException
	 */
	private String formatcodeStr(String code, CodeBean bean) throws CodeFormatException{
		try {
			StringBuffer sp=new StringBuffer();
			String temp=code.substring(0,1);
			while (!temp.equals("")) {
				if (temp.equals("[")) {
					temp=code.substring(code.indexOf("["), code.indexOf("]")+2);
					sp.append(temp+",");
					if (code.indexOf("]")+2!=code.length()) {
						code=code.substring(code.indexOf("]")+2,code.length());
						temp=code.substring(0,1);
					}else {
						temp="";
					}
					
				}else {
					sp.append(temp+",");
					if (1!=code.length()) {
						code=code.substring(1,code.length());
						temp=code.substring(0,1);
					}else {
						temp="";
					}
				}
			}
			String result=sp.toString();
			
			if(result.endsWith(",")){
				result = result.substring(0, result.lastIndexOf(","));
			}
			return result;
		} catch (Exception e) {
			throw new CodeFormatException(-1, "处理转换后号码不符合投注要求", bean.getCode());
		}
	}
	
	
	/**
	 * 投注项验证
	 * 3-3,3-1,3-0, 1-3,1-1,1-0, 0-3,0-1,0-0
	 * @param value
	 * @param bean
	 * @return
	 * @throws CodeFormatException
	 */
	private String getCodeItem(String value, CodeBean bean) throws CodeFormatException{
		if(!hhMaps.containsKey(value)){
			throw new CodeFormatException(-1, "处理转换后号码不符合投注要求", bean.getCode());
		}
		return hhMaps.get(value);
	}
	
	
}
package com.caiyi.lottery.tradesystem.tradecenter.util.code.lc;

import com.caipiao.plugin.helper.CodeFormatException;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import trade.bean.CodeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterLcSFC extends FilterBase{
	
	public static final HashMap<String, String> sfcMaps = new HashMap<String, String>();
	
	static{
		sfcMaps.put("01", "01");
		sfcMaps.put("02", "02");
		sfcMaps.put("03", "03");
		sfcMaps.put("04", "04");
		sfcMaps.put("05", "05");
		sfcMaps.put("06", "06");
		sfcMaps.put("11", "11");
		sfcMaps.put("12", "12");
		sfcMaps.put("13", "13");
		sfcMaps.put("14", "14");
		sfcMaps.put("15", "15");
		sfcMaps.put("16", "16");
	}
	
	@Override
	public void filter(CodeBean bean, FilterResult result) throws CodeFormatException {
		if(bean.getItemType() == CodeBean.NOITEM){
			doSimple(bean, result);
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
			
			//Pattern pattern = Pattern.compile("\\s*\\[.+\\]((\\s*\\d+\\s*=((\\s*\\d\\s*:\\s*\\d\\s*)\\s*/\\s*)*(\\s*\\d\\s*:\\s*\\d\\s*)\\s*,\\s*)*(\\s*\\d+\\s*=((\\s*\\d\\s*:\\s*\\d\\s*)/)*(\\s*\\d\\s*:\\s*\\d\\s*)))\\|\\s*\\d\\s*\\*\\s*\\d\\s*");
			Pattern pattern = Pattern.compile("\\s*\\[胜分差\\]((\\s*\\d+\\s*=((\\s*\\d\\s*\\s*\\d\\s*)\\s*/\\s*)*(\\s*\\d\\s*\\s*\\d\\s*)\\s*,\\s*)*(\\s*\\d+\\s*=((\\s*\\d\\s*\\s*\\d\\s*)/)*(\\s*\\d\\s*\\s*\\d\\s*)))\\|((\\s*\\d+\\s*\\*\\s*\\d+\\s*,\\s*)*(\\s*\\d+\\s*\\*\\s*\\d+\\s*))");
			Matcher matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				doDyjWeb(bean, result, matcher.group(1),matcher.group(10), codeMaps);
				return;
			}
			
			pattern = Pattern.compile("\\s*SFC\\s*\\|((\\s*\\d+\\s*=((\\s*\\d\\s*\\s*\\d\\s*)\\s*/\\s*)*(\\s*\\d\\s*\\s*\\d\\s*)\\s*,\\s*)*(\\s*\\d+\\s*=((\\s*\\d\\s*\\s*\\d\\s*)/)*(\\s*\\d\\s*\\s*\\d\\s*)))\\|((\\s*\\d+\\s*\\*\\s*\\d+\\s*,\\s*)*(\\s*\\d+\\s*\\*\\s*\\d+\\s*))");
			matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				doDyjWeb(bean, result, matcher.group(1),matcher.group(10), codeMaps);
				return;
			}
			
			pattern = Pattern.compile("\\s*sfc\\s*\\|((\\s*\\d+\\s*=((\\s*\\d\\s*\\s*\\d\\s*)\\s*/\\s*)*(\\s*\\d\\s*\\s*\\d\\s*)\\s*,\\s*)*(\\s*\\d+\\s*=((\\s*\\d\\s*\\s*\\d\\s*)/)*(\\s*\\d\\s*\\s*\\d\\s*)))\\|((\\s*\\d+\\s*\\*\\s*\\d+\\s*,\\s*)*(\\s*\\d+\\s*\\*\\s*\\d+\\s*))");
			matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				doDyjWeb(bean, result, matcher.group(1),matcher.group(10), codeMaps);
				return;
			}
			result.setCurrentCode("");
		}
	}
	
	/**
	 * 兼容格式
	 * [胜分差]35=1:2/2:1,37=1:2/2:1/2:3,40=0:2/1:2/2:0/2:1|3*1
	 * SFC|35=1:2/2:1,37=1:2/2:1/2:3,40=0:2/1:2/2:0/2:1|3*1_10
	 * @param bean
	 * @param result
	 * @param code
	 * @param codeMaps
	 * @throws CodeFormatException
	 */
	private void doDyjWeb(CodeBean bean, FilterResult result, String code,String guoguan, HashMap<String, String> codeMaps) throws CodeFormatException{
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		String tmpcode = code.replaceAll("\\s*", "");
		tmpcode = tmpcode.replaceAll(":", "");
		String [] cs = tmpcode.split(",");
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		int len = cs.length;
		//String gg = len + "*1";
		String gg = guoguan;
		
//		if(bean.getGuoguan().equals("1*1") && !(bean.getGuoguan().equals(gg))){
//			throw new CodeFormatException(-1, "浮动奖金玩法仅支持单关投注", bean.getCode());
//		}
//		
//		if(gg.equals("1*1") && !(bean.getGuoguan().equals("1*1"))){
//			throw new CodeFormatException(-1, "固定奖金玩法不支持单关投注", bean.getCode());
//		}
		
		//检查玩法和过关方式是否匹配
		if(!LcUtil.check(bean.getPlaytype(), gg)){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		
		bean.setGuoguan(gg);
		
		for(int i = 0; i < len; i++){
			String [] ccs = cs[i].split("=");
			if(ccs.length != 2){
				throw new CodeFormatException(-1, "投注格式不符合要求", bean.getCode());
			}
			
			try {
				Integer.parseInt(ccs[0]);
			} catch (Exception e) {
				throw new CodeFormatException(-1, "投注场次不符合要求", bean.getCode());
			}
			
			teamsMaps.put(ccs[0], ccs[0]);
			
			sb.append(ccs[0]);
			sb.append("=");
			
			String [] csc = ccs[1].split("/");
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
			tmpMaps.clear();
			sb.append(",");
		}
		if(teamsMaps.size() != len){
			throw new CodeFormatException(-1, "投注场次存在重复", bean.getCode());
		}
		//teamsMaps.clear();
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
	 * 兼容格式
	 * 11,31,30,**,**,** 
	 * 11##31######30## 
	 * 11,31,30
	 * 11-31-30
	 * 113130
	 * 11 31 30
	 * @param bean
	 * @param result
	 * @throws CodeFormatException
	 */
	private void doSimple(CodeBean bean, FilterResult result) throws CodeFormatException{
		//检查玩法和过关方式是否匹配
		if(!LcUtil.check(bean.getPlaytype(), bean.getGuoguan())){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}

		//兼容各种投注分割符号
		String code = bean.getCode();
//		code = code.replaceAll(",|-|\\s+|\\*|\\#|:", "");
//		//code = code.replaceAll("\\*", "#");
		code = code.replaceAll(",|-|\\s+|:", "");
		code = code.replaceAll("\\*", "#");
		int len = code.length();
		if(len % 2 != 0){
			throw new CodeFormatException(-1, "号码不符合要求", bean.getCode());
		}
		
		//检测投注场次
		String itemString = bean.getTeamitems(); 
		String [] teamitems = itemString.split(",");
		int teamlen = teamitems.length;
		if(teamlen < len / 2){
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
		for(int i = 0; i < len / 2; i++){
			String tmp = code.substring(2 * i, 2 * (i+1));
			if("##".equals(tmp)){
				continue;
			}
			sb.append(teamitems[i]);
			sb.append("=");
			
			String value = codeMaps.get(tmp);
			if(value == null){
				sb.append(getCodeItem(tmp, bean));
			}else{
				sb.append(getCodeItem(value, bean));
			}
			sb.append(",");
			count++;
		}
		if(count < LcUtil.getType(bean.getGuoguan())){
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
	 * 投注项验证
	 * 3-3,3-1,3-0, 1-3,1-1,1-0, 0-3,0-1,0-0
	 * @param value
	 * @param bean
	 * @return
	 * @throws CodeFormatException
	 */
	private String getCodeItem(String value, CodeBean bean) throws CodeFormatException{
		if(!sfcMaps.containsKey(value)){
			throw new CodeFormatException(-1, "处理转换后号码不符合投注要求", bean.getCode());
		}
		return sfcMaps.get(value);
	}
	
	public static void main(String[] args) {
		List<String> nlist = new ArrayList<String>();
		nlist.add("13130111");
//		nlist.add("11##31######30##");
//		nlist.add("11,31,30");
//		nlist.add("11-31-30");
//		nlist.add("113130");
//		nlist.add("11 31 30");
		
		List<String> ylist = new ArrayList<String>();
		//ylist.add("SFC|110523001=1:2/1:1,110523002=1:2/1:1/1:3,110524001=0:2/1:2/1:3/1:1|3*4_3");
		ylist.add("SFC|110523001=12/11,110523002=12/11/13,110524001=02/12/13/11|3*4_3");
		
		FilterResult fr = new FilterResult();
		try {
			FilterLcSFC cbf = new FilterLcSFC();
			CodeBean bean = new CodeBean();
			bean.setLottype(96);
			bean.setPlaytype("SFC");
			bean.setCodeitems("01=01,02=02");
			bean.setTeamitems("1,2,3,4");
			bean.setGuoguan("3*1");
			bean.setItemType(CodeBean.NOITEM);
			
//			for(String c : nlist){
//				bean.setCode(c);
//				cbf.doFilterLc(bean, fr);
//			}
			
			for(String c : ylist){
				bean.setItemType(CodeBean.HAVEITEM);
				bean.setCode(c);
				cbf.doFilterLc(bean, fr);
			}
			
		} catch (CodeFormatException e) {

		}
	}

}

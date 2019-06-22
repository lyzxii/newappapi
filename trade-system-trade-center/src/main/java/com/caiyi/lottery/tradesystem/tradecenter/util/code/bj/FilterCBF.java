package com.caiyi.lottery.tradesystem.tradecenter.util.code.bj;

import com.caipiao.plugin.helper.CodeFormatException;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import trade.bean.CodeBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterCBF extends FilterBase{
	
	public static final HashMap<String, String> cbfMaps = new HashMap<String, String>();
	public static final HashMap<String, String> cparaMaps = new HashMap<String, String>();
	
	static{
		cbfMaps.put("9:0", "9:0");
		cbfMaps.put("1:0", "1:0");
		cbfMaps.put("2:0", "2:0");
		cbfMaps.put("2:1", "2:1");
		cbfMaps.put("3:0", "3:0");
		cbfMaps.put("3:1", "3:1");
		cbfMaps.put("3:2", "3:2");
		cbfMaps.put("4:0", "4:0");
		cbfMaps.put("4:1", "4:1");
		cbfMaps.put("4:2", "4:2");
		cbfMaps.put("9:9", "9:9");
		cbfMaps.put("0:0", "0:0");
		cbfMaps.put("1:1", "1:1");
		cbfMaps.put("2:2", "2:2");
		cbfMaps.put("3:3", "3:3");
		cbfMaps.put("0:9", "0:9");
		cbfMaps.put("0:1", "0:1");
		cbfMaps.put("0:2", "0:2");
		cbfMaps.put("1:2", "1:2");
		cbfMaps.put("0:3", "0:3");
		cbfMaps.put("1:3", "1:3");
		cbfMaps.put("2:3", "2:3");
		cbfMaps.put("0:4", "0:4");
		cbfMaps.put("1:4", "1:4");
		cbfMaps.put("2:4", "2:4");
		
		cparaMaps.put("90", "3A");
	    cparaMaps.put("99", "1A");
	    cparaMaps.put("09", "0A");
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
			
			Pattern pattern = Pattern.compile("\\s*((\\[.+\\])|(.+?\\|))(.+)\\|([\\s\\d,\\*]+)");
			Matcher matcher = pattern.matcher(bean.getCode());
			if(matcher.find()){
				doDyjWeb(bean, result, matcher.group(4),matcher.group(5),codeMaps);
				return;
			}
			
			pattern = Pattern.compile("((\\s*\\d+\\s*:\\s*\\[.+?\\]\\s*/\\s*)*(\\s*\\d+\\s*:\\s*\\[.+?\\]\\s*))");
			String _tmpcode = bean.getCode().replaceAll("\\s*", "").replace("平其他", "9:9").replace("平其它", "9:9").replace("1A", "9:9");
			_tmpcode = _tmpcode.replace("胜其他", "9:0").replace("胜其它", "9:0").replace("3A", "9:0");
			_tmpcode = _tmpcode.replace("负其他", "0:9").replace("负其它", "0:9").replace("0A", "0:9");
			matcher = pattern.matcher(_tmpcode);
			if(matcher.find()){
				do5Millions(bean, result, matcher.group(1), codeMaps);
				return;
			}
			
			pattern = Pattern.compile("((\\s*\\d+\\s*→((\\s*\\d\\s*:\\s*\\d\\s*)|\\(((\\s*\\d\\s*:\\s*\\d\\s*)\\s*,\\s*)*(\\s*\\d\\s*:\\s*\\d\\s*)\\))\\s*,\\s*)*(\\s*\\d+→((\\s*\\d\\s*:\\s*\\d\\s*)|\\(((\\s*\\d\\s*:\\s*\\d\\s*),)*(\\s*\\d\\s*:\\s*\\d\\s*)\\)\\s*)))");
			String tmpcode = bean.getCode().replaceAll("\\s*", "").replace("平其他", "9:9").replace("平其它", "9:9").replace("1A", "9:9");
			tmpcode = tmpcode.replace("胜其他", "9:0").replace("胜其它", "9:0").replace("3A", "9:0");
			tmpcode = tmpcode.replace("负其他", "0:9").replace("负其它", "0:9").replace("0A", "0:9");
			matcher = pattern.matcher(tmpcode);
			if(matcher.find()){
				doOkooo(bean, result, matcher.group(1), codeMaps);
				return;
			}
			result.setCurrentCode("");
		}
	}
	
	/**
	 * 兼容okooo格式
	 * 22→1:0,23→2:3,29→1:3	1	2	3_1
	 * 7→(1:0,2:0,2:1,3:0,3:1),28→(1:0,2:0,2:1,3:0,3:1)	25	50	2_1
	 * @param bean
	 * @param result
	 * @param code
	 * @param codeMaps
	 * @throws CodeFormatException
	 */
	private void doOkooo(CodeBean bean, FilterResult result, String code, HashMap<String, String> codeMaps) throws CodeFormatException{
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		String tmpcode = code;
		
		List<String> cs = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(\\d+→((\\d:\\d)|\\(((\\d:\\d),)*(\\d:\\d)\\)),?)");
		Matcher matcher = pattern.matcher(tmpcode);
		while(matcher.find()){
			cs.add(matcher.group());
		}
		int len = cs.size();

		String gg = len + "*1";
		
		//检查玩法和过关方式是否匹配
		if(!BjUtil.check(bean.getPlaytype(), gg)){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		
		bean.setGuoguan(gg);
		
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		for(int i = 0; i < len; i++){
			String [] ccs = cs.get(i).split("→");
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
			
			String tmp = ccs[1].replaceAll("\\(|\\)|:", "");
			String [] csc = tmp.split(",");
			int ilen = csc.length;
			HashMap<String, String> items = new HashMap<String, String>();
			for(int j = 0; j < ilen; j++){
				csc[j] = csc[j].trim();
				String value = codeMaps.get(((csc[j].equals("09")) || (csc[j].equals("90")) || (csc[j].equals("99"))) ? cparaMaps.get(csc[j]) : csc[j]);
				if(value == null){
					sb.append(getCodeItem(csc[j], bean));
					items.put(csc[j], csc[j]);
				}else{
					sb.append(getCodeItem(value, bean));
					items.put(value, value);
				}
				if(j != ilen - 1){
					sb.append("/");
				}
			}
			if(items.size() != ilen){
				throw new CodeFormatException(-1, "投注选项处理后存在重复", bean.getCode());
			}
			
			sb.append(",");
		}
		
		if(teamsMaps.size() != len){
			throw new CodeFormatException(-1, "投注场次存在重复", bean.getCode());
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
	 * 兼容500万格式
	 * 1:[2:2]/2:[1:1]/3:[胜其他]
	 * 1:[2:2]/2:[0:0]/3:[1:4]
	 * @param bean
	 * @param result
	 * @param code
	 * @param codeMaps
	 * @throws CodeFormatException
	 */
	private void do5Millions(CodeBean bean, FilterResult result, String code, HashMap<String, String> codeMaps) throws CodeFormatException{
		HashMap<String, String> teamsMaps = new HashMap<String, String>();
		String tmpcode = code;
		
		String [] cs = tmpcode.split("/");
		int len = cs.length;
		
		String gg = len + "*1";
		
		//检查玩法和过关方式是否匹配
		if(!BjUtil.check(bean.getPlaytype(), gg)){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}
		
		bean.setGuoguan(gg);
		
		StringBuffer sb = new StringBuffer();
		sb.append(bean.getPlaytype());
		sb.append("|");
		for(int i = 0; i < cs.length; i++){
			List<String> list = new ArrayList<String>();
			Pattern pattern = Pattern.compile("\\s*(.+?)\\s*:\\s*\\[(.+?)\\]\\s*");
			Matcher matcher = pattern.matcher(cs[i]);
			if(matcher.find()){
				list.add(matcher.group(1));
				list.add(matcher.group(2));
			}else{
				throw new CodeFormatException(-1, "号码不符合要求", bean.getCode());
			}
			try {
				Integer.parseInt(list.get(0));
			} catch (Exception e) {
				throw new CodeFormatException(-1, "投注场次不符合要求", bean.getCode());
			}
			
			teamsMaps.put(list.get(0), list.get(0));
			
			sb.append(list.get(0));
			sb.append("=");
			
			String tmp = list.get(1).replaceAll("\\[|\\]|:", "");
			String value = codeMaps.get(tmp);
			if(value == null){
				sb.append(getCodeItem(tmp, bean));
			}else{
				sb.append(getCodeItem(value, bean));
			}
			sb.append(",");
		}
		
		if(teamsMaps.size() != len){
			throw new CodeFormatException(-1, "投注场次存在重复", bean.getCode());
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
	 * 兼容格式
	 * [比分]35=1:2/2:1,37=1:2/2:1/2:3,40=0:2/1:2/2:0/2:1|3*1
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
//		String gg = len + "*1";
		String gg = guoguan.replaceAll("\\s*", "");
		
		//检查玩法和过关方式是否匹配
		if(!BjUtil.check(bean.getPlaytype(), gg)){
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
		teamsMaps.clear();
		code = sb.toString();
		if(code.endsWith(",")){
			code = code.substring(0, code.lastIndexOf(","));
		}
		
		code += "|" + bean.getGuoguan();
		
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
		if(!BjUtil.check(bean.getPlaytype(), bean.getGuoguan())){
			throw new CodeFormatException(-1, "过关方式和玩法不匹配", bean.getCode());
		}

		//兼容各种投注分割符号
		String code = bean.getCode();
		code = code.replaceAll(",|-|\\s+", "");
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
		HashMap<String, String> teamsIDMaps = new HashMap<String, String>();
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
			
			teamsIDMaps.put(teamitems[i], teamitems[i]);
			
			count++;
		}
		if(count < BjUtil.getType(bean.getGuoguan())){
			throw new CodeFormatException(-1, "场次不足支持过关方式", bean.getCode());
		}
		
		code = sb.toString();
		if(code.endsWith(",")){
			code = code.substring(0, code.lastIndexOf(","));
		}
		
		code += "|" + bean.getGuoguan();
		
		result.putItems(teamsIDMaps);
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
		if(!cbfMaps.containsKey(value)){
			throw new CodeFormatException(-1, "处理转换后号码不符合投注要求", bean.getCode());
		}
		return cbfMaps.get(value);
	}
	
	public static void main(String[] args) {
		List<String> nlist = new ArrayList<String>();
//		nlist.add("11,31,30,**,**,**");
//		nlist.add("11##31######30##");
//		nlist.add("11,31,30");
//		nlist.add("11-31-30");
//		nlist.add("113130");
		nlist.add("11 31 30");
		
		List<String> ylist = new ArrayList<String>();
//		ylist.add("[比分]35=1:2/2:1,37=1:2/2:1/2:3,40=0:2/1:2/2:0/2:1|3*1");
//		ylist.add("1:[2:2]/2:[1:1]/3:[胜其他]");
//		ylist.add("1:[2:2]/2:[0:0]/3:[1:4]");
		ylist.add("79→1:3,80→3A,84→0:2	1	2	3_1");
//		ylist.add("7→(1:0,2:0,2:1,3:0,3:1),28→(1:0,2:0,2:1,3:0,3:1)	25	50	2_1");
//		ylist.add("71→(1:0,胜其他,1:1),72→(1:0,0:0,1:1),73→(1:0,胜其他,1:1) 27 54 3_1");
		
		FilterResult fr = new FilterResult();
		try {
			FilterCBF cbf = new FilterCBF();
			CodeBean bean = new CodeBean();
			bean.setLottype(86);
			bean.setPlaytype("CBF");
			bean.setCodeitems("0:0=00,0:1=01,0:2=02,0:3=03,0:4=04,1:0=10,1:1=11,1:2=12,1:3=13,1:4=14,2:0=20,2:1=21,2:2=22,2:3=23,2:4=24,3:0=30,3:1=31,3:2=32,3:3=33,4:0=40,4:1=41,4:2=42,4:3=null,4:4=null,0:9=0A,9:9=1A,9:0=3A");
			bean.setTeamitems("1,2,3");
			bean.setGuoguan("3*1");
			bean.setItemType(CodeBean.NOITEM);
			
//			for(String c : nlist){
//				bean.setCode(c);
//				cbf.doFilter(bean, fr);
//			}
			
			for(String c : ylist){
				bean.setItemType(CodeBean.HAVEITEM);
				bean.setCode(c);
				cbf.doFilter(bean, fr);
			}
			
		} catch (CodeFormatException e) {
		}
	}

}

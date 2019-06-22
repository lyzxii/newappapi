package com.caiyi.lottery.tradesystem.tradecenter.util.trade;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.plugin.sturct.LimitCode;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

public class LimitCodeUtil {
	
	private static HashMap<String, Set<LimitCode>> maps = new HashMap<String, Set<LimitCode>>();
	private static HashMap<String, GamePluginAdapter> plugins = new HashMap<String, GamePluginAdapter>();
	private static String lIMIT_CODE_FILE = "/opt/export/data/info/limitcode.xml";
	private static long lastLoad = 0;
	
	public static void checkLimitCode(String gid, GameCastCode gcc, GamePluginAdapter plugin) throws Exception{
		initLimitCode();
		Set<LimitCode> set = maps.get(gid);
		if(set != null){
			plugin.isLimitCode(gcc, set);
		}
	}
	
	private static GamePluginAdapter getGamePluginAdapter(String gid) throws Exception{
		GamePluginAdapter plugin = plugins.get(gid);
		if(plugin == null){
			plugin = (GamePluginAdapter) Class.forName("com.caipiao.plugin.GamePlugin_" + gid).newInstance();
		}
		return plugin;
	}
	
	public static boolean initLimitCode() throws Exception{
		File file = new File(lIMIT_CODE_FILE);
		if(!file.exists()){
			return false;
		}
		if(lastLoad != file.lastModified()){
			maps.clear();
			JXmlWrapper xml = JXmlWrapper.parse(file);
			int count = xml.countXmlNodes("node");
			for(int i = 0; i < count; i++){
				String gid = xml.getStringValue("node[" + i + "].@gid");
				GamePluginAdapter plugin = getGamePluginAdapter(gid);
				String tmpcode = xml.getStringValue("node[" + i + "].@code");
				String [] cs = StringUtil.splitter(tmpcode, ";");
				for(int j = 0; j < cs.length; j++){
					if(StringUtil.isEmpty(cs[j])){
						continue;
					}
					try {
						String code = cs[j] + ":" + xml.getStringValue("node[" + i + "].@playid") + ":" + xml.getStringValue("node[" + i + "].@castid", "1");
						if(Integer.parseInt(gid)>=70){
							code = cs[j];
						}
						GameCastCode gcc = plugin.parseGameCastCode(code);
						LimitCode limit = new LimitCode();
						limit.setGcc(gcc);
						limit.setGid(gid);
						limit.setCode(code);
						Set<LimitCode> set = maps.get(gid);
						if(set == null){
							set = new HashSet<LimitCode>();
						}
						set.add(limit);
						maps.put(gid, set);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
			lastLoad = file.lastModified();
		}
		return true;
	}
	
	public static void main(String[] args) throws Exception {
		LimitCodeUtil.initLimitCode();
	}
}

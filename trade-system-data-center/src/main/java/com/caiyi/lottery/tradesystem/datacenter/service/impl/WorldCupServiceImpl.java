//package com.caiyi.lottery.tradesystem.datacenter.service.impl;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import javax.xml.bind.annotation.XmlElementWrapper;
//
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.jdom.Attribute;
//import org.jdom.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.caiyi.lottery.tradesystem.datacenter.service.WorldCupService;
//import com.caiyi.lottery.tradesystem.util.StringUtil;
//import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
//
//import data.constant.DataConstants;
//import data.dto.WorldCupDTO;
//
///**
// * @author LL
// * 世界杯
// */
//@Service
//public class WorldCupServiceImpl implements WorldCupService{
//
//	private Logger log = LoggerFactory.getLogger(WorldCupServiceImpl.class);
//	private static String winPatternStr = "[\\d]{1,2}胜";
//	private static String flatPatternStr = "[\\d]{1,2}平";
//	private static String lossPatternStr = "[\\d]{1,2}负";
//	private static final String WIN_STR = "胜";
//	private final static String FLAT_STR = "平";
//	private final static String LOSS_STR = "负";
//	private final static String ZERO_STR = "0";
//	private final static String EMPTY_STR = "";
//	
//	@Override
//	public void generateData() {
//		 
//		 Pattern winPattern = Pattern.compile(winPatternStr);
//		 Pattern flatPattern = Pattern.compile(flatPatternStr);
//		 Pattern lossPattern = Pattern.compile(lossPatternStr);
//		 double rand = Math.random();
//		 String jczqUrl = DataConstants.JC_ONSALE_URL+"?rn="+rand;
//		 String dataStr = getData(jczqUrl);
//		 if(StringUtil.isEmpty(dataStr)) {
//			 log.info("主站为返回任何数据,无法为您操作。");
//			 return;
//		 }
//		 Map<String, String> rankMap = prepareIntegral();
//		 List<WorldCupDTO> worldCupDTOs = new ArrayList<WorldCupDTO>();
//		 JXmlWrapper xml = JXmlWrapper.parse(dataStr);
//		 List<JXmlWrapper> rowsEle = xml.getXmlNodeList("rows");
//		 System.out.println(rowsEle.size());
//		 for(JXmlWrapper parentWrapper:rowsEle) {
//			 List<JXmlWrapper> xmlNodeList = parentWrapper.getXmlNodeList("row");
//			 for(JXmlWrapper wrapper:xmlNodeList) {
//				 WorldCupDTO worldCupDTO = new WorldCupDTO();
//				 String name = wrapper.getStringValue("@name");
//				 String mname = wrapper.getStringValue("@mname");
//				 String hn = wrapper.getStringValue("@hn");
//				 String gn = wrapper.getStringValue("@gn");
//				 String et = wrapper.getStringValue("@et");
//				 String hm = wrapper.getStringValue("@hm");
//				 String gm = wrapper.getStringValue("@gm");
//				 String spf = wrapper.getStringValue("@spf");
//				 String spfscale = wrapper.getStringValue("@spfscale");
//				 
//				 worldCupDTO.setName(name);
//				 worldCupDTO.setMname(mname);
//				 worldCupDTO.setEt(et);
//				 worldCupDTO.setGn(gn);
//				 worldCupDTO.setHn(hn);
//				 worldCupDTO.setHm(hm);
//				 worldCupDTO.setGm(gm);
//				 worldCupDTO.setSpf(spf);
//				 worldCupDTO.setSpfscale(spfscale);
//				 
//				 String history = wrapper.getStringValue("@history");
//				 String win = ZERO_STR;
//				 String flat = ZERO_STR;
//				 String loss = ZERO_STR;
//				 
//				 if(StringUtil.isNotEmpty(history)) {
//					 Matcher winMatcher = winPattern.matcher(history);
//					 if(winMatcher.find()) {
//						 String group = winMatcher.group(0);
//						 win = group.substring(0,group.indexOf(WIN_STR));
//					 }
//					 Matcher flatMatcher = flatPattern.matcher(history);
//					 if(flatMatcher.find()) {
//						 String group = flatMatcher.group(0);
//						 flat = group.substring(0,group.indexOf(FLAT_STR));
//					 }
//					 Matcher lossMatcher = lossPattern.matcher(history);
//					 if(lossMatcher.find()) {
//						 String group = lossMatcher.group(0);
//						 loss = group.substring(0,group.indexOf(LOSS_STR));
//					 }
//				 }
//				 
//				 worldCupDTO.setHisWin(win);
//				 worldCupDTO.setHisFlat(flat);
//				 worldCupDTO.setHisLoss(loss);
//				 
//				 String item = wrapper.getStringValue("@itemid");
//				 String hRes = EMPTY_STR;
//				 String gRes = EMPTY_STR;
//				 String hid = EMPTY_STR;
//				 String gid = EMPTY_STR;
//				 if(StringUtil.isNotEmpty(item)) {
//					 String[] basicData = getBasicData(item);
//					 if(null!=basicData) {
//						 hRes = basicData[0];
//						 gRes = basicData[1];
//						 hid = basicData[2];
//						 gid = basicData[3];
//					 }
//				 }
//				 worldCupDTO.setHRes(hRes);
//				 worldCupDTO.setGRes(gRes);
//				 if(StringUtil.isNotEmpty(hid)&&StringUtil.isNotEmpty(gid)) {
//					 worldCupDTO.setHid(hid);
//					 worldCupDTO.setGid(gid);
//					 String hRankJsonStr = rankMap.get(hid);
//					 String gRankJsonStr = rankMap.get(gid);
//					 assembleWorldCupDTO(worldCupDTO,hRankJsonStr,gRankJsonStr);
//				 }
//				 worldCupDTOs.add(worldCupDTO);
//			 }
//		 }
//		 System.out.println(worldCupDTOs.size());
//		 if(null!=worldCupDTOs&&worldCupDTOs.size()>0) {
//			 String jsonString = JSONObject.toJSONString(worldCupDTOs);
//			 //TODO 写出到一个路径
//		 }
//		 
//	}
//	
//	private void assembleWorldCupDTO(WorldCupDTO worldCupDTO, String hRankJsonStr,String gRankJsonStr) {
//		if(null==worldCupDTO||StringUtil.isEmpty(hRankJsonStr)||StringUtil.isEmpty(gRankJsonStr)) {
//			return;
//		}
//		JSONObject hObject = JSONObject.parseObject(hRankJsonStr);
//		JSONObject gObject = JSONObject.parseObject(gRankJsonStr);
//		worldCupDTO.setHDrawNum(hObject.getString("drawNum"));
//		worldCupDTO.setHGoalNum(hObject.getString("goalNum"));
//		worldCupDTO.setHFumbleNum(hObject.getString("fumbleNum"));
//		worldCupDTO.setHWinNum(hObject.getString("winNum"));
//		worldCupDTO.setHLoseNum(hObject.getString("loseNum"));
//		worldCupDTO.setHOrder(hObject.getString("order"));
//		worldCupDTO.setHGroupName(hObject.getString("groupName"));
//		worldCupDTO.setHIntrgral(hObject.getString("intrgral"));
//		
//		worldCupDTO.setGDrawNum(gObject.getString("drawNum"));
//		worldCupDTO.setGGoalNum(gObject.getString("goalNum"));
//		worldCupDTO.setGFumbleNum(gObject.getString("fumbleNum"));
//		worldCupDTO.setGWinNum(gObject.getString("winNum"));
//		worldCupDTO.setGLoseNum(gObject.getString("loseNum"));
//		worldCupDTO.setGOrder(gObject.getString("order"));
//		worldCupDTO.setGGroupName(gObject.getString("groupName"));
//		worldCupDTO.setGIntrgral(gObject.getString("intrgral"));
//	}
//
//	/**
//	 * 准备积分排名数据
//	 * @return
//	 */
//	private Map<String,String> prepareIntegral() {
//		//TODO 这里读取wxy的json数据
//		File file = new File("E:/test.json");
//		Map<String,String> map = new HashMap<String,String>();
//		try {
//			FileReader fr = new FileReader(file);
//			BufferedReader br = new BufferedReader(fr);
//			StringBuilder sb = new StringBuilder();
//			String temp = "";
//			if(null!=(temp=br.readLine())) {
//				sb.append(temp);
//			}
//			String json = sb.toString();
//			JSONObject jsonObject = JSONObject.parseObject(json);
//			Set<String> teamSet = jsonObject.keySet();
//			/**
//			 * key teamQtId
//			 * val teamIntegralJsonStr
//			 */
//			for(String key:teamSet) {
//				JSONArray jsonArray = jsonObject.getJSONArray(key);
//				for(int i=0;i<jsonArray.size();i++) {
//					JSONObject innerObject = jsonArray.getJSONObject(i);
//					String jsonStr = jsonArray.getString(i);
//					String teamId = innerObject.getString("teamId");
//					map.put(teamId,jsonStr);
//				}
//			}
//		} catch (Exception e) {
//			log.error("解析世界杯积分排名数据异常,{}",e);
//		}
//		return map;
//	}
//	
//	/**
//	 * 获取对应比赛的对阵基本面信息
//	 * @param item
//	 * @return
//	 */
//	private String[] getBasicData(String item) {
//		String[] dataArr = new String[4];
//		/**
//		 * 赛程基本面URL
//		 */
//		String basicUrl = DataConstants.MATCH_BASIC_FILE_PATH+item+".xml";
//		JXmlWrapper basicWrapper = null;
//		try {
//			String data = getData(basicUrl);
//			if(StringUtil.isNotEmpty(data)){
//				basicWrapper = JXmlWrapper.parse(data);
//			}else {
//				return null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if(null!=basicWrapper) {
//			Element xmlRoot = basicWrapper.getXmlRoot();
//			String hidAndName = xmlRoot.getAttributeValue("hidAndName");
//			String gidAndName = xmlRoot.getAttributeValue("gidAndName");
//			String hid = "";
//			String gid = "";
//			if(StringUtil.isNotEmpty(hidAndName)&&hidAndName.contains("-")) {
//				hid = hidAndName.split("-")[0];
//			}else {
//				log.error("基本面数据有误,文件url:{}",basicUrl);
//				return null;
//			}
//			if(StringUtil.isNotEmpty(gidAndName)&&gidAndName.contains("-")) {
//				gid = gidAndName.split("-")[0];
//			}else {
//				log.error("基本面数据有误,文件url:{}",basicUrl);
//				return null;
//			}
//			List<Element> children = xmlRoot.getChildren("row");
//			if(null!=children&&children.size()>0) {
//				int hCount = 0;
//				int gCount = 0;
//				StringBuilder hResult = new StringBuilder();
//				StringBuilder gResult = new StringBuilder();
//				for(Element child:children) {
//					String teamId = child.getAttributeValue("teamId");
//					if(teamId.equals(hid)) {
//						if(hCount<5) {
//							hCount = hCount + 1;
//						}else {
//							continue;
//						}
//						hResult.append(","+getWinOrFail(child, hid));
//					}
//					if(teamId.equals(gid)) {
//						if(gCount<5) {
//							gCount = gCount + 1;
//						}else {
//							continue;
//						}
//						gResult.append(","+getWinOrFail(child,gid));
//					}
//				}
//				String hResStr = hResult.toString();
//				String gResStr = gResult.toString();
//				hResStr = hResStr.startsWith(",")?hResStr.substring(1):hResStr;
//				gResStr = gResStr.startsWith(",")?gResStr.substring(1):gResStr;
//				dataArr[0] = hResStr;
//				dataArr[1] = gResStr;
//				dataArr[2] = hid;
//				dataArr[3] = gid;
//			}
//		}
//		return dataArr;
//	}
//	
//	/**
//	 * 根据比分获取赛果，这里的teamId是相对的，不是客队或主队，相对于这个队
//	 * @param wrap
//	 * @param teamId
//	 * @return
//	 */
//	private String getWinOrFail(Element ele,String teamId) {
//		
//		String hScr = ele.getAttributeValue("hsc");
//		String gScr = ele.getAttributeValue("asc");
//		String teamHid = ele.getAttributeValue("teamHId");
//		int hScore = Integer.parseInt(hScr);
//		int gScore = Integer.parseInt(gScr);
//		if(hScore>gScore) {
//			if(teamHid.equals(teamId)) {
//				return WIN_STR;
//			}else {
//				return LOSS_STR;
//			}
//		}else if(hScore<gScore){
//			if(teamHid.equals(teamId)) {
//				return LOSS_STR;
//			}else {
//				return WIN_STR;
//			}
//		}else {
//			return FLAT_STR;
//		}
//	}
//	
//	
//	private String getData(String url){
//		HttpClient client = new HttpClient();
//        GetMethod mothod = new GetMethod(url);
//        mothod.getParams().setContentCharset("utf-8");
//        String respStr = "";
//        try {
//        	client.executeMethod(mothod);
//        	respStr = mothod.getResponseBodyAsString();
//        			 
//        } catch (Exception e) {
//			e.printStackTrace();
//		}
//        return respStr;
//	}
//	
//}

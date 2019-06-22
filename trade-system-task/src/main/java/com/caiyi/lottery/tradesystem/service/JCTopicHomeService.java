package com.caiyi.lottery.tradesystem.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import data.constant.DataConstants;
import data.dto.MatchInformationDto;
import data.dto.TeamIntegralDTO;
import data.dto.WorldCupDTO;
import data.dto.WorldCupMatchInfoDTO;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JCTopicHomeService {

	private static final String MATCHPATH = "/opt/export/data/app/topic/";
	private static final String MATCHINFOPATH = "/opt/export/data/app/topic/matchData/";
	// todo 测试修改 世界杯
	private static final String WORDCUP_MNAME = "世界杯";
	private static final Long RETRY_WAIT_TIME = 6000L;
	private Map<String, WorldCupDTO> numMap = new HashMap<>();
	
	private static Map<String,String> informationMap = new HashMap<>();
	
	static {
		informationMap.put("0", "阵容 ");      //	阵容  阵容介绍，比如有球星                     
		informationMap.put("1", "伤停");       //	伤停  主力伤停                           
		informationMap.put("2", "战意");       //	战意                                 
		informationMap.put("3", "言论");       //	言论  赛前言论                           
		informationMap.put("4", "实力");       //	实力  双方实力对比                         
		informationMap.put("5", "赛程");       //	赛程  场地、天气、赛程排序                     
		informationMap.put("6", "状态");       //	状态  个别球星状态好坏或者球队状态好坏               
		informationMap.put("7", "交锋");       //	交锋  交锋数据，特别是比较极端的交锋数据、主帅、球员等数据     
	}
	
	/**
	 * 分为2个部分： 1.比赛对阵 2.对阵的基本详情（战绩、积分、情报）
	 */
	public void generatorHomeTask() {

		numMap = loadMatchNum();
		List<WorldCupDTO> matchList = createHomeXml(MATCHPATH, "jcTopicMatch.json");
		createMatchXml(matchList);
	}

	/**
	 * 加载轮次数据
	 * 
	 * @return
	 */
	private Map<String, WorldCupDTO> loadMatchNum() {

		Map<String, WorldCupDTO> numMap = new HashMap<>();
		String responseStr = sendHttpReq(DataConstants.TOPICMATCHDATA_URL + "?rand=" + System.currentTimeMillis()); // 获取世界杯赛程

		try {
			JSONObject parseObject = JSON.parseObject(responseStr);
			if (parseObject == null) {
				log.warn("加载世界杯赛程数据失败--");
				return numMap;
			}
			JSONArray jsonArray = parseObject.getJSONObject("data").getJSONArray("roundData");
			if (jsonArray != null && jsonArray.size() > 0) {
				for (int i = 0; i < jsonArray.size(); i++) {

					JSONObject obj1 = jsonArray.getJSONObject(i);
					String num = obj1.getString("roundName");
					JSONArray matchs1 = obj1.getJSONArray("dateData");// 取到当前轮次的比赛了，但内部按日期进行分组了
					for (int j = 0; j < matchs1.size(); j++) {
						JSONObject obj2 = matchs1.getJSONObject(j);
						JSONArray matchs2 = obj2.getJSONArray("matchData");// 取到日期对应的比赛了
						log.info("日期：" + obj2.getString("date") + " 有" + matchs2.size() + "场比赛");

						for (int n = 0; n < matchs2.size(); n++) {
							JSONObject matchE = matchs2.getJSONObject(n);
							Long matchId = matchE.getLong("matchId");
							String groupName = matchE.getString("groupName"); // 分组
							String numStr = "";
							WorldCupDTO dto = new WorldCupDTO();
							numStr = num;
							if (num.indexOf("小组") != -1) {
								numStr = num.replace("小组赛", "小组赛-" + groupName + "组");
							}
							dto.setMatchId(matchId.toString());
							dto.setMatchNum(numStr);
							dto.setHid(matchE.getLong("homeTeamId").toString());
							dto.setGid(matchE.getLong("awayTeamId").toString());

							dto.setGroupName(matchE.getString("groupName"));
							numMap.put(matchId.toString(), dto);

						}
					}
				}
			}
		} catch (Exception e) {
			log.error("获取轮次数据异常，获取数据为：{}",responseStr, e);
		}

		return numMap;
	}

	/**
	 * 生成竞彩世界杯头部文件
	 * 
	 * @param path
	 * @param fileName
	 * @return
	 */
	public List<WorldCupDTO> createHomeXml(String path, String fileName) {
		List<WorldCupDTO> matchList = new ArrayList<>();
		List<JXmlWrapper> rows = null; 
		
		String responseStr = sendHttpReq(DataConstants.JC_ONSALE_URL + "?rand=" + System.currentTimeMillis()); // 获取开售的对阵
		if (StringUtils.isBlank(responseStr)) {
			log.warn("无开售的对阵，不生成世界杯对阵文件");
			return matchList;
		}
		
		
		try {
			JXmlWrapper xml = JXmlWrapper.parse(responseStr);	
			rows = xml.getXmlNodeList("rows");
		} catch (Exception e) {
			log.error("解析数据异常,原始数据为：{}",responseStr,e);
			return matchList;
		}
		
		if (rows == null || rows.size() == 0) {
			log.info("开售比赛数据为null或比赛数据长度为0，爬取开售比赛返回的数据为：{}", responseStr);
			return matchList;
		}
		for(JXmlWrapper rowsE: rows) {
			
			List<JXmlWrapper> xmlNodeList = rowsE.getXmlNodeList("row");
			if (xmlNodeList == null || xmlNodeList.size() == 0) {
				log.info("开售比赛数据为null或比赛数据长度为0，爬取开售比赛返回的数据为：{}", responseStr);
				continue;
			}	
		
			for (JXmlWrapper rowE : xmlNodeList) {
				WorldCupDTO worldCupDTO = new WorldCupDTO();
				
				if(StringUtils.isBlank(rowE.getStringValue("@mname")) || ! WORDCUP_MNAME.equals(rowE.getStringValue("@mname"))) {
					log.warn("非世界杯比赛，该记录为：{}",rowE.toXmlString());
					continue ;
				}

				if ((512 & rowE.getIntValue("@isale")) <= 0) {
					log.warn("世界杯比赛，单关胜平负未开售，该记录为：{}",rowE.toXmlString());
					continue;
				}
				try {
					worldCupDTO.setItemId(
							StringUtils.isNotBlank(rowE.getStringValue("@itemid")) ? rowE.getStringValue("@itemid") : "");
					worldCupDTO.setMatchId(rowE.getStringValue("@rid"));
					
					worldCupDTO.setName(
							StringUtils.isNotBlank(rowE.getStringValue("@name")) ? rowE.getStringValue("@name") : ""); // qc
					worldCupDTO.setMname(
							StringUtils.isNotBlank(rowE.getStringValue("@mname")) ? rowE.getStringValue("@mname") : ""); // leageuName
					worldCupDTO.setEt(StringUtils.isNotBlank(rowE.getStringValue("@et")) ? rowE.getStringValue("@et") : ""); // 投注截至时间
					worldCupDTO.setHn(StringUtils.isNotBlank(rowE.getStringValue("@hn")) ? rowE.getStringValue("@hn") : "-"); // 主队
					worldCupDTO.setGn(StringUtils.isNotBlank(rowE.getStringValue("@gn")) ? rowE.getStringValue("@gn") : "-");
					worldCupDTO.setHm(StringUtils.isNotBlank(rowE.getStringValue("@hm")) ? rowE.getStringValue("@hm") : ""); // 排名
					worldCupDTO.setGm(StringUtils.isNotBlank(rowE.getStringValue("@gm")) ? rowE.getStringValue("@gm") : "");
					worldCupDTO
					.setSpf(StringUtils.isNotBlank(rowE.getStringValue("@spf")) ? rowE.getStringValue("@spf") : ""); // spf赔率
					worldCupDTO.setSpfscale(
							StringUtils.isNotBlank(rowE.getStringValue("@spfscale")) ? rowE.getStringValue("@spfscale")
									: ""); // 投注比例
					
					WorldCupDTO numDto = numMap.get(rowE.getStringValue("@rid"));
					if (numDto == null) {
						numDto = new WorldCupDTO();
					}
					worldCupDTO.setMatchNum(numDto.getMatchNum());
					worldCupDTO.setHid(numDto.getHid());
					worldCupDTO.setGid(numDto.getGid());
					worldCupDTO.setGroupName(numDto.getGroupName());
					if(numDto.getMatchNum().indexOf("小组")==-1) {
						worldCupDTO.setMatchStage("1");					
					}
					
					matchList.add(worldCupDTO);
					
				} catch (Exception e) {
					log.error("解析开售比赛文件异常，该条数据为：{}", rowE.toXmlString(), e);
				}
			}
		}

		try {
			JSONObject json = new JSONObject();
			json.put("code", "0");
			json.put("desc", "查询成功");
			json.put("serverTime", DateUtil.getCurrentDateTime());
			
			Map<String,Object> result = new HashMap<>();
			result.put("path",  "/qtjsbf/topic/pic/team/");
			result.put("matchData",matchList);
			json.put("data", result);	
			
//			json.put("data", matchList);			
//			json.put("path", "/qtjsbf/topic/pic/team/");
				
			writerFile(path, fileName, json.toJSONString());
		} catch (Exception e) {
			log.error("生成竞彩世界杯对阵文件异常", e);
		}
		return matchList;
	}

	/**
	 * 生成竞彩世界杯尾部文件
	 * 
	 * @param matchList
	 */
	public void createMatchXml(List<WorldCupDTO> matchList) {

		if (matchList == null || matchList.size() == 0) {
			log.error("竞彩世界杯数据为null，不生成比赛战绩详情文件,list：{}", matchList);
			return;
		}

		Map<String, TeamIntegralDTO> integralMap = loadItegral();
		Map<String, JSONArray> infomationMap = loadInfomation();

		for (WorldCupDTO dto : matchList) {
			WorldCupMatchInfoDTO info = new WorldCupMatchInfoDTO();
			info.setMatchId(dto.getMatchId());
			info.setItemId(dto.getItemId());
			info.setSort(dto.getItemId());
			info.setHid(dto.getHid());
			info.setGid(dto.getGid());
			info.setQc(StringUtils.isNotBlank(dto.getItemId()) && dto.getItemId().length()>6 ? dto.getItemId().substring(0, 6):"" );

			setZJ(dto, info);// 近期战绩
			setIntegral(dto, info, integralMap);// 积分
			setIntelligences(dto, info, infomationMap);// 情报

			try {
				JSONObject obj = new JSONObject();
				obj.put("code", "0");
				obj.put("desc", "查询成功");
				obj.put("serverTime", DateUtil.getCurrentDateTime());
				
				Map<String,Object> result = new HashMap<>();
				result.put("path", "/qtjsbf/topic/pic/team/");
				result.put("matchData", info);
				obj.put("data", result);
				
//				obj.put("data", info);
//				obj.put("path", "/qtjsbf/topic/pic/team/");
				
				writerFile(MATCHINFOPATH, info.getItemId() + ".json", obj.toString());
			} catch (Exception e) {
				log.error("生成竞彩世界杯比赛详情文件异常", e);
			}
		}

	}

	/**
	 * 加载情报文件
	 * 
	 * @return
	 */
	private Map<String, JSONArray> loadInfomation() {

		Map<String, JSONArray> map = new HashMap<>();
		String respStr = sendHttpReq(DataConstants.TOPICMATCH_INFORMATION_URL + "?rand=" + System.currentTimeMillis());
		if (StringUtils.isBlank(respStr)) {
			log.warn("请求情报数据为null，返回--,响应数据为：{}", respStr);
			return map;
		}

		try {
			JSONObject parseObject = JSON.parseObject(respStr);
			JSONObject obj = parseObject.getJSONObject("data");
			if (obj != null) {
				for (Iterator<String> it = obj.keySet().iterator(); it.hasNext();) {
					String matchId = it.next();
					JSONArray Js = obj.getJSONArray(matchId);
					map.put(matchId, Js);
				}
			}

		} catch (Exception e) {
			log.error("解析情报数据异常--", e);
		}
		return map;
	}

	/**
	 * 加载积分文件
	 * 
	 * @return
	 */
	private Map<String, TeamIntegralDTO> loadItegral() {
		Map<String, TeamIntegralDTO> map = new HashMap<>();
		String respStr = sendHttpReq(DataConstants.TOPICMATCH_INTEGRAL_URL + "?rand=" + System.currentTimeMillis());
		if (StringUtils.isBlank(respStr)) {
			log.warn("请求积分排名失败，响应数据为：{}", respStr);
			return map;
		}

		try {
			JSONObject obj = JSON.parseObject(respStr);
			if (obj == null) {
				log.warn("解析球队排名失败");
				return map;
			}
			JSONArray jsonArray1 = obj.getJSONObject("data").getJSONArray("data");

			for (int i = 0; i < jsonArray1.size(); i++) {

				JSONArray jsonArray2 = jsonArray1.getJSONObject(i).getJSONArray("teamData");
				for (int j = 0; j < jsonArray2.size(); j++) {

					JSONObject jsonObject = jsonArray2.getJSONObject(j);
					TeamIntegralDTO team = new TeamIntegralDTO();
					team.setGroupName(StringUtils.isNotBlank(jsonObject.getString("groupName"))
							? jsonObject.getString("groupName")
							: "");
					team.setIntrgral(
							StringUtils.isNotBlank(jsonObject.getString("intrgral")) ? jsonObject.getString("intrgral")
									: "");
					team.setOrder(
							StringUtils.isNotBlank(jsonObject.getString("order")) ? jsonObject.getString("order") : "");
					team.setTeamId(
							StringUtils.isNotBlank(jsonObject.getString("teamId")) ? jsonObject.getString("teamId")
									: "");
					team.setTeamName(
							StringUtils.isNotBlank(jsonObject.getString("teamName")) ? jsonObject.getString("teamName")
									: "");
					team.setWinNum(
							StringUtils.isNotBlank(jsonObject.getString("winNum")) ? jsonObject.getString("winNum")
									: "");
					team.setDrawNum(
							StringUtils.isNotBlank(jsonObject.getString("drawNum")) ? jsonObject.getString("drawNum")
									: "");
					team.setLoseNum(
							StringUtils.isNotBlank(jsonObject.getString("loseNum")) ? jsonObject.getString("loseNum")
									: "");
					team.setFumbleNum(StringUtils.isNotBlank(jsonObject.getString("fumbleNum"))
							? jsonObject.getString("fumbleNum")
							: "");
					team.setGoalNum(
							StringUtils.isNotBlank(jsonObject.getString("goalNum")) ? jsonObject.getString("goalNum")
									: "");
					map.put(team.getTeamId(), team);

				}
			}
		} catch (Exception e) {
			log.error("解析积分排名异常", e);
		}

		return map;
	}

	private void setIntelligences(WorldCupDTO dto, WorldCupMatchInfoDTO info, Map<String, JSONArray> infomationMap) {

		if (infomationMap == null || infomationMap.size() == 0) {
			log.warn("无情报数据，不填充情报数据,matchId:{}", dto.getMatchId());
			return;
		}

		JSONArray jsonArray = infomationMap.get(dto.getMatchId());
		if (jsonArray == null || jsonArray.size() == 0) {
			log.warn("无情报数据，不填充情报数据,matchId:{}", dto.getMatchId());
			return;
		}

		List<MatchInformationDto> list = new ArrayList<>();
		for (int i = 0; i < jsonArray.size(); i++) {
			try {
				JSONObject obj = jsonArray.getJSONObject(i);
				if (obj != null) {
					String type = obj.getString("type");
					String content = "";
					String title = "";

					MatchInformationDto information = new MatchInformationDto();
					content = obj.getString("content");
					title = obj.getString("title");
					
					information.setMatchId(dto.getMatchId());
					information.setIteamId(dto.getItemId());
					information.setLabel(informationMap.get(obj.getString("type")));
					information.setLabelType(obj.getString("type"));
					information.setTitle(title);
					information.setContent(content);
					information.setCreateTime(obj.getString("publishTime"));
					information.setSort(StringUtils.isNotBlank(obj.getString("sort")) ? Integer.parseInt(obj.getString("sort")):10);
					
					list.add(information);

				}
			} catch (Exception e) {
				log.error("解析情报数据异常，对应比赛为：{}", dto.getMatchId(), e);
			}
		}

		info.setIntelligences(list);
	}

	private void setInformation(MatchInformationDto i1, String title, String content, String publishTime) {
		i1.setTitle(title);
		i1.setContent(content);
		i1.setCreateTime(publishTime);
	}

	private void setIntegral(WorldCupDTO dto, WorldCupMatchInfoDTO info, Map<String, TeamIntegralDTO> integralMap) {

		boolean flag = false;
		if (integralMap == null || integralMap.size() == 0) {
			log.warn("无小组积分数据，不生成小组积分数据,matchid:{}", dto.getMatchId());
			return;
		} else if (StringUtils.isBlank(dto.getMatchNum()) || dto.getMatchNum().indexOf("小组") < 0) {
			log.info("非世界杯小组赛，不加载球队小组积分数据,matchid:{}", dto.getMatchId());
			return;
		}

		TeamIntegralDTO hteam = integralMap.get(dto.getHid());
		TeamIntegralDTO gteam = integralMap.get(dto.getGid());
		if (hteam == null || hteam == null) {
			log.warn("该球队无小组积分数据，不生成小组积分数据，小组积分为【主】：{},小组积分为【客】：{},对阵数据：{}", hteam, gteam, dto);
			return;
		}
		if (StringUtils.isNotBlank(hteam.getOrder()) && StringUtils.isNotBlank(gteam.getOrder())
				&& hteam.getOrder().compareToIgnoreCase(gteam.getOrder()) > 0) {
			flag = true;
		}
		if (flag) {
			info.setIntegral1(setTeamIntegral(hteam));
			info.setIntegral2(setTeamIntegral(gteam));
		} else {
			info.setIntegral1(setTeamIntegral(gteam));
			info.setIntegral2(setTeamIntegral(hteam));
		}
	}

	private String setTeamIntegral(TeamIntegralDTO team) {

		// 小组积分 ：按积分高低排列 ，各项为：排名，teamid，球队名称，比赛场次，spf，进失，积分
		StringBuffer sb = new StringBuffer();

		int num = Integer.parseInt(StringUtils.isNotBlank(team.getWinNum()) ? team.getWinNum() : "0")
				+ Integer.parseInt(StringUtils.isNotBlank(team.getDrawNum()) ? team.getDrawNum() : "0")
				+ Integer.parseInt(StringUtils.isNotBlank(team.getLoseNum()) ? team.getLoseNum() : "0");
		sb.append(StringUtils.isNotBlank(team.getOrder()) ? team.getOrder() : "-").append(";")
				.append(StringUtils.isNotBlank(team.getTeamId()) ? team.getTeamId() : "-").append(";")
				.append(StringUtils.isNotBlank(team.getTeamName()) ? team.getTeamName() : "-").append(";").append(num)
				.append(";").append(StringUtils.isNotBlank(team.getWinNum()) ? team.getWinNum() : "0").append(",")
				.append(StringUtils.isNotBlank(team.getDrawNum()) ? team.getDrawNum() : "0").append(",")
				.append(StringUtils.isNotBlank(team.getLoseNum()) ? team.getLoseNum() : "0").append(";")
				.append(StringUtils.isNotBlank(team.getGoalNum()) ? team.getGoalNum() : "0").append(",")
				.append(StringUtils.isNotBlank(team.getFumbleNum()) ? team.getFumbleNum() : "0").append(";")
				.append(StringUtils.isNotBlank(team.getIntrgral()) ? team.getIntrgral() : "0");

		return sb.toString();
	}

	private void setZJ(WorldCupDTO dto, WorldCupMatchInfoDTO info) {

		String responseStr = sendHttpReq(
				DataConstants.MATCH_BASIC_FILE_PATH + dto.getItemId() + ".xml?rand=" + System.currentTimeMillis()); // 获取开售的对阵
		if (StringUtils.isBlank(responseStr)) {
			log.info("响应数据为null，丢弃数据，matchid:{},数据为:{}", dto.getMatchId(), responseStr);
			return;
		}

		try {
			JXmlWrapper rowsE = JXmlWrapper.parse(responseStr);
			if (StringUtils.isBlank(dto.getHid()) || StringUtils.isBlank(dto.getGid())) {
				dto.setHid(rowsE.getStringValue("@hidAndName").split("-")[0]);
				dto.setGid(rowsE.getStringValue("@gidAndName").split("-")[0]);
				info.setHid(dto.getHid());
				info.setGid(dto.getGid());
			}

			// 加载球队近期战绩
			List<JXmlWrapper> rowEList = rowsE.getXmlNodeList("row");
			if (rowEList != null && rowEList.size() > 0) {
				StringBuffer hsb = new StringBuffer("");
				StringBuffer gsb = new StringBuffer("");

				for (JXmlWrapper rowE : rowEList) {
					String result = getMatchResult(rowE);
					if (StringUtils.isNotBlank(result) && result.contains((dto.getHid()))) {
						hsb.append(result.split("_")[1]).append(",");
					} else if (StringUtils.isNotBlank(result) && result.startsWith(dto.getGid())) {
						gsb.append(result.split("_")[1]).append(",");
					}
				}

				info.setHHistory(
						reverseByBit(hsb.length() >= 10 ? hsb.substring(0, 9) : hsb.substring(0, hsb.length() - 1)));
				info.setGHistory(
						reverseByBit(gsb.length() >= 10 ? gsb.substring(0, 9) : gsb.substring(0, gsb.length() - 1)));
			}

			// 加载历史交锋
			List<JXmlWrapper> vsrowEList = rowsE.getXmlNodeList("vsrow");
			if (vsrowEList != null && vsrowEList.size() > 0) {
				info.setHistoryMatch(loadHistoryMatch(vsrowEList, dto));
			}else {
				info.setHistoryMatch("0,0,0");
			}

		} catch (Exception e) {
			log.error("解析基本面文件数据异常，跳过此条数据,matchid:{}", dto.getMatchId(), e);
		}

	}

	private String loadHistoryMatch(List<JXmlWrapper> vsrowEList, WorldCupDTO dto) {
		StringBuffer sb_history = new StringBuffer("");
		String result = "";
		int size = vsrowEList.size() > 10 ? 10 : vsrowEList.size();

		for (int i = 0; i < size; i++) {
			JXmlWrapper vsrowE = vsrowEList.get(i);
			boolean flag = false;// 主客标识
			if (dto.getHid().equals(vsrowE.getStringValue("@m7Hid"))) {
				flag = true; // 主
			}

			String hsc = vsrowE.getStringValue("@hsc");
			String gsc = vsrowE.getStringValue("@asc");
			if (hsc.compareTo(gsc) > 0) {
				boolean aa = flag ? sb_history.append("胜") != null : sb_history.append("负") != null;
			} else if (hsc.compareTo(gsc) == 0) {
				sb_history.append("平");
			} else {
				boolean aa = flag ? sb_history.append("负") != null : sb_history.append("胜") != null;
			}
			sb_history.append(",");
		}
		result = result + containsNum(sb_history.toString(), "胜") + ",";
		result = result + containsNum(sb_history.toString(), "平") + ",";
		result = result + containsNum(sb_history.toString(), "负");

		return result;
	}

	/**
	 * 计算参数1中参数2出现的次数
	 * 
	 * @param param2
	 * @param param2
	 */
	private int containsNum(String param1, String param2) {
		int num = 0;
		if (param1 != null && param2 != null && param1.indexOf(param2) != -1) {
			num = param1.split(param2).length - 1;
		}
		return num;
	}

	private String getMatchResult(JXmlWrapper rowE) {
		StringBuffer result = new StringBuffer("");
		String teamId = rowE.getStringValue("@teamId");
		result.append(teamId).append("_");
		boolean flag = false;// 主客标识
		if (teamId.equals(rowE.getStringValue("@teamHId"))) {
			flag = true; // 主
		}

		String hsc = rowE.getStringValue("@hsc");
		String gsc = rowE.getStringValue("@asc");
		if (hsc.compareToIgnoreCase(gsc) > 0) {
			boolean aa = flag ? result.append("胜") != null : result.append("负") != null;
		} else if (hsc.compareToIgnoreCase(gsc) == 0) {
			result.append("平");
		} else {
			boolean aa = flag ? result.append("负") != null : result.append("胜") != null;
		}

		return result.toString();
	}

	private String sendHttpReq(String url) {
		HttpClient client = new HttpClient();
		GetMethod mothod = new GetMethod(url);
		mothod.getParams().setContentCharset("utf-8");
		log.info("即将请求的url为：{},请求方式为：{}", url, mothod.getName());
		String respStr = "";
		int flag = 0;
		while (flag < 3) {
			try {
				respStr = executeHttpReq(client, mothod);
				flag = 4;
			} catch (Exception e) {
				log.error("发送请求异常，当前重试次数：{},请求url为：{}", flag++, url, e);
				log.info("开始重试发送http请求，重试url:{}", url);
				try {
					Thread.currentThread().sleep(RETRY_WAIT_TIME);
				} catch (InterruptedException e1) {
					log.error("睡出来的异常", e);
				}
			}
		}
		return respStr;
	}

	private String reverseByBit(String str) {
		if (str == null || str.length() == 1) {
			return null;
		}
		char[] ch = str.toCharArray();// 字符串转换成字符数组
		int len = str.length();
		for (int i = 0; i < len / 2; i++) {
			ch[i] ^= ch[len - 1 - i];
			ch[len - 1 - i] ^= ch[i];
			ch[i] ^= ch[len - 1 - i];
		}
		return new String(ch);
	}

	private String executeHttpReq(HttpClient client, GetMethod mothod) throws IOException {
		client.executeMethod(mothod);
		return mothod.getResponseBodyAsString();
	}

	private void writerFile(String path, String fileName, String resp) throws Exception {
		if (StringUtils.isBlank(path) || StringUtils.isBlank(fileName) || StringUtils.isBlank(resp)) {
			log.error("文件路径或文件名或响应数据为null，不生成文件，path:{},fileName:{},data:{}", path, fileName, resp);
			return;
		}

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		OutputStream out = null;
		OutputStreamWriter outputStreamWriter = null;
		BufferedWriter writer = null;
		try {
			out = new FileOutputStream(path + fileName);
			outputStreamWriter = new OutputStreamWriter(out, "utf-8");
			writer = new BufferedWriter(outputStreamWriter);
			writer.write(resp);
			writer.flush();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outputStreamWriter != null) {
				try {
					outputStreamWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}

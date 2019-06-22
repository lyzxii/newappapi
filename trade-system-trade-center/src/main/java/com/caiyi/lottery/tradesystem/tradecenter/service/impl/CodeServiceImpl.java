package com.caiyi.lottery.tradesystem.tradecenter.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caipiao.game.GameContains;
import com.caipiao.plugin.GamePlugin_50;
import com.caipiao.plugin.helper.CodeFormatException;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.jcutil.JcCastCode;
import com.caipiao.plugin.jcutil.JcItemCodeUtil;
import com.caipiao.plugin.lqutil.LqCastCode;
import com.caipiao.plugin.lqutil.LqItemCodeUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.split.GameSplit;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.PeriodMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.ProjMapper;
import com.caiyi.lottery.tradesystem.tradecenter.service.CodeService;
import com.caiyi.lottery.tradesystem.util.code.CodesUtil;
import com.caiyi.lottery.tradesystem.util.code.CountCodeUtil;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.caiyi.lottery.tradesystem.tradecenter.util.FileCastCodeUtil;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.tradecenter.util.trade.LimitCodeUtil;
import com.caiyi.lottery.tradesystem.util.BeanUtilWrapper;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.util.xml.Xml;

import lombok.extern.slf4j.Slf4j;
import trade.bean.CodeBean;
import trade.bean.TradeBean;
import trade.constants.TradeConstants;
import trade.pojo.PeriodPojo;

@Slf4j
@Service
public class CodeServiceImpl implements CodeService {

	private HashMap<String, GamePluginAdapter> mapsPlugin = new HashMap<String, GamePluginAdapter>();

	@Autowired
	PeriodMapper periodMappper;
	@Autowired
	ProjMapper projMapper;

	@Override
	public int countCodesMoney(TradeBean bean) throws Exception {
		int money = -1;
		String gid = bean.getGid();
		GamePluginAdapter plugin = getGamePluginAdapter(bean);

		if (plugin != null) {
			boolean isDT = false;
			boolean hasSXL = false;
			try {
				String codes = bean.getCodes();
				if (bean.getFflag() == 1) {
					if (!CheckUtil.isNullString(codes)) {
						String pids = bean.getPid();
						String[] tmp = PluginUtil.splitter(pids, ",");
						String ischase = bean.getIschase();
						if (tmp.length > 1 || "1".equals(ischase)) {
							// 文件上传发起追号
							codes = FileCastCodeUtil.getCodesFromPathSZ(bean.getGid(), "/", FileConstant.BASE_PATH,
									codes, bean.getPlay() + "", log);
						} else {
							codes = FileCastCodeUtil.getCodesFromFile(bean.getGid(), pids, FileConstant.BASE_PATH,
									codes, bean.getPlay() + "", log);
						}
					}
				}
				log.info("发起人uid:" + bean.getUid() + "   code:" + codes + "  multi:" + bean.getMuli());
				if ("01".equals(gid) || "07".equals(gid) || "50".equals(gid) || "81".equals(gid) || "56".equals(gid)
						|| "59".equals(gid) || "55".equals(gid)) {// TODO
					GameSplit split = GameSplit.getGameSplit(gid);
					StringBuffer sb = new StringBuffer();
					String[] tmp = PluginUtil.splitter(codes, ";");
					for (int i = 0; i < tmp.length; i++) {
						String code = tmp[i];
						if (!StringUtil.isEmpty(code)) {
							if (split == null) {
								sb.append(code);
							} else {
								sb.append(split.getSplitCode(code));
							}
							sb.append(";");
						}
					}
					tmp = null;
					String[] splcod = sb.toString().split(";");
					log.info("票张数：" + splcod.length + " 用户名:" + bean.getUid() + " gid:" + bean.getGid() + " pid:"
							+ bean.getPid() + " codes:" + bean.getCodes());
					if ("56".equals(gid) || "59".equals(gid) || "55".equals(gid)) {
						if (splcod.length > 10) {
							throw new Exception("11运夺金单倍票张数不能超过5张");
						}
					} else {
						CountCodeUtil.sz(splcod.length, bean.getFflag(), bean.getEndTime(), gid);
					}

				}
				// 当是新时时彩或老时时彩的时候，同样投注号码五星直选和五星通选不能超过50倍
				if (("04".equals(gid) || "20".equals(gid))) {
					if (bean.getMuli() > 999) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为999倍");
					}
					int multiple = 10;// 10倍
					int[] mm = null;
					if (bean.getZflag() == 1) {
						mm = StringUtil.SplitterInt(bean.getMulitys(), ",");
					}
					if (!checkCodeMuli(codes, (bean.getZflag() == 1 && mm != null ? mm[0] : bean.getMuli()),
							multiple)) {
						throw new Exception("对不起，同样号码累计不允许超过" + multiple + "倍！");
					}
				}
				if ("58".equals(gid)) {
					if (bean.getMuli() > 999) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为999倍");
					}
				}

				// 江西11选5、广东11选5、山东11选5、上海11选5，同样玩法同样号码累计不允许超过2000倍
				if (("54".equals(gid) || "55".equals(gid) || "56".equals(gid) || "59".equals(gid))) {
					if (bean.getMuli() > 2000) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为2000倍");
					}
					int multiple = 2000;// 同样玩法同样号码累计不允许2000倍
					if (!checkCodeMuli11x5(codes, bean.getMuli(), multiple)) {
						throw new Exception("对不起，同样号码累计不允许超过" + multiple + "倍！");
					}
				}

				// 江西11选5、广东11选5、山东11选5、上海11选5，同样玩法同样号码累计不允许超过2000倍
				if ("57".equals(gid)) {
					if (bean.getMuli() > 999) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为999倍");
					}
					int multiple = 999;// 同样玩法同样号码累计不允许2000倍
					if (!checkCodeMuli11x5(codes, bean.getMuli(), multiple)) {
						throw new Exception("对不起，同样号码累计不允许超过" + multiple + "倍！");
					}
				}

				// 快三超过999倍，
				if (("05".equals(gid) || "06".equals(gid) || "08".equals(gid))) {
					if (bean.getMuli() > 999) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为999倍");
					}
				}

				// 部分数字彩超过999倍，
				if (("01".equals(gid) || "07".equals(gid) || "50".equals(gid) || "51".equals(gid))) {
					if (bean.getMuli() > 999) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为999倍");
					}
				}
				// 部分数字彩超过99倍，
				if (("52".equals(gid)) || "03".equals(gid) || "53".equals(gid)) {
					if (bean.getMuli() > 999) {
						throw new Exception("对不起，彩种" + gid + "最高倍数为99倍");
					}
				}
				// 3D和排列五的组选六玩法最多选8个号码
				if (("03".equals(gid) || "53".equals(gid))) {
					String code[] = codes.split(";");
					for (int i = 0; i < code.length; i++) {
						String temp[] = code[i].split(":");
						if (temp == null || temp.length < 3) {
							throw new Exception("对不起，号码格式错误! code=" + code[i]);
						} else {
							// 3:3组六复式玩法
							if ("3:3".equals(temp[1] + ":" + temp[2]) && temp[0].split(",").length > 8) {
								throw new Exception("对不起，组选六玩法所选号码不允许超过8个!");
							}
						}
					}
				}

				GameCastCode[] cc = plugin.parseGameCastCodes(codes);
				if (GameContains.isKP(bean.getGid())) {
					if (cc.length > 500) {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_KP_PROJNUM_OUT_OF_LIMIT));
						bean.setBusiErrDesc("快频彩种方案条数不能够超过500条!");
					}
				}
				int total = 0;
				for (int i = 0; i < cc.length; i++) {
					if (!TradeConstants.big.containsKey(gid)) {
						if (cc[i].getCastMoney() > 20000) {
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_SINGLE_MONEY_OUT_OF_LIMIT));
							bean.setBusiErrDesc("单注金额不能超过2万!");
							break;
						}
					}
					if (GameContains.isR9(gid)) {
						if (cc[i].getCastMethod() == GameCastMethodDef.CASTTYPE_DANTUO) {
							isDT = true;
						}
					} else if ("50".equals(gid)) {
						if (cc[i].getPlayMethod() == GamePlugin_50.PM_SXL) {
							hasSXL = true;
						}
					}
					LimitCodeUtil.checkLimitCode(gid, cc[i], plugin);
					total += cc[i].getCastMoney();
				}
				money = total * bean.getMuli();
				if (hasSXL) {
					String[] ps = StringUtil.splitter(bean.getPid(), ",");
					for (int s = 0; s < ps.length; s++) {
						if ("2013053".compareTo(ps[s]) < 0) {
							throw new Exception("自2013年5月11日20:00起(第13053期销售截止后)，停止销售中国体育彩票超级大乐透附加玩法[生肖乐]");
						}
					}
				}
			} catch (CodeFormatException e) {
				log.error("号码格式错误 game=" + gid + " source=" + bean.getSource() + " 用户名:" + bean.getUid() + " pid:"
						+ bean.getPid(), e);
				bean.setBusiErrCode(e.getErrCode());
				bean.setBusiErrDesc(e.getErrDesc());
			}

			if (isDT) {
				checkEndTime(bean);
			}
		} else {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_GAME_NOT_SUPPORT));
			bean.setBusiErrDesc("该彩种暂不支持(" + gid + ")");
		}
		return money;
	}

	@Override
	public GamePluginAdapter getGamePluginAdapter(TradeBean bean) {
		GamePluginAdapter plugin = mapsPlugin.get(bean.getGid());
		if (plugin == null) {
			try {
				plugin = (GamePluginAdapter) Thread.currentThread().getContextClassLoader()
						.loadClass("com.caipiao.plugin.GamePlugin_" + bean.getGid()).newInstance();
				mapsPlugin.put(bean.getGid(), plugin);
			} catch (Exception e) {
				log.error("加载游戏插件失败 game=" + bean.getGid() + " uid:" + bean.getUid() + " pid:" + bean.getPid()
						+ " codes:" + bean.getCodes(), e);
			}
		}
		return plugin;
	}

	/**
	 * 老时时彩和新时时彩，同样投注号码五星直选和五星通选不能超过10倍，复式的需要拆分成单式做判断
	 * 
	 * @author: wangjinyong @createTime：2013-7-30 下午2:02:30
	 * @Methods: checkCodeMuli
	 * @param codes
	 *            投注号码
	 * @param muli
	 *            投注的倍数
	 * @param multiple
	 *            界面设置的倍数上限
	 * @return boolean 没有超过最大倍数时返回true，超过最大倍数返回false
	 */
	public static boolean checkCodeMuli(String codes, int muli, int multiple) {
		// 5:1、5:2、12:1 新时时彩
		// 1:1、12:1 老时时彩
		Map<String, Integer> mapResultCode = new HashMap<String, Integer>();
		Map<String, String> mapPlayType = new HashMap<String, String>();
		mapPlayType.put("5:1", "5:1");
		mapPlayType.put("5:2", "5:2");
		mapPlayType.put("12:1", "12:1");
		mapPlayType.put("1:1", "1:1");
		String code[] = codes.split(";");
		for (int i = 0; i < code.length; i++) {// 循环每一个投注号码
			String temp[] = code[i].split(":");
			if (mapPlayType.containsKey(temp[1] + ":" + temp[2])) {
				if (temp[0].length() == 9) {
					if (mapResultCode.containsKey(temp[0])) {
						int count = mapResultCode.get(temp[0]);
						if ((count + 1) * muli > multiple) {
							return false;
						}
						mapResultCode.put(temp[0], count + 1);
					} else {
						mapResultCode.put(temp[0], 1);
					}
					int count = mapResultCode.get(temp[0]);
					if (count * muli > multiple) {
						return false;
					}
				} else {// 需要拆分成单式
					if (temp[0].length() > 9) {
						String value[][] = new String[5][10];
						String v[] = temp[0].split(",");
						for (int j = 0; j < v.length; j++) {
							for (int k = 0; k < v[j].length(); k++) {
								value[j][k] = v[j].charAt(k) + "";
							}
						}
						List<String> list = new ArrayList<String>();
						list.add("");
						List<String> list1 = sort(0, list, value);
						for (int ii = 0; ii < list1.size(); ii++) {
							if (mapResultCode.containsKey(list1.get(ii))) {
								int count = mapResultCode.get(list1.get(ii));
								if ((count + 1) * muli > multiple) {
									return false;
								}
								mapResultCode.put(list1.get(ii), count + 1);
							} else {
								mapResultCode.put(list1.get(ii), 1);
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 递归计算把复式变为单式
	 * 
	 * @author: wangjinyong @createTime：2013-7-30 下午3:46:46
	 * @Methods: sort
	 * @param start
	 *            从0开始
	 * @param list
	 * @param input
	 *            输入的二维数组
	 * @return List<String> 已单式的形式返回
	 */
	private static List<String> sort(int start, List<String> list, String[][] input) {
		if (start >= input.length) {
			return list;
		}
		List<String> newList = new ArrayList<String>();
		for (int k = 0; k < list.size(); k++) {
			String s = list.get(k);
			for (int i = 0; i < input[start].length; i++) {
				if (input[start][i] != null) {
					newList.add(
							(s == null || s.trim().length() == 0) ? (input[start][i]) : (s + "," + input[start][i]));
				}
			}
		}
		list.clear();
		start++;
		return sort(start, newList, input);
	}

	/**
	 * 11选5倍数限制，累计不允许超过500倍
	 * 
	 * @author: wangjinyong
	 * @createTime：2013-10-8 上午10:50:04
	 * @Methods: checkCodeMuliNew
	 * @param codes
	 *            投注的号码
	 * @param muli
	 *            投注的倍数
	 * @param maxMmultiple
	 *            不允许超过的倍数上限
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean checkCodeMuli11x5(String codes, int muli, int maxMmultiple) throws Exception {
		// 04,08:02:01;03,04,07:03:01;01,02,05,09:04:01;02,04,07,10,11:05:01;02,03,04,06,07,09:06:01;04,05,06,07,08,09,10:07:01;01,03,04,05,06,07,08,11:08:01;
		// 09:01:01;02|09:09:01;02|01|09:10:01;03,09:11:01;03,05,08:12:01
		Map<String, HashMap<String, Integer>> mapResultCode = new HashMap<String, HashMap<String, Integer>>();// 存放结果集，key=玩法表示、value=(key=投注号码，value=累计注数)
		Map<String, String> mapPlayType = new HashMap<String, String>();// key=玩法标识、value=号码长度
		mapPlayType.put("02:01", "2");// [任选二] 号码长度为2
		mapPlayType.put("03:01", "3");// [任选三] 号码长度为3
		mapPlayType.put("04:01", "4");// [任选四] 号码长度为4
		mapPlayType.put("05:01", "5");// [任选五] 号码长度为5
		mapPlayType.put("06:01", "6");// [任选六] 号码长度为6
		mapPlayType.put("07:01", "7");// [任选七] 号码长度为7
		mapPlayType.put("08:01", "8");// [任选八] 号码长度为8
		mapPlayType.put("01:01", "1");// [前一直选] 号码长度为1
		mapPlayType.put("09:01", "2");// [前二直选] 号码长度为2
		mapPlayType.put("10:01", "3");// [前三直选] 号码长度为3
		mapPlayType.put("11:01", "2");// [前二组选] 号码长度为2
		mapPlayType.put("12:01", "3");// [前三组选] 号码长度为3
		String code[] = codes.split(";");// code[]为单注号码，例如：02,03,04,06,07,09:06:01
		for (int i = 0; i < code.length; i++) {// 循环每一个投注号码
			String temp[] = code[i].split(":");// temp[0]为投注的号码
			String tempValue = mapPlayType.get(temp[1] + ":" + temp[2]);// temp[1]:temp[2]为玩法标识、tempValue为号码长度
			if (tempValue != null) {
				if (tempValue != null && tempValue.trim().length() > 0) {
					// 同号码倍数累加做判断
					if (temp[0].contains("|") && temp[0].split("\\|").length < Integer.valueOf(tempValue)) {
						throw new Exception("投注号码格式错误");
					}
					if (!temp[0].contains("|") && temp[0].split(",").length < Integer.valueOf(tempValue)) {
						throw new Exception("投注号码格式错误");
					}
					if ((!temp[0].contains("|") && temp[0].split(",").length == Integer.valueOf(tempValue))
							|| (temp[0].contains("|") && !temp[0].contains(",")
									&& temp[0].split("\\|").length == Integer.valueOf(tempValue))) {// 单式，不需要拆分成单式
						temp[0] = temp[0].replace("|", ",");// 把竖线替换成逗号，给前二直选和前三直选使用
						if (mapResultCode.containsKey(temp[1] + ":" + temp[2])) {
							HashMap<String, Integer> tem = mapResultCode.get(temp[1] + ":" + temp[2]);
							if (tem.containsKey(temp[0])) {
								tem.put(temp[0], tem.get(temp[0]) + 1);
							} else {
								tem.put(temp[0], 1);
							}
							mapResultCode.put(temp[1] + ":" + temp[2], tem);
							// 做一次倍数上限判断
							int count = mapResultCode.get(temp[1] + ":" + temp[2]).get(temp[0]);
							if (count * muli > maxMmultiple) {
								return false;
							}
						} else {
							HashMap<String, Integer> tem = new HashMap<String, Integer>();
							tem.put(temp[0], 1);
							mapResultCode.put(temp[1] + ":" + temp[2], tem);
						}
						// 做一次倍数上限判断
						int count = mapResultCode.get(temp[1] + ":" + temp[2]).get(temp[0]);
						if (count * muli > maxMmultiple) {
							return false;
						}
					} else {// 复式，需要拆分成单式
						List<String> list = null;
						if (temp[0].contains("|")) {// 前二直选、前三直选
							int length = temp[0].split("\\|").length;
							String[][] value = new String[length][];
							for (int j = 0; j < temp[0].split("\\|").length; j++) {
								value[j] = temp[0].split("\\|")[j].split(",");
							}
							List<String> listParameter = new ArrayList<String>();
							listParameter.add("");
							list = sort(0, listParameter, value);
						} else {// 除了前二直选、前三直选的其它玩法
							list = CodesUtil.getCmnList(temp[0], temp[0].split(",").length,
									Integer.valueOf(mapPlayType.get(temp[1] + ":" + temp[2])));
						}

						for (int j = 0; j < list.size(); j++) {
							temp[0] = list.get(j);
							if (mapResultCode.containsKey(temp[1] + ":" + temp[2])) {
								HashMap<String, Integer> tem = mapResultCode.get(temp[1] + ":" + temp[2]);
								if (tem.containsKey(temp[0])) {
									tem.put(temp[0], tem.get(temp[0]) + 1);
								} else {
									tem.put(temp[0], 1);
								}
								mapResultCode.put(temp[1] + ":" + temp[2], tem);
								// 做一次倍数上限判断
								int count = mapResultCode.get(temp[1] + ":" + temp[2]).get(temp[0]);
								if (count * muli > maxMmultiple) {
									return false;
								}
							} else {
								HashMap<String, Integer> tem = new HashMap<String, Integer>();
								tem.put(temp[0], 1);
								mapResultCode.put(temp[1] + ":" + temp[2], tem);
							}
							// 做一次倍数上限判断
							int count = mapResultCode.get(temp[1] + ":" + temp[2]).get(temp[0]);
							if (count * muli > maxMmultiple) {
								return false;
							}
						}
					}
				} else {
					throw new Exception("投注号码格式错误");
				}
			}
		}
		return true;
	}

	// 检测截止时间
	public void checkEndTime(TradeBean bean) throws Exception {
		if (GameContains.isR9(bean.getGid())) {
			PeriodPojo period = periodMappper.queryEndTime(bean.getGid(), bean.getPid());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date cfendtime = sdf.parse(period.getFendtime());
			if (System.currentTimeMillis() > cfendtime.getTime()) {
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
				bean.setBusiErrDesc("任选九胆拖方案投注按单式投注截止时间截止,下次请赶早");
				log.info("任选9投注时间已截止,当前时间:" + System.currentTimeMillis() + " 文件投注截止时间:" + cfendtime.getTime() + " 用户名:"
						+ bean.getUid() + " 彩种:" + bean.getGid() + " 期次:" + bean.getPid());
			}
		}
	}

	@Override
	public void saveCastCodeToFile(TradeBean bean) throws Exception {

		String codes = bean.getCodes();
		codes = FileCastCodeUtil.getCodesFromCode(bean.getGid(), codes, bean.getExtendtype(), "1", log);
		String gid = bean.getGid();
		String pid = bean.getPid();
		String uid = bean.getUid();

		long time = System.currentTimeMillis();
		String name = uid + gid + time + pid;
		String filename = "";

		filename = MD5Util.compute(name) + ".txt";

		File dir = new File(FileConstant.BASE_PATH + File.separator + gid + File.separator + pid);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, filename);
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(codes.getBytes());
		fout.close();
		fout = null;

		bean.setFflag(1);
		bean.setCodes(filename);
	}

	@Override
	public void checkCodeCount(TradeBean bean) throws Exception {
		int _gid = Integer.valueOf(bean.getGid());

		String stime = CountCodeUtil.maps.get(bean.getGid());
		if (stime != null) {
			PeriodPojo period = periodMappper.queryNomarlEndState(stime, bean.getGid(), bean.getPid());
			if (period != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date cfendtime = sdf.parse(period.getFendtime());
				if (_gid >= 80 && _gid <= 83) {
					if (bean.getFflag() == 1 && bean.getMoney() >= 100000) {
						double time = (cfendtime.getTime() - System.currentTimeMillis()) / 1000 / 60;
						if (time < 80) {
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
							bean.setBusiErrDesc("10万元以上(含)单式方案截止时间为官方截止前80分钟,下次请提早投注");
							log.info("10万元以上(含)单式方案截止时间为官方截止前80分钟,下次请提早投注,用户名:" + bean.getUid() + " gid:"
									+ bean.getGid() + " pid:" + bean.getPid() + " codes:" + bean.getCodes());
						}
					}
					if (!CheckUtil.isNullString(bean.getCodes()) && period.getSalestate() == 0) {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PERIOD_NOTSURE));
						bean.setBusiErrDesc(bean.getPid() + "期比赛对阵未确定，当前预售中，您可以发起先发起后上传方案。");
						log.info(bean.getPid() + "期比赛对阵未确定，当前预售中，您可以发起先发起后上传方案,用户名:" + bean.getUid() + " gid:"
								+ bean.getGid() + " pid:" + bean.getPid() + " codes:" + bean.getCodes());
					}
				}
			}
		}
	}

	@Override
	public String checkJcCode(TradeBean bean) throws Exception {
		String codes = bean.getCodes();
		if (bean.getFflag() == 1) {// 是文件投注
			if (!CheckUtil.isNullString(codes)) {
				codes = FileCastCodeUtil.getCodesFromFile(bean.getGid(), bean.getPid(), FileConstant.BASE_PATH, codes,
						"1", log);
				if (null == codes) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_FILE_NOT_FIND));
					bean.setBusiErrDesc("投注失败");
					log.info("gid:" + bean.getGid() + " pid:" + bean.getPid() + " uid:" + bean.getUid() + " codes:+"
							+ codes + " 用户的投注文件未找到");
					return codes;
				}
			} else {
				log.info("后上传方案 游戏=" + bean.getGid() + " 期次=" + bean.getPid() + " 金额=" + bean.getMoney() + " 倍数="
						+ bean.getMuli() + " 用户=" + bean.getUid() + " codes:" + codes);
			}
		} else {
			if (CheckUtil.isNullString(codes)) {
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PARAM_NULL));
				bean.setBusiErrDesc("不是文件投注，必须提供投注号码！");
				log.info("非文件投注，投注号码不能为空,用户名:" + bean.getUid() + " gid:" + bean.getGid() + " pid:" + bean.getPid()
						+ " codes:" + bean.getCodes());
				return codes;
			}
			if (bean.getCodes().length() >= 3900) {
				bean.setFflag(1);
			}
			if (!CheckUtil.isNullString(codes)) {
				// 30秒内不允许重复投注
				boolean result = getSameTicketZl(bean);
				if (result) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_NOT_CAST_REPEAT));
					bean.setBusiErrDesc("30秒内不允许重复投注！");
					log.info("30秒内不允许重复投注！用户名:" + bean.getUid() + " gid:" + bean.getGid() + " pid:" + bean.getPid()
							+ " codes:" + bean.getCodes());
					return codes;
				}
			}

			codes = FileCastCodeUtil.getCodesFromCode(bean.getGid(), codes, bean.getExtendtype(), "1", log);
			if (StringUtil.isEmpty(codes)) {
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_ERROR_CODE));
				bean.setBusiErrDesc("投注号码格式错误，请检查您的投注选项。</br>投注号码：" + bean.getCodes());
				log.info("投注号码格式错误,用户名:" + bean.getUid() + " gid:" + bean.getGid() + " pid:" + bean.getPid() + " codes:"
						+ bean.getCodes());
				return codes;
			}
		}
		return codes;
	}

	@Override
	public boolean getSameTicketKm(TradeBean bean) {
		TradeBean tradebean = new TradeBean();
		BeanUtilWrapper.copyPropertiesIgnoreNull(bean, tradebean);
		String[] coda = tradebean.getCodes().split(";");
		tradebean.setCodes("");
		if (coda.length <= 10) {
			tradebean.setCodes(bean.getCodes());
		}
		int count = projMapper.countSameProjKm(tradebean);
		if (count > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getSameTicketZl(TradeBean bean) {
		TradeBean tradebean = new TradeBean();
		BeanUtilWrapper.copyPropertiesIgnoreNull(bean, tradebean);
		String[] coda = tradebean.getCodes().split(";");
		tradebean.setCodes("");
		if (coda.length <= 10) {
			tradebean.setCodes(bean.getCodes());
		}
		int count = projMapper.countSameProjZL(tradebean);
		if (count > 0) {
			return true;
		}
		return false;
	}

	// 投注时检测codes值是否正确
	@Override
	public boolean checkGame(TradeBean bean) {
		if ("50".equals(bean.getGid()) && bean.getCodes().indexOf("$") > 0) {
			log.info("大乐透胆拖投注时检测投注codes格式是否正确,codes=" + bean.getCodes());
			// 35$01,02,03,04,05,06,07,14|05$01,02,03,04,06:1:5
			String[] codarray = bean.getCodes().split(";");
			for (String cod : codarray) {
				int pipe = cod.indexOf("|");
				String frontendCodes = cod.substring(0, pipe);
				String backendCodes = cod.substring(pipe + 1);
				if (backendCodes.indexOf("$") > 0 && frontendCodes.indexOf("$") < 0) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_ERROR_CODE));
					bean.setBusiErrDesc("大乐透胆拖投注请设置前区胆码！");
					log.info("大乐透胆拖投注无前区胆码,codes=" + bean.getCodes() + " 用户名:" + bean.getUid());
					return false;
				}
			}

		}

		if ("10".equals(bean.getGid())) { // 江西快三 格式转换,版本限制
			String jxk3code = bean.getCodes();
			String[] jxk3arr = jxk3code.split(";");
			StringBuilder sb = new StringBuilder();
			for (String cod : jxk3arr) {
				String c7code = "";
				if (cod.endsWith(":7:1")) {// 二同号单选
					String[] c7arr = cod.split(":");
					if (c7arr.length != 3) {
						return true;
					}
					String c7 = c7arr[0];
					String[] c7num = c7.split("\\|");
					if (c7num.length != 2) {
						return true;
					}
					String[] c7_1 = c7num[1].split(",");
					String c7code1 = "";
					for (String c71 : c7_1) {
						c7code1 += c7num[0] + "," + c7num[0] + "," + c71 + ":" + c7arr[1] + ":" + c7arr[2] + ";";
					}
					c7code = c7code1.substring(0, c7code1.length() - 1);
				} else if (cod.endsWith(":2:1")) {
					c7code = cod.replace("0,0,0", "aaa");
				} else if (cod.endsWith(":5:1")) {
					c7code = cod.replace("0,0,0", "abc");
				} else {
					c7code = cod;
				}
				sb.append(c7code).append(";");
			}
			bean.setCodes(sb.toString().substring(0, sb.length() - 1));
		}
		return true;
	}

	@Override
	public void checkItem(String gid, JXmlWrapper xml, HashMap<String, Long> cvals, String gg) throws Exception {
		if (GameContains.isFootball(gid) || GameContains.isBasket(gid)) {
			if (xml != null) {
				Xml match = Xml.parse(xml.toXmlString());
				for (Iterator<String> keys = cvals.keySet().iterator(); keys.hasNext();) {
					String itemid = keys.next();
					long lc = cvals.get(itemid);
					if (GameContains.isFootball(gid)) {
						// isale spf cbf bqc jqs
						String isale = match.getStringValue("//row[@itemid='" + itemid + "']//@isale");
						String mname = match.getStringValue("//row[@itemid='" + itemid + "']//@name");
						if (!StringUtil.isEmpty(isale)) {
							int sale = Integer.parseInt(isale);
							log.info("判断是否在售 gg" + gg + "  isale" + isale);
							if (gg.indexOf("1*1") != -1) {
								log.info("判断是否在售 单关");
								for (int i = 6; i < 11; i++) {// 11110
									if (Long.bitCount(JcItemCodeUtil.getLongItem((i - 5), lc)) >= 1) {
										if (Long.bitCount(sale & (1L << (i - 1))) <= 0) {
											throw new RuntimeException(
													"场次：" + mname + " 单关玩法：" + JcCastCode.getPrefix(i - 5) + " 已停售");
										}
									}
								}
							} else {
								for (int i = JcItemCodeUtil.RQSPF; i < JcItemCodeUtil.HH; i++) {// 11110
									if (Long.bitCount(JcItemCodeUtil.getLongItem(i, lc)) >= 1) {
										if (Long.bitCount(sale & (1L << (i - 1))) <= 0) {
											throw new RuntimeException(
													"场次：" + mname + " 过关玩法：" + JcCastCode.getPrefix(i) + " 已停售");
										}
									}
								}
							}

						}
					} else if (GameContains.isBasket(gid)) {
						String isale = match.getStringValue("//row[@itemid='" + itemid + "']//@isale");
						String mname = match.getStringValue("//row[@itemid='" + itemid + "']//@name");
						if (!StringUtil.isEmpty(isale)) {
							int sale = Integer.parseInt(isale);
							if (gg.indexOf("1*1") != -1) {
								for (int i = 9; i < 13; i++) {
									if (Long.bitCount(LqItemCodeUtil.getLongItem((i - 4), lc)) >= 1) {
										if (Long.bitCount(sale & (1L << (i - LqItemCodeUtil.SF))) <= 0) {
											throw new RuntimeException(
													"场次：" + mname + " 单关玩法：" + LqCastCode.getPrefix(i - 4) + " 已停售");
										}
									}
								}
							} else {
								for (int i = LqItemCodeUtil.SF; i < LqItemCodeUtil.HH; i++) {
									if (Long.bitCount(LqItemCodeUtil.getLongItem(i, lc)) >= 1) {
										if (Long.bitCount(sale & (1L << (i - LqItemCodeUtil.SF))) <= 0) {
											throw new RuntimeException(
													"场次：" + mname + " 过关玩法：" + LqCastCode.getPrefix(i) + " 已停售");
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public boolean checkZqOptimizeCode(TradeBean bean, FilterResult result) {
		try {
			String str = "3=3,1=1,0=0";// 自定义选项
			GamePluginAdapter plugin = getGamePluginAdapter(bean);
			if (plugin != null) {
				CodeBean codebean = new CodeBean();
				codebean.setCodeitems(str);// 自定义
				codebean.setPlaytype(TradeConstants.ds_playid.get(bean.getGid()));
				codebean.setLottype(Integer.parseInt(bean.getGid()));

				int total = 0;
				String codes = bean.getCodes();
				String[] codd = codes.split(";");
				for (int i = 0; i < codd.length; i++) {
					codebean.setItemType(CodeBean.HAVEITEM);
					codebean.setCode(codd[i]);
					codebean.setGuoguan(bean.getGuoguan());
					String[] codestring = codd[i].split("_");
					int bs = 1;// 单式解析倍数
					int len = codestring.length;
					if (len == 2) {
						if (StringUtil.getNullInt(codestring[1].trim()) > 0) {
							bs = Integer.parseInt(codestring[1].trim());
						} else {
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_MULI_ERROR));
							bean.setBusiErrDesc("投注失败");
							log.info("投注格式中倍数异常,code=" + codd[i] + " 用户名:" + bean.getUid() + " gid:" + bean.getGid()
									+ " codes:" + bean.getCodes() + " newCodes:" + bean.getNewcodes());
							return false;
						}
					} else {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_FORMAT_ERROR));
						bean.setBusiErrDesc("投注失败");
						log.info("投注格式异常,code=" + codd[i] + " 用户名:" + bean.getUid() + " gid:" + bean.getGid()
								+ " codes:" + bean.getCodes() + " newCodes:" + bean.getNewcodes());
						return false;
					}

					FilterBase.doFilterJc(codebean, result);

					if (isValid(result.getCurrentCode())) {
						try {
							GameCastCode gcc = plugin.parseGameCastCode(result.getCurrentCode());
							total += gcc.getCastMoney() * bs;
						} catch (Exception e) {
							log.info("请检查上传文件的格式,参考标准格式样本" + e.getMessage() + " uid:" + bean.getUid() + " currentCode:"
									+ result.getCurrentCode() + " gid:" + bean.getGid() + " codes:" + bean.getCodes());
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPLOADFILE_CAST_ERROR));
							bean.setBusiErrDesc("上传文件格式错误");
							return false;
						}
						for (int n = 1; n < bs; n++) {
							result.addCode(result.getCurrentCode());
						}
						if (total > 1000000) {
							log.info("上传文件中检测到注数超过限制范围！ total:" + total + " uid:" + bean.getUid() + " gid:"
									+ bean.getGid() + " codes:" + bean.getCodes() + " newcodes:" + bean.getNewcodes());
							bean.setBusiErrDesc("上传文件中检测到注数超过限制范围！");
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPLOADFILE_CAST_OUT_OF_LIMIT));
							return false;
						}
					}
				}

				if (total != bean.getMoney()) {
					log.info("上传文件中检测到注数与实际金额不相符！ total:" + total + " uid:" + bean.getUid() + " gid:" + bean.getGid()
							+ " codes:" + bean.getCodes() + " newcodes:" + bean.getNewcodes() + " money:"
							+ bean.getMoney());
					bean.setBusiErrDesc("上传文件中检测到注数与实际金额不相符！");
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPLOADFILE_MONEY_NOT_MATCH));
					return false;
				}
			} else {
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PLAY_NOT_SUPPORT));
				bean.setBusiErrDesc("该玩法暂未开通");
				log.info("该玩法未开通,用户名:" + bean.getUid() + " codes:" + bean.getCodes() + " newcodes:" + bean.getNewcodes()
						+ " gid:" + bean.getGid());
				return false;
			}
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.TRADE_CHECK_ZQ_CODE_ERROR));
			bean.setBusiErrDesc("投注失败");
			log.error("检测奖金优化投注内容异常,用户名:" + bean.getUid() + " codes:" + bean.getCodes() + " newcodes:"
					+ bean.getNewcodes() + " gid:" + bean.getGid(), e);
			return false;
		}
		return true;
	}

	private boolean isValid(String tmp) {
		if (tmp.indexOf("=") == -1) {
			return false;
		}
		return true;
	}

	@Override
	public void refreshJcNewCodes(TradeBean bean) {
		String codes = bean.getCodes();
		String oldnewcodes = bean.getNewcodes();
		StringBuilder newcodes = new StringBuilder();
		if ("70".equals(bean.getGid())) {
			String code = oldnewcodes.substring(oldnewcodes.indexOf("|") + 1, oldnewcodes.indexOf(";"));
			String yhfs = oldnewcodes.substring(oldnewcodes.indexOf(";"));
			String wanfa = oldnewcodes.substring(0, oldnewcodes.indexOf("|") + 1);
			newcodes.append(wanfa);
			String[] arr = code.split(",");
			int length = arr.length;
			for (int i = 0; i < length; i++) {
				String id = arr[i].substring(0, arr[i].indexOf(">"));
				if (codes.indexOf(id) < 0) {
					continue;
				}
				newcodes.append(arr[i]);
				if (i < length - 1) {
					newcodes.append(",");
				}
			}
			newcodes.append(yhfs);
		} else {
			String[] arr = oldnewcodes.split("\\/", -1);
			int length = arr.length;
			for (int i = 0; i < length; i++) {
				String id = arr[i].substring(0, arr[i].indexOf("["));
				if (codes.indexOf(id) < 0) {
					continue;
				}
				newcodes.append(arr[i]);
				if (i < length - 1) {
					newcodes.append("/");
				}
			}
		}
		bean.setNewcodes(newcodes.toString());
	}

	@Override
	public boolean checkLqOptimizeCode(TradeBean bean, FilterResult result) {
		try {
			String str = "3=3,1=1,0=0";// 自定义选项
			GamePluginAdapter plugin = getGamePluginAdapter(bean);
			if (plugin != null) {
				String codes = bean.getCodes();
				String[] codd = codes.split(";");
				CodeBean codebean = new CodeBean();
				codebean.setCodeitems(str); // 自定义
				codebean.setPlaytype(TradeConstants.JCLQPLAYID.get(bean.getGid()));
				codebean.setLottype(Integer.parseInt(bean.getGid()));

				int total = 0;
				for (int i = 0; i < codd.length; i++) {
					codebean.setItemType(CodeBean.HAVEITEM);
					codebean.setCode(codd[i]);
					codebean.setGuoguan(bean.getGuoguan());
					String[] codestring = codd[i].split("_");
					int bs = 1; // 单式解析倍数
					int len = codestring.length;
					if (len == 2) {
						if (StringUtil.getNullInt(codestring[1].trim()) > 0) {
							bs = Integer.parseInt(codestring[1].trim());
						} else {
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_MULI_ERROR));
							bean.setBusiErrDesc("投注失败");
							log.info("投注格式中倍数异常,code=" + codd[i] + " 用户名:" + bean.getUid() + " gid:" + bean.getGid()
									+ " codes:" + bean.getCodes() + " newCodes:" + bean.getNewcodes());
							return false;
						}
					} else {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_FORMAT_ERROR));
						bean.setBusiErrDesc("投注失败");
						log.info("投注格式异常,code=" + codd[i] + " 用户名:" + bean.getUid() + " gid:" + bean.getGid()
								+ " codes:" + bean.getCodes() + " newCodes:" + bean.getNewcodes());
						return false;
					}

					FilterBase.doFilterLc(codebean, result);

					if (isValid(result.getCurrentCode())) {
						try {
							GameCastCode gcc = plugin.parseGameCastCode(result.getCurrentCode());
							total += gcc.getCastMoney() * bs;
						} catch (Exception e) {
							log.info("请检查上传文件的格式,参考标准格式样本" + e.getMessage() + " uid:" + bean.getUid() + " currentCode:"
									+ result.getCurrentCode() + " gid:" + bean.getGid() + " codes:" + bean.getCodes());
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPLOADFILE_CAST_ERROR));
							bean.setBusiErrDesc("上传文件格式错误");
							return false;
						}
						for (int n = 1; n < bs; n++) {
							result.addCode(result.getCurrentCode());
						}
						if (total > 1000000) {
							log.info("上传文件中检测到注数超过限制范围！ total:" + total + " uid:" + bean.getUid() + " gid:"
									+ bean.getGid() + " codes:" + bean.getCodes() + " newcodes:" + bean.getNewcodes());
							bean.setBusiErrDesc("上传文件中检测到注数超过限制范围！");
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPLOADFILE_CAST_OUT_OF_LIMIT));
							return false;
						}
					}

				}

				if (total != bean.getMoney()) {
					log.info("上传文件中检测到注数与实际金额不相符！ total:" + total + " uid:" + bean.getUid() + " gid:" + bean.getGid()
							+ " codes:" + bean.getCodes() + " newcodes:" + bean.getNewcodes() + " money:"
							+ bean.getMoney());
					bean.setBusiErrDesc("上传文件中检测到注数与实际金额不相符！");
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPLOADFILE_MONEY_NOT_MATCH));
					return false;
				}
			} else {
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_PLAY_NOT_SUPPORT));
				bean.setBusiErrDesc("该玩法暂未开通");
				log.info("该玩法未开通,用户名:" + bean.getUid() + " codes:" + bean.getCodes() + " newcodes:" + bean.getNewcodes()
						+ " gid:" + bean.getGid());
			}
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.TRADE_CHECK_ZQ_CODE_ERROR));
			bean.setBusiErrDesc("投注失败");
			log.error("检测奖金优化投注内容异常,用户名:" + bean.getUid() + " codes:" + bean.getCodes() + " newcodes:"
					+ bean.getNewcodes() + " gid:" + bean.getGid(), e);
		}
		return true;
	}

	@Override
	public void refreshLcNewCodes(TradeBean bean) {
		String codes = bean.getCodes();
		String oldnewcodes = bean.getNewcodes();
		StringBuilder newcodes = new StringBuilder();
		if ("71".equals(bean.getGid())) {
			String code = oldnewcodes.substring(oldnewcodes.indexOf("|") + 1, oldnewcodes.indexOf(";"));
			String yhfs = oldnewcodes.substring(oldnewcodes.indexOf(";"));
			String wanfa = oldnewcodes.substring(0, oldnewcodes.indexOf("|") + 1);
			newcodes.append(wanfa);
			String[] arr = code.split(",");
			int length = arr.length;
			for (int i = 0; i < length; i++) {
				String id = arr[i].substring(0, arr[i].indexOf(">"));
				if (codes.indexOf(id) < 0) {
					continue;
				}
				newcodes.append(arr[i]);
				if (i < length - 1) {
					newcodes.append(",");
				}
			}
			newcodes.append(yhfs);
		} else {
			String[] arr = oldnewcodes.split("\\/", -1);
			int length = arr.length;
			for (int i = 0; i < length; i++) {
				String id = arr[i].substring(0, arr[i].indexOf("["));
				if (codes.indexOf(id) < 0) {
					continue;
				}
				newcodes.append(arr[i]);
				if (i < length - 1) {
					newcodes.append("/");
				}
			}
		}
		bean.setNewcodes(newcodes.toString());
	}
}

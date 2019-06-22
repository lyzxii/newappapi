package com.caiyi.lottery.tradesystem.tradecenter.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caipiao.game.GameContains;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.jcutil.JcItemBean;
import com.caipiao.plugin.lqutil.LqItemBean;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.split.GameSplit;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.CpgameMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.ProjMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.ProjStatsTaskMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.ProjXzjzMapper;
import com.caiyi.lottery.tradesystem.tradecenter.service.BaseService;
import com.caiyi.lottery.tradesystem.tradecenter.service.CastService;
import com.caiyi.lottery.tradesystem.tradecenter.service.CodeService;
import com.caiyi.lottery.tradesystem.tradecenter.service.MatchService;
import com.caiyi.lottery.tradesystem.tradecenter.service.PeriodService;
import com.caiyi.lottery.tradesystem.util.code.CountCodeUtil;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.caiyi.lottery.tradesystem.tradecenter.util.FileCastCodeUtil;
import com.caiyi.lottery.tradesystem.util.matrix.MatrixUtils;
import com.caiyi.lottery.tradesystem.tradecenter.util.trade.LimitCodeUtil;
import com.caiyi.lottery.tradesystem.usercenter.clientwrapper.UserBasicInfoWrapper;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.Util;
import com.caiyi.lottery.tradesystem.util.cache.Cache;
import com.caiyi.lottery.tradesystem.util.cache.CacheManager;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import lombok.extern.slf4j.Slf4j;
import trade.bean.TradeBean;
import trade.bean.jclq.LcMatchBean;
import trade.bean.jczq.JcMatchBean;
import trade.constants.TradeConstants;
import trade.dto.CastDto;
import trade.util.TradeUtil;

@Slf4j
@Service
public class CastServiceImpl implements CastService{
	
	@Autowired
	BaseService baseService;
	@Autowired
	UserBasicInfoWrapper userBasicInfoWrapper;
	@Autowired
	CodeService codeService;
	@Autowired
	ProjMapper projMapper;
	@Autowired
	CpgameMapper cpgameMapper;
	@Autowired
	ProjXzjzMapper projXzjzMapper;
	@Autowired
	ProjStatsTaskMapper projStatsTaskMapper;
	@Autowired
	PeriodService periodService;
	@Autowired
	MatchService matchService;

	@Override
	public CastDto proj_cast_app(TradeBean bean) {
		CastDto castDto = new CastDto();
		if(!codeService.checkGame(bean)){
			return castDto;
		}
		String nextPid = periodService.mpEndTimeReminder(bean);
		if(bean.getBusiErrCode() != 0){
			castDto.setNextPid(nextPid);
			return castDto;
		}
		String gid = bean.getGid();
		String tzcodes = ""; // 在旋转投注中,用来保存旋转之前的投注号码
		if (gid.compareToIgnoreCase("84") >= 0 || gid.compareToIgnoreCase("72") == 0
				|| gid.compareToIgnoreCase("70") == 0 || gid.compareToIgnoreCase("71") == 0) {
			castDto = jproj_cast_app(bean);
		} else {
			if(!TradeUtil.check(TradeUtil.CAST_HM, bean)){
				return castDto;
			}
			//检测用户交易红包
			if(!baseService.checkUserRedpacket(bean)){
				return castDto;
			}
			if(bean.getType() == 0){
				bean.setWrate(0);
			}
			//查询数字彩投注截止时间
			periodService.querySZCPeriodEndTime(bean);
			if(GameContains.isKP(bean.getGid())){
				CountCodeUtil.checkKPChunJieEndTime(bean, bean.getEndTime());
			}
			if(bean.getFflag() == 1 && CheckUtil.isNullString(bean.getCodes())){
				// 后上传方案
				log.info("后上传方案 游戏=" + gid + " 期次=" + bean.getPid() + " 金额=" + bean.getMoney() + " 倍数="
						+ bean.getMuli() + " 用户=" + bean.getUid());
			} else {
				//旋转矩阵获取旋转矩阵号码
				if (1 == bean.getXzflag()){ // 如果是旋转投注,则,先将投注号码按旋转(缩水)方式拆成单式
					tzcodes = bean.getCodes(); // 保存旋转之前的投注号码
					bean.setExtendtype(9);
					bean.setCodes(MatrixUtils.getMatrixCodesStr(bean.getGid(), tzcodes));
				}
				try {
					//根据投注内容统计金额
					int money = codeService.countCodesMoney(bean);
					if (bean.getMoney() != money && bean.getBusiErrCode() == 0) {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_MONEY_NOT_MATCH));
						bean.setBusiErrDesc("金额不正确,实际金额(" + money + ")");
						log.info("用户订单的金额不正确,上传money:"+bean.getMoney()+" 计算money:"+money+
								" uid:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes());
						return castDto;
					}
					
					//30s内不允许重复投注
					boolean result = codeService.getSameTicketKm(bean);
					if(result){
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_NOT_CAST_REPEAT));
						bean.setBusiErrDesc("30秒内不允许重复投注！");
						log.info("30秒内不允许重复投注,用户名:"+bean.getUid()+" codes:"+bean.getCodes()+" gid:"+bean.getGid()+" pid:"+bean.getPid());
						return castDto;
					}
					if(bean.getCodes().length() >= 3900){
						codeService.saveCastCodeToFile(bean);
					}
					codeService.checkCodeCount(bean);
					if(bean.getBusiErrCode() == 0){
						log.info("存储过程t_proj_cast 参数=====" + " uid" + "==" + bean.getUid() + " pwd" + "=="
								+ bean.getPwd() + " gid" + "==" + bean.getGid() + " pid" + "==" + bean.getPid()
								+ " play" + "==" + bean.getPlay() + " codes" + "==" + bean.getCodes() + " muli"
								+ "==" + bean.getMuli() + " fflag" + "==" + bean.getFflag() + " type" + "=="
								+ bean.getType() + " name" + "==" + bean.getName() + " desc" + "==" + bean.getDesc()
								+ " money" + "==" + bean.getMoney() + " tnum" + "==" + bean.getTnum() + " bnum"
								+ "==" + bean.getBnum() + " pnum" + "==" + bean.getPnum() + " oflag" + "=="
								+ bean.getOflag() + " wrate" + "==" + bean.getWrate() + " comeFrom" + "=="
								+ bean.getComeFrom() + " source" + "==" + bean.getSource() + " endTime" + "=="
								+ bean.getEndTime() + " zid" + "==" + bean.getZid() + " guoguan" + "=="
								+ bean.getGuoguan() + " upay" + "==" + bean.getUpay() + " cupacketid" + "=="
								+ bean.getCupacketid() + " redpacket_money" + "==" + bean.getRedpacket_money()
								+ " imoneyrange" + "==" + bean.getImoneyrange() + " extendtype" + "=="
								+ bean.getExtendtype());
						cpgameMapper.t_proj_cast(bean);
						if(bean.getBusiErrCode() == 0){
							bean.setPayorderid(bean.getHid());
							castDto.setProjid(bean.getHid());
							castDto.setBalance(bean.getBalance());
							if(1 == bean.getXzflag()){
								String code = "";
								if(tzcodes.split(";").length == 1){
									code = bean.getCodes();
								}
								projXzjzMapper.insertXzjzRecord(bean.getPayorderid(), bean.getGid(), code, bean.getCodes());
							}
						}
						
						// 投注量统计通过线程来处理 这里只在任务表里增加一条数据 后台线程来处理任务表
						if (bean.getBusiErrCode() == 0) {
							String hid = bean.getHid();
							List<String> tztjGidList = Arrays.asList(new String[] { "80", "81", "82", "83" });
							if (!"".equals(hid) && tztjGidList.contains(gid)) {
								projStatsTaskMapper.insertProjStatsTask(hid, gid);
							}
						}
						
						log.info("发起方案   结果=" + bean.getBusiErrCode() + " 描叙=" + bean.getBusiErrDesc() + " 游戏=" + gid
								+ " 期次=" + bean.getPid() + " 金额=" + bean.getMoney() + " 倍数=" + bean.getMuli() + " 用户="
								+ bean.getUid() + " 投注号码=" + bean.getCodes() + "   文件标志=" + bean.getFflag() + " 方案编号="
								+ bean.getHid() + " type=" + bean.getType() + " name=" + bean.getName() + " desc="
								+ bean.getDesc() + " tnum=" + bean.getTnum() + " bnum=" + bean.getBnum() + " pnum="
								+ bean.getPnum() + " oflag=" + bean.getOflag() + " wrate=" + bean.getWrate() + " comeFrom"
								+ bean.getComeFrom() + " source=" + bean.getSource() + " endTime=" + bean.getEndTime()
								+ " zid=" + bean.getZid() + " guoguan=" + bean.getGuoguan() + " upay=" + bean.getUpay()
								+ " extendtype=" + bean.getExtendtype());
					}
				} catch (Exception e) {
					bean.setBusiErrCode(Integer.parseInt(ErrorCode.TRADE_PCAST_ERROR));
					bean.setBusiErrDesc(e.getMessage());
					log.error("proj_cast_app投注异常,gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes()+" 用户名:"+bean.getUid(),e);
				}
			}
		}
		return castDto;
	}
	
	@Override
	public CastDto jproj_cast_app(TradeBean bean) {
		CastDto castDto = new CastDto();
		if (bean.getUpay() == 1) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_UPAY_FORBID));
			bean.setBusiErrDesc("系统升级中~保存订单暂停使用~");
			return castDto;
		}
		String gid = bean.getGid();
		periodService.setDefaultBeidanPid(bean);
		if(!TradeUtil.check(TradeUtil.CAST_HM, bean)){
			return castDto;
		}
		if (bean.getType() == 0) {// 代购 提成比例为0
			bean.setWrate(0);
		}
		//检测用户交易红包
		if(!baseService.checkUserRedpacket(bean)){
			return castDto;
		}
		try {
			String codes = bean.getCodes();
			log.info("来源：" + bean.getSource() + " 版本：" + bean.getComboid() + "条件值："
					+ (bean.getSource() >= 2000 && codes.startsWith("DXF|") && bean.getComboid() != 1));
			bean.setName("快乐购彩");
			bean.setDesc("随缘！买彩票讲究的是运气、缘分和坚持。");
			codes = codeService.checkJcCode(bean);
			if(bean.getBusiErrCode() != 0){
				return castDto;
			}
			int size = 0;
			String matches = "";
			JXmlWrapper xml = matchService.getMatchList(bean.getGid(), bean.getPid());
			GamePluginAdapter plugin = codeService.getGamePluginAdapter(bean);
			GameSplit split = GameSplit.getGameSplit(bean.getGid());
			if (!CheckUtil.isNullString(codes)) {
				String[] tmp = StringUtil.splitter(codes, ";");

				HashMap<String, Integer> cnums = new HashMap<String, Integer>();
				HashMap<String, Integer> piaoInfo = new HashMap<String, Integer>(); // 出票信息
				for (int i = 0; i < tmp.length; i++) {
					String key = tmp[i];
					if (!StringUtil.isEmpty(key)) {
						Integer val = cnums.get(key);
						int _val = (val == null ? 0 : val.intValue()) + 1;
						cnums.put(key, _val);
					}
				}

				int money = 0;
				Set<String> set = new HashSet<String>();
				HashMap<String, Long> cvals = new HashMap<String, Long>();
				for (Iterator<String> keys = cnums.keySet().iterator(); keys.hasNext();) {
					String key = keys.next();
					int mul = cnums.get(key);
					GameCastCode gcc = plugin.parseGameCastCode(key);
					log.info("增加限号,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes());
					LimitCodeUtil.checkLimitCode(gid, gcc, plugin);// 增加限号
					log.info("增加限号====,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes());
					matches += gcc.getMatchID(); // 场次汇总
					money += gcc.getCastMoney() * mul;// 金额汇总
					if (GameContains.isFootball(gid) || GameContains.isBasket(gid) || GameContains.isBeiDan(gid)) {
						String billCode = split.getSplitCode(key);
						String[] codeList = billCode.split(";");
						for (int i = 0; i < codeList.length; i++) {
							String piaokey = codeList[i];
							if (!StringUtil.isEmpty(piaokey)) {
								Integer val = piaoInfo.get(piaokey);
								int _val = (val == null ? mul : val + mul);
								piaoInfo.put(piaokey, _val);
							}
						}
					} else {
						size += gcc.getCombineNum(); // 票张数
					}
					// 过关方式汇总
					if (gcc.getGuoguans() != null) {
						String[] gs = StringUtil.splitter(gcc.getGuoguans(), ",");
						for (String s : gs) {
							if (!StringUtil.isEmpty(s)) {
								set.add(s);
							}
						}
					}

					// 投注项汇总
					if (gcc.getItems() != null) {
						for (Object obj : gcc.getItems()) {
							String itemid = obj instanceof JcItemBean ? ((JcItemBean) obj).getItemid()
									: ((LqItemBean) obj).getItemid();
							long _code = obj instanceof JcItemBean ? ((JcItemBean) obj).getCode()
									: ((LqItemBean) obj).getCode();
							Long lcode = cvals.get(itemid);
							long lc = (lcode == null ? 0 : lcode.longValue()) | _code;
							cvals.put(itemid, lc);
						}
					}
				}

				if (GameContains.isFootball(gid) || GameContains.isBasket(gid) || GameContains.isBeiDan(gid)) {
					for (Iterator<String> keys = piaoInfo.keySet().iterator(); keys.hasNext();) {
						String key = keys.next();
						int mul = piaoInfo.get(key);
						int pmc = CountCodeUtil.muliSize(bean.getMuli());
						if (mul == 1) {
							size += pmc;
						} else {
							size += CountCodeUtil.muliSize(bean.getMuli() * mul);
						}
					}
				}
				// 特殊时期增加单倍票张数限制 can 方案倍数限制
				// 只有账户支付限制方案倍数,保存订单不限制
				if ((GameContains.isFootball(gid) || GameContains.isBasket(gid) || GameContains.isGYJ(gid))
						&& bean.getUpay() == 0) {
					int piaoSize = piaoInfo.size();
					if (piaoSize > 500) {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_SINGLE_TICKET_NUM_OUT_OF_LIMIT));
						bean.setBusiErrDesc("很抱歉,暂时无法投注单倍票张数大于500张的方案,您当前方案共 " + piaoSize + " 张票。");
						log.info("投注单倍票数大于500张,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" codes:"+bean.getCodes()+" pid:"+bean.getPid());
						return castDto;
					}
				}
				cnums.clear();
				cnums = null;
				StringBuffer sb = new StringBuffer();
				for (Iterator<String> its = set.iterator(); its.hasNext();) {
					sb.append(its.next()).append(",");
				}
				set.clear();
				set = null;
				bean.setGuoguan(sb.toString());
				codeService.checkItem(gid, xml, cvals, sb.toString());
				cvals.clear();
				cvals = null;
				if (bean.getMoney() != money * bean.getMuli()) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CAST_MONEY_NOT_MATCH));
					bean.setBusiErrDesc("金额不正确,实际金额(" + money + ")");
					log.info("投注金不匹配,用户名:"+bean.getUid()+" ");
					return castDto;
				}
			}
			if (bean.getBusiErrCode() == 0) {
				// 检查场次是否正确
				String endTime = "";
				if (CheckUtil.isNullString(matches)) {// 后上传

					if (GameContains.isFootball(gid) || GameContains.isBasket(gid)) {
						long l = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 3;
						endTime = DateUtil.getDateTime(l);
						endTime = CountCodeUtil.checkJCChunJieEndTime(endTime);
					} else {
						endTime = matchService.getMatchMaxEndTime(gid, bean.getPid());
						Date d = DateUtil.parserDateTime(endTime);

						long l = d.getTime();
						if (l < System.currentTimeMillis()) {
							endTime = "";
						} else {
							endTime = DateUtil.getDateTime(l);
						}
					}
					log.info("endTime1=======" + endTime);
				} else {
					List<String> lst = new ArrayList<String>();
					String[] ss = StringUtil.splitter(matches, ",");
					for (int i = 0; i < ss.length; i++) {
						if (!lst.contains(ss[i]) && !StringUtil.isEmpty(ss[i])) {
							lst.add(ss[i]);
						}
					}
					matches = ",";
					for (int i = 0; i < lst.size(); i++) {
						matches += lst.get(i) + ",";
					}
					log.info("matches ==========" + matches + "gid ==========" + gid);
					endTime = matchService.getMatchMinEndTime(gid, bean.getPid(), xml, matches);
					log.info("endTime2-----------" + endTime);

					if ("".equals(endTime)) {
						bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
						bean.setBusiErrDesc("所选方案已截止");
						log.info("投注方案截止时间已截止,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes());
						return castDto;
					}
					Date d = DateUtil.parserDateTime(endTime);
					if (bean.getFflag() == 1) {// 是文件投注
						if (GameContains.isFootball(gid) || GameContains.isBasket(gid)) {
							if(bean.getSource()==4 || bean.getSource()==6 || bean.getSource()==7 || bean.getExtendtype() ==6 ||  
									bean.getExtendtype() ==11 ||  bean.getExtendtype() ==12 ||  bean.getExtendtype() ==13 ||  
									bean.getExtendtype() ==14 || bean.getExtendtype() == 15){//单关配  奖金优化  截止时间改为复试
								log.info("奖金优化，单关配走复试截止时间"+endTime+" 用户名:"+bean.getUid()+" gid:"+bean.getGid()+
										" pid:"+bean.getPid()+" codes:"+bean.getCodes());
							}else{
								long l = d.getTime() - 1000 * 60 * 15;
								endTime = DateUtil.getDateTime(l);
							}

						} else if (GameContains.isBeiDan(gid)) {
							long l = d.getTime() - 1000 * 60 * 15;
							endTime = DateUtil.getDateTime(l);
						} else {
							long l = d.getTime() - 1000 * 60 * 10;
							endTime = DateUtil.getDateTime(l);
						}
					}
				}

				if (endTime.length() == 0) {
					log.info("endTime3====" + endTime.length());
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
					if (CheckUtil.isNullString(matches)) {
						bean.setBusiErrDesc("该期所有的场次已经截止销售！");
					} else {
						bean.setBusiErrDesc("所选择的比赛场次中已经截止销售！");
					}
					return castDto;
				} else {
					endTime = CountCodeUtil.getSpecialTimeRange(gid, endTime);
					bean.setEndTime(endTime);
					// 处理竞彩的期次编号问题
					if (GameContains.isBeiDan(gid)) {
						try {
							CountCodeUtil.bd(size, bean.getFflag(), endTime);
						} catch (Exception e) {
							log.error("北单,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes(), e);
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
							bean.setBusiErrDesc(e.getMessage());
							return castDto;
						}
					} else if (GameContains.isFootball(gid) || GameContains.isBasket(gid)) {// JC
						if (bean.getFflag() == 1) {// 是文件投注 文件投注用传过来的期次
							bean.setPid(CheckUtil.isNullString(bean.getPid())
									? StringUtil.replaceString(endTime.substring(0, 10), "-", "") : bean.getPid());
						} else {
							bean.setPid(StringUtil.replaceString(endTime.substring(0, 10), "-", ""));
						}
						try {
							log.info("票的张数：" + size + "  方案发起人：" + bean.getUid());
							CountCodeUtil.jc(size, bean.getFflag(), bean.getEndTime());
						} catch (Exception e) {
							bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
							bean.setBusiErrDesc(e.getMessage());
							return castDto;
						}
					} else if (GameContains.isGYJ(gid)) {// GYJ
						// nothing
					}
				}
			}

			if (bean.getBusiErrCode() == 0) {
				if (bean.getCodes().length() >= 3900) {
					codeService.saveCastCodeToFile(bean);// 生成文件
				}
				if (bean.getBusiErrCode() == 0) {
					bean.setZid(matches);
					cpgameMapper.t_proj_cast(bean);
					if (bean.getBusiErrCode() == 0) {
						try {
							saveProjMatchFile(bean);
						} catch (Exception e) {
							log.error("生成方案对阵,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()
							+" hid:"+bean.getHid()+" codes:"+bean.getCodes(), e);
						}
						bean.setPayorderid(bean.getHid());
						castDto.setProjid(bean.getHid());
						castDto.setBalance(bean.getBalance());
					}
				}
			}

			// 投注量统计通过线程来处理 这里只在任务表里增加一条数据 后台线程来处理任务表
			if (bean.getBusiErrCode() == 0) {
				String hid = bean.getHid();
				List<String> tztjGidList = Arrays.asList(new String[] { "85", "72", "90", "94", "95", "97", "70" });
				if (!"".equals(hid) && tztjGidList.contains(gid)) {
					projStatsTaskMapper.insertProjStatsTask(hid, gid);
				}
			}
			String pre = "发起方案   结果=";
			if (bean.getUpay() == 1) {
			    pre = "保存方案   结果=";
			}
			log.info(pre + bean.getBusiErrCode() + " 描叙=" + bean.getBusiErrDesc() + " 游戏=" + gid + " 期次=" + bean.getPid() + " 金额=" + bean.getMoney() + " 倍数="
					+ bean.getMuli() + " 用户=" + bean.getUid() + " 支付方式=" + bean.getUpay() + " 投注号码=" + bean.getCodes() + "  场次=" + matches+" 方案描述="+bean.getDesc());
			try{
				//购买完比赛自动添加关注
				matchService.addMatchFollow(matches,bean);
			}catch(Exception e){
				log.error("添加比赛关注出现异常,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" codes:"+bean.getCodes(),e);
			}
		} catch (Exception e) {
			bean.setBusiErrCode(Integer.parseInt(ErrorCode.TRADE_JCAST_ERROR));
			bean.setBusiErrDesc("投注失败");
			log.error("竞彩投注失败,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes(),e);
		}
		return castDto;
	}
	
	private void saveProjMatchFile(TradeBean tb) throws Exception {
		if (CheckUtil.isNullString(tb.getZid())) {
			return;
		}
		String[] fields = new String[] { "spf", "cbf", "bqc", "sxp", "jqs", "rqspf", "cbf", "bqc", "jqs", "spf", "sf",
				"rfsf", "sfc", "dxf", "sp", "sp" };// 玩法属性节点
		String ppath = "/opt/export/data/guoguan"; // 方案文件保存路径

		File fdir = new File(
				ppath + File.separator + tb.getGid() + File.separator + tb.getPid() + File.separator + "proj");
		if (!fdir.exists()) {
			fdir.mkdirs();
		}

		String[] matches = StringUtil.splitter(tb.getZid(), ",");
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append("\r\n");
		sb.append("<items ").append(JXmlUtil.createAttrXml("uid", tb.getType() == 0 ? "******" : tb.getUid()));
		sb.append(JXmlUtil.createAttrXml("pid", tb.getPid()));
		sb.append(JXmlUtil.createAttrXml("projid", tb.getHid()));
		sb.append(JXmlUtil.createAttrXml("spdate", DateUtil.getDateTime(System.currentTimeMillis())));
		sb.append(JXmlUtil.createAttrXml("builddate", DateUtil.getDateTime(System.currentTimeMillis())));
		sb.append(" >\r\n");

		JXmlWrapper xml =  matchService.getMatchXmlFromFile(tb.getGid(), tb.getPid());
		if (xml == null) {
			return;
		}

		// 场次编号排序
		int[] sortMatches = new int[matches.length];
		for (int i = 0; i < sortMatches.length; i++) {
			if (matches[i] != null && matches[i].length() > 0 && matches[i].trim().length() > 0) {
				sortMatches[i] = Integer.valueOf(matches[i].trim());
			}
		}
		Arrays.sort(sortMatches);

		for (int i = 0; i < sortMatches.length; i++) {
			JXmlWrapper mXml = getMatchInfo(xml, sortMatches[i] + "");
			if (mXml == null) {
				log.info("无场次[" + sortMatches[i] + "]信息");
				continue;
			}
			sb.append("<item ");
			sb.append(JXmlUtil.createAttrXml("id", sortMatches[i] + ""));// ID

			if (GameContains.isGYJ(tb.getGid())) {
				sb.append(JXmlUtil.createAttrXml("name", mXml.getStringValue("@name")));
				sb.append(JXmlUtil.createAttrXml("audit", "0"));
				sb.append(JXmlUtil.createAttrXml("bt", mXml.getStringValue("@matchtime")));
				sb.append(JXmlUtil.createAttrXml("spvalue", mXml.getStringValue("@"
						+ fields[(Integer.parseInt(tb.getGid()) == 98 ? 99 : Integer.parseInt(tb.getGid())) - 85])));
			} else {
				sb.append(JXmlUtil.createAttrXml("hn", mXml.getStringValue("@hn")));
				sb.append(JXmlUtil.createAttrXml("vn", mXml.getStringValue("@gn")));

				sb.append(JXmlUtil.createAttrXml("hs", ""));
				sb.append(JXmlUtil.createAttrXml("vs", ""));

				if (GameContains.isBeiDan(tb.getGid())) {
					sb.append(JXmlUtil.createAttrXml("lose", mXml.getStringValue("@close") + ""));
					sb.append(JXmlUtil.createAttrXml("hhs", ""));
					sb.append(JXmlUtil.createAttrXml("hvs", ""));
					sb.append(JXmlUtil.createAttrXml("bet3", mXml.getStringValue("@b3")));
					sb.append(JXmlUtil.createAttrXml("bet1", mXml.getStringValue("@b1")));
					sb.append(JXmlUtil.createAttrXml("bet0", mXml.getStringValue("@b0")));
					sb.append(JXmlUtil.createAttrXml("bt", mXml.getStringValue("@bt")));
					if (Integer.parseInt(tb.getGid()) == 84) {
						sb.append(JXmlUtil.createAttrXml("cup", mXml.getStringValue("@ccup")));
						sb.append(JXmlUtil.createAttrXml("matchname", mXml.getStringValue("@mname")));
					}
					sb.append(JXmlUtil.createAttrXml("spvalue", ""));
				} else if (GameContains.isFootball(tb.getGid())) {
					sb.append(JXmlUtil.createAttrXml("lose", mXml.getStringValue("@close") + ""));
					sb.append(JXmlUtil.createAttrXml("hhs", ""));
					sb.append(JXmlUtil.createAttrXml("hvs", ""));
					sb.append(JXmlUtil.createAttrXml("bet3", mXml.getStringValue("@bet3")));
					sb.append(JXmlUtil.createAttrXml("bet1", mXml.getStringValue("@bet1")));
					sb.append(JXmlUtil.createAttrXml("bet0", mXml.getStringValue("@bet0")));
					sb.append(JXmlUtil.createAttrXml("bt", mXml.getStringValue("@mt")));
					String[] fs = new String[] { "rqspf", "cbf", "bqc", "jqs", "spf" };
					StringBuffer _sb = new StringBuffer();
					for (int k = 0; k < fs.length; k++) {
						_sb.append(mXml.getStringValue("@" + fs[k]));
						if (k != fs.length - 1) {
							_sb.append("|");
						}
					}
					sb.append(JXmlUtil.createAttrXml("spvalue", _sb.toString()));
				} else if (GameContains.isBasket(tb.getGid())) {
					sb.append(JXmlUtil.createAttrXml("lose",
							"0|" + mXml.getStringValue("@close") + "|0|" + mXml.getStringValue("@zclose")));
					sb.append(JXmlUtil.createAttrXml("bet3", mXml.getStringValue("@bet3")));
					sb.append(JXmlUtil.createAttrXml("bet0", mXml.getStringValue("@bet0")));
					sb.append(JXmlUtil.createAttrXml("bt", mXml.getStringValue("@mt")));
					String[] fs = new String[] { "sf", "rfsf", "sfc", "dxf" };
					StringBuffer _sb = new StringBuffer();
					for (int k = 0; k < fs.length; k++) {
						_sb.append(mXml.getStringValue("@" + fs[k]));
						if (k != fs.length - 1) {
							_sb.append("|");
						}
					}
					sb.append(JXmlUtil.createAttrXml("spvalue", _sb.toString()));
				}
			}
			sb.append(JXmlUtil.createAttrXml("result", ""));
			sb.append(JXmlUtil.createAttrXml("cancel", "0"));
			sb.append("/>\r\n");
		}
		sb.append("</items>\r\n");
		FileCastCodeUtil.write_to_file(new File(fdir, tb.getHid().toLowerCase() + ".xml"), sb);
		sb = null;
	}
	
	private JXmlWrapper getMatchInfo(JXmlWrapper xml, String matchid) {
		if (xml != null) {
			int count = xml.countXmlNodes("row");
			for (int i = 0; i < count; i++) {
				if (matchid.equals(xml.getStringValue("row[" + i + "].@itemid"))
						|| matchid.equals(xml.getStringValue("row[" + i + "].@mid"))
						|| matchid.equals(xml.getStringValue("row[" + i + "].@cindex"))) {
					return xml.getXmlNode("row[" + i + "]");
				}
			}
		}
		return null;
	}

	@Override
	public CastDto project_optimize_zq(TradeBean bean) {
		CastDto castDto = new CastDto();
		log.info("竞彩奖金优化投注project_dgpcreate,用户名=" + bean.getUid() + ",source=" + bean.getSource() + ","
				+ "type=" + bean.getType() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion()+" codes:"+bean.getCodes());
		if(0!=bean.getType()){
			log.info("不能发起合买,nickid=" + bean.getUid() + ",type=" + bean.getType()+ ",yhfs=" + bean.getYhfs());
			return castDto;
		}
		List<JcMatchBean> matchList = matchService.getJchhMatch();
		if(!matchService.setJchhMatchPid(matchList, bean)){
			return castDto;
		}
		
		FilterResult result = new FilterResult();
		if(!codeService.checkZqOptimizeCode(bean,result)){
			return castDto;
		}
		String filename;
		try {
			filename = saveOptCodeFile(bean, result);
		} catch (Exception e) {
			log.error("文件存储失败,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes()+" newCodes:"+bean.getNewcodes(),e);
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OPTIMIZE_FILE_SAVE_FAIL));
			bean.setBusiErrDesc("文件存储失败");
			return castDto;
		}
		if (StringUtil.getNullInt(bean.getSource())==6||StringUtil.getNullInt(bean.getSource())==7
				|| bean.getExtendtype()==6 || bean.getExtendtype()==7 || bean.getExtendtype()==11 || bean.getExtendtype()==14) { //奖金优化记录原单/一场制胜
		    codeService.refreshJcNewCodes(bean);
		    saveOptOriginalFile(bean, filename);
		}
		if(bean.getBusiErrCode() != 0){
			return castDto;
		}
		
		bean.setCodes(bean.getGid()+"_"+filename+"_n.txt");
		//设置场次
		bean.setZid(","+result.getTeamItems()+",");
		bean.setPlay(0);// 玩法
		bean.setFflag(1);// 文件标志（0 是号码 1 是文件）
		bean.setEndTime("");
		castDto = proj_cast_app(bean);
		return castDto;
	}

	@Override
	public CastDto project_optimize_lq(TradeBean bean) {
		CastDto castDto = new CastDto();
		log.info("篮彩奖金优化投注project_dgpcreate,用户名=" + bean.getUid() + ",source=" + bean.getSource() + ","
				+ "type=" + bean.getType() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion()+" codes:"+bean.getCodes());
		if(0!=bean.getType()){
			log.info("不能发起合买,nickid=" + bean.getUid() + ",type=" + bean.getType()+ ",yhfs=" + bean.getYhfs());
			return castDto;
		}
        Cache cache = null;
        CacheManager cm = CacheManager.getCacheManager();   
        cache = cm.getCacheMatch(bean.getGid(), bean.getPid());
        if (cache == null || cache.isExpired()) {
    		List<LcMatchBean> matchList = matchService.getBasketMatch(bean.getGid());
            cache = new Cache(bean.getGid() + bean.getPid(), matchList, System.currentTimeMillis() + (1000 * 60), false);				
            Cache ca = new Cache(TradeConstants.JCLQPLAYID.get(bean.getGid()) + bean.getPid(), matchList, System.currentTimeMillis()+1000*60, false);             
            cm.putCacheMatch(TradeConstants.JCLQPLAYID.get(bean.getGid()), bean.getPid(), ca);
            log.info(TradeConstants.JCLQPLAYID.get(bean.getGid())+"_"+ bean.getPid()+"本地缓存更新");
            cache = ca;
        } 
        
        if (cache != null){
        	 List<LcMatchBean> matchList = (List<LcMatchBean>) cache.getValue();
        	 matchService.setLcMatchPid(matchList, bean);
        }
        FilterResult result = new FilterResult();
		if(!codeService.checkLqOptimizeCode(bean, result)){
			return castDto;
		}
		String filename;
		try {
			filename = saveOptCodeFile(bean, result);
		} catch (Exception e) {
			log.error("文件存储失败,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" codes:"+bean.getCodes()+" newCodes:"+bean.getNewcodes(),e);
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OPTIMIZE_FILE_SAVE_FAIL));
			bean.setBusiErrDesc("文件存储失败");
			return castDto;
		}
		if (StringUtil.getNullInt(bean.getSource()) == 6 || bean.getExtendtype() == 6) { //奖金优化记录原单
			codeService.refreshLcNewCodes(bean);
			saveOptOriginalFile(bean, filename);
		}
		if(bean.getBusiErrCode() != 0){
			return castDto;
		}
		bean.setCodes(bean.getGid()+"_"+filename+"_n.txt");
		//设置场次
		bean.setZid(","+result.getTeamItems()+",");
		bean.setPlay(0);// 玩法
		bean.setFflag(1);// 文件标志（0 是号码 1 是文件）
		bean.setEndTime("");
		castDto = proj_cast_app(bean);
		return castDto;
	}

	//保存奖金优化原始文件
	private void saveOptOriginalFile(TradeBean bean, String filename) {
		String jjyh = StringUtil.getNullString(bean.getNewcodes());
		if (jjyh.split(";").length == 1) {
		    jjyh = bean.getNewcodes() + ";" + bean.getYhfs();
		}
		String[] jjyharr = jjyh.split(";");
		if (jjyharr.length == 2) {
		    String [] jjyhcod = jjyharr[0].split("\\|");
		    String jjyhcods = null;
		    if (jjyhcod.length == 3){
		        jjyhcods = jjyhcod[0] + "|" + jjyhcod[1];
		    } else {
		        jjyhcods = jjyharr[0];
		    }
		    StringBuffer yhxml = new StringBuffer();
		    yhxml.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		    yhxml.append("<xml>");
		    yhxml.append("<row ").append(JXmlUtil.createAttrXml("code", bean.getCodes()));
		    yhxml.append(" ").append(JXmlUtil.createAttrXml("matchs", jjyhcods));
		    yhxml.append(" ").append(JXmlUtil.createAttrXml("yhfs", jjyharr[1]));
		    yhxml.append(JXmlUtil.createAttrXml("missmatch", String.valueOf(bean.getPn())));
		    yhxml.append(" />");
		    yhxml.append("</xml>");
		    boolean flag = Util.SaveFile(yhxml.toString(), FileConstant.BASE_PATH  + File.separator + bean.getGid() + File.separator + bean.getPid(),bean.getGid()+"_"+filename+"_yh.xml", "utf-8");
		    if (!flag){
		    	log.info(filename+"_yh.xml"+"：存储失败,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" pid:"+bean.getPid()+" content:"+yhxml.toString());
				bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OPTIMIZE_FILE_SAVE_FAIL));
				bean.setBusiErrDesc("文件存储失败");
		    }
		}
	}

	//保存奖金优化拆分文件
	private String saveOptCodeFile(TradeBean bean, FilterResult result)
			throws Exception, FileNotFoundException, IOException {
		long time = System.currentTimeMillis();
		String name = bean.getUid() + bean.getGid() + time + bean.getPid();
		String filename = MD5Util.compute(name);
		File dir = new File(FileConstant.BASE_PATH + File.separator + bean.getGid() + File.separator + bean.getPid());
		if (!dir.exists()) {
		    dir.mkdirs();
		}
		File file = new File(dir, bean.getGid() + "_" + filename + "_n.txt");
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(result.getAllCodeToFile().getBytes());
		fout.close();
		log.info("文件完成检测,文件路径:"+file.getAbsolutePath()+" file.exists()"+file.exists()+" file.isFile()"+file.isFile());
		File newFile = new File(file.getAbsolutePath());
		log.info("文件完成检测,文件路径:"+newFile.getAbsolutePath()+" newfile.exists()"+newFile.exists()+" newfile.isFile()"+newFile.isFile());
		return filename;
	}
}

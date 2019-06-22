package com.caiyi.lottery.tradesystem.tradecenter.service.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.PeriodMapper;
import com.caiyi.lottery.tradesystem.tradecenter.service.PeriodService;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.proj.ProjUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import lombok.extern.slf4j.Slf4j;
import trade.bean.TradeBean;
import trade.pojo.PeriodPojo;

@Slf4j
@Service
public class PeriodServiceImpl implements PeriodService{
	@Autowired
	PeriodMapper periodMapper;
	
	//查询数字彩期次信息
	@Override
	public void querySZCPeriodEndTime(TradeBean bean) {
		PeriodPojo period = periodMapper.queryEndTime(bean.getGid(), bean.getPid());
		if(null != period){
			if(bean.getCodes().length() >= 3900 || bean.getFflag() == 1){
				bean.setEndTime(period.getFendtime());
			} else {
				bean.setEndTime(period.getEndtime());
			}
		}
		log.info("查询数字彩截止时间,gid=" + bean.getGid() + ",pid=" + bean.getPid() + ",endtime=" + bean.getEndTime());
	}
	
	/**
	 * 如果客户端没有传递北单期号到服务器,取北单对阵文件中的期号作为默认期号.
	 */
	@Override
	public void setDefaultBeidanPid(TradeBean bean) {
		if (GameContains.isBeiDan(bean.getGid()) && CheckUtil.isNullString(bean.getPid())) {
			JXmlWrapper bdXml = JXmlWrapper.parse(new File(FileConstant.BD_PERIOD));
			if("84".equals(bean.getGid())){
				bdXml = JXmlWrapper.parse(new File(FileConstant.SFGG_PERIOD));
			}
			Element root = bdXml.getXmlRoot();
			bean.setPid(root.getAttributeValue("pid"));
		}
	}

	@Override
	public String mpEndTimeReminder(TradeBean bean) {
		String nextpid = "";
		// 当该字段为true是时，表示进行慢频数字彩截止时间至开奖时间期间的提示
		if("true".equals(bean.getMpRemider())){
			try {
				// 慢频数字彩截止时间至开奖时间提示
				Map<String, String> info = checkBetweenAtAndEt(bean.getGid(), bean.getPid(), bean.getMpAgree());
				if ("true".equals(info.get("flag"))) {
					bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_MP_CAST_NEXT_PID));
					bean.setBusiErrDesc("今日开奖的" + info.get("overduepid") + "期" + info.get("endtime") + "已截止，正在预约第"
							+ info.get("nextpid") + "期，是否确定预约？");
					nextpid = info.get("nextpid");
					return nextpid;
				}	
			} catch (ParseException e) {
				log.error("检测慢频截止至开奖时间出错,用户名:"+bean.getUid()+" pid:"+bean.getUid()+" gid:"+bean.getGid()+" code:"+bean.getCodes(),e);
			}
		}
		return nextpid;
	}
	
	//检测是否在截止至开奖时间
	private Map<String, String> checkBetweenAtAndEt(String gid, String rpid, String confirm) throws ParseException{
		Map<String, String> info = new HashMap<String, String>();
		info.put("flag", "false");
		// 不是慢频数字彩，或者用户同意购买当前期次则不在检测
		if (!ProjUtils.isSlowSzc(gid) || ("true".equals(confirm))) {
			return info;
		}
		String xmlpath = FileConstant.DATA_DIR + "phot" + File.separator + gid;
		JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath, "c.xml"));
		int count = xml.countXmlNodes("row");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now = Calendar.getInstance();
		Calendar endtime = Calendar.getInstance();
		Calendar openAwardTime = Calendar.getInstance();
		// 截止时间
		String et = null;
		// 开奖时间
		String oa = null;
		// 期次
		String pid = null;
		for (int i = 0; i < count; i++) {
			pid = xml.getStringValue("row[" + i + "].@pid");
			if (!pid.equals(rpid)) {
				continue;
			}
			et = xml.getStringValue("row[" + (i + 1) + "].@et");
			endtime.setTime(df.parse(et));
			oa = xml.getStringValue("row[" + (i + 1) + "].@at");
			openAwardTime.setTime(df.parse(oa));
			String nextpid = xml.getStringValue("row[" + i + "].@pid");
			String overduepid = xml.getStringValue("row[" + (i + 1) + "].@pid");
			// 截止时间大于系统时间,当confirm为true
			if (now.compareTo(endtime) > 0 && now.compareTo(openAwardTime) < 0) {
				info.put("flag", "true");
				int hh = endtime.get(Calendar.HOUR_OF_DAY);
				int min = endtime.get(Calendar.MINUTE);
				info.put("endtime", hh + ":" + min);
				info.put("nextpid", nextpid);
				info.put("overduepid", overduepid);
			}
			break;
		}
		return info;
	}
}

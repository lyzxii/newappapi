package com.caiyi.lottery.tradesystem.tradecenter.service.impl;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.innerclient.RedisClient;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.*;
import com.caiyi.lottery.tradesystem.tradecenter.service.MatchService;
import com.caiyi.lottery.tradesystem.tradecenter.util.ValueComparator;
import com.caiyi.lottery.tradesystem.util.CheckUtil;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import com.caiyi.lottery.tradesystem.util.xml.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trade.bean.TradeBean;
import trade.bean.jclq.LcMatchBean;
import trade.bean.jczq.JcMatchBean;
import trade.constants.TradeConstants;
import trade.dto.SelectMatch;
import trade.dto.SelectMatchDto;
import trade.pojo.JcMatchPojo;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Service
public class MatchServiceImpl implements MatchService{
	
	@Autowired
	MatchMapper matchMapper;
	@Autowired
	JcMatchMapper jcMatchMapper;
	@Autowired
	BasketMatchMapper basketMatchMapper;
	@Autowired
	TopMatchMapper topMatchMapper;
	@Autowired
	BdSfggMatchMapper bdSfggMatchMapper;
	@Autowired
	RedisClient redisClient;
	@Autowired
	MatchFollowMapper matchFollowMapper;
	
	@Override
	public JXmlWrapper getMatchList(String gid, String pid) {
		List<Map<String, Object>> matchList = null;
		String type = "4";
		if (GameContains.isFootball(gid)) {
			pid = "";
			type = "5";
			matchList = jcMatchMapper.jcMatchList();
		} else if (Integer.parseInt(gid) == 84) {
			matchList = bdSfggMatchMapper.bdSfggMatchList(pid);
		} else if (GameContains.isBasket(gid)) {
			pid = "";
			type = "6";
			matchList = basketMatchMapper.basketMatchList();
		} else if (GameContains.isGYJ(gid)) {
			type = StringUtil.getNullString((Integer.valueOf(gid) - 98));
			matchList = topMatchMapper.topMatchList(type, pid);
		} else if (GameContains.isBeiDan(gid)) {
			matchList = matchMapper.bdMatchList(type, pid);
		} else {
			matchList = matchMapper.matchList(type, pid);
		}
		
		if(null != matchList && matchList.size() > 0){
			return XmlUtil.parseMapList(matchList, "row", "Resp");
		}else{
			return null;
		}
	}

	@Override
	public String getMatchMaxEndTime(String gid, String pid) {
		JXmlWrapper obj = getMatchList(gid, pid);
		String type = "4";
		if (GameContains.isFootball(gid)) {
			type = "5";// JC
		} else if (GameContains.isBasket(gid)) {
			type = "6";// LQ
		} else if (GameContains.isGYJ(gid)) {
			type = "7";// GYJ
		}

		if (obj != null) {
			String ret = "";
			int count = obj.countXmlNodes("row");
			for (int i = 0; i < count; i++) {
				if (type.equalsIgnoreCase("6")) { // LQ
					String et = obj.getStringValue("row[" + i + "].@et");
					if (ret.length() == 0) {
						ret = et;
					} else {
						if (ret.compareToIgnoreCase(et) <= 0) {
							ret = et;
						}
					}
				} else if (type.equalsIgnoreCase("5")) {// JC
					String et = obj.getStringValue("row[" + i + "].@et");
					if (ret.length() == 0) {
						ret = et;
					} else {
						if (ret.compareToIgnoreCase(et) <= 0) {
							ret = et;
						}
					}
				} else if (type.equalsIgnoreCase("7")) {// GYJ
					String et = obj.getStringValue("row[" + i + "].@endtime");
					if (ret.length() == 0) {
						ret = et;
					} else {
						if (ret.compareToIgnoreCase(et) <= 0) {
							ret = et;
						}
					}
				} else {// BD
					String et = obj.getStringValue("row[" + i + "].@et");

					if (ret.length() == 0) {
						ret = et;
					} else {
						if (ret.compareToIgnoreCase(et) <= 0) {
							ret = et;
						}
					}
				}
			}
			return ret;
		} else {
			return "";
		}
	}

	@Override
	public String getMatchMinEndTime(String gid, String pid, JXmlWrapper obj, String matches) {
		String type = "4";// BD
		if (GameContains.isFootball(gid)) {
			type = "5";// JC
		} else if (GameContains.isBasket(gid)) {
			type = "6";// LQ
		} else if (GameContains.isGYJ(gid)) {
			type = "7";// GYJ [冠亚军]
		}
		if (obj != null) {
			String[] ms = StringUtil.splitter(matches, ",");
			HashMap<String, String> maps = new HashMap<String, String>();

			String ret = "";
			int count = obj.countXmlNodes("row");
			for (int i = 0; i < count; i++) {
				String mid = "";

				if (type.equalsIgnoreCase("6")) {// LQ
					// 篮球
					mid = obj.getStringValue("row[" + i + "].@itemid");
					String et = obj.getStringValue("row[" + i + "].@et");
					if (matches.indexOf("," + mid + ",") >= 0) {
						if (ret.length() == 0) {
							ret = et;
						} else {
							if (ret.compareToIgnoreCase(et) >= 0) {
								ret = et;
							}
						}
					}
				} else if (type.equalsIgnoreCase("5")) {// JC
					mid = obj.getStringValue("row[" + i + "].@itemid");
					String et = obj.getStringValue("row[" + i + "].@et");
					if (matches.indexOf("," + mid + ",") >= 0) {
						if (ret.length() == 0) {
							ret = et;
						} else {
							if (ret.compareToIgnoreCase(et) >= 0) {
								ret = et;
							}
						}
					}
				} else if (type.equalsIgnoreCase("7")) {// GYJ
					mid = obj.getStringValue("row[" + i + "].@cindex");
					String et = obj.getStringValue("row[" + i + "].@endtime");
					if (matches.indexOf("," + mid + ",") >= 0) {
						if (ret.length() == 0) {
							ret = et;
						} else {
							if (ret.compareToIgnoreCase(et) >= 0) {
								ret = et;
							}
						}
					}
				} else {// BD
					mid = obj.getStringValue("row[" + i + "].@mid");
					String et = obj.getStringValue("row[" + i + "].@et");
					if (matches.indexOf("," + mid + ",") >= 0) {
						if (ret.length() == 0) {
							ret = et;
						} else {
							if (ret.compareToIgnoreCase(et) >= 0) {
								ret = et;
							}
						}
					}
				}
				maps.put(mid, mid);
			}
			for (int j = 0; j < ms.length; j++) {
				if (!CheckUtil.isNullString(ms[j])) {
					if (!maps.containsKey(ms[j]))
						ret = "";
				}
			}

			maps.clear();
			maps = null;
			return ret;
		} else {
			return "";
		}
	}

	@Override
	public JXmlWrapper getMatchXmlFromFile(String gid, String pid) {
		String path = FileConstant.BD_DIR + pid;
		if (GameContains.isFootball(gid)) {// JC
			path = FileConstant.JC_DIR;
		} else if (GameContains.isBasket(gid)) {// LQ
			path = FileConstant.BASKET_DIR;
		} else if (GameContains.isGYJ(gid)) {// GYJ
			path = FileConstant.GYJ_DIR + pid;
		}
		if (Integer.parseInt(gid) == 84) {
			path = FileConstant.SFGG_DIR + pid;
		}

		String fn = TradeConstants.matchnames.get(gid);
		File file = new File(path, fn);

		if (file.exists()) {
			return JXmlWrapper.parse(file);
		} else {
			log.info(file.getAbsolutePath() + " NOT FOUND");
		}
		return null;
	}

	//添加比赛关注
	@Override
	public void addMatchFollow(String matches, TradeBean bean) {
		Map<String, String> map = new HashMap<>();
		CacheBean cacheBean = new CacheBean();
		if(TradeConstants.jcGid.contains(bean.getGid())){
			cacheBean.setKey("newzlk_football_jc_unfinish");
			JXmlWrapper zqXml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.TRADECENTER);
			if(null == zqXml){
				return;
			}
			map.put("gtype", "70");
			map.put("gameid", "0");
			addMatchFollowData(map,matches,bean,zqXml);
		}else if(TradeConstants.lcGid.contains(bean.getGid())){
			cacheBean.setKey("newzlk_basketball_unfinish");
			JXmlWrapper lqXml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.TRADECENTER);
			if(null==lqXml){
				return;
			}
			map.put("gtype", "94");
			map.put("gameid", "1");
			addMatchFollowData(map,matches,bean,lqXml);
		}else if(TradeConstants.bdGid.contains(bean.getGid())){
			cacheBean.setKey("newzlk_football_bd_unfinish");
			JXmlWrapper bdXml = redisClient.getXmlString(cacheBean, log, SysCodeConstant.TRADECENTER);
			if(null==bdXml){
				return;
			}
			map.put("gtype", "85");
			map.put("gameid", "0");
			addMatchFollowData(map,matches,bean,bdXml);
		}
	}

	//添加比赛关注数据
	private void addMatchFollowData(Map<String, String> map, String matches, TradeBean bean, JXmlWrapper xml) {
		String[] matchArr = matches.split(",");
		for(String match : matchArr){
			if(StringUtil.isEmpty(match)){
				continue;
			}
			String gameid = map.get("gameid");
			String gtype = map.get("gtype");
			List<JXmlWrapper> rowsList = xml.getXmlNodeList("rows");
			for(JXmlWrapper rows : rowsList){
				List<JXmlWrapper> rowList = rows.getXmlNodeList("row");
				for(JXmlWrapper row : rowList){
					String compareId = "";
					if("85".equals(gtype)){
						compareId = row.getStringValue("@sort");
					}else{
						compareId = row.getStringValue("@roundItemId");
					}
					if((match).equals(compareId)){
						String qc = row.getStringValue("@qc");
						String rid = row.getStringValue("@rid");
						int count = matchFollowMapper.isFollowMatch(bean.getUid(), rid, gameid);
						if(count>0){//已经关注过
							continue;
						}else{
							matchFollowMapper.insertMatchFollow(bean.getUid(),rid,gameid,qc,compareId,gtype);
						}
					}
				}
			}
		}
	}

	@Override
	public List<JcMatchBean> getJchhMatch() {
		JXmlWrapper xml = JXmlWrapper.parse(new File("/opt/export/data/jincai", "jc_hh.xml"));
		int count = xml.countXmlNodes("row");
		List<JcMatchBean> mList = new ArrayList<JcMatchBean>();
		for (int i = 0; i < count; i++) {
			String mid = xml.getStringValue("row[" + i + "].@itemid");
			String hn = xml.getStringValue("row[" + i + "].@hn");
			String gn = xml.getStringValue("row[" + i + "].@gn");
			String bt = xml.getStringValue("row[" + i + "].@mt");
			String et = xml.getStringValue("row[" + i + "].@et");
			
			String fet = "";
			Date tmpet = DateUtil.parserDateTime(et);
			fet = DateUtil.getDateTime(tmpet.getTime());
			
			String b3 = xml.getStringValue("row[" + i + "].@bet3");
			String b1 = xml.getStringValue("row[" + i + "].@bet1");
			String b0 = xml.getStringValue("row[" + i + "].@bet0");
			int close = xml.getIntValue("row[" + i + "].@close", 0);
			String mname = xml.getStringValue("row[" + i + "].@name");

		    JcMatchBean mb = new JcMatchBean();	
			mb.setItemid(mid);
			mb.setHn(hn);
			mb.setGn(gn);
			mb.setBt(bt);
			mb.setEt(fet);
			mb.setB3(b3);
			mb.setB1(b1);
			mb.setB0(b0);
			mb.setClose(close);
			mb.setMname(mname);
			mList.add(mb);
		}
		return mList;
	}

	//获取比赛场次截止时间
	@Override
	public boolean setJchhMatchPid(List<JcMatchBean> matchlist, TradeBean bean) {
		Date firsttime = null;
		
		//获取方案的截至时间	
		String[] itemstr = StringUtil.splitter(bean.getItems(), ",");
		int chang = itemstr.length;
		for(int i=0;i<chang;i++){
			for(int ii=0;ii<matchlist.size();ii++){
			  if (matchlist.get(ii).getItemid().equals(itemstr[i])){
				  Date tmpdate =DateUtil.parserDateTime(matchlist.get(ii).getEt());
				  if (firsttime==null){
					  firsttime=tmpdate;
				  }else{	  
					  if (tmpdate.getTime() < firsttime.getTime()){
						  firsttime = tmpdate;
					  }
				  }
			  }
			}
		}
		if (firsttime == null) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CANNOT_GET_MATCH_TIME));
			bean.setBusiErrDesc("投注失败");
			log.info("投注失败：无法获取方案截止时间 uid:"+bean.getUid()+" gid:"+bean.getGid()+" codes:"+bean.getCodes()+" items:"+bean.getItems());
			return false;
		}
		if (System.currentTimeMillis() > firsttime.getTime()) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
			bean.setBusiErrDesc("方案已截止");
			log.info("方案截止时间为：" + DateUtil.getDateTime(firsttime.getTime()) + " 下次请提前,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" codes:");
			return false;
		}
		
		firsttime.setTime(firsttime.getTime()- 1000 * 60 * 10);  //期次=截止时间-10分钟，需要与后台保持一致
        String expect = DateUtil.getDateTime(firsttime.getTime(),"yyyyMMdd");
        bean.setPid(expect);
        return true;
	}

	@Override
	public List<LcMatchBean> getBasketMatch(String gid) {
        String[] fn = new String[] {"basket_sf.xml", "basket_rfsf.xml", "basket_sfc.xml", "basket_dxf.xml", "basket_hh.xml"};
        
        int value = "71".equals(gid) ? 4 : Integer.parseInt(TradeConstants.playid.get(gid)) - 94;
        JXmlWrapper xml = JXmlWrapper.parse(new File("/opt/export/data/basket", fn[value]));
      
        int count = xml.countXmlNodes("row");
        List<LcMatchBean> mList = new ArrayList<LcMatchBean>();
        for (int i = 0; i < count; i++) {
            String mid = xml.getStringValue("row[" + i + "].@itemid");
            String hn = xml.getStringValue("row[" + i + "].@hn");
            String gn = xml.getStringValue("row[" + i + "].@gn");
            String bt = xml.getStringValue("row[" + i + "].@mt");
            String et = xml.getStringValue("row[" + i + "].@et");
            String b3 = xml.getStringValue("row[" + i + "].@bet3");
            String b0 = xml.getStringValue("row[" + i + "].@bet0");
            String mname = xml.getStringValue("row[" + i + "].@name");
            String close = xml.getStringValue("row[" + i + "].@close");
            if (value == 3){
                close = xml.getStringValue("row[" + i + "].@zclose");
            }

            LcMatchBean mb = new LcMatchBean(); 
            mb.setItemid(mid);
            mb.setHn(hn);
            mb.setGn(gn);
            mb.setBt(bt);
            mb.setEt(et);
            mb.setB3(b3);
            mb.setB0(b0);
            mb.setMname(mname);
            mb.setClose(close);

            switch (value) {
                case 0:
                    mb.setSpv(xml.getStringValue("row[" + i + "].@sf"));
                    break;
                case 1:
                    mb.setSpv(xml.getStringValue("row[" + i + "].@rfsf"));
                    break;
                case 2:
                    mb.setSpv(xml.getStringValue("row[" + i + "].@sfc"));
                    break;
                case 3:
                    mb.setSpv(xml.getStringValue("row[" + i + "].@dxf"));
                    break;
                case 4:
                    mb.setSpv(xml.getStringValue("row[" + i + "].@sf") + "," + xml.getStringValue("row[" + i + "].@rfsf") + "," + xml.getStringValue("row[" + i + "].@sfc") + "," + xml.getStringValue("row[" + i + "].@dxf"));
                    break;
                default:
                    break;
            }
            mList.add(mb);
				
        }
		return mList;
	}

	@Override
	public boolean setLcMatchPid(List<LcMatchBean> matchlist, TradeBean bean) {
        Date firsttime = null;
		//获取方案的截至时间	
        String[] itemstr = StringUtil.splitter(bean.getItems(), ",");
        int chang = itemstr.length;
        for (int i = 0; i < chang; i++){
            for (int ii = 0; ii < matchlist.size(); ii++){
                if (matchlist.get(ii).getItemid().equals(itemstr[i])){
                    Date tmpdate = DateUtil.parserDateTime(matchlist.get(ii).getEt());
                    if (firsttime == null){
                        firsttime = tmpdate;
                    } else {	  
                        if (tmpdate.getTime() < firsttime.getTime()){
                            firsttime = tmpdate;
                        }
                    }
                }
            }
        }
        if (firsttime == null) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_CANNOT_GET_MATCH_TIME));
			bean.setBusiErrDesc("投注失败");
			log.info("投注失败：无法获取方案截止时间 uid:"+bean.getUid()+" gid:"+bean.getGid()+" codes:"+bean.getCodes()+" items:"+bean.getItems());
			return false;
		}
        if (System.currentTimeMillis() > firsttime.getTime()) {
			bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_OUT_OF_ENDTIME));
			bean.setBusiErrDesc("方案已截止");
			log.info("方案截止时间为：" + DateUtil.getDateTime(firsttime.getTime()) + " 下次请提前,用户名:"+bean.getUid()+" gid:"+bean.getGid()+" codes:");
			return false;
        }
        firsttime.setTime(firsttime.getTime() - 1000 * 60 * 10);  //期次=截止时间-10分钟，需要与后台保持一致
        String expect = DateUtil.getDateTime(firsttime.getTime(), "yyyyMMdd");
        bean.setPid(expect);
		return true;
	}

	/**
	 * 一场致胜 选择匹配场次
	 */
	@Override
	public List<SelectMatchDto> selectMatchingDz(TradeBean bean) {
		log.info("一场致胜，选择匹配场次信息  uid==" + bean.getUid() + ",codes==" + bean.getCodes());
		String buyCode=bean.getCodes();// 170215002>SPF=3+RQSPF=3,170215003>SPF=1+RQSPF=0
		String[] codes = buyCode.split(",");
		List<SelectMatchDto> dtoList=new ArrayList<>();
		for (String zxitemid : codes) {
			bean.setItemid(zxitemid);
			DecimalFormat df=new DecimalFormat("#.00");
			if(!getMatchAppkey(bean, df,0)){//获取appkey
				getMatchAppkey(bean,df,1);
			}
			String ppitemid = bean.getAppkey();//匹配场次itemid
			if(StringUtil.isEmpty(ppitemid)){
              log.warn("zxitemid:{}匹配场次为空",zxitemid);
			}
			dtoList.add(builderResultJson(zxitemid,ppitemid));
		}
		return dtoList;
	}

	private SelectMatchDto builderResultJson( String zxitemid,String ppitemid) {
		JXmlWrapper xml = JXmlWrapper.parse(new File("/opt/export/data/jincai", "jc_hh.xml"));
		List<JXmlWrapper> xmlNodeList = xml.getXmlNodeList("row");
		SelectMatchDto dto=new SelectMatchDto();
		dto.setZxitemid(zxitemid);
		for (JXmlWrapper row : xmlNodeList) {
			String mid = row.getStringValue("@itemid");
			if(ppitemid.equals(mid)){
				SelectMatch selectMatch=new SelectMatch();
				selectMatch.setItemid(row.getStringValue("@itemid"));
				selectMatch.setMid(row.getStringValue("@mid"));
				selectMatch.setHn(row.getStringValue("@hn"));
				selectMatch.setGn(row.getStringValue("@gn"));
				selectMatch.setEt(row.getStringValue("@et"));
				selectMatch.setMt(row.getStringValue("@mt"));
				selectMatch.setMname(row.getStringValue("@mname"));
				selectMatch.setClose(row.getStringValue("@close"));
				selectMatch.setName(row.getStringValue("@name"));
				selectMatch.setSpf(row.getStringValue("@spf"));
				selectMatch.setRqspf(row.getStringValue("@rqspf"));
				dto.setMatch(selectMatch);
			}
		}
       return dto;
	}

	private boolean getMatchAppkey(TradeBean bean, DecimalFormat df,int flag) {
		List<String> endtimeList=null;
		if(flag==0){
			endtimeList=jcMatchMapper.queryMatchEndTimeAfter();
		}else{
			endtimeList=jcMatchMapper.queryMatchEndTimeBefore();
		}
		if(endtimeList!=null&&endtimeList.size()!=0){
            for(String endtime:endtimeList){
                List<JcMatchPojo> jcMatchPojoList=null;
                if(flag==0){
					jcMatchPojoList=jcMatchMapper.getAfterMatchInfo(bean.getItemid(),endtime);
				}else{
					jcMatchPojoList=jcMatchMapper.getBeforeMatchInfo(bean.getItemid(),endtime);
				}
                if(jcMatchPojoList!=null&&jcMatchPojoList.size()>4){
                    String smallSpvItemid = getSmallSpvItemid(bean, df,jcMatchPojoList);
                    if(!StringUtil.isEmpty(smallSpvItemid)){
                        bean.setAppkey(smallSpvItemid);
                    }
                    return true;
                }
            }
        }
        return false;
	}

	/**
	 * 获取胜平负，让球胜平负；sp绝对值最小的场次
	 */
	private String getSmallSpvItemid(TradeBean bean, DecimalFormat df,List<JcMatchPojo> jcMatchlist) {
		HashMap<String,Double> map = new HashMap<String, Double>();
		for(JcMatchPojo jcMatch:jcMatchlist){
			String citemid = jcMatch.getItemid();
			Double close = Double.valueOf(jcMatch.getClose());
			String spf = jcMatch.getSpf();
			String rqspf =jcMatch.getRqspf();
			if(StringUtil.isEmpty(spf) || StringUtil.isEmpty(rqspf)){
				bean.setBusiErrCode(-1);
				bean.setBusiErrDesc("一场致胜匹配场次失败");
				log.error("获取胜平负，让球胜平负，sp绝对值最小的场次失败,spf,rqspf为空");
				return "";
			}
			String[] spfs = spf.split(",");
			String[] rqspfs = rqspf.split(",");
			double zs = Double.valueOf(spfs[0]);
			double ks = Double.valueOf(spfs[2]);
			double rqzs = Double.valueOf(rqspfs[0]);
			double rqks = Double.valueOf(rqspfs[2]);
			Double absoluteSpv = 0.0;
			if(close < 0){//主让球
				String spv = df.format(Math.abs(zs-rqks));//主胜-让球客胜
				absoluteSpv = Double.valueOf(spv);
			}else{//客让球
				String spv = df.format(Math.abs(ks-rqzs));//客胜-让球主胜
				absoluteSpv = Double.valueOf(spv);
			}
			if(!map.containsKey(citemid)){
				map.put(citemid, absoluteSpv);
			}
		}
		//根据value排序，返回场次ID
		ValueComparator bvc =  new ValueComparator(map);
		TreeMap<String,Double> sorted_map = new TreeMap<String,Double>(bvc);
		sorted_map.putAll(map);
		return sorted_map.lastKey();//筛选出来的场次编号
	}
}

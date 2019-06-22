package com.caiyi.lottery.tradesystem.tradecenter.service;



import java.util.List;

import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;

import trade.bean.TradeBean;
import trade.bean.jclq.LcMatchBean;
import trade.bean.jczq.JcMatchBean;
import trade.dto.SelectMatchDto;


//比赛场次业务
public interface MatchService {
	//获取场次信息
	JXmlWrapper getMatchList(String gid, String pid);
	//获取最大场次截止时间
	String getMatchMaxEndTime(String gid, String pid);
	//获取最小场次截止时间
	String getMatchMinEndTime(String gid, String pid, JXmlWrapper obj, String matches);
	//从文件获取场次信息
    JXmlWrapper getMatchXmlFromFile(String gid, String pid);
    //添加比赛关注
    void addMatchFollow(String matches, TradeBean bean);
    //获取竞彩混合所有比赛
    List<JcMatchBean> getJchhMatch();
    //设置竞彩pid
    boolean setJchhMatchPid(List<JcMatchBean> matchlist, TradeBean bean);
    //获取篮彩比赛场次信息
	List<LcMatchBean> getBasketMatch(String gid);
    //设置篮彩pid
    boolean setLcMatchPid(List<LcMatchBean> matchlist, TradeBean bean);

    List<SelectMatchDto> selectMatchingDz(TradeBean bean);
}	

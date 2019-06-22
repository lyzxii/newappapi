package com.caiyi.lottery.tradesystem.usercenter.service.impl;

import java.util.List;

import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.dao.AdsenseMapper;
import com.caiyi.lottery.tradesystem.usercenter.service.UserActivationCallbackService;
import com.caiyi.lottery.tradesystem.util.DateTimeUtil;
import com.caiyi.lottery.tradesystem.util.HttpClientUtil;

import bean.UserBean;
import pojo.Adsense;

@Service
public class UserActivationCallbackServiceImpl implements UserActivationCallbackService {

	private Logger log = LoggerFactory.getLogger(UserActivationCallbackServiceImpl.class);

	@Autowired
	private AdsenseMapper adsenseMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void adInvoke(UserBean bean) throws Exception{
		log.info("广告统计匹配idfa或ip信息  idfa="+ bean.getAid() + " ip="+bean.getIpAddr());
		bean.setBusiErrCode(Integer.parseInt(BusiCode.FAIL));
		bean.setBusiErrDesc("激活失败");

		int num = adsenseMapper.getNumByAidOrIp(bean.getAid(), bean.getIpAddr());
		if (num > 0) {
			log.info("重复匹配idfa或ip信息  idfa=" + bean.getAid() + " ip=" + bean.getIpAddr());
			bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_INVOKE_REPEAT));
			bean.setBusiErrDesc("重复匹配idfa或ip信息");

		} else {

			boolean idfa_result = invokeIdfa(bean);// 匹配idfa
			if (!idfa_result || bean.getAuth() == 100) {
				invokeIp(bean);// idfa未查询到则匹配ip
			}

			// idfa跟ip均未匹配成功
			if (bean.getAuth() == 200) {

				List<Adsense> adsenses = adsenseMapper.getByAidAndIp(bean.getAid(), bean.getIpAddr());
				if (adsenses == null || adsenses.size() <= 0) {
					// 记录未匹配上的记录
					int ret = adsenseMapper.insert(bean.getAid(), bean.getSource(), bean.getIpAddr(), bean.getImei(),
							"1");
					if (ret == 1) {
						bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_INVOKE_MATCH_NOFOUND));
						bean.setBusiErrDesc("没有找到匹配");
					} else {
						bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_INVOKE_MATCH_FAIL));
						bean.setBusiErrDesc("ip记录失败");
					}
				} else {
					log.info("累计查询到idfa记录条数：" + adsenses.size());
					for (Adsense ad : adsenses) {
						String cadddate = ad.getAdddate();
						String current = DateTimeUtil.getCurrentFormatDate("yyyy-MM-dd HH:mm:ss");
						String startDateInput = DateTimeUtil.getBeforeXminTime(current, 12 * 60 * 60 * 1000);
						if (current.compareTo(cadddate) < 0 || startDateInput.compareTo(cadddate) > 0) {
							updateTimeNotifyDesc(bean, cadddate, current);
							log.info("idfa==" + bean.getAid() + "或ip==" + bean.getIpAddr()
									+ "不匹配，原因：添加时间不在当前时间12小时内(当前时间==" + current + ",添加时间==" + cadddate + ")");
						}

						// source不匹配
						String isource = ad.getSource();
						int source = bean.getSource();
						if (source != Integer.valueOf(isource)) {
							updateSourceNotifyDesc(bean, cadddate, isource, source);
							log.info("idfa==" + bean.getAid() + "或ip==" + bean.getIpAddr()
									+ "不匹配，原因：source不值不匹配(客户端source==" + source + ",数据库source==" + isource + ")");
						}
					}
				}
			}
		}
		
		if (bean.getBusiErrCode() == Integer.valueOf(BusiCode.USER_INVOKE_MATCH_SUCCESS).intValue()) {
			log.info("----adNotify.url------>" + bean.getBackurl());
			if (StringUtils.isNotBlank(bean.getBackurl())) {
				String str = HttpClientUtil.callHttpGet(bean.getBackurl());
				log.info("通知广告联盟结果:" + str);
			}
		}
		if (bean.getBusiErrCode() == Integer.valueOf(BusiCode.USER_INVOKE_MATCH_SUCCESS).intValue() || bean.getBusiErrCode() == Integer.valueOf(ErrorCode.USER_INVOKE_MATCH_FAIL).intValue() || bean.getBusiErrCode() == Integer.valueOf(BusiCode.USER_INVOKE_MATCH_NOFOUND).intValue()) {
			bean.setBusiErrCode(0);
			bean.setBusiErrDesc("激活接口调用成功");
		}

	}

	private void updateSourceNotifyDesc(UserBean bean, String cadddate, String isource, int source) {
		int ret = adsenseMapper.updateNotifyDescByIdOrIp("idfa==" + bean.getAid() + "或ip==" + bean.getIpAddr()
				+ "不匹配，原因：source不值不匹配(客户端source==" + source + ",数据库source==" + isource + ")", bean.getAid(),
				bean.getIpAddr(), cadddate);
		if (ret == 1) {
			log.info("匹配ip==" + bean.getIpAddr() + "更新状态成功");
			bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_INVOKE_MATCH_SUCCESS));
			bean.setBusiErrDesc("匹配成功");
		} else {
			log.info("匹配 ip==" + bean.getIpAddr() + "更新状态失败");
			bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_INVOKE_MATCH_FAIL));
			bean.setBusiErrDesc("记录匹配失败");
		}

	}

	private void updateTimeNotifyDesc(UserBean bean, String cadddate, String current) {
		int ret = adsenseMapper.updateNotifyDescByIdOrIp("idfa==" + bean.getAid() + "或ip==" + bean.getIpAddr()
				+ "不匹配，原因：添加时间不在当前时间12小时内(当前时间==" + current + ",添加时间==" + cadddate + ")", bean.getAid(),
				bean.getIpAddr(), cadddate);
		if (ret == 1) {
			log.info("匹配 ip==" + bean.getIpAddr() + "更新状态成功");
			bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_INVOKE_MATCH_SUCCESS));
			bean.setBusiErrDesc("匹配成功");
		} else {
			log.info("匹配ip==" + bean.getIpAddr() + "更新状态失败");
			bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_INVOKE_MATCH_FAIL));
			bean.setBusiErrDesc("记录匹配失败");
		}
	}

	/**
	 * 匹配id
	 * 
	 * @param bean
	 */
	private boolean invokeIp(UserBean bean) {
		log.info("匹配ip==" + bean.getIpAddr() + "  时间==" + System.currentTimeMillis());
		boolean flag = false;
		if (StringUtils.isBlank(bean.getIpAddr())) {
			return flag;
		}

		List<Adsense> adsenses = adsenseMapper.getByIpAndSource(bean.getIpAddr(), bean.getSource());
		Adsense adsense;
		if (adsenses != null && adsenses.size() == 1) {
			adsense = adsenses.get(0);
			int ret = adsenseMapper.updateByIp(bean.getIpAddr(), bean.getImei(), "ip匹配激活", bean.getIpAddr(),
					adsense.getAdddate());
			if (ret == 1) {
				log.info("匹配ip==" + bean.getIpAddr() + "更新状态成功");
				bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_INVOKE_MATCH_SUCCESS));
				bean.setBusiErrDesc("匹配成功");
			} else {
				log.info("匹配ip==" + bean.getIpAddr() + "更新状态失败");
				bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_INVOKE_MATCH_FAIL));
				bean.setBusiErrDesc("记录匹配失败");
			}

			bean.setBackurl(adsense.getCallback());
			adsenseMapper.updateAbsenseClientChannelBySourceAndChannel(bean.getSource(), adsense.getChannel());
			flag = true;
		} else {

			bean.setAuth(bean.getAuth() + 100);// 200表示idfa跟ip均未查询到
		}

		log.info("为查询到相关ip记录，ip==" + bean.getIpAddr() + "时间==" + System.currentTimeMillis());
		return flag;
	}

	/**
	 * 匹配idfa
	 * 
	 * @param bean
	 * @return
	 */
	private boolean invokeIdfa(UserBean bean) {
		log.info("匹配idfa==" + bean.getAid() + "  时间==" + System.currentTimeMillis());
		// 匹配idfa或者ip
		boolean flag = false;
		if (StringUtils.isBlank(bean.getAid())) {
			return flag;
		}

		List<Adsense> adsenses = adsenseMapper.getByAidAndSource(bean.getAid(), bean.getSource());
		Adsense adsense;
		if (adsenses != null && adsenses.size() == 1) {
			adsense = adsenses.get(0);
			int ret = adsenseMapper.updateByAid(bean.getIpAddr(), bean.getImei(), "idfa匹配激活", bean.getAid(),
					adsense.getAdddate());
			if (ret == 1) {
				log.info("匹配idfa==" + bean.getAid() + "更新状态成功");
				bean.setBusiErrCode(Integer.valueOf(BusiCode.USER_INVOKE_MATCH_SUCCESS));
				bean.setBusiErrDesc("匹配成功");
			} else {
				log.info("匹配idfa==" + bean.getAid() + "更新状态失败");
				bean.setBusiErrCode(Integer.valueOf(ErrorCode.USER_INVOKE_MATCH_FAIL));
				bean.setBusiErrDesc("记录匹配失败");
			}
			bean.setBackurl(adsense.getCallback());
			adsenseMapper.updateAbsenseClientChannelBySourceAndChannel(bean.getSource(), adsense.getChannel());
			flag = true;
		} else {

			bean.setAuth(100);// idfa未查询到
		}
		log.info("为查询到相关idfa记录，idfa==" + bean.getAid() + "时间==" + System.currentTimeMillis());
		return flag;
	}

}

package com.caiyi.lottery.tradesystem.integralcenter.service.impl;

import bean.UserBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.Page;
import com.caiyi.lottery.tradesystem.bean.Result;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import integral.bean.IntegralPageBean;
import com.caiyi.lottery.tradesystem.integralcenter.dao.IntegralCenterQueryDao;
import com.caiyi.lottery.tradesystem.integralcenter.service.IntegralCenterQueryService;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.usercenter.client.UserInterface;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import integral.bean.IntegralBean;
import integral.bean.IntegralParamBean;
import pojo.Acct_UserPojo;
import pojo.UserPojo;
import pojo.UserRecordPojo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IntegralCenterQueryServiceImpl implements IntegralCenterQueryService {

    private Logger logger = LoggerFactory.getLogger(IntegralCenterQueryServiceImpl.class);

    @Autowired
    IntegralCenterQueryDao dao;

    @Autowired
    UserInterface userInterface;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResp<IntegralBean> sign(UserBean bean) throws Exception {
        BaseResp<IntegralBean> result = new BaseResp<IntegralBean>();
        IntegralBean integralBean = new IntegralBean();
        logger.info("积分中心---> 开始手动签到");
        String uid = bean.getUid();
        if (com.caiyi.lottery.tradesystem.util.StringUtil.isEmpty(uid)) {
            result.setCode(Result.FAIL);
            result.setDesc("用户名不能为空");
            return result;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String today = sdf.format(new Date());

        if (cannotSign(uid)) { //根据购彩记录查询是否有签到资格
            integralBean.setFlag(Result.FAIL);
            result.setCode(Result.FAIL);
            result.setDesc("非购彩用户，无签到资格");
            result.setData(integralBean);
            logger.info("用户[" + bean.getUid() + "]无签到资格，flag == -1");
            return result;
        }
        logger.info("用户[" + uid + "]有签到资格");
        integralBean.setCode(Result.FAIL);

        if (!checkCurrentUser(uid, today)) {//检测该用户在签到表中的状态
            result.setData(integralBean);
            result.setCode(Result.FAIL);
            result.setDesc("无该用户或插入失败");
            return result;
        }

        int i = clickToSign(today, uid, bean.getHasSignDays());///增加天数


        if (1 == i) {
            String type = "每日签到";
            int dayPoints = 25;
            int sevenDayPoints = 0;
            int biztype = 265;
            logger.info("用户" + bean.getUid() + "手动签到获取积分记入流水开始");
            int j = insertPointCharge(bean, type, dayPoints, biztype);//插入积分流水表
            if (1 != j) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("手动签到获取积分记入流水失败");
                throw new Exception("手动签到获取积分记入流水失败");
            }

            logger.info("用户" + bean.getUid() + "手动签到操作增加天数成功，记入积分流水表结果，[j]==" + j);
            bean.setMemBasicVal(bean.getMemBasicVal() + 25);

            if ("6".equals(bean.getHasSignDays())) {//已经签到了六天，今日是第七天签到，增加七日额外积分流水和积分值
                logger.info("用户" + bean.getUid() + "过去已连签六天，今日第七天签到");
                type = "连签七天";
                sevenDayPoints = 175;
                biztype = 211;//连签七天 biztype值
                int k = insertPointCharge(bean, type, sevenDayPoints, biztype);//插入积分流水表
                logger.info("用户" + bean.getUid() + "连签七天，记入积分流水表结果，[j]==" + j);
                if (k != 1) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    bean.setBusiErrDesc("手动签到获取积分记入流水失败");
                    logger.info("用户" + bean.getUid() + "连签七天获取积分记入流水失败,回滚状态");
                    throw new Exception("连签七天获取积分记入流水失败");
                }
                logger.info("用户" + bean.getUid() + "连签七天获取积分记入流水成功");
            }

            int points = dayPoints + sevenDayPoints;

            int k = updateUserPoint(bean, points);//更新用户积分

            if (j == 1 && k == 1) {
                logger.info("用户" + bean.getUid() + "积分添加成功");
                integralBean.setCode(Result.SUCCESS);
                result.setCode(Result.SUCCESS);
                result.setDesc("积分添加成功");
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("积分添加失败");
                logger.info("用户" + bean.getUid() + "积分添加失败");
                throw new Exception("积分添加失败");
            }

        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("签到天数+1失败");
            logger.info("用户" + bean.getUid() + "签到天数+1失败");
            throw new Exception("签到天数+1失败");
        }
        result.setData(integralBean);
        return result;
    }


    @Override
    public BaseResp<IntegralBean> integralCenterImage(UserBean bean) throws Exception {
        BaseResp<IntegralBean> result = new BaseResp<IntegralBean>();
        logger.info("积分中心--->开始查询用户" + bean.getUid() + "积分中心信息");
        String uid = bean.getUid();
        if (com.caiyi.lottery.tradesystem.util.StringUtil.isEmpty(uid)) {
            result.setCode(BusiCode.FAIL);
            result.setDesc("用户名不能为空");
            return result;
        }

        //查询头像、等级、当前积分值
        IntegralBean integralBean = queryBasicInfo(uid);
        //查询签到天数信息和签到状态
        IntegralBean integralBean1 = querySignInfo(uid);
        //查询每日已获取积分
        String todayGetPoints = queryDayPoints(uid);
        //查询银行卡和身份证绑定及积分领取信息
        IntegralBean integralBean2 = queryBindInfo(uid);

        integralBean.setCode(integralBean1.getCode());
        integralBean.setSignDays(integralBean1.getSignDays());
        integralBean.setStatus(integralBean1.getStatus());

        integralBean.setTotalPoints(todayGetPoints);

        integralBean.setIsGetPoint(integralBean2.getIsGetPoint());
        integralBean.setTask(integralBean2.getTask());
        integralBean.setBindIdCard(integralBean2.getBindIdCard());
        integralBean.setBindBankCard(integralBean2.getBindBankCard());
        result.setCode(BusiCode.SUCCESS);
        result.setDesc("success");
        result.setData(integralBean);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public BaseResp<IntegralBean> getUserPoints(UserBean bean) throws Exception {
        IntegralBean integralBean = new IntegralBean();
        BaseResp<IntegralBean> result = new BaseResp<IntegralBean>();
        logger.info("用户[" + bean.getUid() + "]手动领取积分参数==[pointform--" + bean.getGetPointForm() + "");
        integralBean.setCode(Result.FAIL);
        // 银行卡和身份证绑定状态
        integralBean = queryBindInfo(bean.getUid());
        String bindIdCard = integralBean.getBindIdCard();
        String bindBankCard = integralBean.getBindBankCard();
        // 积分领取状态
        String task = integralBean.getTask();

        String from = bean.getGetPointForm();
        int itask = 0;
        String type = "类型判断错误";
        int biztype = 263;// 绑定身份证领取biztype值
        String bitand = "";

        if ("2".equals(from)) {// 身份证领取积分，银行卡未领取
            itask = 2;// 10
            type = "绑定身份证领取500积分";
            if (!"0".equals(bindIdCard)) {// 未绑身份证
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("身份证未绑定");
                result.setData(integralBean);
                return result;
            }
            if ("0".equals(task)) {
                bitand = "0";
//                sql = "update tb_user set itaskinit = ? where cnickid = ? and bitand(?,0) = 0";
            } else {
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("身份证积分领取请求错误");
                result.setData(integralBean);
                return result;
            }
        } else if ("1".equals(from)) {// 身份证未领取积分，银行卡领取
            itask = 1;// 01
            type = "绑定银行卡领取500积分";
            biztype = 210;// 绑定银行卡领取biztype值
            if (!"0".equals(bindBankCard)) {// 银行卡未绑
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("银行卡未绑定");
                result.setData(integralBean);
                return result;
            }
            if ("0".equals(task)) {
                bitand = "0";
//                sql = "update tb_user set itaskinit = ? where cnickid = ? and bitand(?,0) = 0";
            } else {
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("银行卡积分领取请求错误");
                result.setData(integralBean);
                return result;
            }
        } else if ("3".equals(from)) {// 身份证已领取积分，银行卡领取
            itask = 3;// 11
            type = "绑定银行卡领取500积分";
            biztype = 210;// 绑定银行卡领取biztype值
            if (!"0".equals(bindBankCard)) {// 银行卡
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("银行卡未绑定");
                result.setData(integralBean);
                return result;
            }
            if ("2".equals(task)) {
                bitand = "2";
//                sql = "update tb_user set itaskinit = ? where cnickid = ? and bitand(?,2) = 2";
            } else {
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("银行卡积分领取请求错误");
                result.setData(integralBean);
                return result;
            }

        } else if ("4".equals(from)) {// 身份证领取积分，银行卡已领取
            itask = 3;// 11
            type = "绑定身份证领取500积分";
            if (!"0".equals(bindIdCard)) {// 身份证未绑定
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("身份证未绑定");
                result.setData(integralBean);
                return result;
            }
            if ("1".equals(task)) {
                bitand = "1";
//                sql = "update tb_user set itaskinit = ? where cnickid = ? and bitand(?,1) = 1";
            } else {
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("身份证积分领取请求错误");
                result.setData(integralBean);
                return result;
            }

        } else {
            integralBean.setCode(Result.FAIL);
            result.setCode(Result.FAIL);
            result.setDesc("参数错误");
            result.setData(integralBean);
            return result;
        }

        int i = clickToGetPoints(itask, bean.getUid(), task, bitand);

        if (i == 1) {
            // 积分流水表
            int j = insertPointCharge(bean, type, 500, biztype);
            // 更新用户积分表update
            int k = updateUserPoint(bean, 500);
            if (j == 1 && k == 1) {
                integralBean.setCode(Result.SUCCESS);
                result.setCode(Result.SUCCESS);
                result.setDesc("领取成功");
                result.setData(integralBean);
            } else {
                integralBean.setCode(Result.FAIL);
                result.setCode(Result.FAIL);
                result.setDesc("领取失败");
                result.setData(integralBean);
                throw new Exception("领取积分失败");
            }
        } else {
            integralBean.setCode(Result.FAIL);
            result.setCode(Result.FAIL);
            result.setDesc("领取出错");
            result.setData(integralBean);
            throw new Exception("领取积分出错");
        }
        return result;
    }

    @Override
    public BaseResp<Page> getExperienceDetail(UserBean bean) throws Exception {
        PageHelper.startPage(bean.getPn(), bean.getPs());
        BaseResp<Page> result = new BaseResp<>();
        List<IntegralPageBean> pageBean = experienceDetail(bean);
        PageInfo<IntegralPageBean> info = new PageInfo<IntegralPageBean>(pageBean);
        Page page = new Page(bean.getPs(), bean.getPn(), info.getPages(), info.getTotal(), pageBean);
        if (null != pageBean) {
            result.setCode(BusiCode.SUCCESS);
            result.setDesc("查询成功");
            result.setData(page);
            return result;
        }
        result.setCode(BusiCode.USER_QUERYACCOUNT_NODATA);
        result.setDesc("暂无数据");
        result.setData(page);
        return result;
    }

    @Override
    public BaseResp<Page> getPointsDetail(UserBean bean) throws Exception {
        PageHelper.startPage(bean.getPn(), bean.getPs());
        BaseResp<Page> result = new BaseResp<>();
        List<IntegralPageBean> pageBean = pointsDetail(bean);
        PageInfo<IntegralPageBean> info = new PageInfo<IntegralPageBean>(pageBean);
        Page page = new Page(bean.getPs(), bean.getPn(), info.getPages(), info.getTotal(), pageBean);
        if (null != pageBean) {
            result.setCode(BusiCode.SUCCESS);
            result.setDesc("查询成功");
            result.setData(page);
            return result;
        }
        result.setCode(BusiCode.USER_QUERYACCOUNT_NODATA);
        result.setDesc("暂无数据");
        result.setData(page);
        return result;
    }

    public IntegralBean queryBasicInfo(String uid) {
        logger.info("开始查询" + uid + "头像、等级、当前积分值");
        BaseReq<String> req = new BaseReq(SysCodeConstant.INTEGRALCENTER);
        req.setData(uid);
        BaseResp<Acct_UserPojo> response = userInterface.integralQueryBasicInfo(req);
        IntegralBean bean = new IntegralBean();
        Acct_UserPojo acct_userPojo = response.getData();
        bean.setUserPhoto(acct_userPojo.getUserImg());
        bean.setCurrentPoint(String.valueOf(acct_userPojo.getUserpoint()));
        bean.setCurrentLevel(acct_userPojo.getGradeid());
//        IntegralBean bean = dao.queryBasicInfo(uid);
        String userPhoto = bean.getUserPhoto();
        if (userPhoto != null && userPhoto.startsWith("http://")) {
            userPhoto = userPhoto.substring(22);
            bean.setUserPhoto(userPhoto);
        }
        return bean;
    }

    @Transactional(rollbackFor = {Exception.class})
    public IntegralBean querySignInfo(String uid) throws Exception {
        logger.info("开始查询" + uid + "签到天数信息和签到状态");
        //查询签到表是否有签到记录
        IntegralBean resultBean = new IntegralBean();

        int flag = dao.hasSignRecord(uid);
        if (1 != flag) {
            logger.info("从未有签到操作，签到表没有[" + uid + "]用户");
            resultBean.setSignDays("0");
            resultBean.setStatus("0");
            logger.info("用户[" + uid + "]今日签到状态==[signDays--0,status--0]");
            return resultBean;
        }

        //检测签到是否连续
        IntegralBean conBean = dao.isContinuous(uid);
        if (conBean != null) {
            String differ = conBean.getDiffer();
            String isigned = conBean.getSigned();
            String status = conBean.getStatus();
            logger.info("查询[" + uid + "]是否连续签到基本信息==[differ--" + differ + ",isigned--" + isigned + ",status--" + status + "]");
            if ((Integer.valueOf(differ) > 1 || "7".equals(isigned)) && !"0".equals(differ)) {//昨日未签到或昨日已满七天，清空签到天数，设置签到状态为0
                //清空用户签到天数
                int i = dao.clearSignDays(uid);
                if (i == 1) {
                    resultBean.setSignDays("0");
                } else {
                    resultBean.setCode(Result.FAIL);
                    throw new Exception("非连续签到，清除签到信息失败");
                }
            }

            if (!"0".equals(differ) && "1".equals(status)) {//今日未签到
                int j = dao.clearSignStatus(uid);//今日未签到，清空昨日状态
                if (j != 1) {
                    resultBean.setCode(Result.FAIL);
                    throw new Exception("非连续签到，清除签到信息失败");
                }
            }

        } else {
            resultBean.setCode(Result.FAIL);
            return resultBean;
        }

        IntegralBean infoBean = dao.getSignInfo(uid);
        if (infoBean != null) {
            String days = infoBean.getSigned();
            String status = infoBean.getStatus();
            resultBean.setSignDays(days);
            resultBean.setStatus(status);
            logger.info("用户[" + uid + "]今日签到状态==[signDays--" + days + ",status--" + status + "]");
            return resultBean;
        }
        return resultBean;
    }

    public String queryDayPoints(String uid) {
        logger.info("查询" + uid + "每日已获取积分");
        return dao.queryDayPoints(uid);
    }

    public IntegralBean queryBindInfo(String uid) {
        int bindIdCard = 1;
        int bindBankCard = 1;
        IntegralBean resultBean = new IntegralBean();
        BaseReq<String> req = new BaseReq(SysCodeConstant.INTEGRALCENTER);
        req.setData(uid);
        BaseResp<UserPojo> resp = userInterface.integralQueryIdBankBinding(req);
        UserPojo pojo = resp.getData();
        String idCard = pojo.getIdcard();
        String bankCard = pojo.getBankCard();

        if (!StringUtil.isEmpty(idCard)) {
            bindIdCard = 0;
            resultBean.setBindIdCard(bindIdCard + "");//已绑身份证
        } else {
            resultBean.setBindIdCard(bindIdCard + "");//未绑身份证
        }
        if (!StringUtil.isEmpty(bankCard)) {
            bindBankCard = 0;
            resultBean.setBindBankCard(bindBankCard + "");//已绑银行卡
        } else {
            resultBean.setBindBankCard(bindBankCard + "");//未绑银行卡
        }
        logger.info("用户[" + uid + "]身份证银行卡绑定状态，[bindIdCard=" + bindIdCard + ",bindBankCard=" + bindBankCard + "]");

        if (0 == bindIdCard || 0 == bindBankCard) {
            String task = pojo.getTaskInit();
            logger.info("task:" + task);
            String binary = Integer.toBinaryString(Integer.valueOf(task));
            resultBean.setIsGetPoint(binary);
            resultBean.setTask(task);
            logger.info("用户[" + uid + "]积分中心积分领取状态，[task=" + task + ",isGetPoint=" + binary + "]");
        }
        return resultBean;
    }

    public boolean cannotSign(String uid) {
        BaseReq<String> req = new BaseReq(SysCodeConstant.INTEGRALCENTER);
        req.setData(uid);
        BaseResp<String> idaigou = userInterface.cannotSign(req);
        String total = idaigou.getData();
//        String idaigou = dao.cannotSign(uid);
        if (StringUtil.isEmpty(total)) {
            return true;
        }
        return Integer.valueOf(total) <= 0;
    }

    public boolean checkCurrentUser(String uid, String date) {
        logger.info("开始查询用户[" + uid + "]签到表中的状态");
        int i = dao.hasSignRecord(uid);
        if (1 == i) {//已存在用户
            logger.info("签到表存在用户[" + uid + "]");
            return true;
        } else {//用户有消费记录，未签到过，插入用户到签到表
            logger.info("用户[" + uid + "]有签到资格，签到表无该用户");
            boolean flag = insertUserToSign(uid, date);
            if (flag)
                return true;
        }
        return false;
    }

    public int clickToSign(String date, String uid, String hasSignDays) {
        IntegralParamBean paramBean = new IntegralParamBean();
        paramBean.setUid(uid);
        paramBean.setDate(date);
        paramBean.setHasSignDays(hasSignDays);
        return dao.clickToSign(paramBean);
    }

    public int insertPointCharge(UserBean bean, String type, int point, int biztype) {
        Map<String, Object> paramMap = new HashMap<>();
        IntegralParamBean paramBean = new IntegralParamBean();
        paramBean.setUid(bean.getUid());
        paramBean.setPoint(point);
        paramBean.setType(type);
        paramBean.setBiztype(biztype);
        paramBean.setMemBasicVal(bean.getMemBasicVal() + "");
        paramBean.setTotal((bean.getMemBasicVal() + point) + "");
        paramBean.setUserLevel(bean.getUserLevel());
//        int i = userInterface.insertPointCharge(paramBean);
        int i = dao.insertPointCharge(paramBean);
        return i;
    }

    public int updateUserPoint(UserBean bean, int points) {
        IntegralParamBean paramBean = new IntegralParamBean();
        paramBean.setUid(bean.getUid());
        paramBean.setPoint(points);

//        int i = userInterface.updateUserPoint(paramBean);
        int i = dao.updateUserPoint(paramBean);
        return i;
    }

    public int clickToGetPoints(int itask, String uid, String task, String bitand) {
        IntegralParamBean bean = new IntegralParamBean();
        bean.setItask(itask + "");
        bean.setUid(uid);
        bean.setTask(task);
        bean.setBitand(bitand);
        BaseReq<IntegralParamBean> req = new BaseReq(SysCodeConstant.INTEGRALCENTER);
        req.setData(bean);
        BaseResp<Integer> response = userInterface.clickToGetPoints(req);
        Integer in = response.getData();
        return in;
    }

    @Override
    public IntegralBean queryVipUserInfo(UserBean bean) {
        logger.info("开始查询用户[" + bean.getUid() + "]会员中心信息");
        BaseReq<String> req = new BaseReq(SysCodeConstant.INTEGRALCENTER);
        req.setData(bean.getUid());
        BaseResp<UserRecordPojo> resp = userInterface.queryVipUserInfo(req);
        IntegralBean integralBean = new IntegralBean();
        UserRecordPojo pojo = resp.getData();
        integralBean.setCurrentLevel(pojo.getGradeid());//等级
        integralBean.setLevelName(pojo.getLevelTitle());//等级称谓
        integralBean.setCurrentLevelExp(pojo.getLevelExper());
        integralBean.setCurrentExp(pojo.getExpir());//经验
        integralBean.setUserPhoto(pojo.getUserImg());//头像链接
//        IntegralBean integralBean = dao.queryVipUserInfo(bean.getUid());
//        integralBean.setCurrentLevelExp(integralBean.getNextLevelExp());
        if (null != integralBean) {
            String userPhoto = integralBean.getUserPhoto();
            if (userPhoto != null && userPhoto.startsWith("http://")) {
                userPhoto = userPhoto.substring(22);
                integralBean.setUserPhoto(userPhoto);
            }
            String cgradename = integralBean.getLevelName();//等级和称谓，eg：V0-初入彩圈
            if (!StringUtil.isEmpty(cgradename)) {
                String levelName = cgradename.substring(cgradename.indexOf("-") + 1, cgradename.length());
                integralBean.setLevelName(levelName);
            }
            String igradeid = integralBean.getCurrentLevel();
            if (Integer.valueOf(igradeid) < 10) {
                logger.info("开始查询用户[" + bean.getUid() + "]下一等级信息");
                IntegralBean integralBean1 = getNextLevel(igradeid);
                if (null != integralBean1) {
                    integralBean.setNextLevel(integralBean1.getNextLevel());
                    integralBean.setNextLevelExp(integralBean1.getNextLevelExp());
                } else {
                    integralBean.setNextLevel("-1");
                    integralBean.setNextLevelExp("-1");
                    logger.info("用户[" + bean.getUid() + "]下一等级查询失败");
                }
            }
            return integralBean;
        } else {
            integralBean.setCode(Result.FAIL);
            logger.info("用户[" + bean.getUid() + "]会员中心信息查询结果为空");
        }
        return integralBean;
    }

    public List<IntegralPageBean> experienceDetail(UserBean bean) {
        return dao.experienceDetail(bean.getUid());
    }

    public List<IntegralPageBean> pointsDetail(UserBean bean) {
        return dao.pointsDetail(bean.getUid());
    }

    private IntegralBean getNextLevel(String nextLevel) {
        int level = Integer.valueOf(nextLevel) + 1;
        BaseReq<String> req = new BaseReq(SysCodeConstant.INTEGRALCENTER);
        req.setData(nextLevel);
        BaseResp<String> resp = userInterface.queryLevelExper(req);
        String exper = resp.getData();
//        IntegralBean integralBean = dao.getNextLevel(level);
        IntegralBean integralBean = new IntegralBean();
        integralBean.setNextLevel(level + "");
        return integralBean;
    }


    public boolean insertUserToSign(String uid, String date) {
        logger.info("开始将用户[" + uid + "]插入签到表");
        int i = dao.insertUserToSign(uid, date, "0", "0");
        if (1 == i) {
            logger.info("将用户[" + uid + "]插入签到表成功");
            return true;
        } else {
            logger.info("将用户[" + uid + "]插入签到表失败");
            return false;
        }
    }
}

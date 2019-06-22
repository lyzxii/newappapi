package com.caiyi.lottery.tradesystem.tradecenter.service.impl;

import bean.UserBean;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caipiao.game.GameContains;
import com.caipiao.plugin.GamePlugin_50;
import com.caipiao.plugin.helper.CodeFormatException;
import com.caipiao.plugin.helper.GameCastMethodDef;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.helper.PluginUtil;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caipiao.split.GameSplit;
import com.caiyi.lottery.tradesystem.BaseBean;
import com.caiyi.lottery.tradesystem.base.BaseReq;
import com.caiyi.lottery.tradesystem.base.BaseResp;
import com.caiyi.lottery.tradesystem.bean.CacheBean;
import com.caiyi.lottery.tradesystem.constants.BaseConstant;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.constants.SysCodeConstant;
import com.caiyi.lottery.tradesystem.redis.client.RedisInterface;
import com.caiyi.lottery.tradesystem.redis.util.CacheUtil;
import com.caiyi.lottery.tradesystem.returncode.BusiCode;
import com.caiyi.lottery.tradesystem.returncode.ErrorCode;
import com.caiyi.lottery.tradesystem.tradecenter.dao.*;
import com.caiyi.lottery.tradesystem.tradecenter.dao.CancleZhMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.CpgameMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.PeriodMapper;
import com.caiyi.lottery.tradesystem.tradecenter.dao.ProjXzjzMapper;
import com.caiyi.lottery.tradesystem.tradecenter.util.FileCastCodeUtil;
import com.caiyi.lottery.tradesystem.util.matrix.MatrixUtils;
import com.caiyi.lottery.tradesystem.tradecenter.util.trade.LimitCodeUtil;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBaseInterface;
import com.caiyi.lottery.tradesystem.usercenter.client.UserBasicInfoInterface;
import com.caiyi.lottery.tradesystem.util.*;
import com.caiyi.lottery.tradesystem.util.code.CountCodeUtil;
import com.caiyi.lottery.tradesystem.util.proj.ProjUtils;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import dto.ZhRecordResults;
import trade.bean.TradeErrCode;
import trade.pojo.ZhRecordPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caiyi.lottery.tradesystem.tradecenter.service.BaseService;
import com.caiyi.lottery.tradesystem.tradecenter.service.CastService;
import com.caiyi.lottery.tradesystem.tradecenter.service.TradeService;

import lombok.extern.slf4j.Slf4j;
import trade.bean.TradeBean;
import trade.dto.CastDto;

import trade.constants.TradeConstants;
import trade.pojo.PeriodPojo;
import trade.util.DateConvertUtil;
import trade.util.FileUtil;
import trade.util.FilterUtil;
import trade.util.TradeUtil;
import util.UserErrCode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.caiyi.lottery.tradesystem.constants.FileConstant.BASE_PATH;
import static com.caiyi.lottery.tradesystem.tradecenter.service.impl.CodeServiceImpl.checkCodeMuli;
import static com.caiyi.lottery.tradesystem.tradecenter.service.impl.CodeServiceImpl.checkCodeMuli11x5;

@Slf4j
@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    BaseService baseService;

    @Autowired
    CastService castService;
    @Autowired
    RedisInterface redisInterface;

    @Autowired
    UserBaseInterface userBaseInterface;

    @Autowired
    CancleZhMapper cancleZhMapper;

    @Autowired
    UserBasicInfoInterface userBasicInfoInterface;

    @Autowired
    PeriodMapper periodMapper;

    @Autowired
    CpgameMapper cpgameMapper;

    @Autowired
    ProjXzjzMapper projXzjzMapper;

    @Autowired
    ZhItemsMapper zhItemsMapper;

    private HashMap<String, GamePluginAdapter> mapsPlugin = new HashMap<String, GamePluginAdapter>();

    @Override
    public CastDto pcast(TradeBean bean) {
        if (!baseService.checkBanActivity(bean)) {
            return null;
        }
        if (!baseService.checkBeforeBuy(bean)) {
            return null;
        }
        CastDto castDto = castService.proj_cast_app(bean);
        return castDto;
    }

	@Override
	public CastDto jcast(TradeBean bean) {
        if (!baseService.checkBanActivity(bean)) {
            return null;
        }
        if (!baseService.checkBeforeBuy(bean)) {
            return null;
        }
        if(bean.getMoney() > 1000000){
        	bean.setBusiErrCode(Integer.parseInt(BusiCode.TRADE_MONEY_OUT_OF_LIMIT));
        	bean.setBusiErrDesc("方案金额必须小于1,000,000元");
        	log.info("方案投注金额超过限制,gid:"+bean.getGid()+" 用户名:"+bean.getUid()+" codes:"+bean.getCodes()+" money:"+bean.getMoney());
        	return null;
        }
        CastDto castDto = castService.jproj_cast_app(bean);
		return castDto;
	}

    @Override
    public void hmzhremind(TradeBean bean) {
        //检测是否禁止充值支付,true为禁止,false为不禁止
        try {
            if (checkBanActivity(bean)) return;
            opencache(bean);
            hmzhRemind(bean);
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("检测是否禁止充值支付出错");
            log.error("检测是否禁止充值支付出错,玩法=" + bean.getGid() + ",用户名=" + bean.getUid() + ",投注类型标志=" + bean.getActivityflag(),e);
        }
    }

    @Override
    public String zcancel(TradeBean bean) {
        String resp = "取消追号前检测输入参数错误";
        if (!checkParam4CancelZhuihao(bean)) return resp;
        return cancel_zhuihao(bean);
    }

    @Override
    public void zcastnew(TradeBean bean) {
        if (StringUtil.isEmpty(bean.getUid())) {
            bean.setWhitelistGrade(0);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("用户名为空");
            return;
        }
        if (!checkZhQs(bean) || checkBanActivity(bean) || "0".equals(checkParam4Zhuihao(bean))){
            return;
        }
        resetPid(bean);//追号重设期次排版
        if (checkBeforeZhuihao(bean)) {
            return;
        }
        cast_zhuihao_app(bean);
    }

    private void cast_zhuihao_app(TradeBean bean) {
        // //TODO停售
        if (!"56".equals(bean.getGid()) && !"55".equals(bean.getGid()) && !"59".equals(bean.getGid())
                && !"10".equals(bean.getGid()) && !"04".equals(bean.getGid()) && !"09".equals(bean.getGid())
                && !"01".equals(bean.getGid()) && !"03".equals(bean.getGid()) && !"07".equals(bean.getGid())
                && !"50".equals(bean.getGid()) && !"51".equals(bean.getGid()) && !"52".equals(bean.getGid())
                && !"53".equals(bean.getGid())) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_CANNOT_ZHUIHAO));
            bean.setBusiErrDesc("系统升级中，暂时不能追号！");
        }
        checkGame(bean);
        if (bean.getBusiErrCode() != 0) {
            return;
        }
        try {
            TradeUtil.check(TradeUtil.CAST_ZH, bean);
            bean.setMuli(1);
            String[] newpid = bean.getPid().split(",");
            log.info("newpid==" + Arrays.toString(newpid) + ",gid==" + bean.getGid() + ",uid==" + bean.getUid());
            PeriodPojo periodPojo = periodMapper.queryEndTime(bean.getGid(), newpid[0]);
            if (periodPojo == null) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("查询endtime失败");
                log.info("查询endtime失败,newpid==" + Arrays.toString(newpid) + ",gid==" + bean.getGid() + ",uid==" + bean.getUid());
                return;
            }
            String endtime = periodPojo.getEndtime();
            String fendtime = periodPojo.getFendtime();
            if (bean.getCodes().length() >= 3900 || bean.getFflag() == 1) {
                endtime = fendtime;
            }
            bean.setEndTime(endtime);

            if (GameContains.isKP(bean.getGid())) {
                CountCodeUtil.checkKPChunJieEndTime(bean, bean.getEndTime());
            }
            String tzcodes = "";
            if (1 == bean.getXzflag()) // 如果是旋转矩阵投注方式,则先拆分旋转后的号码
            {
                tzcodes = bean.getCodes(); // 保存旋转之前的投注号码
                // bean.setSource(9);
                bean.setExtendtype(9);
                bean.setCodes(MatrixUtils.getMatrixCodesStr(bean.getGid(), tzcodes));
            }
            int money = countCodesMoney(bean);
            int tmoney = 0;
            String mulitys = bean.getMulitys();
            int[] mm = StringUtil.SplitterInt(mulitys, ",");
            bean.setPnum(mm.length);
            for (int i = 0; i < mm.length; i++) {
                tmoney += (mm[i] * money);
            }
            log.info("uid==" + bean.getUid() + ",bean.getMoney==" + bean.getMoney() + "money==" + money + ",tmoney==" + tmoney + ",mulitys==" + mulitys);
            if (money <= 0 || bean.getMoney() != tmoney) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_CANNOT_ZHUIHAO));
                bean.setBusiErrDesc("追号金额不正确 实际金额（" + tmoney + ")");
            }
            if (bean.getBusiErrCode() == 0) {
                if (bean.getCodes().length() >= 3900) {
                    String pids = bean.getPid();
                    bean.setPid("zhuihao");
                    this.saveZhuiHaoCastCodeToFile(bean);// 生成文件
                    bean.setPid(pids);
                }
                bean.setMoney(money);

                cpgameMapper.t_cast_zh(bean);
                if (0 != bean.getBusiErrCode()) {
                    bean.setBusiErrDesc(UserErrCode.getErrDesc(bean.getBusiErrCode()));
                } else {
                    if (1 == bean.getXzflag()) // 如果是旋转矩阵投注,则将旋转投注详细保存到TB_PROJ_XZJZ表中
                    {
                        // 则将旋转投注详细保存到TB_PROJ_XZJZ表中
                        projXzjzMapper.insertXzjzRecord(bean.getZid(), bean.getGid(), tzcodes, (tzcodes.split(";").length == 1) ? bean.getCodes() : "");
                    }
                    bean.setPayorderid(bean.getZid());
                    bean.setEndTime(bean.getEndTime());
                    bean.setBusiXml(bean.getZid());
                }

                if (bean.getBusiErrCode() != 0) {
                    if (bean.getBusiErrCode() == 1001) {
                        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_BALANCE_IS_NOT_ENOUGH));// 余额不足
                    } else if (bean.getBusiErrDesc().contains("期次")) {
                        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_QICI_EXCEPTION));// 期次异常
                    } else {
                        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_CANNOT_ZHUIHAO));// 其它异常
                    }
                    bean.setBusiErrDesc(bean.getBusiErrDesc().replace("\"", ""));
                }
            }
        } catch (CodeFormatException e) {
            log.info("手机端投注号码格式错误 : source=" + bean.getSource() + "	codes=" + bean.getCodes() + "	gid="
                    + bean.getGid());
            log.info("手机端投注号码格式错误 :", e);
            log.info("手机端投注号码格式错误,返回提示 :" + e.getErrDesc());
            bean.setBusiErrCode(4);
            bean.setBusiErrDesc(e.getErrDesc());
        } catch (RuntimeException e) {
            bean.setBusiErrCode(4);
            bean.setBusiErrDesc(e.getMessage());
        } catch (Exception e) {
            bean.setBusiErrCode(4);
            bean.setBusiErrDesc(TradeErrCode.getErrDesc(bean.getBusiErrCode()));
            log.error("追号异常,用户:{}",bean.getUid(),e);
        }
    }

    /**
     * 检查号码格式
     *
     * @param bean
     * @return
     */
    private int countCodesMoney(TradeBean bean) throws Exception{

        int money = -1;
        String gid = bean.getGid();
        GamePluginAdapter plugin = mapsPlugin.get(gid);
        if (plugin == null) {
            try {
                plugin = (GamePluginAdapter) Thread.currentThread().getContextClassLoader()
                        .loadClass("com.caipiao.plugin.GamePlugin_" + gid).newInstance();
                mapsPlugin.put(gid, plugin);
            } catch (Exception e) {
                log.error("加载游戏插件失败 game=" + gid, e);
            }
        }

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
                            codes = FileCastCodeUtil.getCodesFromPathSZ(bean.getGid(), "/", BASE_PATH, codes,
                                    bean.getPlay() + "",log);
                        } else {
                            codes = FileCastCodeUtil.getCodesFromFile(bean.getGid(), pids, BASE_PATH, codes,
                                    bean.getPlay() + "", log);
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
                    log.info("票张数：" + splcod.length);
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
                        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_KP_OVERTOOP_ITEMS_LIMIT));
                        bean.setBusiErrDesc("快频彩种方案条数不能够超过500条!");
                    }
                }
                int total = 0;
                for (int i = 0; i < cc.length; i++) {
                    if (!TradeConstants.big.containsKey(gid)) {
                        if (cc[i].getCastMoney() > 20000) {
                            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_KP_OVERTOOP_SINGLE_DAY_MONEY_LIMIT));
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
                log.error("号码格式错误 game=" + gid + " source=" + bean.getSource(), e);
                bean.setBusiErrCode(e.getErrCode());
                bean.setBusiErrDesc(e.getErrDesc());
                // } catch (Exception e) {
                // bean.setBusiErrCode(1);
                // bean.setBusiErrDesc(e.getMessage());
            }
            if (isDT) {
                checkEndTime(gid, bean);
            }
        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_GAME_NOT_SUPPORT));
            bean.setBusiErrDesc("该彩种暂不支持(" + gid + ")");
        }
        return money;

    }

    public void checkEndTime(String gid, TradeBean bean) {
        if (GameContains.isR9(gid)) {
            PeriodPojo periodPojo = periodMapper.queryEndTime(bean.getGid(), bean.getPid());
            String cfendtime = periodPojo.getFendtime();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            try {
                date = format.parse(cfendtime);
                if (System.currentTimeMillis() > date.getTime()) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    bean.setBusiErrDesc("任选九胆拖方案投注按单式投注截止时间截止,下次请赶早");
                }
            } catch (ParseException e) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("转换异常");
                log.error("CountCodeUtil-->checkEndTime:日期转换错误,uid=" + bean.getUid(), e);
            }
        }
    }

    private void saveZhuiHaoCastCodeToFile(TradeBean bean) throws Exception {
        String codes = bean.getCodes();
        String gid = bean.getGid();
        String pid = bean.getPid();
        String uid = bean.getUid();

        long time = System.currentTimeMillis();
        String name = uid + gid + time + pid;
        String filename = "";

        filename = MD5Util.compute(name) + ".txt";

        File dir = new File(BASE_PATH + File.separator + gid + File.separator + pid);
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

    private void checkGame(TradeBean bean) {

        if ("50".equals(bean.getGid()) && bean.getCodes().indexOf("$") > 0) {
            log.info("大乐透胆拖投注时检测投注codes格式是否正确,codes=" + bean.getCodes());
            // 35$01,02,03,04,05,06,07,14|05$01,02,03,04,06:1:5
            String[] codarray = bean.getCodes().split(";");
            for (String cod : codarray) {
                int pipe = cod.indexOf("|");
                String frontendCodes = cod.substring(0, pipe);
                String backendCodes = cod.substring(pipe + 1);
                if (backendCodes.indexOf("$") > 0 && frontendCodes.indexOf("$") < 0) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_CANNOT_ZHUIHAO));
                    bean.setBusiErrDesc("大乐透胆拖投注请设置前区胆码！");
                    log.info("大乐透胆拖投注无前区胆码,codes=" + bean.getCodes());
                }
            }

        }
        if ("10".equals(bean.getGid())) { // 江西快三 格式转换,版本限制
            String appversion = bean.getAppversion();
            if (!StringUtil.isEmpty(appversion)) {
                appversion = appversion.replaceAll("\\.", "");
                int aps = Integer.parseInt(appversion);
                if (aps < 441) {
                    bean.setBusiErrCode(5);
                    bean.setBusiErrDesc("请升级版本后再购买！");
                    log.info("版本号,appversion=" + bean.getAppversion());
                    return;
                } else {
                    if (aps <= 13010 && aps > 10000) {
                        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_LOW_VERSION_CLIENT));
                        bean.setBusiErrDesc("请升级版本后再购买！");
                        log.info("版本号,appversion=" + bean.getAppversion());
                        return;
                    }
                }
            }

            String jxk3code = bean.getCodes();
            String[] jxk3arr = jxk3code.split(";");
            StringBuilder sb = new StringBuilder();
            for (String cod : jxk3arr) {
                String c7code = "";
                if (cod.endsWith(":7:1")) {// 二同号单选
                    String[] c7arr = cod.split(":");
                    if (c7arr.length != 3) {
                        return;
                    }
                    String c7 = c7arr[0];
                    String[] c7num = c7.split("\\|");
                    if (c7num.length != 2) {
                        return;
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
            // sb.toString().substring(0, sb.length()-1);
            // System.out.println(sb.toString().substring(0, sb.length()-1));
            bean.setCodes(sb.toString().substring(0, sb.length() - 1));
        }

    }

    /**
     * 追号检测,只有双色球和大乐透可以追号,且只能追5期
     */
    private boolean checkBeforeZhuihao(TradeBean bean) {
        log.info("追号检测,nickid=" + bean.getUid() + ",gid=" + bean.getGid());
        checkBeforeBuy(bean);
        int maxLen = maxLen(bean.getGid());
        if (bean.getBusiErrCode() != 0) return true;
        if ("56".equals(bean.getGid()) || "55".equals(bean.getGid()) || "59".equals(bean.getGid())
                || "09".equals(bean.getGid()) || "04".equals(bean.getGid()) || "10".equals(bean.getGid())) {
            String[] pidArr = bean.getPid().split(",", -1);
            if (bean.getPid().indexOf("161208") > 0 && "10".equals(bean.getGid())) {
                // logger.info("追号期数大于50期,nickid=" + bean.getUid() + ",gid=" +
                // bean.getGid() + ",pid=" + bean.getPid());
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("该彩种暂不支持跨天追号");
                return true;
            }
            if(maxLen <=0){
                maxLen = 50;
            }
            return zhResult(bean, pidArr, maxLen);

        } else if ("01".equals(bean.getGid()) || "03".equals(bean.getGid()) || "07".equals(bean.getGid())
                || "50".equals(bean.getGid()) || "51".equals(bean.getGid()) || "52".equals(bean.getGid())
                || "53".equals(bean.getGid())) {
            String[] pidArr = bean.getPid().split(",", -1);
            if(maxLen <=0){
                maxLen = 10;
            }
            return zhResult(bean, pidArr, maxLen);
        } else {
            log.info("当前彩种不能追号,nickid=" + bean.getUid() + ",gid=" + bean.getGid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("系统升级中，暂时不能追号");
            return true;
        }
    }

    public boolean zhResult(TradeBean bean,String[] pidArr,int maxLen){
        if (pidArr.length > maxLen) {
            log.info("追号期数大于" + maxLen + "期,nickid=" + bean.getUid() + ",gid=" + bean.getGid() + ",pid=" + bean.getPid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("暂时只能追" + maxLen + "期哦");
            return true;
        };
        return false;
    };


    private static int maxLen(String gid) {
        int max = -1;
        String path = "/opt/export/www/cms/news/ad/lottery_controller_" + gid + ".xml";
//        String path = "D:\\opt\\export\\www\\cms\\news\\ad\\lottery_controller_" + gid + ".xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(path));
        JXmlWrapper qs = xml.getXmlNode("qs");
        if(null != qs){
            max = qs.getIntValue("@max");
        }
        return max;
    }

    @Override
    public int checkBeforeBuy(TradeBean bean) {
        log.info("投注前检测开售情况,投注来源和白名单等级,nickid=" + bean.getUid() + ",source=" + bean.getSource() + ",gid=" + bean.getGid() + ",zflag=" + bean.getZflag());
        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_PAUSE_SALE));
        bean.setBusiErrDesc("系统升级中~暂停销售~");
        int c = preCheckParam(bean);
        if(0 != c) return c;
        int grade = 0;
        queryUserWhitelistGrade(bean);
        int queryResult = bean.getBusiErrCode();
        log.info("投注前检测白名单等级,nickid=" + bean.getUid() + ",grade=" + bean.getWhitelistGrade() + ",errCode="
                + bean.getBusiErrCode());
        bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_PAUSE_SALE));
        bean.setBusiErrDesc("系统升级中~暂停销售~");
        if (queryResult == 0) {
            grade = bean.getWhitelistGrade();
        } else {
            return 1;
        }
        if (grade == 100) {
            // 白名单状态为100的账户不受任何限制,可直接投注
            bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
            bean.setBusiErrDesc("可以投注");
            return 1;
        }
        JXmlWrapper tsxx = JXmlWrapper.parse(new File("/opt/export/data/info/config/sysexp/gameconfig.xml"));
        List<JXmlWrapper> rows = tsxx.getXmlNodeList("row");
        int isale = -1;
        String gid = null;
        for (JXmlWrapper row : rows) {
            gid = row.getStringValue("@gid");
            if (bean.getGid().equals(gid)) {
                isale = row.getIntValue("@isale");
                break;
            }
        }
        if (isale == 0) {
            // 彩种完全停售,不能投注
            log.info("彩种完全停售不能购彩,nickid=" + bean.getUid() + ",gid=" + bean.getGid());
        } else if (isale == 1) {
            // 彩种完全开售,可以投注
            bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
            bean.setBusiErrDesc("可以投注");
        } else if (isale == 2) {
            // 彩种只对白名单用户开售,进一步检测白名单等级,投注来源和投注客户端版本
            // TODO 主站,触屏和WP用户停售
            if (bean.getSource() < 1000 || bean.getSource() >= 5000 || TradeConstants.noBuySource.containsKey(bean.getSource())) {
                log.info("主站,触屏用户停售不能购彩,nickid=" + bean.getUid() + ",source=" + bean.getSource());
            } else {
                boolean isNew = false;
                // 可投注的最低客户端版本号还需要重新确认
                if (UserSourceMapUtil.isAndriodLotteryUser(bean)) {
                    isNew = isNewApp(bean.getAppversion(), "o2o9188buy", "android");
                } else if (UserSourceMapUtil.isWPUser(bean)) {
                    isNew = isNewApp(bean.getAppversion(), "o2o9188buy", "wp");
                } else if (UserSourceMapUtil.isIOSLotteryUser(bean)) {
                    isNew = isNewApp(bean.getAppversion(), "o2o9188buy", "ios");
                } else if (UserSourceMapUtil.isTouchUser(bean)) {
                    isNew = isNewApp(bean.getAppversion(), "o2o9188buy", "touch");
                    isNew = true;
                } else {
                    log.info("非法source值不能购彩,nickid=" + bean.getUid() + ",source=" + bean.getSource());
                    return 1;
                }
                // 用户客户端版本小于最低可投注版本时不能投注
                if (!isNew) {
                    log.info("用户客户端版本小于最低可投注版本不能购彩,nickid=" + bean.getUid() + ",appversion=" + bean.getAppversion());
                    return 1;
                }
                if (grade == 2) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("可以投注");
                } else {
                    // bean.setBusiXml("");
                    log.info("白名单等级不足不能购彩,nickid=" + bean.getUid() + ",grade=" + grade);
                }
            }
        } else {
            // 彩种开售状态未知,不能投注
            log.info("彩种开售状态未知,nickid=" + bean.getUid() + ",isale=" + isale);
        }
        return 1;
    }

    public static void main(String[] args) {

        String gid = "70";
        maxLen(gid);
        String path = "D:\\opt\\export\\www\\cms\\news\\ad\\lottery_controller_"+gid+".xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(path));
        JXmlWrapper qs = xml.getXmlNode("qs");
        int max = qs.getIntValue("@max");
        System.out.println("max:"+max);
//        return qs.getIntValue("@max");



        TradeServiceImpl tradeService = new TradeServiceImpl();




        TradeBean bean = new TradeBean();
        bean.setGid("55");
        bean.setMoney(30);
        bean.setMulitys("1");
        bean.setUid("gjj168");
        bean.setZflag(1);
        bean.setMuli(1);
        bean.setFflag(0);
        bean.setCodes("04,06,08:03:01;01,08,10:03:01;02,06,08:03:01");
        //tradeService.countCodesMoney(bean);

        ////int money = tradeService.countCodesMoney(bean);

        int tmoney = 0;
        String mulitys = bean.getMulitys();
        int[] mm = StringUtil.SplitterInt(mulitys, ",");
        bean.setPnum(mm.length);
        for (int i = 0; i < mm.length; i++) {
            //tmoney += (mm[i] * money);
        }
        System.out.println(tmoney);






        String indata = "&appScheme=no&cupacketid=null&redpacket_money=0&money=0&totalMoney=&cType=&startTime=1517103222391&func=null&extendtype=0&source=0&logintype=0&appversion=&mtype=0&accesstoken=&appid=&session1=&session2=&bdMoney=0&smoney=&zs=0&bs=1";
        String[] ss = FilterUtil.split(indata, "&");
        System.out.println(Arrays.toString(ss));
    }

    @Override
    public Map<String, String> decodeJjyhBetInfo(TradeBean bean) {
        Map<String, String> maps = new HashMap<String, String>();
        String incheckor = bean.getCheckor();
        String inmessage = bean.getMessage();
        log.info("篮彩奖金优化web支付decodeJjyhBetInfo,用户名=" + bean.getUid() + ",玩法=" + bean.getGid() + ",incheckor=" + incheckor + ",inmessage=" + inmessage);
        try {
            String newCheckor = MD5Util.compute(inmessage + TradeConstants.MD5KEY).toUpperCase();
            if (newCheckor.equalsIgnoreCase(incheckor)) {
                byte[] src1 = GeneralBase64Utils.decode(inmessage); // 数据解析
                byte[] zsrc1 = FilterUtil.decompressBytes(src1);
                String indata = new String(zsrc1).trim();
                String[] ss = FilterUtil.split(indata, "&");
                int j = 0;
                int length = 0;
                String key = "";
                String value = "";
                for (int i = 0; i < ss.length; i++) {
                    j = ss[i].indexOf("=");
                    length = ss[i].length();
                    key = ss[i].substring(0, j);
                    value = ss[i].substring(j + 1, length);
                    maps.put(key, value);
                }
                maps.put("gid_name", GameContains.names.get(maps.get("gid")));
                int logintype = Integer.parseInt(maps.get("logintype"));
                String accesstoken = maps.get("accesstoken");
                String appid = maps.get("appid");
                String appversion = maps.get("appversion");
                String mtype = maps.get("mtype");
                String source = maps.get("source");
                bean.setLogintype(logintype); //检测登录时使用
                bean.setAccesstoken(accesstoken);
                bean.setAppid(appid);
                log.info("mtype=="+mtype);
                log.info("source="+source);
                bean.setAppversion(appversion);
                bean.setMtype(Integer.parseInt(mtype));
                bean.setSource(Integer.parseInt(source));
                log.info("decodeBetInfo  token登录支付-------logintype=" + logintype + " accesstoken=" + accesstoken + " appid=" + appid);
            } else {
                log.info("普通投注web支付decodeBetInfo,校验信息不一致,用户名=" + bean.getUid() + ",incheckor=" + incheckor + ",inmessage=" + inmessage);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("支付失败");
            log.info("普通投注web支付decodeBetInfo出现异常", e);
        }
        return maps;
    }

    @Override
    public Map<String, String> decodeBetInfo(TradeBean bean) {
        Map<String, String> maps = new HashMap<String, String>();
        String incheckor = bean.getCheckor();
        String inmessage = bean.getMessage();
        log.info("普通投注web支付decodeBetInfo,用户名=" + bean.getUid() + ",玩法=" + bean.getGid() + ",incheckor=" + incheckor + ",inmessage=" + inmessage);
        try {
            String newCheckor = MD5Util.compute(inmessage + TradeConstants.MD5KEY).toUpperCase();
            if (newCheckor.equalsIgnoreCase(incheckor)) {
                byte[] src1 = GeneralBase64Utils.decode(inmessage); // 数据解析
                byte[] zsrc1 = FilterUtil.decompressBytes(src1);
                String indata = new String(zsrc1).trim();
                String[] ss = FilterUtil.split(indata, "&");
                int j = 0;
                int length = 0;
                String key = "";
                String value = "";
                for (int i = 0; i < ss.length; i++) {
                    j = ss[i].indexOf("=");
                    length = ss[i].length();
                    key = ss[i].substring(0, j);
                    value = ss[i].substring(j + 1, length);
                    maps.put(key, value);
                }
                maps.put("gid_name", GameContains.names.get(maps.get("gid")));
                int logintype = Integer.parseInt(maps.get("logintype"));
                String accesstoken = maps.get("accesstoken");
                String appid = maps.get("appid");
                String appversion = maps.get("appversion");
                String mtype = maps.get("mtype");
                String source = maps.get("source");
                bean.setLogintype(logintype); //检测登录时使用
                bean.setAccesstoken(accesstoken);
                bean.setAppid(appid);
                log.info("mtype=="+mtype);
                log.info("source="+source);
                bean.setAppversion(appversion);
                bean.setMtype(Integer.parseInt(mtype));
                bean.setSource(Integer.parseInt(source));
                log.info("decodeBetInfo  token登录支付-------logintype=" + logintype + " accesstoken=" + accesstoken + " appid=" + appid);
            } else {
                log.info("普通投注web支付decodeBetInfo,校验信息不一致,用户名=" + bean.getUid() + ",incheckor=" + incheckor + ",inmessage=" + inmessage);
            }
        } catch (Exception e) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("支付失败");
            log.info("普通投注web支付decodeBetInfo出现异常", e);
        }
        return maps;
    }

    private void queryUserWhitelistGrade(TradeBean bean) {
        log.info("查询用户白名单等级,nickid=" + bean.getUid());
        bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
        bean.setBusiErrDesc("查询成功");
        BaseReq<BaseBean> baseReq = new BaseReq<>(SysCodeConstant.TRADECENTER);
        BaseBean baseBean = new BaseBean();
        baseBean.setUid(bean.getUid());
        baseReq.setData(baseBean);
        BaseResp<String> resp = userBasicInfoInterface.queryUserWhiteGrade(baseReq);
        if(resp == null){
            log.info("查新白名单失败，resp==" + resp +",uid=="+bean.getUid());
            return;
        }
        int grade = Integer.valueOf(resp.getData());
        if (1 == grade || 2 == grade || 3 == grade || 4 == grade || 5 == grade) {
            grade = (1 == grade) ? 1 : 2;
        } else if (grade != 100) {
            grade = 0;
        }
        log.info("白名单等级,nickid=" + bean.getUid() + ",grade=" + grade);
        bean.setWhitelistGrade(grade);
    }

    private int preCheckParam(TradeBean bean) {
        if(bean.getPid().indexOf("161208")>0 && "10".equals(bean.getGid())){
//			logger.info("追号期数大于50期,nickid=" + bean.getUid() + ",gid=" + bean.getGid() + ",pid=" + bean.getPid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("该彩种暂停销售所选期次");
        }
        // 不能发起合买
        if (bean.getType() == 1) {
            bean.setBusiErrDesc("系统升级中，暂时无法发起合买");
            log.info("不能发起合买,nickid=" + bean.getUid() + ",source=" + bean.getSource() + ",gid=" + bean.getGid());
            return 1;
        }
        if (bean.getUpay() == 1) {
            log.info("不能保存订单,nickid=" + bean.getUid() + ",source=" + bean.getSource() + ",gid=" + bean.getGid());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_PAUSE_SALE));
            bean.setBusiErrDesc("系统升级中~保存订单暂停使用~");
            return 1;
        }
        if ((bean.getSource() == 1400 || bean.getSource() == 1401)
                && isNewApp(bean.getAppversion(), "android9188buy", "android")) {
            log.info("用户客户端版本小于最低可投注版本不能购彩,nickid=" + bean.getUid() + ",appversion=" + bean.getAppversion()
                    + ",source=" + bean.getSource());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_PAUSE_SALE));
            bean.setBusiErrDesc("系统升级中~暂停销售~");
            return 1;
        }
        if ((bean.getSource() == 1436 || bean.getSource() == 1437)
                && isNewApp(bean.getAppversion(), "android1436buy", "android")) {
            log.info("用户客户端版本小于最低可投注版本不能购彩,nickid=" + bean.getUid() + ",appversion=" + bean.getAppversion()
                    + ",source=" + bean.getSource());
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_PAUSE_SALE));
            bean.setBusiErrDesc("系统升级中~暂停销售~");
            return 1;
        }
        return 0;
    }

    public boolean isNewApp(String appversion, String controlId, String type) {
        JXmlWrapper node = FileUtil.getRow("/opt/export/www/cms/news/ad/57.xml", controlId);
        if (node == null || StringUtil.isEmpty(appversion)) {
            return false;
        }
        String baseVersion = node.getStringValue("@" + type);
        boolean isNew = false;
        if ("android".equals(type) && baseVersion.indexOf("~") > 1) {
            String[] newVersionArr = baseVersion.split(",");
            for (String newVersion : newVersionArr) {
                int index = newVersion.indexOf("~");
                if (index < 1) {
                    continue;
                }
                int min = Integer.parseInt(newVersion.substring(0, index));
                int max = Integer.parseInt(newVersion.substring(index + 1));
                if (Integer.parseInt(appversion) >= min && Integer.parseInt(appversion) <= max) {
                    isNew = true;
                    break;
                }
            }
        } else {
            if (baseVersion.indexOf(".") > 0) {
                isNew = appversion.compareTo(baseVersion) >= 0;
            } else {
                if (appversion.contains(".")) {
                    appversion = appversion.replace(".", "");
                }
                isNew = Integer.parseInt(appversion) - Integer.parseInt(baseVersion) >= 0;
            }
        }
        return isNew;
    }

    //追号重设期次排版
    public int resetPid(TradeBean bean){
        String pid=bean.getPid();
        if ((pid.indexOf(",")<0) || (bean.getIzhflag() == 1)) {
            return 1;
        }
        String [] parr=pid.split(",",-1);
        String newPid=parr[0];
        String xmlpath="/opt/export/data/phot/"+bean.getGid()+"/s.xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));
        int count = xml.countXmlNodes("row");
        int j=0;
        for(int i = 0; i < count; i++){
            String p=xml.getStringValue("row["+i+"].@pid");
            if (newPid.equals(p)) {
                j=i;
                break;
            }
        }

        int m=parr.length+j;
        for(j=j+1; j < m; j++){
            String p=xml.getStringValue("row["+j+"].@pid");
            newPid+=","+p;
        }
        bean.setPid(newPid);
        return 1;
    }

    /**
     * 取消追号前检测输入参数是否正确
     */
    public String checkParam4Zhuihao(TradeBean bean) {
        log.info("追号前检测输入参数是否正确,nickid=" + bean.getUid());
        int ret = isEmptyNickidAndPwd(bean);
        if (ret == 0 || bean.getBusiErrCode() != 0) {
            ret = 0;
        }
        if (StringUtil.isEmpty(bean.getGid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("彩种指定不明确");
            ret = 0;
        }
        log.info("追号金额，money=="+bean.getMoney() +",code = " +bean.getBusiErrCode());
        if (bean.getMoney() <= 0) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("追号金额不正确");
            ret = 0;
        }
        if (StringUtil.isEmpty(bean.getPid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("追号期号不能为空");
            ret = 0;
        }
        if (ret == 0) {
            log.info("追号操作参数错误,nickid=" + bean.getUid() + ",errDesc=" + bean.getBusiErrDesc());
        }
        return String.valueOf(ret);
    }

    /**
     * 追号检测
     */
    public boolean checkZhQs(TradeBean bean) {
        // 期次校验
        try {
            String gid = bean.getGid();
            String pid = bean.getPid();
            if (StringUtil.isEmpty(gid) || StringUtil.isEmpty(pid)) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_EMPTY_GID_AND_PID));
                bean.setBusiErrDesc("期次或彩种编号不能为空");
                return false;
            }
            if ((pid.indexOf(",") >= 0)) {
                String[] parr = pid.split(",");
                pid = parr[0];
            }

            String szcPhaseInfo = getSzcPhaseInfo(gid);
            if (szcPhaseInfo.equals(pid)) {
                return true;
            } else {
                if (StringUtil.isEmpty(szcPhaseInfo)) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_NOT_EXITS_PID));
                    bean.setBusiErrDesc("未找到符合条件的彩种信息");
                    return false;
                }
                bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_CURRENT_PID_DISABLE));
                bean.setBusiErrDesc(bean.getPid() + "期已截止,是否从当前" + szcPhaseInfo + "期开始追号?");
                bean.setBusiXml(szcPhaseInfo);
                return false;
            }
        } catch (ParseException e) {
            log.error("智能追号期次检测出错,gid:" + bean.getGid() + " pid:" + bean.getPid(),e);
            bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_AUTO_CHECK_PID));
            bean.setBusiErrDesc("追号期次检测出错,pid:" + bean.getPid() + " gid:" + bean.getGid());
            return false;
        }
    }

    public static String parseValue(Object value) {
        String str = null;
        if (value instanceof Double) {
            double data = Double.parseDouble(String.valueOf(value));
            if (data == 0) {
                str = "0";
            } else {
                DecimalFormat df = new DecimalFormat("###########0.00");
                str = df.format(data);
                if (str.endsWith(".00")) {
                    str = str.substring(0, str.length() - 3);
                }
            }
        } else if (value instanceof Date) {
            str = DateConvertUtil.format(Date.class.cast(value), "yyyy-MM-dd HH:mm:ss");
        } else {
            str = String.valueOf(value);
        }
        return str;
    }

    /**
     * 查询数字彩当前期次信息.
     *
     * @param gid
     *            彩种id
     *
     */
    private String getSzcPhaseInfo(String gid) throws ParseException {
        String info = "";
        if (!ProjUtils.isSzc(gid)) {
            return info;
        }
        String xmlpath = FileConstant.DATA_DIR + "phot" + File.separator + gid;
        JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath, "s.xml"));
        int count = xml.countXmlNodes("row");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar now = Calendar.getInstance();
        Calendar endtime = Calendar.getInstance();
        // 截止时间
        String et = null;
        // 期次
        String pid = null;
        for (int i = 0; i < count; i++) {
            et = xml.getStringValue("row[" + i + "].@et");
            pid = xml.getStringValue("row[" + i + "].@pid");
            endtime.setTime(df.parse(et));
            // 截止时间大于系统时间
            if (endtime.compareTo(now) > 0) {
                info = pid;
                break;
            }
        }
        return info;
    }

    //停止追号
    public String cancel_zhuihao(TradeBean bean) {
        log.info("用户停止追号：" + bean.getUid() + " zid: " + bean.getZid());
        String result = "";
        List<ZhRecordPojo> list = queryRecordList(bean);
        String istate = "";
        String did = "";
        for (ZhRecordPojo pojo : list) {
            istate = pojo.getIstate();
            if ("0".equals(istate)) {
                if (StringUtil.isNotEmpty(did)) {
                    did += "," + pojo.getIdetailid();
                }else {
                    did = pojo.getIdetailid();
                }
            }
        }
        if (StringUtil.isEmpty(did)) {
            bean.setBusiErrCode(-1);
            bean.setBusiErrDesc("没有可停止的期次");
            return result;
        }
        String[] cdid = did.split(",");
        for (String e : cdid) {
            if (StringUtil.isNotEmpty(e)) {
                bean.setDid(e);
                cancleZhMapper.cancle_zhuihao(bean);
            }
        }
        if (bean.getBusiErrCode() != 0) return result;
        return parseToString(bean, list);
    }

    private String parseToString(TradeBean bean, List<ZhRecordPojo> list) {
        JSONObject json = new JSONObject();
        json.put("code", bean.getBusiErrCode());
        json.put("desc", bean.getBusiErrDesc());
        json.put("data", list);
        return json.toJSONString();
    }

    private List<ZhRecordPojo> queryRecordList(TradeBean bean) {
        UserBean userBean = new UserBean();
        userBean.setGid(bean.getGid());
        userBean.setTid(bean.getZid());
        userBean.setUid(bean.getUid());
        userBean.setFlag(19);
        //用户中心查询信息
        return getInfoFromCenter(userBean);
    }

    private List<ZhRecordPojo> getInfoFromCenter(UserBean bean) {
        List<ZhRecordPojo> list = new ArrayList<>();
        try {
            ZhRecordResults<ZhRecordPojo> records = new ZhRecordResults<>();
            records = query_user_info(bean);
            log.info("交易中心-->调用查询记录结果：records==" + records.toJsonString());
            if (records != null) {
                list = records.getArray();
            }
        } catch (Exception e) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("查询失败");
            log.error("tradecenter --> queryRecordList 查询失败，uid=" + bean.getUid(), e);
        }
        return list;
    }

    public ZhRecordResults<ZhRecordPojo> query_user_info(UserBean bean) throws Exception {
        ZhRecordResults<ZhRecordPojo> results = new ZhRecordResults();
        long threadId = Thread.currentThread().getId();
        if (!isAvailable(bean, threadId)) return results;
        //u_query_19
        long start = System.currentTimeMillis();
        List<ZhRecordPojo> recordList = queryRecord(bean);
        log.info("线程ID=" + threadId + ",queryUserInfo不分页查询记录信息(总数=" + recordList.size() + ")耗时：" + (System.currentTimeMillis() - start) / 1000 + "s");
        log.debug(bean.getUid() + "\t" + bean.getTid() + "\t" + bean.getStime() + "\t" + bean.getEtime());

        if (bean.getFlag() == 36 && (Integer.parseInt(bean.getGid()) == 1 || Integer.parseInt(bean.getGid()) == 50)) { //查询追号记录 套餐追号要求当前期之后过滤投注号码
            String curr_issue = getCurrPid(bean);
            long l = System.currentTimeMillis();
			results.setTr(bean.getTr() + "");
			results.setTp(bean.getTp() + "");
			results.setPs(bean.getPs() + "");
			results.setPn(bean.getPn() + "");
			for (ZhRecordPojo pojo : recordList){
			    int pid = Integer.valueOf(pojo.getCperiodid());
			    if(pid > Integer.parseInt(curr_issue)){
			        pojo.setCcodes("");
			    }
			}
			results.setArray(recordList);
			bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
			bean.setBusiErrDesc("查询成功");
			log.info("线程ID=" + threadId + ",queryUserInfo生成xml耗时：" + (System.currentTimeMillis() - l)/1000 + "s");
        }else {
			results.setTr(bean.getTr() + "");
			results.setTp(bean.getTp() + "");
			results.setPs(bean.getPs() + "");
			results.setPn(bean.getPn() + "");
			results.setArray(recordList);
			bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
			bean.setBusiErrDesc("查询成功");
        }
        return results;
    }

    private List<ZhRecordPojo> queryRecord(UserBean bean) {
        String gid = bean.getGid();
        String tid = bean.getTid();
        List<ZhRecordPojo> recordList = zhItemsMapper.queryZhByZhId(gid, tid);
        return recordList;
    }

    private String getCurrPid(UserBean bean) throws Exception{
        String curr_issue = "";
        JXmlWrapper xml = JXmlWrapper.parse(new File("/opt/export/data/phot/" + bean.getGid() + "/s.xml"));
        int count = xml.countXmlNodes("row");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < count; i++) {
            String pid = xml.getStringValue("row[" + i + "].@pid");
            String et = xml.getStringValue("row[" + i + "].@et");
            long l = format.parse(et).getTime();
            if(System.currentTimeMillis() > l){
                continue;
            }
            if(!CheckUtil.isNullString(pid)){
                curr_issue = pid;
                break;
            }
        }
        return curr_issue;
    }

    private boolean isAvailable(UserBean bean, long threadId) {
        // 触屏,h5,wp和主站不显示购彩记录和返利明细
        if ((bean.getFlag() == 27 || bean.getFlag() == 40) && (bean.getSource() < 1000 || bean.getSource() >= 5000)) {
            bean.setBusiErrCode(UserErrCode.ERR_USER_NOT_EXITS);
            bean.setBusiErrDesc("没有该用户的相关记录");
            return false;
        }
        // 触屏,h5,wp和主站充值记录不显示返利转充值
        if (bean.getFlag() == 14 && (bean.getSource() < 1000 || bean.getSource() >= 5000 || (bean.getSource() >= 3000 && bean.getSource() < 4000))) {
            bean.setAid("98");
        }
        if (GameContains.canNotUse(bean.getGid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("不支持的彩种");
            return false;
        }
        if (!StringUtil.isEmpty(bean.getStime())) {
            if (!checkDate(bean.getStime(), "yyyy-MM-dd")) {
                bean.setBusiErrCode(UserErrCode.ERR_CHECK);
                bean.setBusiErrDesc("起始日期格式错误");
                return false;
            }
        }

        if (!StringUtil.isEmpty(bean.getEtime())) {
            if (!checkDate(bean.getEtime(), "yyyy-MM-dd")) {
                bean.setBusiErrCode(UserErrCode.ERR_CHECK);
                bean.setBusiErrDesc("终止日期格式错误");
                return false;
            }
        }

        if (!CheckUtil.isNullString(bean.getStime())) {
            if (bean.getFlag() == 42 || bean.getFlag() == 57) {//手机端查询账户信息是否有通知
                //不需再转换
            } else {
                Date date = DateUtil.parserDate(bean.getStime());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                bean.setStime(df.format(date));
            }
        }
        log.info("线程ID=" + threadId + ",queryUserInfo查询参数：gid = " + bean.getGid() + " cnickid = " + bean.getUid() + " flag = " + bean.getFlag() + " agentId = "
                + bean.getCuserId() + " stime = " + bean.getStime() + " etime = " + bean.getEtime() + " newvalue = " + bean.getNewValue());
        String cur = DateUtil.getCurrentFormatDate("HH:mm");
        if ((bean.getFlag() == 27 || bean.getFlag() == 30) && !(cur.compareToIgnoreCase("01:00") >= 0 && cur.compareToIgnoreCase("19:00") <= 0)) {
            bean.setBusiErrCode(3000);
            bean.setBusiErrDesc("尊敬的用户，交易流水的查询服务仅在非高峰期提供(01：00-19：00)。");
            return false;
        }
        return true;
    }

    public boolean checkDate(String date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date d = null;
        boolean flag = true;
        try {
            d = df.parse(date);
            flag = true;
        } catch (Exception e) {
            flag = false;
        }
//        String s1 = df.format(d);
        return flag;
    }

    /**
     * 取消追号前检测输入参数是否正确
     */
    public boolean checkParam4CancelZhuihao(TradeBean bean) {
        log.info("取消追号前检测输入参数是否正确,nickid=" + bean.getUid());
        int ret = isEmptyNickidAndPwd(bean);
        if (ret == 0 || bean.getBusiErrCode() != 0) {
            return false;
        }
        if (StringUtil.isEmpty(bean.getGid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("彩种指定不明确");
            return false;
        }
        if (StringUtil.isEmpty(bean.getZid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("撤销追号指定不明");
            return false;
        }
        if (ret == 0) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("撤销追号指定不明");
            log.info("取消追号操作参数错误,nickid=" + bean.getUid() + ",errDesc=" + bean.getBusiErrDesc());
            return false;
        }
        return true;
    }

    public int isEmptyNickidAndPwd(TradeBean bean) {
        int ret = 0;
        if (StringUtil.isEmpty(bean.getUid())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("用户名不能为空");
        } else if (StringUtil.isEmpty(bean.getPwd())) {
            bean.setBusiErrCode(UserErrCode.ERR_CHECK);
            bean.setBusiErrDesc("登录密码不能为空");
        } else {
            ret = 1;
        }
        return ret;
    }

    /**
     * 合买追号停售提示.
     */
    public int hmzhRemind(TradeBean bean) {
        log.info("合买追号停售状态检测,玩法=" + bean.getGid() + ",用户名=" + bean.getUid() + ",投注类型标志=" + bean.getActivityflag());
        JXmlWrapper tsxx = JXmlWrapper.parse(new File(FileConstant.GAME_CONFIG));
        List<JXmlWrapper> rows = tsxx.getXmlNodeList("row");
        int isale = -1;
        String gid = null;
        for (JXmlWrapper row : rows) {
            gid = row.getStringValue("@gid");
            if (bean.getGid().equals(gid)) {
                isale = row.getIntValue("@isale");
                break;
            }
        }
        log.info("合买追号停售状态=" + isale + ",玩法=" + bean.getGid());
        int maxLen = maxLen(bean.getGid());
        if (isale == -1) {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("未知玩法id");
        } else if (isale == 1 || isale == 2 || bean.getGrade() == 100) {
            if (bean.getActivityflag() == 1) {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("系统升级中，暂时无法发起合买");
            } else if (bean.getActivityflag() == 2) {
                if ("56".equals(bean.getGid()) || "59".equals(bean.getGid()) || "55".equals(bean.getGid()) || "10".equals(bean.getGid()) || "09".equals(bean.getGid()) || "04".equals(bean.getGid())) {
                    String[] pidArr = bean.getPid().split(",", -1);
                    if(maxLen <=0){
                        maxLen = 50;
                    }
                    zhResult(bean, pidArr, maxLen);
                } else if ("01".equals(bean.getGid()) || "03".equals(bean.getGid()) || "07".equals(bean.getGid()) || "50".equals(bean.getGid()) || "51".equals(bean.getGid()) || "52".equals(bean.getGid()) || "53".equals(bean.getGid())) {
                    String[] pidArr = bean.getPid().split(",", -1);
                    if(maxLen <=0){
                        maxLen = 10;
                    }
                    zhResult(bean, pidArr, maxLen);
                } else {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    bean.setBusiErrDesc("系统升级中，暂时无法发起追号");
                }
            } else {
                // 普通投注暂时不做任何检测
                bean.setBusiErrDesc("普通投注暂时不做任何检测");
            }
        } else {
            bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
            bean.setBusiErrDesc("系统升级中，暂时无法发起合买/追号");
        }

        if(0 == bean.getBusiErrCode()){
            bean.setBusiErrDesc("可以追号");
        }
        return 1;
    }

    //缓存获取白名单  仅供展示用户或者测试
    public int opencache(TradeBean bean) throws Exception {
        String ouser = "0";
        CacheBean cacheBean = new CacheBean();
        cacheBean.setKey(bean.getAppid());
        String catcheString = CacheUtil.getString(cacheBean, log, redisInterface, SysCodeConstant.TRADECENTER);
        if (StringUtil.isNotEmpty(catcheString)) {
            JSONObject json = JSON.parseObject(catcheString);
            if (!StringUtil.isEmpty(json.getString("paramJson"))) {
                JSONObject jsObj = JSON.parseObject(json.getString("paramJson"));
                ouser = jsObj.getString(BaseConstant.OPENUSER);
            }
        }
        if (!StringUtil.isEmpty(ouser)) {
            bean.setGrade(Integer.parseInt(ouser));
        }

        return 1;
    }

    //检测是否禁止充值支付,true为禁止,false为不禁止checkZhQs
    public boolean checkBanActivity(TradeBean bean) {
        JXmlWrapper xml = JXmlWrapper.parse(new File(FileConstant.BAN_ACTIVITY));
        List<JXmlWrapper> banNodeList = xml.getXmlNodeList("ban-activity");
        for (JXmlWrapper banNode : banNodeList) {
            String openFlag = banNode.getXmlNode("business-rules").getXmlNode("open").getStringValue("@flag");
            if ("1".equals(openFlag)) {
                if (ParseGeneralRulesUtil.parseGeneralRules(banNode.getXmlNode("general-rules"), bean)) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.TRADE_PID_PAUSE_SALE));
                    bean.setBusiErrDesc("系统升级中~暂停销售~");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String encodeJjyhBetInfo(TradeBean bean) {
        log.info("篮彩奖金优化投注web支付encodeJjyhBetInfo,用户名=" + bean.getUid() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion() + ",source=" + bean.getSource() + ",codes=" + bean.getCodes());
        String url = "";
        try {
            url = baseService.setJjyhRequestUrl(bean, bean.getSessionId1(), bean.getSessionId2(), bean.getRequestUrl());
            log.info("篮彩奖金优化投注web支付encodeJjyhBetInfo,url=" + url);
        } catch (Exception e) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("支付失败");
            log.error("篮彩奖金优化投注web支付encodeJjyhBetInfo出现异常,用户名=" + bean.getUid() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion() + ",source=" + bean.getSource() + ",codes=" + bean.getCodes());

        }
        return url;
    }

    @Override
    public String encodeBetInfo(TradeBean bean) {
        log.info("普通投注web支付encodeBetInfo,用户名=" + bean.getUid() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion() + ",source=" + bean.getSource() + ",codes=" + bean.getCodes());
        String str = "";
        try {
            String url = baseService.setRequestUrl(bean, bean.getSessionId1(), bean.getSessionId2(), bean.getRequestUrl());
            log.info("普通投注web支付encodeBetInfo,用户名=" + bean.getUid() + ",url=" + url + ",codes=" + bean.getCodes());
            boolean result = true;
            if (!StringUtil.isEmpty(bean.getCodes())) {// 投注号码和期次信息保存到缓存中。。。防止大量投注url过长的问题
                Map<String, String> iosBetInfo = new HashMap<String, String>();
                String pid = bean.getPid();
                if ((TradeConstants.ZH_GID.indexOf(bean.getGid().trim() + ",") != -1) && bean.getcType().equals("ZH") && (bean.getIzhflag() == 0)) { //izhflag 1 隔期追号   0 连续追号
                    pid = getCperiodids(bean.getGid(), bean.getPid().split(",")[0], Integer.parseInt(bean.getFind())); //期次从期次文件获取
                }
                iosBetInfo.put("codes", bean.getCodes());
                iosBetInfo.put("pid", pid);
                CacheBean cacheBean = new CacheBean();
                cacheBean.setKey(bean.getUid() + "_iosBetInfo");
                cacheBean.setValue(JSONObject.toJSONString(iosBetInfo));
                cacheBean.setTime(86400000);
                result = CacheUtil.setString(cacheBean, log, redisInterface, SysCodeConstant.TRADECENTER);// 投注号码信息和期次信息
                if (result) {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.SUCCESS));
                    bean.setBusiErrDesc("生成加密投注信息结束");
                    return url;
                }else {
                    bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                    bean.setBusiErrDesc("保存期次和号码到缓存失败");
                    log.info("普通投注web支付encodeBetInfo,保存期次和号码到缓存失败,用户名=" + bean.getUid() + ",codes=" + bean.getCodes());
                }
            } else {
                bean.setBusiErrCode(Integer.valueOf(BusiCode.FAIL));
                bean.setBusiErrDesc("投注号码为空");
                log.info("普通投注web支付encodeBetInfo,投注号码为空,用户名=" + bean.getUid() + ",codes=" + bean.getCodes());
            }
        } catch (Exception e) {
            bean.setBusiErrCode(1);
            bean.setBusiErrDesc("支付失败");
            log.error("普通投注web支付encodeBetInfo出现异常o,用户名=" + bean.getUid() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion() + ",source=" + bean.getSource() + ",codes=" + bean.getCodes(),e);
        }
        return str;
    }


    /**
     * 获取追号期次
     *
     * @param gid:期次编号
     * @param firstPid：传入的追号第一期
     * @param number：总期次数目
     * @return
     */
    public static String getCperiodids(String gid, String firstPid, int number) {

//		String filePath = "D:\\opt\\export\\data\\phot\\"+gid+"\\";
        String filePath = "/opt/export/data/phot/" + gid + "/";

        JXmlWrapper xmlWapper = JXmlWrapper.parse(new File(filePath, "s.xml"));
        String cperiodids = "";
        int c = xmlWapper.countXmlNodes("row");
        double tradeFirstQC = Double.parseDouble(firstPid);
        if (c < number) {
            return null;
        }
        int length = 0;
        for (int i = 0; i < c; i++) {
            String pid = xmlWapper.getStringValue("row[" + i + "].@pid");
            double QC = Double.parseDouble(pid);
            if (QC >= tradeFirstQC) {//可追期次
                length++;
                cperiodids += pid + ",";
                if (length == number) {
                    break;
                }
            }
        }
        if (number != length) {
            return null;
        } else {
            if (cperiodids.indexOf(firstPid) == -1) {
                return null;
            }
        }

        return cperiodids.substring(0, cperiodids.length() - 1);

    }

	@Override
	public CastDto jczq_optimize_proj(TradeBean bean) {
        if(!baseService.checkBanActivity(bean)) {
            return null;
        }
        
        if(!baseService.checkBeforeBuy(bean)){
        	return null;
        }
        
        CastDto castDto = castService.project_optimize_zq(bean);
		return castDto;
	}

	@Override
	public CastDto jclq_optimize_proj(TradeBean bean) {
        if(!baseService.checkBanActivity(bean)) {
            return null;
        }
        
        if(!baseService.checkBeforeBuy(bean)){
        	return null;
        }
        
        CastDto castDto = castService.project_optimize_lq(bean);
		return castDto;
	}
}

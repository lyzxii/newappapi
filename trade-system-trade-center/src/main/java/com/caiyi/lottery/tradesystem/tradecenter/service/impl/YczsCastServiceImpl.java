package com.caiyi.lottery.tradesystem.tradecenter.service.impl;


import com.caipiao.plugin.helper.CodeFormatException;
import com.caipiao.plugin.helper.GamePluginAdapter;
import com.caipiao.plugin.sturct.GameCastCode;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.tradecenter.service.BaseService;
import com.caiyi.lottery.tradesystem.tradecenter.service.CastService;
import com.caiyi.lottery.tradesystem.tradecenter.service.TradeService;
import com.caiyi.lottery.tradesystem.tradecenter.service.YczsCastService;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.FilterBase;
import com.caiyi.lottery.tradesystem.tradecenter.util.code.constants.TradeJC;
import com.caiyi.lottery.tradesystem.util.DateUtil;
import com.caiyi.lottery.tradesystem.util.MD5Util;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.Util;
import com.caiyi.lottery.tradesystem.util.cache.Cache;
import com.caiyi.lottery.tradesystem.util.cache.CacheManager;
import com.caiyi.lottery.tradesystem.util.code.FilterResult;
import com.caiyi.lottery.tradesystem.util.xml.JXmlUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trade.bean.CodeBean;
import trade.bean.TradeBean;
import trade.bean.jczq.JcMatchBean;
import trade.dto.JcCastDto;

import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一场致胜投注
 */
@Service
@Slf4j
public class YczsCastServiceImpl implements YczsCastService {

    @Autowired
    TradeService tradeService;

    @Autowired
    CastService castService;

    @Autowired
    BaseService baseService;

    private Map<String, GamePluginAdapter> mapsPlugin = new ConcurrentHashMap<>();

    private Map<String,String> leguage = new ConcurrentHashMap<>();

    @Override
    public JcCastDto yczs_cast(TradeBean bean) throws Exception{
        if(!baseService.checkBanActivity(bean)){
            return null;
        }
        return project_yczs_create(bean);
    }

    @Override
    public JcCastDto project_yczs_create(TradeBean bean) throws Exception {
        log.info("竞彩奖金优化投注project_yczs_create,用户名=" + bean.getUid() + ",source=" + bean.getSource() + "," +
                "ishm=" + bean.getType() + ",玩法=" + bean.getGid() + ",appversion=" + bean.getAppversion());
        if (bean.getBusiErrCode() != 0) {
            return null;
        }
        String ccodes=bean.getCodes();
        log.info("投注前检测开售状态和投注条件,用户名=" + bean.getUid() + ",source=" + bean.getSource());
        tradeService.checkBeforeBuy(bean);
        if (bean.getBusiErrCode() != 0) {
            log.info("不能投注,用户名=" + bean.getUid() + ",errDesc=" + bean.getBusiErrDesc());
            bean.setBusiErrCode(bean.getBusiErrCode());
            bean.setBusiErrDesc(bean.getBusiErrDesc());
            return null;
        }
        GamePluginAdapter plugin = mapsPlugin.get(bean.getGid());
        if (plugin == null) {
            try {
                plugin = (GamePluginAdapter) Thread.currentThread().getContextClassLoader().loadClass("com.caipiao.plugin.GamePlugin_" + bean.getGid()).newInstance();
                mapsPlugin.putIfAbsent(bean.getGid(), plugin);
                log.info("加载游戏插件成功 game=" + bean.getGid());
            } catch (Exception e) {
                throw new RuntimeException("加载游戏插件失败 game=" + bean.getGid());
            }
        }
        String filename = "";
        String str = "3=3,1=1,0=0";//自定义选项
        String items="";
        if(plugin != null){
            FilterResult result = new FilterResult();
            CodeBean codebean = new CodeBean();
            codebean.setCodeitems(str);//自定义
            codebean.setPlaytype(TradeJC.ds_playid.get(bean.getGid()));
            codebean.setLottype(Integer.parseInt(bean.getGid()));
            int total = 0;
            checkDeadTime(bean);//检查方案截至时间
            String gid = bean.getGid();
            String pid = bean.getPid();
            String codes = bean.getCodes();
            filename = MD5Util.compute(bean.getUid() + gid + System.currentTimeMillis() + pid);
            File dir = new File(FileConstant.BASE_PATH + File.separator + gid + File.separator + pid);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, gid+"_"+filename+"_n.txt");
            FileOutputStream fout = new FileOutputStream(file);
            String[] codd = codes.split(";");
            for(int i=0;i<codd.length; i++){
                total = checkCodeFormat(bean,result,plugin,codebean, total, codd[i]);
            }
            if(total != bean.getMoney()){
                throw new RuntimeException("上传文件中检测到注数与实际金额不相符！");
            }
            fout.write(result.getAllCodeToFile().getBytes());
            fout.flush();
            fout.close();
            items=result.getTeamItems();
        }else{
            throw new RuntimeException("该玩法暂未开通");
        }
        bean.setFflag(1);// 文件标志（0 是号码 1 是文件）
        bean.setPlay(0);// 玩法
        bean.setCodes(bean.getGid()+"_"+filename+"_n.txt");// 投注号码（文件投注的文件名）
        bean.setEndTime("");// 截止时间
        bean.setZid(","+items+",");
        castService.proj_cast_app(bean);//调用存储过程
        if (bean.getBusiErrCode() != 0) {
            String errdesc = StringUtil.isEmpty(bean.getBusiErrDesc()) ? "投注异常:请查看投注记录确认是否投注成功" : "投注失败:" + bean.getBusiErrDesc();
            bean.setBusiErrCode(bean.getBusiErrCode());
            bean.setBusiErrDesc(errdesc);
            return null;
            //throw new RuntimeException(errdesc);
        }
        bean.setCodes(ccodes);

        if (bean.getExtendtype()==15) {
            writeCastFile(bean, filename);//写入一场致胜投注文件
        }
        writeZxAndPpFile(bean, filename);//一场致胜区分 自选场，匹配场
        //TODO
        JcCastDto dto=new JcCastDto();
        dto.setProjid(bean.getHid());
        dto.setBalance(bean.getBalance());
        return dto;
    }

    private void writeZxAndPpFile(TradeBean bean, String filename) {
        String zxcodes = bean.getZxcodes();//自选  170216001>SPF=3/0;170216002>SPF=3/1+RQSPF=0;170216003>SPF=3
        String ppcodes = bean.getPpcodes();//匹配  170216001>SPF=3+RQSPF=0;170216002>SPF=0+RQSPF=3;170216002>SPF=0+RQSPF=3
        String[] zxCodes = zxcodes.split(";");
        String[] ppCodes = ppcodes.split(";");
        StringBuilder builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        builder.append("<root>");
        for(int i=0;i<zxCodes.length;i++){
            builder.append("<row> ");
            builder.append("<zxitem ").append(JXmlUtil.createAttrXml("id", zxCodes[i].substring(0, zxCodes[i].indexOf(">"))));
            builder.append(" ").append(JXmlUtil.createAttrXml("ccodes", zxCodes[i].substring(zxCodes[i].indexOf(">")+1, zxCodes[i].length()))).append(" />");
            builder.append("<ppitem ").append(JXmlUtil.createAttrXml("id", ppCodes[i].substring(0, ppCodes[i].indexOf(">"))));
            builder.append(" ").append(JXmlUtil.createAttrXml("ccodes", ppCodes[i].substring(ppCodes[i].indexOf(">")+1, ppCodes[i].length()))).append(" />");
            builder.append(" </row>");
        }
        builder.append("</root>");
        if (!Util.SaveFile(builder.toString(),"/opt/export/data/guoguan/" + bean.getGid() + "/" + bean.getPid() + "/" + bean.getHid().toLowerCase() + "_yczs.xml", "utf-8")){
            log.error(filename+"_yczs.xml"+"：存储失败");
            throw new RuntimeException("存储失败");
        }
    }

    private void writeCastFile(TradeBean bean,String filename) {
        refreshNewCodes(bean);
        String gid=bean.getGid();
        String pid=bean.getPid();
        String jjyh = StringUtil.getNullString(bean.getNewcodes());
        if (jjyh.split(";").length==1) {
            jjyh = bean.getNewcodes() + ";" + bean.getYhfs();
        }
        String[] jjyharr = jjyh.split(";");
        if (jjyharr.length==2) {
            String [] jjyhcod = jjyharr[0].split("\\|");
            String jjyhcods = null;
            if(jjyhcod.length==3){
                jjyhcods = jjyhcod[0]+"|"+jjyhcod[1];
            }else{
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
            if (!Util.SaveFile(yhxml.toString(), FileConstant.BASE_PATH  + File.separator + gid + File.separator + pid,gid+"_"+filename+"_yh.xml", "utf-8")){
                log.error(filename+"_yh.xml"+"：存储失败");
                throw new RuntimeException("存储失败");
            }
        }
    }

    private int checkCodeFormat(TradeBean bean, FilterResult result, GamePluginAdapter plugin,CodeBean codebean, int total, String code) throws CodeFormatException {
        codebean.setItemType(CodeBean.HAVEITEM);
        codebean.setCode(code);
        codebean.setGuoguan(bean.getGgtype());
        String [] codestring = code.split("_");
        int bs=1;//单式解析倍数
        int len=codestring.length;
        if(len==2){
            if(StringUtil.getNullInt(codestring[1].trim())>0){
                bs=Integer.parseInt(codestring[1].trim());
            }else{
                throw new RuntimeException("投注格式中倍数异常,code="+ code);
            }
        }else{
            throw new RuntimeException("投注格式异常,code="+ code);
        }
        FilterBase.doFilterJc(codebean, result);
        if(isValid(result.getCurrentCode())){
            try {
                GameCastCode gcc = plugin.parseGameCastCode(result.getCurrentCode());
                total += gcc.getCastMoney()*bs;
            } catch (Exception e) {
                throw new RuntimeException("请检查上传文件的格式,参考标准格式样本" + e.getMessage());// +
            }
            for(int n=1;n<bs;n++){
                result.addCode(result.getCurrentCode());
            }
            if(total>1000000){
                throw new RuntimeException("上传文件中检测到注数超过限制范围！");
            }
        }
        return total;
    }

    //检查投注截止时间
    private void checkDeadTime(TradeBean bean) {
        String items = bean.getItems();
        bean.setPlayid("70");
        Cache cache = jcMatchCache(bean);
        if (cache!=null){
            Date firsttime = null;
            List<JcMatchBean> mb= (List<JcMatchBean>) cache.getValue();
            //获取方案的截至时间
            String[] itemstr = StringUtil.splitter(items, ",");
            int chang = itemstr.length;
            for(int i=0;i<chang;i++){
                for(int ii=0;ii<mb.size();ii++){
                    if (mb.get(ii).getItemid().equals(itemstr[i])){
                        Date tmpdate = DateUtil.parserDateTime(mb.get(ii).getEt());
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
                throw new RuntimeException("投注失败：无法获取方案截止时间");
            }
            if (System.currentTimeMillis() > firsttime.getTime()) {
                throw new RuntimeException("方案截止时间为：" + DateUtil.getDateTime(firsttime.getTime()) + " 下次请提前");
            }
            firsttime.setTime(firsttime.getTime()- 1000 * 60 * 10);  //期次=截止时间-10分钟，需要与后台保持一致
            String expect = DateUtil.getDateTime(firsttime.getTime(),"yyyyMMdd");
            bean.setPid(expect);
        } else {
            log.error("单式缓存调用失败");
            throw new RuntimeException("单式缓存调用失败");
        }
    }


    /**
     * 竞彩对阵缓存
     * @param bean
     * @return
     */
    private Cache jcMatchCache(TradeBean bean) {
        Cache cache = null;
        CacheManager cm = CacheManager.getCacheManager();
        cache = cm.getCacheMatch(TradeJC.playid.get(bean.getPlayid()), bean.getPid());
        if (cache == null||cache.isExpired()) {
            JXmlWrapper lsxml = JXmlWrapper.parse(new File("/opt/export/data/jincai/leguage_matching.xml"));
            int lscount = lsxml.countXmlNodes("row");
            String fullname = null;
            String shortname = null;
            for(int z = 0; z < lscount; z++){
                fullname = lsxml.getStringValue("row[" + z + "].@fullname");
                shortname = lsxml.getStringValue("row[" + z + "].@shortname");
                if(!leguage.containsKey(fullname))
                    leguage.put(fullname, shortname);
            }
            JXmlWrapper xml = JXmlWrapper.parse(new File("/opt/export/data/jincai", "jc_hh.xml"));
            int count = xml.countXmlNodes("row");
            List<JcMatchBean> mList = new ArrayList<JcMatchBean>();
            for (int i = 0; i < count; i++) {
                String mid = xml.getStringValue("row[" + i + "].@itemid");
                String hn = xml.getStringValue("row[" + i + "].@hn");
                String gn = xml.getStringValue("row[" + i + "].@gn");
                String bt = xml.getStringValue("row[" + i + "].@mt");
                String et = xml.getStringValue("row[" + i + "].@et");
                String b3 = xml.getStringValue("row[" + i + "].@bet3");
                String b1 = xml.getStringValue("row[" + i + "].@bet1");
                String b0 = xml.getStringValue("row[" + i + "].@bet0");
                String spf = xml.getStringValue("row[" + i + "].@spf");
                String rqspf = xml.getStringValue("row[" + i + "].@rqspf");
                int close = xml.getIntValue("row[" + i + "].@close", 0);
                String mname = xml.getStringValue("row[" + i + "].@name");
                String lmnames = xml.getStringValue("row[" + i + "].@mname");
                String lmname = lmnames.length() > 4 ? lmnames.substring(0, 4): lmnames;
                lmname = (leguage!=null && leguage.get(lmnames)!=null ? leguage.get(lmnames):lmname);
                String cl = xml.getStringValue("row[" + i + "].@cl");
                String isale = xml.getStringValue("row[" + i + "].@isale");
                JcMatchBean mb = new JcMatchBean();
                mb.setItemid(mid);
                mb.setHn(hn);
                mb.setGn(gn);
                mb.setBt(bt);
                mb.setEt(et);
                mb.setB3(b3);
                mb.setB1(b1);
                mb.setB0(b0);
                mb.setClose(close);
                mb.setMname(mname);
                mb.setLmname(lmname);
                mb.setCl(cl);
                mb.setIsale(isale);
                mb.setSpf(spf);
                mb.setRqspf(rqspf);
                mb.setSpv(xml.getStringValue("row[" + i + "].@bqc")+","+xml.getStringValue("row[" + i + "].@cbf")+","+xml.getStringValue("row[" + i + "].@jqs")+","+xml.getStringValue("row[" + i + "].@rqspf")+","+xml.getStringValue("row[" + i + "].@spf"));
                mList.add(mb);
            }
            Cache ca= new Cache(TradeJC.playid.get(bean.getPlayid())+bean.getPid(), mList, System.currentTimeMillis()+1000*60, false);
            cm.putCacheMatch(TradeJC.playid.get(bean.getPlayid()), bean.getPid(), ca);
            cache = ca;
        }

        return cache;
    }

    private boolean isValid(String tmp){
        if(tmp.indexOf("=") == -1){
            return false;
        }
        return true;
    }

    public void refreshNewCodes(TradeBean bean) {
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
}

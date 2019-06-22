package order.util;

import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.ConcurrentSafeDateUtil;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import com.caiyi.lottery.tradesystem.util.proj.LiveBfUtil;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import order.dto.*;

import java.io.File;
import java.util.*;

/**
 * 篮球对阵
 *
 * @author GJ
 * @create 2018-01-11 14:49
 **/
public class BaketMatchsUtil {

    private static final String []  yhfslist = {"中奖优先", "平稳盈利", "奖金优先"};
    public static final String [] wk ={"日","一","二","三","四","五","六"};
    public static final String pattern = "yyyy-MM-dd";

    public static HashMap<String, String> SFC = new HashMap<String, String>();
    static {
        SFC.put("01", "6");
        SFC.put("02", "7");
        SFC.put("03", "8");
        SFC.put("04", "9");
        SFC.put("05", "10");
        SFC.put("06", "11");
        SFC.put("11", "0");
        SFC.put("12", "1");
        SFC.put("13", "2");
        SFC.put("14", "3");
        SFC.put("15", "4");
        SFC.put("16", "5");
    }

    public static HashMap<String, String> SF = new HashMap<String, String>();
    static {
        SF.put("3", "1");
        SF.put("0", "0");
    }

    public static HashMap<String, String> DXF = new HashMap<String, String>();
    static {
        DXF.put("0", "1");
        DXF.put("3", "0");
    }

    public static String getsfsel(String sel){
        return sel.replace("0", "主负").replace("3", "主胜");
    }

    public static String getdxfsel(String sel){
        return  sel.replace("0", "小分").replace("3", "大分");
    }

    public static String getsfcsel(String sel){
        return SFCSEL.get(sel);
    }



    public static HashMap<String, String> SFCSEL = new HashMap<String, String>();
    static{
        SFCSEL.put("01", "主胜 1-5分");
        SFCSEL.put("02", "主胜 6-10分");
        SFCSEL.put("03", "主胜 11-15分");
        SFCSEL.put("04", "主胜 16-20分");
        SFCSEL.put("05", "主胜 21-25分");
        SFCSEL.put("06", "主胜 26+分");

        SFCSEL.put("11", "主负 1-5分");
        SFCSEL.put("12", "主负 6-10分");
        SFCSEL.put("13", "主负 11-15分");
        SFCSEL.put("14", "主负 16-20分");
        SFCSEL.put("15", "主负 21-25分");
        SFCSEL.put("16", "主负 26+分");
    }

    /**
     * 蓝彩对阵详情
     * @param gid 彩种编号
     * @param flag 1表示从中奖排行榜查看详情,0表示其它
     * @return
     * @throws Exception
     */
    public static void loadInfo(GamesProjectDTO gamesProjectDTO, ProjectInfoDTO projectInfoDTO, String gid, int flag, int grade) throws Exception{
        String source = projectInfoDTO.getSource();
        if ("6".equals(source)) {//6 奖金优化对阵
            getJJYHDuizhen(gamesProjectDTO, projectInfoDTO, flag, grade);
            return ;
        } else if ("13".equals(source)) { //纯单关投注 //&& codes.indexOf(";") > -1
            getHhDgDuizhen(gamesProjectDTO, projectInfoDTO, flag, grade);
            return;
        }
        if (!"71".equals(gid)) {
            getDuizhen(gamesProjectDTO, projectInfoDTO, flag, grade);
            return;
        }else {
            getHhDuizhen(gamesProjectDTO, projectInfoDTO, flag, grade);
            return;
        }
    }

    /**
     * 获取竞彩足球混合投注对阵详情
     * @param gamesProjectDTO
     * @param projectInfoDTO
     * @param flag
     * @param grade
     * @throws Exception
     */
    private static void getHhDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag,int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());

        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if (flag == 1) {
            //todo 是否是方案所有人查看方案, same=1 是, same=0 不是
        }
        String ifile =projectInfoDTO.getIfile();
        String ccodes = projectInfoDTO.getCcodes();
        if(!"1".equals(ifile)){
            setWinzs(projectInfoDTO);
            String gg=getHhDZcodes(ccodes);
            projectInfoDTO.setGg(gg);
            Map<String, String> dmap = new  HashMap<String, String>(); //设胆map
            Map<String, String> map=new  HashMap<String, String>();//key 131124020 vaule 3/1
            getCodeMap(true,dmap,map,ccodes);

            String xmlpath = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" + projectInfoDTO.getHid().toLowerCase() + ".xml";
            JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));


            int count = xml.countXmlNodes("item");

            java.util.Calendar c = java.util.Calendar.getInstance();
            MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
            List<MatchDTO> matchDTOList = new ArrayList<>();
            Map<String, Map<String, String>> lcDataMap = LiveBfUtil.lcDataMap();


            for (int i = 0; i < count; i++) {

                String id=xml.getStringValue("item["+i+"].@id");//场次编号
                String tdate="20"+id.substring(0,2)+"-"+id.substring(2,4)+"-"+id.substring(4,6);
                c.setTime(ConcurrentSafeDateUtil.parse(tdate, pattern));
                String name = "周"+wk[c.get(Calendar.DAY_OF_WEEK)-1]+""+id.substring(6,9);

                String[] ms = new String[1];
                ms[0] = id;
                String[] minfoarr = new String[8];
                minfoarr[0] = name;
                minfoarr[1] = xml.getStringValue("item[" + i + "].@hn");
                minfoarr[2] = xml.getStringValue("item[" + i + "].@lose");
                minfoarr[3] = xml.getStringValue("item[" + i + "].@vn");
                minfoarr[4] = xml.getStringValue("item[" + i + "].@hs");
                minfoarr[5] = xml.getStringValue("item[" + i + "].@vs");
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, lcDataMap);
                matchDTO.setIsdan(isDan(dmap,id));
                String sp=xml.getStringValue("item["+i+"].@spvalue");
                String cancel = xml.getStringValue("item["+i+"].@cancel");
                if("1".equals(cancel)){
                    sp = "1,1|1,1|1,1,1,1,1,1,1,1,1,1,1,1|1,1";
                }
                String [] spvalues=sp.split("\\|",-1);//spvalue//赔率   sf.xml|rfsf.xml|sfc.xml|dxf.xml

                String cod="";
                String tz=map.get(id);
                if (!StringUtil.isEmpty(tz)) {
                    String [] d=tz.split("\\+");
                    for (int j = 0; j < d.length; j++) {
                        //混合71HM2013112842172852
                        //HH|131128301&gt;SF=0+RFSF=0+DXF=3+SFC=02/03/13,131128302&gt;SF=3+DXF=0|2*1
                        int index=0;
                        String [] arr= d[j].split("\\=");
                        String wf=arr[0];
                        String [] tarra=arr[1].split("\\/");
                        String [] spvalue=null;
                        for (int k = 0; k < tarra.length; k++) {
                            if ("DXF".equals(wf)) {
                                spvalue = spvalues[3].split(",");
                                index=Integer.valueOf(DXF.get(tarra[k]));
                                //index=Integer.valueOf(SF.get(tarra[k]))+2+2+12;
                            }else if ("SFC".equals(wf)) {
                                spvalue = spvalues[2].split(",");
                                index=Integer.valueOf(SFC.get(tarra[k]));
                                //index=Integer.valueOf(SFC.get(tarra[k]))+2+2;
                            }else if ("RFSF".equals(wf)) {
                                spvalue = spvalues[1].split(",");
                                index=Integer.valueOf(SF.get(tarra[k]));
                                //index=Integer.valueOf(SF.get(tarra[k]))+2;
                            }else{
                                spvalue = spvalues[0].split(",");
                                index=Integer.valueOf(SF.get(tarra[k]));
                            }

                            if ("".equals(cod)) {
                                cod="HH|"+wf+"="+tarra[k]+"_"+spvalue[index];
                            }else {
                                cod+=","+wf+"="+tarra[k]+"_"+spvalue[index];
                            }

                        }

                    }
                }
                matchDTO.setCcodes(cod);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);
        }
        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);

    }
    private static void getDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag,int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        String gid = projectInfoDTO.getGid();
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if (flag == 1) {
            //todo 是否是方案所有人查看方案, same=1 是, same=0 不是
        }
        String ifile =projectInfoDTO.getIfile();
        String ccodes = projectInfoDTO.getCcodes();
        if(!"1".equals(ifile)){
            setWinzs(projectInfoDTO);
            String gg=getDZcodes(ccodes);
            projectInfoDTO.setGg(gg);
            Map<String, String> dmap = new  HashMap<String, String>(); //设胆map
            Map<String, String> map=new  HashMap<String, String>();//key 131124020 vaule 3/1
            getCodeMap(false,dmap,map,ccodes);

            String xmlpath = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" + projectInfoDTO.getHid().toLowerCase() + ".xml";
            JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));


            int count = xml.countXmlNodes("item");

            java.util.Calendar c = java.util.Calendar.getInstance();
            MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
            List<MatchDTO> matchDTOList = new ArrayList<>();
            Map<String, Map<String, String>> lcDataMap = LiveBfUtil.lcDataMap();

            for (int i = 0; i < count; i++) {
                String id=xml.getStringValue("item["+i+"].@id");//场次编号
                String tdate="20"+id.substring(0,2)+"-"+id.substring(2,4)+"-"+id.substring(4,6);
                c.setTime(ConcurrentSafeDateUtil.parse(tdate, pattern));
                String name = "周"+wk[c.get(Calendar.DAY_OF_WEEK)-1]+""+id.substring(6,9);
                String[] ms = new String[1];
                ms[0] = id;
                String[] minfoarr = new String[8];
                minfoarr[0] = name;
                minfoarr[1] = xml.getStringValue("item[" + i + "].@hn");
                minfoarr[2] = xml.getStringValue("item[" + i + "].@lose");
                minfoarr[3] = xml.getStringValue("item[" + i + "].@vn");
                minfoarr[4] = xml.getStringValue("item[" + i + "].@hs");
                minfoarr[5] = xml.getStringValue("item[" + i + "].@vs");
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, lcDataMap);
                matchDTO.setIsdan(isDan(dmap,id));


                String spvalue=xml.getStringValue("item["+i+"].@spvalue");
                String cancel = xml.getStringValue("item["+i+"].@cancel");
                if("1".equals(cancel)){
                    spvalue = "1,1|1,1|1,1,1,1,1,1,1,1,1,1,1,1|1,1";
                }
                String [] sparr=spvalue.split("\\|",-1);//spvalue//赔率   sf.xml|rfsf.xml|sfc.xml|dxf.xml"
                String [] sp=null;
                Map<String, String> m=null;//
                String prefix=null;
                switch (Integer.valueOf(gid)) {

                    case 94: // 竞彩篮球-胜负
                        sp=sparr[0].split(",");
                        m=SF;
                        prefix="SF";
                        break;
                    case 95: // 竞彩篮球-让分胜负
                        sp=sparr[1].split(",");
                        m=SF;
                        prefix="RFSF";
                        break;
                    case 96: // 竞彩篮球-胜分差
                        sp=sparr[2].split(",");
                        prefix="SFC";
                        m=SFC;
                        break;
                    case 97: // 竞彩篮球-大小分
                        sp=sparr[3].split(",");
                        m=DXF;
                        prefix="DXF";
                        break;
                    //			case 71: // 竞彩篮球-混合过关
                    //				sp=spvalue.split(",");
                    //				break;
                    //变：http://www.9188.com/data/jincai/sp/131226/131226040.xml
                    default:
                        break;
                }

                String cod = "";
                String tz=map.get(id);
                if(!StringUtil.isEmpty(tz)){
                    String [] d=tz.split("/");
                    for (int j = 0; j < d.length; j++) {
                        int index=Integer.valueOf(m.get(d[j]));
                        if ("".equals(cod)) {
                            cod=prefix+"|"+d[j]+"_"+sp[index];
                        }else {
                            cod+=","+d[j]+"_"+sp[index];
                        }
                    }
                }
                matchDTO.setCcodes(cod);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);
        }
        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }
    private static void getHhDgDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag,int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if (flag == 1) {
            //todo 是否是方案所有人查看方案, same=1 是, same=0 不是
        }
        String ifile =projectInfoDTO.getIfile();
        String ccodes = projectInfoDTO.getCcodes();
        if (!"1".equals(ifile)){
            projectInfoDTO.setGg("单关");
            setWinzs(projectInfoDTO);
            Map<String, String> map = new  HashMap<String, String>();
            getccodesMap(map, ccodes);
            String xmlpath = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" + projectInfoDTO.getHid().toLowerCase() + ".xml";
            JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));


            int count = xml.countXmlNodes("item");

            java.util.Calendar c = java.util.Calendar.getInstance();
            MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
            List<MatchDTO> matchDTOList = new ArrayList<>();
            Map<String, Map<String, String>> lcDataMap = LiveBfUtil.lcDataMap();
            for (int i = 0; i < count; i++) {
                String id = xml.getStringValue("item[" + i + "].@id"); //场次编号
                String tdate = "20" + id.substring(0, 2) + "-" + id.substring(2, 4) + "-" + id.substring(4, 6);
                c.setTime(ConcurrentSafeDateUtil.parse(tdate, pattern));
                String name = "周" + wk[c.get(Calendar.DAY_OF_WEEK) - 1 ] + "" + id.substring(6, 9);

                String[] ms = new String[1];
                ms[0] = id;
                String[] minfoarr = new String[8];
                minfoarr[0] = name;
                minfoarr[1] = xml.getStringValue("item[" + i + "].@hn");
                minfoarr[2] = xml.getStringValue("item[" + i + "].@lose");
                minfoarr[3] = xml.getStringValue("item[" + i + "].@vn");
                minfoarr[4] = xml.getStringValue("item[" + i + "].@hs");
                minfoarr[5] = xml.getStringValue("item[" + i + "].@vs");
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, lcDataMap);
                matchDTO.setIsdan("0");
                String sp = xml.getStringValue("item[" + i + "].@spvalue");
                String cancel = xml.getStringValue("item["+i+"].@cancel");
                if("1".equals(cancel)){
                    sp = "1,1|1,1|1,1,1,1,1,1,1,1,1,1,1,1|1,1";
                }
                String [] spvalues = sp.split("\\|",-1); //spvalue//赔率   sf.xml|rfsf.xml|sfc.xml|dxf.xml

                String cod = "";
                String tz = map.get(id);
                if (!StringUtil.isEmpty(tz)) {
                    String [] d = tz.split("\\+");
                    for (int j = 0; j < d.length; j++) {
                        //混合71HM2013112842172852
                        //HH|131128301&gt;SF=0+RFSF=0+DXF=3+SFC=02/03/13,131128302&gt;SF=3+DXF=0|2*1
                        int index = 0;
                        String [] arr = d[j].split("\\=");
                        String wf = arr[0];
                        if ("".equals(cod)) {
                            cod = wf;
                        } else {
                            cod += "+" + wf;
                        }

                        String [] tarra = arr[1].split("\\/");
                        String [] spvalue = null;
                        for (int k = 0; k < tarra.length; k++) {
                            if ("DXF".equals(wf)) {
                                spvalue = spvalues[3].split(",");
                                index = Integer.valueOf(DXF.get(tarra[k].split("\\_")[0]));
                            } else if ("SFC".equals(wf)) {
                                spvalue = spvalues[2].split(",");
                                index = Integer.valueOf(SFC.get(tarra[k].split("\\_")[0]));
                            } else if ("RFSF".equals(wf)) {
                                spvalue = spvalues[1].split(",");
                                index = Integer.valueOf(SF.get(tarra[k].split("\\_")[0]));
                            } else {
                                spvalue = spvalues[0].split(",");
                                index = Integer.valueOf(SF.get(tarra[k].split("\\_")[0]));
                            }

                            if (k == 0){
                                cod += "|" + tarra[k].split("\\_")[0] + "_" + spvalue[index] + "_" + Integer.valueOf(tarra[k].split("\\_")[1]) * 2;
                            } else {
                                cod += "," + tarra[k].split("\\_")[0] + "_" + spvalue[index] + "_" + Integer.valueOf(tarra[k].split("\\_")[1]) * 2;
                            }

                        }

                    }
                }
                matchDTO.setCcodes(cod);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);
        }
        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    private static void getJJYHDuizhen(GamesProjectDTO gamesProjectDTO,ProjectInfoDTO projectInfoDTO, int flag,int grade) throws Exception {
        projectInfoDTO.setHid(projectInfoDTO.getHid().toLowerCase());
        // 奖金优化标识
        projectInfoDTO.setJjyh("1");
        if (!"3".equals(projectInfoDTO.getCast())) {
            // 出票时间
            projectInfoDTO.setCtime(null);
        }
        if (flag == 1) {
         //todo 是否是方案所有人查看方案, same=1 是, same=0 不是
        }
        setWinzs(projectInfoDTO);
        Map<String, String> matchdata = new HashMap<String, String>();
        Map<String, String> spmap = new HashMap<String, String>();
        String ccode = projectInfoDTO.getCcodes();
        String xmlpath = FileConstant.GUOGUAN_DIR + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/proj/" + projectInfoDTO.getHid().toLowerCase() + ".xml";
        JXmlWrapper xml = JXmlWrapper.parse(new File(xmlpath));
        int count = xml.countXmlNodes("item");
        Calendar c =Calendar.getInstance();
        for (int i = 0; i < count; i++) {
            String id = xml.getStringValue("item[" + i + "].@id");
            String hn = xml.getStringValue("item[" + i + "].@hn");
            String vn = xml.getStringValue("item[" + i + "].@vn");
            String hs = xml.getStringValue("item[" + i + "].@hs");
            String gs = xml.getStringValue("item[" + i + "].@vs");

            String lose = xml.getStringValue("item[" + i + "].@lose");//lose="0|-10.5|0|199.5"
            String spvalue = xml.getStringValue("item[" + i + "].@spvalue");
            String cancel = xml.getStringValue("item["+i+"].@cancel");
            if("1".equals(cancel)){
                spvalue = "1,1|1,1|1,1,1,1,1,1,1,1,1,1,1,1|1,1";
            }

            String tdate = "20" + id.substring(0,2) + "-" + id.substring(2,4) + "-" + id.substring(4,6);
            c.setTime(ConcurrentSafeDateUtil.parse(tdate, pattern));
            String mid = "周" + wk[c.get(Calendar.DAY_OF_WEEK) - 1 ] + "" + id.substring(6, 9);

            matchdata.put(id, mid + "_" + hn + "_" + lose + "_" + vn + "_" + hs + "_" + gs);
            spmap.put(id, spvalue);
        }

        String yhfile = ccode.replace("_n.txt", "_yh.xml");
        String originxmlpath = FileConstant.BASE_PATH + projectInfoDTO.getGid() + "/" + projectInfoDTO.getPid() + "/" + yhfile;
        File file = new File(originxmlpath);
        if (file == null || !file.exists()) {
            return ;
        }
        String gid = projectInfoDTO.getGid();
        JXmlWrapper jxml = JXmlWrapper.parse(file);
        int yhfs = Integer.parseInt(jxml.getStringValue("row.@yhfs"));
        String yhmatchs = jxml.getStringValue("row.@matchs");
        String yhcode = jxml.getStringValue("row.@code");
        int missmatch = 0;
        String missstr = jxml.getStringValue("row.@missmatch");
        if (!StringUtil.isEmpty(missstr)) {
            missmatch = Integer.parseInt(missstr);
        }
        Map<String, Map<String, String>> lcDataMap = LiveBfUtil.lcDataMap();

        MatchInfoDTO matchInfoDTO = new MatchInfoDTO();
        List<MatchDTO> matchDTOList = new ArrayList<>();

        PassInfoDTO passInfoDTO = new PassInfoDTO();
        List<PassDTO> passDTOList = new ArrayList<>();
        matchInfoDTO.setFs(yhfslist[yhfs]);
        if ("71".equals(gid)) { //竞彩篮球混投
            yhmatchs = yhmatchs.substring(3); //去掉HH|
            String[] yhmatchsList = yhmatchs.split("\\,");
            for (int i = 0; i < yhmatchsList.length; i++) {
                String[] ms = yhmatchsList[i].split("\\>");
                String minfo = matchdata.get(ms[0]);
                String[] minfoarr = minfo.split("\\_", -1);
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, lcDataMap);
                setccodes(matchDTO, spmap.get(ms[0]), ms[1]);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);

            String gg=getGgStr(yhcode,matchdata,passDTOList);
            passInfoDTO.setGg(gg);
            passInfoDTO.setMissmatch(String.valueOf(missmatch));
            passInfoDTO.setPassinfo(passDTOList);
            gamesProjectDTO.setPassInfo(passInfoDTO);
            projectInfoDTO.setGg(gg);

        }else {
            String[]  yhmatchsList = yhmatchs.split("\\/");
            for (int i = 0; i < yhmatchsList.length; i++){
                String[] ms = yhmatchsList[i].replace("]", "").split("\\[", -1);
                String minfo = matchdata.get(ms[0]);
                String [] minfoarr = minfo.split("\\_", -1);
                MatchDTO matchDTO = getMatchDTO(ms, minfoarr, lcDataMap);
                getOtherCodes(matchDTO, spmap.get(ms[0]), ms[1],gid);
                matchDTOList.add(matchDTO);
            }
            matchInfoDTO.setMatchs(matchDTOList);
            gamesProjectDTO.setMatchInfo(matchInfoDTO);

            String gg=getGgOtherStr(yhcode,matchdata,gid,passDTOList);
            passInfoDTO.setGg(gg);
            passInfoDTO.setMissmatch(String.valueOf(missmatch));
            passInfoDTO.setPassinfo(passDTOList);
            gamesProjectDTO.setPassInfo(passInfoDTO);
            projectInfoDTO.setGg(gg);
        }
        ProcessDTO processDTO = ProjectMatchsUtil.getProcess(projectInfoDTO, grade);
        gamesProjectDTO.setProcessInfo(processDTO);
        gamesProjectDTO.setProjectInfo(projectInfoDTO);
    }

    private static void getccodesMap( Map<String, String> map,String codes){
        String dgcodes = formatCodeByLanCai(codes);
        String [] tarr = dgcodes.split("\\,");

        String [] xz = null;
        for (int i = 0; i < tarr.length; i++) {
            xz = tarr[i].split("\\>");
            map.put(xz[0], xz[1]);
        }
    }

    private static void getCodeMap(boolean isHh,Map<String, String> dmap, Map<String, String> map,String ccodes){
        String str = "=";
        if (isHh) {
            str = ">";
        }
        String [] codarr=ccodes.split("\\|");
        if (codarr.length >= 3){
            String [] tarr=codarr[1].replaceAll("\\$", ",").split(",");
            String [] xz=null;
            for (int i = 0; i < tarr.length; i++) {
                xz=tarr[i].split(str);
                map.put(xz[0], xz[1]);
            }
        }
        if (codarr.length >= 3){
            if (codarr[1].indexOf("$") > 0) {
                String dan = codarr[1].split("\\$")[0];
                String [] darr = dan.split(",");
                String [] da = null;
                for (int i = 0; i < darr.length; i++) {
                    da = darr[i].split(str);
                    dmap.put(da[0], da[1]);
                }
            }
        }


    }

    private static String getDZcodes(String ccodes){
        String gstr=null;
        String [] codarr=ccodes.split("\\|");////DXF|131128302=3,131128304=0|2*1
        if (codarr.length>=3){
            if ("1*1".equals(codarr[2])) {
                codarr[2]="单关";
            }
            gstr=codarr[2].replace("*", "串");
        }
        return gstr==null?null:gstr.replaceAll("([,]+)1串1", ",单关").replaceAll("^1串1", "单关");
    }

    private static String getHhDZcodes(String ccodes){
        String gstr=null;
        String [] codarr=ccodes.split("\\|");////DXF|131128302=3,131128304=0|2*1
        if (codarr.length>=3){
             gstr=codarr[2].replace("*", "串");
        }
        return gstr==null?null:gstr.replaceAll("([,]+)1串1", ",单关").replaceAll("^1串1", "单关");
    }

    private static String getGgOtherStr(String yhcode,Map<String, String> matchdata, String gid,List<PassDTO> passDTOList){
        String gg = ""; //过关方式

        String []  yhcodeList = yhcode.split(";");
        for (int i = 0; i < yhcodeList.length; i++){
            String []  mcarr = yhcodeList[i].split("\\|");
            String []  marr = mcarr[1].split("\\,");
            String mstr = "";
            for (int j = 0; j < marr.length; j++){
                String []  mt = marr[j].split("\\=");
                String minfo = matchdata.get(mt[0]);
                String []  minfoarr = minfo.split("\\_");
                if ("94".equals(gid)) {
                    mstr += minfoarr[1] + "(" + getsfsel(mt[1]) + "),";
                } else if ("95".equals(gid)) {
                    mstr += minfoarr[1] + "(让分" + getsfsel(mt[1]) + "),";
                } else if ("96".equals(gid)) {
                    mstr += minfoarr[1] + "(" + getsfcsel(mt[1]) + "),";
                } else if ("97".equals(gid)){
                    mstr += minfoarr[1] + "(" + getdxfsel(mt[1]) + "),";
                }
            }
            String []  yhggbs = mcarr[2].split("\\_");
            PassDTO passDTO = new PassDTO();
            passDTO.setStr(mstr.substring(0, mstr.length() - 1));
            passDTO.setBs(yhggbs[1]);

            if (gg.indexOf(yhggbs[0]) != -1) {
            } else {
                gg = gg + yhggbs[0] + ",";
            }
            passDTOList.add(passDTO);
        }
        return gg;
    }

    private static void getOtherCodes( MatchDTO matchDTO,String spvalue,String ms,String gid){
        String [] sparr = spvalue.split("\\|", -1);

        String[]  mid = ms.split("\\,", -1);
        String toustr = "";
        int length = mid.length;
        for (int m = 0; m < length; m++) {
            if ("94".equals(gid)) { // 竞彩篮球-胜负
                String [] sp = sparr[0].split(",");
                toustr += "SF" + "_" + mid[m] + "_" + sp[Integer.valueOf(SF.get(mid[m]))] + ",";
            } else  if ("95".equals(gid)) { // 竞彩篮球-让分胜负
                String [] sp = sparr[1].split(",");
                toustr += "RFSF" + "_" + mid[m] + "_" + sp[Integer.valueOf(SF.get(mid[m]))] + ",";
            }  else if ("96".equals(gid)) { // 竞彩篮球-胜分差
                String[] sp = sparr[2].split(",");
                toustr += "SFC" + "_" + mid[m] + "_" + sp[Integer.valueOf(SFC.get(mid[m]))] + ",";
            } else if ("97".equals(gid)) { // 竞彩篮球-大小分
                String[] sp = sparr[3].split(",");
                toustr += "DXF" + "_" + mid[m] + "_" + sp[Integer.valueOf(DXF.get(mid[m]))] + ",";
            }
        }
        matchDTO.setCcodes(toustr.substring(0, toustr.length() - 1));

    }

    private static String getGgStr(String yhcode,Map<String, String> matchdata, List<PassDTO> passDTOList){
        String gg = "";
        String[]  yhcodeList = yhcode.split("\\;"); //HH|130828003>RQSPF=3,130828004>SPF=0,130828005>SPF=0,130828006>SPF=3|4*1_1
        for (int i = 0; i < yhcodeList.length; i++){
            String[]  mcarr = yhcodeList[i].split("\\|");
            String[]  marr = mcarr[1].split("\\,"); //130828003>RQSPF=3,130828004>SPF=0
            String mstr = "";
            for (int j = 0; j < marr.length; j++){
                String[]  mt = marr[j].split("\\>");
                String minfo = matchdata.get(mt[0]);
                String[]  minfoarr = minfo.split("\\_", -1);
                mstr +=  minfoarr[1] + "(";
                String[]  mid = mt[1].split("\\+"); //RQSPF=3+SPF=0
                for (int m = 0; m < mid.length; m++){
                    String key = mid[m].split("\\=")[0]; //RQSPF  SPF
                    String val = mid[m].split("\\=")[1]; //0/3 3
                    String[]  values = val.split("\\/");
                    for (int k = 0; k < values.length; k++) {
                        if ("RFSF".equals(key)){
                            mstr += "让分"  + getsfsel(values[k]);
                        } else if ("SF".equals(key)){
                            mstr += getsfsel(values[k]);
                        } else if ("SFC".equals(key)){
                            mstr += getsfcsel(values[k]);
                        } else if ("DXF".equals(key)){
                            mstr += getdxfsel(values[k]);
                        }
                    }
                }

                mstr += "),";
            }
            String[]  yhggbs = mcarr[2].split("\\_");

            PassDTO passDTO = new PassDTO();
            passDTO.setStr(mstr.substring(0, mstr.length() - 1));
            passDTO.setBs(yhggbs[1]);

            if (gg.indexOf(yhggbs[0]) != -1) {
            } else {
                gg = gg + yhggbs[0] + ",";
            }
            passDTOList.add(passDTO);
        }
        return gg;
    }

    private static MatchDTO getMatchDTO( String[] ms, String[] minfoarr,Map<String, Map<String, String>> jcDataMap){
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setId(ms[0]);
        matchDTO.setName(minfoarr[0]);
        matchDTO.setHn(minfoarr[1]);
        matchDTO.setGn(minfoarr[3]);
        matchDTO.setHs(minfoarr[4]);
        matchDTO.setGs(minfoarr[5]);

        String close = "" , zclose = "";
        String [] lose =  minfoarr[2].split("\\|");
        if (lose.length == 4) {
            close = lose[1];
            zclose = lose[3];
        }
        matchDTO.setClose(close);
        matchDTO.setZclose(zclose);
        //添加对应资料库相关数据
        Map<String, String> jcData = jcDataMap.get(ms[0]);
        if (null == jcData) {
            matchDTO.setIsForward("0");
        } else {
            matchDTO.setIsForward("1");
            matchDTO.setQc(jcData.get("qc"));
            matchDTO.setSort(jcData.get("sort"));
            matchDTO.setRid(jcData.get("rid"));
            matchDTO.setSid(jcData.get("mid"));
            matchDTO.setLn(jcData.get("ln"));
        }
        return matchDTO;
    }

    private static void setccodes( MatchDTO matchDTO,String spvaluess,String va){
        String []  spvalues = spvaluess.split("\\|", -1);
        String[] touzuarr = va.split("\\+"); //RQSPF=3+SPF=0
        String toustr = "";
        String [] spvalue = null;
        for (int j = 0; j < touzuarr.length; j++){
            int index = 0;
            String wf = touzuarr[j].split("\\=")[0]; //RQSPF  SPF
            String val = touzuarr[j].split("\\=")[1]; //0/3 3

            String[] tarra = val.split("\\/");
            for (int k = 0; k < tarra.length; k++) {
                if ("DXF".equals(wf)) {
                    spvalue = spvalues[3].split(",");
                    index = Integer.valueOf(DXF.get(tarra[k]));
                } else if ("SFC".equals(wf)) {
                    spvalue = spvalues[2].split(",");
                    index = Integer.valueOf(SFC.get(tarra[k]));
                } else if ("RFSF".equals(wf)) {
                    spvalue = spvalues[1].split(",");
                    index = Integer.valueOf(SF.get(tarra[k]));
                } else {
                    spvalue = spvalues[0].split(",");
                    index = Integer.valueOf(SF.get(tarra[k]));
                }
                if ("".equals(toustr)) {
                    toustr = wf + "_" + tarra[k] + "_" + spvalue[index]; //"HH|" +
                } else {
                    toustr += "," + wf + "_" + tarra[k] + "_" + spvalue[index];
                }

            }
        }
        matchDTO.setCcodes(toustr.substring(0, toustr.length()));
    }

    private static void setWinzs(ProjectInfoDTO projectInfoDTO){
        if (projectInfoDTO.getWininfo() != null) {
            String[] winarr = projectInfoDTO.getWininfo().split("\\|");
            if (winarr.length >= 3) {
                // 中奖注数
                projectInfoDTO.setWinzs(winarr[0]);
            }
        }
    }


    public static  String formatCodeByLanCai(String dgCodes){

        Map<String, String> sfMap = new HashMap<String, String>(); // 竞彩篮球-胜负 SF
        Map<String, String> rfsfMap = new HashMap<String, String>(); // 竞彩篮球-让分胜负  RFSF
        Map<String, String> sfcMap = new HashMap<String, String>(); // 竞彩篮球-胜分差  SFC
        Map<String, String> dxfMap = new HashMap<String, String>(); // 竞彩篮球-大小分  DXF

        List<String> keylist = new ArrayList<String>();


        String [] tarr = dgCodes.split(";");
        String [] xz = null;
        String [] iz = null;
        String bs = null;
        for (int i = 0; i < tarr.length; i++) {
            xz = tarr[i].split("\\|");
            iz = xz[1].split(">");
            bs = xz[2].split("\\_")[1];

            if (!keylist.contains(iz[0])) {
                keylist.add(iz[0]);
            }

            if (iz[1].indexOf("RFSF") > -1) {
                String v = rfsfMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    rfsfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    rfsfMap.put(iz[0], iz[1] + "_" + bs);
                }

            } else if (iz[1].indexOf("SFC") > -1) {
                String v = sfcMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    sfcMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    sfcMap.put(iz[0], iz[1] + "_" + bs);
                }

            } else if (iz[1].indexOf("SF") > -1) {
                String v = sfMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    sfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    sfMap.put(iz[0], iz[1] + "_" + bs);
                }

            } else if (iz[1].indexOf("DXF") > -1) {
                String v = dxfMap.get(iz[0]);
                if (!StringUtil.isEmpty(v)) {
                    dxfMap.put(iz[0], v + "/" + iz[1].split("\\=")[1] + "_" + bs);
                } else {
                    dxfMap.put(iz[0], iz[1] + "_" + bs);
                }
            }
        }


        StringBuilder sb = new StringBuilder();

        boolean flag = false;
        for (String key : keylist) {

            if (flag) {
                sb.append(",");
            } else {
                flag = true;
            }

            boolean start = true;

            if (rfsfMap.size() > 0) {
                String value = rfsfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (sfMap.size() > 0) {
                String value = sfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (sfcMap.size() > 0) {
                String value = sfcMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }

            if (dxfMap.size() > 0) {
                String value = dxfMap.get(key);
                if (!StringUtil.isEmpty(value)) {
                    if (start) {
                        sb.append(key).append(">").append(value);
                        start = false;
                    } else {
                        sb.append("+").append(value);
                    }
                }
            }
        }

        return sb.toString();
    }

    private static String isDan(Map<String, String> map,String key){
        return map.get(key) == null? "0":"1";
    }
}

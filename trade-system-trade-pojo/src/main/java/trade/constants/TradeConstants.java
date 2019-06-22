package trade.constants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TradeConstants {
    public static final String ENCODING = "UTF-8";
    public static final String ZH_GID = "01,03,04,06,07,08,09,10,20,50,51,52,53,54,55,56,57,59";//追号彩种



    public static final String jczqHHGid = "70";
    public static final String jczqSPFGid = "72";
    public static final String jczqRQSPFGid = "90";
    public static final String jczqCBFGid = "91";
    public static final String jczqBQCGid = "92";
    public static final String jczqJQSGid = "93";
    public static final String jczqSPF = "spf";
    public static final String jczqRQSPF = "rqspf";
    public static final String jczqCBF = "cbf";
    public static final String jczqBQC = "bqc";
    public static final String jczqJQS = "jqs";
    public static final String[] jczqFs = {jczqRQSPF, jczqCBF, jczqBQC, jczqJQS, jczqSPF};
    public static final String[] jczqGidArr = {jczqRQSPFGid, jczqCBFGid, jczqBQCGid, jczqJQSGid, jczqSPFGid};
    public static final Map<String, String> jczqGid2FsMap = new HashMap<String, String>();
    static {
        jczqGid2FsMap.put(jczqSPFGid, jczqSPF);
        jczqGid2FsMap.put(jczqRQSPFGid, jczqRQSPF);
        jczqGid2FsMap.put(jczqCBFGid, jczqCBF);
        jczqGid2FsMap.put(jczqBQCGid, jczqBQC);
        jczqGid2FsMap.put(jczqJQSGid, jczqJQS);

    }
    public static final Map<String, String> jczqFs2GidMap = new HashMap<String, String>();
    static {
        jczqFs2GidMap.put(jczqSPF, jczqSPFGid);
        jczqFs2GidMap.put(jczqRQSPF, jczqRQSPFGid);
        jczqFs2GidMap.put(jczqCBF, jczqCBFGid);
        jczqFs2GidMap.put(jczqBQC, jczqBQCGid);
        jczqFs2GidMap.put(jczqJQS, jczqJQSGid);
    }
    public static final Map<String, String> jczqFs2NameMap = new HashMap<String, String>();
    static {
        jczqFs2NameMap.put(jczqSPF, "胜平负");
        jczqFs2NameMap.put(jczqRQSPF, "让球");
        jczqFs2NameMap.put(jczqCBF, "比分");
        jczqFs2NameMap.put(jczqBQC, "半全场");
        jczqFs2NameMap.put(jczqJQS, "总进球");
    }
    public static final Map<String, String> jczqGid2NameMap = new HashMap<String, String>();
    static {
        jczqGid2NameMap.put(jczqSPFGid, "胜平负");
        jczqGid2NameMap.put(jczqRQSPFGid, "让球");
        jczqGid2NameMap.put(jczqCBFGid, "比分");
        jczqGid2NameMap.put(jczqBQCGid, "半全场");
        jczqGid2NameMap.put(jczqJQSGid, "总进球");
    }
    public static final String jclqHHGid = "71";
    public static final String jclqSFGid = "94";
    public static final String jclqRFSFGid = "95";
    public static final String jclqSFCGid = "96";
    public static final String jclqDXFGid = "97";
    public static final String jclqSF = "sf";
    public static final String jclqRFSF = "rfsf";
    public static final String jclqSFC = "sfc";
    public static final String jclqDXF = "dxf";
    public static final String[] jclqFs = {jclqSF, jclqRFSF, jclqSFC, jclqDXF};
    public static final String[] jclqGidArr = {jclqSFGid, jclqRFSFGid, jclqSFCGid, jclqDXFGid};
    public static final Map<String, String> jclqFs2NameMap = new HashMap<String, String>();
    static {
        jclqFs2NameMap.put(jclqSF, "胜负");
        jclqFs2NameMap.put(jclqRFSF, "让分");
        jclqFs2NameMap.put(jclqSFC, "胜分差");
        jclqFs2NameMap.put(jclqDXF, "大小分");
    }
    public static final Map<String, String> jclqFs2GidMap = new HashMap<String, String>();
    static {
        jclqFs2GidMap.put(jclqSF, jclqSFGid);
        jclqFs2GidMap.put(jclqRFSF, jclqRFSFGid);
        jclqFs2GidMap.put(jclqSFC, jclqSFCGid);
        jclqFs2GidMap.put(jclqDXF, jclqDXFGid);
    }
    public static Map<String, String> matchnames = new HashMap<String, String>();
    static {
        matchnames.put("84", "sfgg.xml");
        matchnames.put("85", "spf.xml");
        matchnames.put("86", "cbf.xml");
        matchnames.put("87", "bqc.xml");
        matchnames.put("88", "sxp.xml");
        matchnames.put("89", "jqs.xml");
        matchnames.put("70", "jc_hh.xml");
        matchnames.put("90", "jc_hh.xml");
        matchnames.put("91", "jc_hh.xml");
        matchnames.put("92", "jc_hh.xml");
        matchnames.put("93", "jc_hh.xml");
        matchnames.put("72", "jc_hh.xml");
        matchnames.put("71", "basket_hh.xml");
        matchnames.put("94", "basket_hh.xml");
        matchnames.put("95", "basket_hh.xml");
        matchnames.put("96", "basket_hh.xml");
        matchnames.put("97", "basket_hh.xml");
        matchnames.put("98", "gj.xml");
        matchnames.put("99", "gyj.xml");
    }

    public static Map<String, String> JC_SPF = new HashMap<String, String>();
    static {
        JC_SPF.put("3", "0");
        JC_SPF.put("1", "1");
        JC_SPF.put("0", "2");
    }

    public static HashMap<String, String> JC_JQS = new HashMap<String, String>();
    static {
        JC_JQS.put("0", "0");
        JC_JQS.put("1", "1");
        JC_JQS.put("2", "2");
        JC_JQS.put("3", "3");
        JC_JQS.put("4", "4");
        JC_JQS.put("5", "5");
        JC_JQS.put("6", "6");
        JC_JQS.put("7", "7");
    }

    public static Map<String, String> JC_CBF = new HashMap<String, String>();
    static {
        JC_CBF.put("1:0", "0");
        JC_CBF.put("2:0", "1");
        JC_CBF.put("2:1", "2");
        JC_CBF.put("3:0", "3");
        JC_CBF.put("3:1", "4");
        JC_CBF.put("3:2", "5");
        JC_CBF.put("4:0", "6");
        JC_CBF.put("4:1", "7");
        JC_CBF.put("4:2", "8");
        JC_CBF.put("5:0", "9");
        JC_CBF.put("5:1", "10");
        JC_CBF.put("5:2", "11");
        JC_CBF.put("9:0", "12");// 胜其它
        JC_CBF.put("0:0", "13");
        JC_CBF.put("1:1", "14");
        JC_CBF.put("2:2", "15");
        JC_CBF.put("3:3", "16");
        JC_CBF.put("9:9", "17");// 平其它
        JC_CBF.put("0:1", "18");
        JC_CBF.put("0:2", "19");
        JC_CBF.put("1:2", "20");
        JC_CBF.put("0:3", "21");
        JC_CBF.put("1:3", "22");
        JC_CBF.put("2:3", "23");
        JC_CBF.put("0:4", "24");
        JC_CBF.put("1:4", "25");
        JC_CBF.put("2:4", "26");
        JC_CBF.put("0:5", "27");
        JC_CBF.put("1:5", "28");
        JC_CBF.put("2:5", "29");
        JC_CBF.put("0:9", "30");// 负其它
    }

    public static Map<String, String> JC_BQC = new HashMap<String, String>();
    static {
        JC_BQC.put("3-3", "0");
        JC_BQC.put("3-1", "1");
        JC_BQC.put("3-0", "2");
        JC_BQC.put("1-3", "3");
        JC_BQC.put("1-1", "4");
        JC_BQC.put("1-0", "5");
        JC_BQC.put("0-3", "6");
        JC_BQC.put("0-1", "7");
        JC_BQC.put("0-0", "8");
    }

    public static Map<String, String> LC_SFC = new HashMap<String, String>();
    static {
        LC_SFC.put("01", "6");
        LC_SFC.put("02", "7");
        LC_SFC.put("03", "8");
        LC_SFC.put("04", "9");
        LC_SFC.put("05", "10");
        LC_SFC.put("06", "11");
        LC_SFC.put("11", "0");
        LC_SFC.put("12", "1");
        LC_SFC.put("13", "2");
        LC_SFC.put("14", "3");
        LC_SFC.put("15", "4");
        LC_SFC.put("16", "5");
    }

    public static HashMap<String, String> LC_SFC_NAME = new HashMap<String, String>();
    static{
        LC_SFC_NAME.put("01", "主胜 1-5分");
        LC_SFC_NAME.put("02", "主胜 6-10分");
        LC_SFC_NAME.put("03", "主胜 11-15分");
        LC_SFC_NAME.put("04", "主胜 16-20分");
        LC_SFC_NAME.put("05", "主胜 21-25分");
        LC_SFC_NAME.put("06", "主胜 26+分");

        LC_SFC_NAME.put("11", "客胜 1-5分");
        LC_SFC_NAME.put("12", "客胜 6-10分");
        LC_SFC_NAME.put("13", "客胜 11-15分");
        LC_SFC_NAME.put("14", "客胜 16-20分");
        LC_SFC_NAME.put("15", "客胜 21-25分");
        LC_SFC_NAME.put("16", "客胜 26+分");
    }

    public static Map<String, String> LC_SF = new HashMap<String, String>();
    static {
        LC_SF.put("3", "1");
        LC_SF.put("0", "0");
    }

    public static Map<String, String> LC_DXF = new HashMap<String, String>();
    static {
        LC_DXF.put("0", "1");
        LC_DXF.put("3", "0");
    }
    public static final String [] wk ={"日","一","二","三","四","五","六"};
    public static List<String> GIDS_JCZQ = Arrays.asList(new String[] {"70", "72", "90", "91", "92", "93"});
    public static List<String> GIDS_BEIDAN = Arrays.asList(new String[] {"85", "86", "87", "88", "89"});
    public static List<String> GIDS_JCLQ = Arrays.asList(new String[] {"94", "95", "96", "97","71"});
    public static List<String> GIDS_GUANYAJUN = Arrays.asList(new String[] {"99","98"});

    public static HashMap<String,String> gradeDef= new HashMap<String,String>();
    static{
        gradeDef.put("80", "一等奖,二等奖");
        gradeDef.put("81", "一等奖");
        gradeDef.put("82", "一等奖");
        gradeDef.put("83", "一等奖");

        gradeDef.put("01", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖");
        gradeDef.put("03", "直选,组三,组六");
        gradeDef.put("04", "五星奖,三星奖,二星奖,一星奖,大小单双,二星组选,五星通选一等奖,五星通选二等奖,五星通选三等奖");
        gradeDef.put("05", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("06", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("07", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖,七等奖");
        gradeDef.put("08", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("09", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("10", "和值,三同号通选,三同号单选,三不同号,三连号通选,二同号复选,二同号单选,二不同号");
        gradeDef.put("20", "五星奖,四星一等奖,四星二等奖,三星奖,二星奖,一星奖,大小单双,二星组选,五星通选一等奖,五星通选二等奖,五星通选三等奖,任选一,任选二,三星组三,三星组六");
        //0,0,0,0,1,27,0,0,0,0,0,0,0,0,0,0,0
//		gradeDef.put("50", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖,七等奖,八等奖,生肖乐,追加一等奖,追加二等奖,追加三等奖,追加四等奖,追加五等奖,追加六等奖,追加七等奖,,宝钻一等奖,宝钻二等奖,宝钻三等奖,宝钻四等奖");
        gradeDef.put("50", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖,追加一等奖,追加二等奖,追加三等奖,追加四等奖,追加五等奖,追加六等奖,追加七等奖,,宝钻一等奖,宝钻二等奖,宝钻三等奖,宝钻四等奖");
        gradeDef.put("51", "一等奖,二等奖,三等奖,四等奖,五等奖,六等奖");
        gradeDef.put("52", "一等奖");
        gradeDef.put("53", "直选,组三,组六");
        gradeDef.put("54", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("55", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("56", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("57", "前一直选,任选二,任选三,任选四,任选五,任选六,任选七,任选八,前二直选,前三直选,前二组选,前三组选");
        gradeDef.put("58", "任选一,任选二,任选三,任选四,任选五,任选六,同花,同花顺,顺子,豹子,对子,同花包选,同花顺包选,顺子包选,豹子包选,对子包选");
    }

    public static HashMap<String,String> gidPrefix= new HashMap<String,String>();
    static{
        gidPrefix.put("94", "SF");
        gidPrefix.put("95", "RFSF");
        gidPrefix.put("96", "SFC");
        gidPrefix.put("97", "DXF");
    }

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

    public static HashMap<String,HashMap<String,String>> gidSpIndex= new HashMap<String,HashMap<String,String>>();
    static{
        gidSpIndex.put("94", SF);
        gidSpIndex.put("95", SF);
        gidSpIndex.put("96", SFC);
        gidSpIndex.put("97", DXF);
    }

    public static final String []  yhfs = {"中奖优先", "平稳盈利", "奖金优先"};

    public static HashMap<String, String> JQS = new HashMap<String, String>();
    static {
        JQS.put("0", "0");
        JQS.put("1", "1");
        JQS.put("2", "2");
        JQS.put("3", "3");
        JQS.put("4", "4");
        JQS.put("5", "5");
        JQS.put("6", "6");
        JQS.put("7", "7");
    }

    public static HashMap<String, String> BQC = new HashMap<String, String>();
    static {
        BQC.put("3-3", "0");
        BQC.put("3-1", "1");
        BQC.put("3-0", "2");
        BQC.put("1-3", "3");
        BQC.put("1-1", "4");
        BQC.put("1-0", "5");
        BQC.put("0-3", "6");
        BQC.put("0-1", "7");
        BQC.put("0-0", "8");
    }

    public static HashMap<String, String> CBF = new HashMap<String, String>();
    static {

        CBF.put("1:0", "0");
        CBF.put("2:0", "1");
        CBF.put("2:1", "2");
        CBF.put("3:0", "3");
        CBF.put("3:1", "4");
        CBF.put("3:2", "5");
        CBF.put("4:0", "6");
        CBF.put("4:1", "7");
        CBF.put("4:2", "8");
        CBF.put("5:0", "9");
        CBF.put("5:1", "10");
        CBF.put("5:2", "11");
        CBF.put("9:0", "12");//胜其它
        CBF.put("0:0", "13");
        CBF.put("1:1", "14");
        CBF.put("2:2", "15");
        CBF.put("3:3", "16");
        CBF.put("9:9", "17");//平其它
        CBF.put("0:1", "18");
        CBF.put("0:2", "19");
        CBF.put("1:2", "20");
        CBF.put("0:3", "21");
        CBF.put("1:3", "22");
        CBF.put("2:3", "23");
        CBF.put("0:4", "24");
        CBF.put("1:4", "25");
        CBF.put("2:4", "26");
        CBF.put("0:5", "27");
        CBF.put("1:5", "28");
        CBF.put("2:5", "29");
        CBF.put("0:9", "30");//负其它
    }

    public static HashMap<String, String> SPF = new HashMap<String, String>();
    static {
        SPF.put("3", "0");
        SPF.put("1", "1");
        SPF.put("0", "2");
    }

    public static HashMap<String, Integer> DELAY_GID_MAP = new HashMap<String, Integer>();
    static{
        //key彩种id    value开奖延迟时间(秒)
        DELAY_GID_MAP.put("04", 60+20);//老时时彩
        DELAY_GID_MAP.put("54", 20+20);//11选5
        DELAY_GID_MAP.put("55", 45+20);//广东11选5
        DELAY_GID_MAP.put("56", 45+20);//十一运夺金
        DELAY_GID_MAP.put("59", 45+20);//新11选5
        DELAY_GID_MAP.put("20", 80+20);//新时时彩
        DELAY_GID_MAP.put("06", 50+20);//快3
        DELAY_GID_MAP.put("08", 20+20);//福彩快3
        DELAY_GID_MAP.put("57", 100+20);//上海11选5
        DELAY_GID_MAP.put("58", 50);//快乐扑克3
        DELAY_GID_MAP.put("59", 45+20);//新11选5
        DELAY_GID_MAP.put("09", 50);//江苏快3
        DELAY_GID_MAP.put("10", 50);//江苏快3
    }

    public static HashMap<String, String> JC_GID_MAP = new HashMap<String, String>();
    static{
        JC_GID_MAP.put("85", "");
        JC_GID_MAP.put("86", "");
        JC_GID_MAP.put("87", "");
        JC_GID_MAP.put("88", "");
        JC_GID_MAP.put("89", "");
        JC_GID_MAP.put("90", "");
        JC_GID_MAP.put("91", "");
        JC_GID_MAP.put("92", "");
        JC_GID_MAP.put("93", "");
        JC_GID_MAP.put("94", "");
        JC_GID_MAP.put("95", "");
        JC_GID_MAP.put("96", "");
        JC_GID_MAP.put("97", "");
        JC_GID_MAP.put("70", "");
        JC_GID_MAP.put("71", "");
        JC_GID_MAP.put("72", "");
    }

    public static HashMap<String, String> JC_ZQ_MAP = new HashMap<String, String>();
    static{
        JC_ZQ_MAP.put("70", "");
        JC_ZQ_MAP.put("72", "");
        JC_ZQ_MAP.put("90", "");
        JC_ZQ_MAP.put("91", "");
        JC_ZQ_MAP.put("92", "");
        JC_ZQ_MAP.put("93", "");
    }
    
	public final static Set<String> bdGid = new HashSet<>();
	public final static Set<String> jcGid = new HashSet<>();
	public final static Set<String> lcGid = new HashSet<>();
	public static Map<String, String> playid = new HashMap<String,String>();
    public static Map<String, String> JCLQPLAYID = new HashMap<String, String>();
	static{
		
		bdGid.add("84");
		bdGid.add("85");
		bdGid.add("86");
		bdGid.add("87");
		bdGid.add("88");
		bdGid.add("89");
		
		jcGid.add("90");
		jcGid.add("91");
		jcGid.add("92");
		jcGid.add("93");
		jcGid.add("70");
		jcGid.add("72");
		jcGid.add("73");
		
		lcGid.add("94");
		lcGid.add("95");
		lcGid.add("96");
		lcGid.add("97");
		lcGid.add("71");
		lcGid.add("74");

        playid.put("94", "94"); //胜负
        playid.put("95", "95"); //让分胜负
        playid.put("96", "96"); //胜分差
        playid.put("97", "97"); //大小分
        playid.put("71", "71"); //篮彩混合过关
        
        JCLQPLAYID.put("94", "SF"); // 竞彩篮球-胜负
        JCLQPLAYID.put("95", "RFSF"); // 竞彩篮球-让分胜负
        JCLQPLAYID.put("96", "SFC"); //竞彩篮球-胜分差
        JCLQPLAYID.put("97", "DXF"); // 竞彩篮球-大小分
        JCLQPLAYID.put("71", "HH"); // 竞彩篮球-混合过关
    }
	
	public static HashMap<String, String> big = new HashMap<String, String>();
	static{
		//单注大额方案
		big.put("80", "80");
		big.put("81", "81");
		big.put("01", "01");
		big.put("50", "50");
	}
	
	public static Map<String, String> ds_playid = new HashMap<String, String>();
	static {
		ds_playid.put("90", "RQSPF");// 让球胜平负
		ds_playid.put("91", "CBF");// 比分
		ds_playid.put("92", "BQC");// 半全场
		ds_playid.put("93", "JQS");// 总进球数
		ds_playid.put("70", "HH");// 总进球数
		ds_playid.put("72", "SPF");// 胜平负
	}
	
    public static HashMap<Integer, String> noBuySource = new HashMap<Integer, String>();
    static {
        noBuySource.put(1419, "闲鱼彩票2");
        noBuySource.put(1421, "闲鱼彩票4");
        noBuySource.put(1422, "闲鱼彩票5");
        noBuySource.put(1426, "闲鱼彩票8");
        noBuySource.put(1427, "闲鱼彩票9");
    }
	
	public static final String MD5KEY = "9188cp_ios_cast_9188";
}

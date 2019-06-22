package trade.util;

import com.caipiao.game.GameContains;
import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.xml.JXmlWrapper;
import lombok.extern.slf4j.Slf4j;
import trade.bean.TradeBean;
import trade.constants.TradeConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FileUtil {
	
	public  static boolean copyFile(String src, String dest){
		File fileSrc = new File(src);
		if(!fileSrc.exists()){
			return false;
		}
		File destSrc = new File(dest);
		return copyFile(fileSrc,destSrc);
	}
	
	public  static boolean copyFile(File src, File dest){
		    FileInputStream fi = null;
	        FileOutputStream fo = null;
	        FileChannel in = null;
	        FileChannel out = null;
	        try {
	            fi = new FileInputStream(src);
	            fo = new FileOutputStream(dest);
	            in = fi.getChannel();//得到对应的文件通道
	            out = fo.getChannel();//得到对应的文件通道
	            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
	            return true;
	        } catch (IOException e) {
	            log.error("copyFile src:"+src+" dest:"+dest,e);
	            return false;
	        } finally {
	            try {
	                fi.close();
	                in.close();
	                fo.close();
	                out.close();
	            } catch (IOException e) {
                    log.error("copyFile src:"+src+" dest:"+dest,e);
	            }
	        }
	}
	
	/**
	 * 读取过关文件中的投注sp值数据.
	 */
    public Map<String, List<String>> readGuoguanSp(TradeBean bean) {
        StringBuilder path = new StringBuilder();
        path.append(FileConstant.GUOGUAN_DIR);
        path.append(File.separator);
        path.append(bean.getGid());
        path.append(File.separator);
        path.append(bean.getPid());
        path.append(File.separator);
        path.append("proj");
        path.append(File.separator);
        path.append(bean.getHid().toLowerCase());
        path.append(".xml");
        File file = new File(path.toString());
        if (file == null || !file.exists()) {
            return null;
        }
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        List<JXmlWrapper> items = JXmlWrapper.parse(file).getXmlNodeList("item");
        List<String> spvalues = null;
        String matchid = null;
        for (JXmlWrapper item : items) {
            matchid = item.getStringValue("@id");
            spvalues = Arrays.asList(item.getStringValue("@spvalue").split("\\|", -1));
            result.put(matchid, spvalues);
        }
        return result;
    }
	
    private List<JXmlWrapper> readDuizhenFile(String gid, String pid) {
        File duizhen = null;
        JXmlWrapper xml = null;
        if (GameContains.isFootball(gid)) {
            duizhen = new File(FileConstant.JINCAI_DIR, TradeConstants.matchnames.get(gid));
        } else if (GameContains.isBasket(gid)) {
            duizhen = new File(FileConstant.BASKET_DIR, TradeConstants.matchnames.get(gid));
        }
        if (duizhen != null && duizhen.exists()) {
            xml = JXmlWrapper.parse(duizhen);
        }
        List<JXmlWrapper> rows = null;
        if (xml != null) {
            rows = xml.getXmlNodeList("row");
        }
        return rows;
    }
	
    /**
     * 读取最新对阵文件中的sp值(目前仅支付竞彩足球和竞彩篮球2个彩种).
     */
    public Map<String, List<String>> readCurrentSp(TradeBean bean, List<String> matchs) {
        List<JXmlWrapper> rows = readDuizhenFile(bean.getGid(), bean.getPid());
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (String matchid : matchs) {
            for (JXmlWrapper row : rows) {
                if (matchid.equals(row.getStringValue("@itemid"))) {
                    List<String> spList = extractSp(row, bean.getGid());
                    if (spList != null && spList.size() > 0) {
                        result.put(matchid, spList);
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * 读取最新对阵文件中的比赛数据(场次id,场次名,主队名,客队名,让球/让分数)(目前仅支付竞彩足球和竞彩篮球2个彩种).
     */
    public Map<String, List<String>> readMatchData(TradeBean bean, List<String> matchs) {
        List<JXmlWrapper> rows = readDuizhenFile(bean.getGid(), bean.getPid());
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (String matchid : matchs) {
            List<String> data = new ArrayList<String>();
            for (JXmlWrapper row : rows) {
                if (matchid.equals(row.getStringValue("@itemid"))) {
                    data.add(row.getStringValue("@name"));
                    data.add(row.getStringValue("@hn"));
                    data.add(row.getStringValue("@gn"));
                    data.add(row.getStringValue("@close"));
                    break;
                }
            }
            result.put(matchid, data);
        }
        return result;
    }
	
    private List<String> extractSp(JXmlWrapper row, String gid) {
        List<String> spList = new ArrayList<String>();
        String[] fs = null;
        if (GameContains.isFootball(gid)) {
            fs = TradeConstants.jczqFs;
        } else if (GameContains.isBasket(gid)) {
            fs = TradeConstants.jclqFs;
        } else {
            return spList;
        }
        for (String item : fs) {
            spList.add(row.getStringValue("@" + item));
        }
        return spList;
    }
    
    public String getspfsel(String sel){
        return sel.replace("0", "负").replace("1", "平").replace("3", "胜");
    }
    
    public String getcbf(String sel){
        return sel.replace("9:0", "胜其它").replace("9:9", "平其它").replace("0:9", "负其它");
    }
    
    public String get2xuan1(String sel){
        return sel.replace("RQSPF=3", "主不败").replace("RQSPF=0", "客不败").replace("SPF=0", "客胜").replace("SPF=3", "主胜");
    }
    
    /**
     * 根据rowid获取xml文件row节点.
     */
    public static JXmlWrapper getRow(String path, String rowid) {
        JXmlWrapper row = null;
        File file = new File(path);
        if (file != null && file.exists()) {
            JXmlWrapper xml = JXmlWrapper.parse(file);
            List<JXmlWrapper> nodes = xml.getXmlNodeList("row");
            for (JXmlWrapper node : nodes) {
                if (rowid.equals(node.getStringValue("@rowid"))) {
                    row = node;
                    break;
                }
            }
        }
        return row;
    }
    
  /**
   * 屏蔽用户手机号和QQ号码
   */
  	public static String shield(String str){
  		String regEx = "\\d{4}";
  		String regEx1 = "\\d{5}\\d*";

      	Pattern pat = Pattern.compile(regEx);
      	Matcher mat = pat.matcher(str);

      	Pattern pat1= Pattern.compile(regEx1);
      	Matcher mat1 = pat1.matcher(str);
  		
  		if(mat.find() && mat1.find()){
  			str = str.replace(mat1.group(),mat.group()+"**");
  		}
  		
  		return str;
  	}
}

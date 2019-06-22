package com.caiyi.lottery.tradesystem.util.xml;

import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class XmlUtil {
	public final static String XML_HEAD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	public static void append(StringBuilder xml, String key, Object value) {
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
		} else {
			str = String.valueOf(value);
		}
		xml.append(JXmlUtil.encode(key));
		xml.append("=\"");
		xml.append(JXmlUtil.encode(str));
		xml.append("\" ");
	}

	//合买用户记录
	public static String hemaiPerXmlString(JXmlWrapper xml){
		StringBuffer sb = new StringBuffer();

		JXmlWrapper jxml=xml.getXmlNode("count");

		sb.append("<count ");
		sb.append(JXmlUtil.createAttrXml("tp",jxml.getStringValue("@tp")));
		sb.append(JXmlUtil.createAttrXml("rc", jxml.getStringValue("@rc")));
		sb.append(JXmlUtil.createAttrXml("pn", jxml.getStringValue("@pn")));
		sb.append(JXmlUtil.createAttrXml("ps", jxml.getStringValue("@ps")));
		sb.append("/>");

		int count = xml.countXmlNodes("row");
		for(int i = 0; i < count; i++){
			sb.append("<row ").append(JXmlUtil.createAttrXml("nickid", xml.getStringValue("row["+i+"].@nickid")));
			sb.append(JXmlUtil.createAttrXml("bmoney", xml.getStringValue("row["+i+"].@bmoney")));
			sb.append(JXmlUtil.createAttrXml("buydate", xml.getStringValue("row["+i+"].@buydate")));
			sb.append(JXmlUtil.createAttrXml("ic", xml.getStringValue("row["+i+"].@cancel")));
			sb.append(JXmlUtil.createAttrXml("award", xml.getStringValue("row["+i+"].@award")));
			sb.append(JXmlUtil.createAttrXml("rmoney", xml.getStringValue("row["+i+"].@rmoney")));//总奖金
			sb.append(JXmlUtil.createAttrXml("rpmoney", xml.getStringValue("row["+i+"].@irpmoney")));//红包金额
			sb.append(JXmlUtil.createAttrXml("amoney", xml.getStringValue("row["+i+"].@amoney")));//对应奖金
			sb.append("/>");
		}
		return sb.toString();
	}

	//竞彩足球开奖文件格式
	public static String toJczqXmlString(JXmlWrapper xml,String did){
		java.text.DateFormat df=new java.text.SimpleDateFormat("yyMMdd");
		java.text.DateFormat sf2 =new java.text.SimpleDateFormat("yyyy-MM-dd");
		String sfstr = "";
		try {
			sfstr=sf2.format(df.parse(did));
		} catch (ParseException e) {
			log.error("时间格式转换错误,did:"+did,e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<rows ");
		sb.append(JXmlUtil.createAttrXml("did",sfstr));
		sb.append(" >");
		int count = xml.countXmlNodes("row");
		for(int i = 0; i < count; i++){
			sb.append("<row ").append(JXmlUtil.createAttrXml("pid", xml.getStringValue("row["+i+"].@pid")));
			sb.append(JXmlUtil.createAttrXml("mid", xml.getStringValue("row["+i+"].@mid")));
			sb.append(JXmlUtil.createAttrXml("mn", xml.getStringValue("row["+i+"].@mn")));
			sb.append(JXmlUtil.createAttrXml("sn", xml.getStringValue("row["+i+"].@sn")));
			sb.append(JXmlUtil.createAttrXml("ms", xml.getStringValue("row["+i+"].@ms")));
			sb.append(JXmlUtil.createAttrXml("ss", xml.getStringValue("row["+i+"].@ss")));
			sb.append(JXmlUtil.createAttrXml("hms", xml.getStringValue("row["+i+"].@hms")));
			sb.append(JXmlUtil.createAttrXml("hss", xml.getStringValue("row["+i+"].@hss")));
			sb.append(JXmlUtil.createAttrXml("lose", xml.getStringValue("row["+i+"].@lose")));
			sb.append(JXmlUtil.createAttrXml("mname",getMname(xml.getStringValue("row["+i+"].@mname"))));
			sb.append(JXmlUtil.createAttrXml("mt", xml.getStringValue("row["+i+"].@mt")));
			sb.append(JXmlUtil.createAttrXml("cid", xml.getStringValue("row["+i+"].@cid")));
			sb.append("/>");
		}
		sb.append("</rows>");
		return sb.toString();
	}


	//竞彩篮球开奖文件格式
	public static String toJclqXmlString(JXmlWrapper xml,String did){
		java.text.DateFormat df=new java.text.SimpleDateFormat("yyMMdd");
		java.text.DateFormat sf2 =new java.text.SimpleDateFormat("yyyy-MM-dd");
		String sfstr = "";
		try {
			sfstr=sf2.format(df.parse(did));
		} catch (ParseException e) {
			log.error("时间格式转换错误,did:"+did,e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<rows ");
		sb.append(JXmlUtil.createAttrXml("did",sfstr));
		sb.append(" >");
		int count = xml.countXmlNodes("row");
		for(int i = 0; i < count; i++){
			sb.append("<row ").append(JXmlUtil.createAttrXml("pid", xml.getStringValue("row["+i+"].@tid")));
			sb.append(JXmlUtil.createAttrXml("mid", xml.getStringValue("row["+i+"].@mid")));
			sb.append(JXmlUtil.createAttrXml("mn", xml.getStringValue("row["+i+"].@mn")));
			sb.append(JXmlUtil.createAttrXml("sn", xml.getStringValue("row["+i+"].@sn")));
			sb.append(JXmlUtil.createAttrXml("ms", xml.getStringValue("row["+i+"].@ms")));
			sb.append(JXmlUtil.createAttrXml("ss", xml.getStringValue("row["+i+"].@ss")));
			//sb.append(JXmlUtil.createAttrXml("lose", xml.getStringValue("row["+i+"].@lose")));
			sb.append(JXmlUtil.createAttrXml("lose",String.format("%.1f", StringUtil.getNullDouble(xml.getStringValue("row[" + i + "].@lose"))) + ""));
			sb.append(JXmlUtil.createAttrXml("zclose", xml.getStringValue("row["+i+"].@zclose")));
			sb.append(JXmlUtil.createAttrXml("mname",getMname(xml.getStringValue("row["+i+"].@mname"))));
			sb.append(JXmlUtil.createAttrXml("mt", xml.getStringValue("row["+i+"].@mt")));
			sb.append(JXmlUtil.createAttrXml("cid", xml.getStringValue("row["+i+"].@cid")));
			sb.append("/>");
		}
		sb.append("</rows>");
		return sb.toString();
	}

	//北单开奖文件格式
	public static String toBdXmlString(JXmlWrapper xml){
		StringBuffer sb = new StringBuffer();
		sb.append("<rows>");
		int count = xml.countXmlNodes("row");
		for(int i = 0; i < count; i++){
			sb.append("<row ").append(JXmlUtil.createAttrXml("pid", xml.getStringValue("row["+i+"].@expect")));
			sb.append(JXmlUtil.createAttrXml("mid", xml.getStringValue("row["+i+"].@mid")));
			sb.append(JXmlUtil.createAttrXml("mn", xml.getStringValue("row["+i+"].@hn")));
			sb.append(JXmlUtil.createAttrXml("sn", xml.getStringValue("row["+i+"].@gn")));
			sb.append(JXmlUtil.createAttrXml("ms", xml.getStringValue("row["+i+"].@ms")));
			sb.append(JXmlUtil.createAttrXml("ss", xml.getStringValue("row["+i+"].@ss")));
			sb.append(JXmlUtil.createAttrXml("hms", xml.getStringValue("row["+i+"].@hms")));
			sb.append(JXmlUtil.createAttrXml("hss", xml.getStringValue("row["+i+"].@hss")));
			sb.append(JXmlUtil.createAttrXml("lose", xml.getStringValue("row["+i+"].@close")));
			sb.append(JXmlUtil.createAttrXml("mname",xml.getStringValue("row["+i+"].@mname")));
			sb.append(JXmlUtil.createAttrXml("mt", xml.getStringValue("row["+i+"].@bt")));
			sb.append("/>");
		}
		sb.append("</rows>");
		return sb.toString();
	}

	public static String getMname(Pattern p, String mname) {
		Matcher m = p.matcher(mname);
		if(m.lookingAt()) {
			int pos = m.end();
			mname = mname.substring(pos);
		}
		return mname;
	}

	public static String toBdsfggXmlString(JXmlWrapper xml) {
		Pattern p = Pattern.compile("[0-9-0-9]*");
		StringBuilder builder = new StringBuilder();
		builder.append("<rows ");
		builder.append(JXmlUtil.createAttrXml("pid", xml.getStringValue("row[0].@expect")));
		builder.append(">");
		int count = xml.countXmlNodes("row");
		String temp = null;
		for(int i=0;i<count;i++) {
			temp = "row[" + i + "].@";
			builder.append("<row ");
			builder.append(JXmlUtil.createAttrXml("mid", xml.getStringValue(temp + "mid")));
			builder.append(JXmlUtil.createAttrXml("mn", xml.getStringValue(temp + "hn")));
			builder.append(JXmlUtil.createAttrXml("sn", xml.getStringValue(temp + "gn")));
			builder.append(JXmlUtil.createAttrXml("ms", xml.getStringValue(temp + "ms")));
			builder.append(JXmlUtil.createAttrXml("ss", xml.getStringValue(temp + "ss")));
			builder.append(JXmlUtil.createAttrXml("cl", xml.getStringValue(temp + "cl")));
			builder.append(JXmlUtil.createAttrXml("ccup", xml.getStringValue(temp + "ccup")));
			builder.append(JXmlUtil.createAttrXml("lose", xml.getStringValue(temp + "close")));
			builder.append(JXmlUtil.createAttrXml("mname", getMname(p, xml.getStringValue(temp + "mname"))));
			builder.append(JXmlUtil.createAttrXml("mt", xml.getStringValue(temp + "bt")));
			builder.append(JXmlUtil.createAttrXml("iaudit", xml.getStringValue(temp + "iaudit")));
			builder.append(JXmlUtil.createAttrXml("icancel", xml.getStringValue(temp + "icancel")));
			builder.append(JXmlUtil.createAttrXml("istatus", xml.getStringValue(temp + "istatus")));
			builder.append("/>");
		}
		builder.append("</rows>");
		return builder.toString();
	}

	//胜负彩
	public static String toSfcXmlString(JXmlWrapper xml,String gid){
		StringBuffer sb = new StringBuffer();
		sb.append("<rows ");
		String ginfos = xml.getXmlRoot().getAttributeValue("ginfo");
		String[] ginfoarr = ginfos.split(",");
		String ginfo = "";
		if("80".equals(gid)){
			if(ginfoarr.length>2 ){
				if(!"0".equals(ginfoarr[2])){
					ginfo = Integer.parseInt(ginfoarr[0].replace(",", ""))+Integer.parseInt(ginfoarr[2].replace(",", ""))+","+ginfoarr[1];
				}else{
					ginfo = ginfoarr[0]+","+ginfoarr[1];
				}
			}else{
				ginfo = ginfos;
			}
		}else{
			if(ginfoarr.length>1 ){
				if(!"0".equals(ginfoarr[1])){
					ginfo = Integer.parseInt(ginfoarr[0].replace(",", ""))+Integer.parseInt(ginfoarr[1].replace(",", ""))+"";
				}else{
					ginfo = ginfoarr[0];
				}
			}else{
				ginfo = ginfos;
			}
		}


		sb.append(JXmlUtil.createAttrXml("ginfo", ginfo));
		sb.append(JXmlUtil.createAttrXml("ninfo", xml.getXmlRoot().getAttributeValue("ninfo")));
		sb.append(JXmlUtil.createAttrXml("gsale", xml.getXmlRoot().getAttributeValue("gsale")));
		sb.append(JXmlUtil.createAttrXml("gpool", xml.getXmlRoot().getAttributeValue("gpool")));
		sb.append(">");
		int count = xml.countXmlNodes("row");
		for(int i = 0; i < count; i++){
			sb.append("<row ").append(JXmlUtil.createAttrXml("id", xml.getStringValue("row["+i+"].@id")));
			sb.append(JXmlUtil.createAttrXml("hn", xml.getStringValue("row["+i+"].@hn")));
			sb.append(JXmlUtil.createAttrXml("vn", xml.getStringValue("row["+i+"].@vn")));
			String hs=xml.getStringValue("row["+i+"].@hs");
			String vs=xml.getStringValue("row["+i+"].@vs");
			String result=xml.getStringValue("row["+i+"].@result");
			sb.append(JXmlUtil.createAttrXml("hs",StringUtil.isEmpty(hs) == true ? "*":hs));
			sb.append(JXmlUtil.createAttrXml("vs",StringUtil.isEmpty(vs) == true ? "*":vs));
			sb.append(JXmlUtil.createAttrXml("result",StringUtil.isEmpty(result) == true ? "*":result));
			sb.append("/>");
		}
		sb.append("</rows>");
		return sb.toString();
	}

	public static String getMname(String mname){
		return MnameMaps.get(mname) == null ? mname:MnameMaps.get(mname);
	}

	public static HashMap<String, String> MnameMaps = new HashMap<String, String>();
	static
	{
		MnameMaps.put("世界杯","世界杯");
		MnameMaps.put("奥运会男足","奥运男足");
		MnameMaps.put("联合会杯","联合会杯");
		MnameMaps.put("世界U20锦标赛","世青赛");
		MnameMaps.put("女足世界杯","女世界杯");
		MnameMaps.put("奥运会女足","奥运女足");
		MnameMaps.put("国际赛","国际赛");
		MnameMaps.put("世界俱乐部杯","世俱杯");
		MnameMaps.put("俱乐部友谊赛","俱乐部赛");
		MnameMaps.put("世界杯预选赛","世预赛");
		MnameMaps.put("亚洲杯","亚洲杯");
		MnameMaps.put("东亚四强赛","四强赛");
		MnameMaps.put("东南亚锦标赛","东南亚锦");
		MnameMaps.put("亚运会男足","亚运男足");
		MnameMaps.put("亚洲杯预选赛","亚预赛");
		MnameMaps.put("东亚女足四强赛","女四强赛");
		MnameMaps.put("亚洲冠军联赛","亚冠");
		MnameMaps.put("澳大利亚超级联赛","澳超");
		MnameMaps.put("日本职业联赛","日职");
		MnameMaps.put("日本乙级联赛","日乙");
		MnameMaps.put("日本天皇杯","天皇杯");
		MnameMaps.put("日本超级杯","日超杯");
		MnameMaps.put("日本联赛杯","日联赛杯");
		MnameMaps.put("欧洲杯","欧洲杯");
		MnameMaps.put("欧洲U21锦标赛","欧青赛");
		MnameMaps.put("欧洲杯预选赛","欧预赛");
		MnameMaps.put("欧洲U21预选赛","欧青预赛");
		MnameMaps.put("欧洲冠军联赛","欧冠");
		MnameMaps.put("欧罗巴联赛","欧罗巴");
		MnameMaps.put("欧洲超级杯","欧超杯");
		MnameMaps.put("英格兰超级联赛","英超");
		MnameMaps.put("英格兰冠军联赛","英冠");
		MnameMaps.put("英格兰甲级联赛","英甲");
		MnameMaps.put("英格兰乙级联赛","英乙");
		MnameMaps.put("英格兰足总杯","英足总杯");
		MnameMaps.put("英格兰社区盾杯","社区盾杯");
		MnameMaps.put("英格兰联赛杯","英联赛杯");
		MnameMaps.put("英格兰锦标赛","英锦标赛");
		MnameMaps.put("德国甲级联赛","德甲");
		MnameMaps.put("德国乙级联赛","德乙");
		MnameMaps.put("德国杯","德国杯");
		MnameMaps.put("德国超级杯","德超杯");
		MnameMaps.put("意大利甲级联赛","意甲");
		MnameMaps.put("意大利杯","意大利杯");
		MnameMaps.put("意大利超级杯","意超杯");
		MnameMaps.put("西班牙甲级联赛","西甲");
		MnameMaps.put("西班牙乙级联赛","西乙");
		MnameMaps.put("西班牙国王杯","国王杯");
		MnameMaps.put("西班牙超级杯","西超杯");
		MnameMaps.put("法国甲级联赛","法甲");
		MnameMaps.put("法国乙级联赛","法乙");
		MnameMaps.put("法国杯","法国杯");
		MnameMaps.put("法国超级杯","法超杯");
		MnameMaps.put("法国联赛杯","法联赛杯");
		MnameMaps.put("荷兰甲级联赛","荷甲");
		MnameMaps.put("荷兰乙级联赛","荷乙");
		MnameMaps.put("荷兰杯","荷兰杯");
		MnameMaps.put("荷兰超级杯","荷超杯");
		MnameMaps.put("葡萄牙超级联赛","葡超");
		MnameMaps.put("葡萄牙杯","葡萄牙杯");
		MnameMaps.put("葡萄牙超级杯","葡超杯");
		MnameMaps.put("葡萄牙联赛杯","葡联赛杯");
		MnameMaps.put("苏格兰超级联赛","苏超");
		MnameMaps.put("苏格兰足总杯","苏足总杯");
		MnameMaps.put("苏格兰联赛杯","苏联赛杯");
		MnameMaps.put("瑞典超级联赛","瑞超");
		MnameMaps.put("瑞典杯","瑞典杯");
		MnameMaps.put("瑞典超级杯","瑞超杯");
		MnameMaps.put("挪威超级联赛","挪超");
		MnameMaps.put("挪威杯","挪威杯");
		MnameMaps.put("挪威超级杯","挪超杯");
		MnameMaps.put("非洲杯","非洲杯");
		MnameMaps.put("美洲杯","美洲杯");
		MnameMaps.put("南美解放者杯","解放者杯");
		MnameMaps.put("南美俱乐部杯","俱乐部杯");
		MnameMaps.put("南美优胜者杯","优胜者杯");
		MnameMaps.put("巴西甲级联赛","巴甲");
		MnameMaps.put("巴西杯","巴西杯");
		MnameMaps.put("阿根廷甲级联赛","阿甲");
		MnameMaps.put("阿根廷杯","阿根廷杯");
		MnameMaps.put("中北美金杯赛","金杯赛");
		MnameMaps.put("中北美冠军联赛","中北美冠");
		MnameMaps.put("美国职业大联盟","美职联");
		MnameMaps.put("美国公开赛杯","公开赛杯");
	}

	//三级菜单格式
	public static String toDeXmlString(JXmlWrapper xml){
		String gid=xml.getXmlRoot().getAttributeValue("gid");

		String pid=xml.getXmlRoot().getAttributeValue("pid");
		StringBuffer sb = new StringBuffer();
		sb.append("<rows ");
		sb.append(JXmlUtil.createAttrXml("gid", gid));
		sb.append(JXmlUtil.createAttrXml("pid", pid));
		String code = xml.getXmlRoot().getAttributeValue("code");
		if ("01".equals(gid) && Integer.valueOf(pid) > 2014143 && !StringUtil.isEmpty(code)) {
			code = code.substring(0, 20);
		}
		sb.append(JXmlUtil.createAttrXml("acode", code));
		sb.append(JXmlUtil.createAttrXml("code", code));
		sb.append(JXmlUtil.createAttrXml("gsale", xml.getXmlRoot().getAttributeValue("gsale")));

		if ("50".equals(gid)) {//大乐透
			StringBuffer gsb=new StringBuffer();
			StringBuffer nsb=new StringBuffer();
			String ginfo[]=xml.getXmlRoot().getAttributeValue("ginfo").split(",");
			String ninfo[]=xml.getXmlRoot().getAttributeValue("ninfo").split(",");

			if (new Integer(pid) > 2014051) {  //大乐透新规则
				//兼容老版更新新版
				for (int i = 0; i < ginfo.length; i++) {
					if (i==5) {//六等奖
						gsb.append(ginfo[i]).append(",");
						gsb.append("--").append(",");//七等奖
						gsb.append("--").append(",");//八等奖
						//gsb.append("0").append(",");//生肖乐
					}else {
						if (i==ginfo.length-1) {
							gsb.append(ginfo[i]);
							//gsb.append("--").append(",");//追加六等奖
							gsb.append(",").append("--");//追加七等奖
						}else {
							gsb.append(ginfo[i]).append(",");
						}
					}
				}

				for (int i = 0; i < ninfo.length; i++) {
					if (i==5) {//六等奖
						nsb.append(ninfo[i]).append(",");
						nsb.append("--").append(",");//七等奖
						nsb.append("--").append(",");//八等奖
						//nsb.append("0").append(",");//生肖乐
					}else{
						if (i==ninfo.length-1) {
							nsb.append(ninfo[i]);
							//nsb.append("--").append(",");//追加六等奖
							nsb.append(",").append("--");//追加七等奖
						}else {
							nsb.append(ninfo[i]).append(",");
						}
					}
				}
			}else {
				for (int i = 0; i < ginfo.length; i++) {
					if (i==8) {
						continue;
					}else {
						if (i==ginfo.length-1) {
							gsb.append(ginfo[i]);
						}else {
							gsb.append(ginfo[i]).append(",");
						}
					}
				}

				for (int i = 0; i < ninfo.length; i++) {
					if (i==8) {
						continue;
					}else {
						if (i==ninfo.length-1) {
							nsb.append(ninfo[i]);
						}else {
							nsb.append(ninfo[i]).append(",");
						}
					}
				}
			}

			sb.append(JXmlUtil.createAttrXml("ginfo", gsb.toString()));
			sb.append(JXmlUtil.createAttrXml("ninfo", nsb.toString()));
		}else {
			String ginfo = xml.getXmlRoot().getAttributeValue("ginfo");
			String ninfo = xml.getXmlRoot().getAttributeValue("ninfo");
			if ("01".equals(gid) && Integer.valueOf(pid) > 2014143 && !StringUtil.isEmpty(ginfo)) { //双色球2014144期增加幸运篮球后
				String [] infoarr = ginfo.split(",");
				if (infoarr.length == 7) {
					ginfo =  ginfo.substring(0, ginfo.lastIndexOf(","));
					ninfo =  ninfo.substring(0, ninfo.lastIndexOf(","));
				}
			}
			sb.append(JXmlUtil.createAttrXml("ginfo", ginfo));
			sb.append(JXmlUtil.createAttrXml("ninfo", ninfo));
		}

		sb.append(JXmlUtil.createAttrXml("gpool", xml.getXmlRoot().getAttributeValue("gpool")));
		sb.append(JXmlUtil.createAttrXml("atime", xml.getXmlRoot().getAttributeValue("atime")));
		if ("03".equals(gid)) { //福彩3D增加试机号
			sb.append(JXmlUtil.createAttrXml("trycode", xml.getXmlRoot().getAttributeValue("trycode")));//试机号
		}
		sb.append(">");
		sb.append("</rows>");
		return sb.toString();
	}


	public static String toZHXmlString(JXmlWrapper xml){
		StringBuffer sb = new StringBuffer();
		int count = xml.countXmlNodes("row");
		for(int i = 0; i < count; i++){
			sb.append("<row ").append(JXmlUtil.createAttrXml("gid", xml.getStringValue("row["+i+"].@gid")));
			sb.append(JXmlUtil.createAttrXml("zhid", xml.getStringValue("row["+i+"].@zhid")));//追号id
			sb.append(JXmlUtil.createAttrXml("pnums", xml.getStringValue("row["+i+"].@pnums")));//总期数
			sb.append(JXmlUtil.createAttrXml("finish", xml.getStringValue("row["+i+"].@finish")));//是否完成 0 否 1是
			sb.append(JXmlUtil.createAttrXml("tmoney", xml.getStringValue("row["+i+"].@tmoney")));//总金额
			sb.append(JXmlUtil.createAttrXml("adddate", xml.getStringValue("row["+i+"].@adddate")));//发起时间
			sb.append(JXmlUtil.createAttrXml("success", xml.getStringValue("row["+i+"].@success")));//完成期次
			sb.append(JXmlUtil.createAttrXml("bonus", xml.getStringValue("row["+i+"].@bonus")));//中奖金额
			sb.append(JXmlUtil.createAttrXml("casts", xml.getStringValue("row["+i+"].@casts")));//已投注金额
			sb.append(JXmlUtil.createAttrXml("reason",xml.getStringValue("row["+i+"].@reason")));//状态
			sb.append(JXmlUtil.createAttrXml("failure",xml.getStringValue("row["+i+"].@failure")));//取消追号期数
			sb.append(JXmlUtil.createAttrXml("zhflag",xml.getStringValue("row["+i+"].@zhflag")));//是否中奖停止
			sb.append(JXmlUtil.createAttrXml("zhtype",xml.getStringValue("row["+i+"].@zhtype")));//追号类型
			sb.append("/>");
		}
		return sb.toString();
	}


	public static String  getBiztype(String biztype){
		if (Integer.valueOf(biztype) >= 200) {
			return INM.get(biztype);
		} else {
			return OUTM.get(biztype);
		}
	}

	public static String getLotname(String gid){
		return LOT.get(gid);
	}

	// 交易类型定义
	public static HashMap<String, String> INM = new HashMap<String, String>();
	static {
		INM.put("200", "用户充值");
		INM.put("201", "自购中奖");
		INM.put("202", "跟单中奖");
		INM.put("203", "中奖提成");
		INM.put("204", "追号中奖");
		INM.put("210", "自购撤单返款");
		INM.put("211", "认购撤单返款");
		INM.put("212", "追号撤销返款");
		INM.put("213", "提现撤销返款");
		INM.put("214", "提款失败转款");
		INM.put("215", "保底返款");
		INM.put("216", "红包派送");
		INM.put("300", "转款");
		INM.put("250", "红包过期返款");
		INM.put("252", "撤单红包过期返款");
		INM.put("251", "后台红包退款");
		INM.put("253", "撤单红包过期返款");
		INM.put("254", "撤单红包过期返款");
		INM.put("256", "跟买中奖");
		INM.put("257", "中奖打赏");

		INM.put("219", "多期机选撤销返款");
		INM.put("302", "补派奖金");
		INM.put("303", "补派奖金");
		INM.put("304", "网站赔偿");
	}

	public static HashMap<String, String> OUTM = new HashMap<String, String>();
	static {
		OUTM.put("100", "自购");
		OUTM.put("101", "认购");
		OUTM.put("102", "追号");
		OUTM.put("103", "保底认购");
		OUTM.put("104", "提现");
		OUTM.put("105", "保底冻结");
		OUTM.put("99", "转账");
		OUTM.put("98", "套餐追号");
		OUTM.put("90", "送红包");
		OUTM.put("113", "中奖打赏");
	}

	// 充值类型
	public static HashMap<String, String> CHONGZHI = new HashMap<String, String>();
	static {
		CHONGZHI.put("9000", "支付宝");
		CHONGZHI.put("9001", "支付宝");
		CHONGZHI.put("9002", "支付宝");
		CHONGZHI.put("9005", "支付宝");
		CHONGZHI.put("2017", "支付宝");
		CHONGZHI.put("2001", "支付宝");
		CHONGZHI.put("3014", "支付宝");
		CHONGZHI.put("2000", "支付宝");
		CHONGZHI.put("9014", "支付宝");
		CHONGZHI.put("2005", "支付宝");
		CHONGZHI.put("9017", "支付宝");
		CHONGZHI.put("2002", "支付宝");
		CHONGZHI.put("10000", "支付宝");
		CHONGZHI.put("10001", "支付宝");
		CHONGZHI.put("9018", "支付宝");
		CHONGZHI.put("9019", "支付宝");

		CHONGZHI.put("4000", "盛付通(借记卡)");
		CHONGZHI.put("4001", "盛付通(信用卡)");
		CHONGZHI.put("4002", "盛付通(借记卡)");
		CHONGZHI.put("4003", "盛付通(信用卡)");
		CHONGZHI.put("4004", "盛付通(借记卡)");
		CHONGZHI.put("4005", "盛付通(信用卡)");
		CHONGZHI.put("4006", "盛付通(借记卡)");
		CHONGZHI.put("4007", "盛付通(信用卡)");

		CHONGZHI.put("2015", "银联电话充值");
		CHONGZHI.put("9016", "银联电话充值");
		CHONGZHI.put("9006", "银联电话充值");
		CHONGZHI.put("9007", "银联电话充值");

		CHONGZHI.put("9008", "手机充值卡");
		CHONGZHI.put("9009", "手机充值卡");
		CHONGZHI.put("2016", "手机充值卡");

		CHONGZHI.put("9114", "联动优势(借记卡)");
		CHONGZHI.put("9115", "联动优势(借记卡)");
		CHONGZHI.put("9113", "联动优势(借记卡)");
		CHONGZHI.put("9004", "联动优势(信用卡)");
		CHONGZHI.put("9003", "联动优势(信用卡)");
		CHONGZHI.put("9015", "联动优势(信用卡)");

		CHONGZHI.put("2052", "连连支付(借记卡)");
		CHONGZHI.put("2053", "连连支付(信用卡)");
		CHONGZHI.put("2054", "连连支付(借记卡)");
		CHONGZHI.put("2055", "连连支付(信用卡)");
		CHONGZHI.put("2056", "连连支付(借记卡)");
		CHONGZHI.put("2057", "连连支付(信用卡)");

		CHONGZHI.put("3000", "微信支付");

		CHONGZHI.put("13", "上海导购预付卡");
		CHONGZHI.put("19", "建行直联");
		CHONGZHI.put("18", "兴业直联");
		CHONGZHI.put("20", "兴业直联触屏");
		CHONGZHI.put("99", "手工充值");
		CHONGZHI.put("97", "提款失败退款");
		CHONGZHI.put("98", "网站优惠");
		CHONGZHI.put("17", "杉德支付");

		CHONGZHI.put("22","ApplePay(借记卡)");
		CHONGZHI.put("23","ApplePay(信用卡)");
		CHONGZHI.put("24","支付宝转账");
		CHONGZHI.put("25","支付宝转账");
		CHONGZHI.put("27","支付宝转账");
		CHONGZHI.put("28","支付宝转账");

		CHONGZHI.put("6000","银联支付(借记卡)");
		CHONGZHI.put("6001","银联支付(信用卡)");
		CHONGZHI.put("6002","银联支付(借记卡)");
		CHONGZHI.put("6003","银联支付(信用卡)");
		CHONGZHI.put("6004","银联支付(借记卡)");
		CHONGZHI.put("6005","银联支付(信用卡)");
		CHONGZHI.put("6006","银联支付(借记卡)");
		CHONGZHI.put("6007","银联支付(信用卡)");

		CHONGZHI.put("7000","易宝支付(借记卡)");
		CHONGZHI.put("7001","易宝支付(信用卡)");
		CHONGZHI.put("7002","易宝支付(借记卡)");
		CHONGZHI.put("7003","易宝支付(信用卡)");
		CHONGZHI.put("7004","易宝支付(借记卡)");
		CHONGZHI.put("7005","易宝支付(信用卡)");
		CHONGZHI.put("7006","易宝支付(借记卡)");
		CHONGZHI.put("7007","易宝支付(信用卡)");
		CHONGZHI.put("7010","智慧支付(借记卡)");
		CHONGZHI.put("7011","智慧支付(信用卡)");
		CHONGZHI.put("7012","智慧支付(借记卡)");
		CHONGZHI.put("7013","智慧支付(信用卡)");
		CHONGZHI.put("7014","智慧支付(借记卡)");
		CHONGZHI.put("7015","智慧支付(信用卡)");
		CHONGZHI.put("7016","智慧支付(借记卡)");
		CHONGZHI.put("7017","智慧支付(信用卡)");

		CHONGZHI.put("6008","银联快捷支付");
		CHONGZHI.put("6009","银联快捷支付");
		CHONGZHI.put("6010","银联快捷支付");

		//梓微信
		CHONGZHI.put("30","微信支付");
		CHONGZHI.put("31","微信支付");
		CHONGZHI.put("32","微信支付");
		//中信微信
		CHONGZHI.put("50","微信支付");
		CHONGZHI.put("51","微信支付");
		//飞客支付宝
		CHONGZHI.put("60","支付宝(苹果)");
		CHONGZHI.put("61","支付宝(安卓)");
		CHONGZHI.put("62","支付宝(H5)");
		//盛付通微信
		CHONGZHI.put("40","微信支付");
		CHONGZHI.put("41","微信支付");
		CHONGZHI.put("42","微信支付");
		CHONGZHI.put("44","支付宝");
		CHONGZHI.put("45","支付宝");
		CHONGZHI.put("46","支付宝");
		CHONGZHI.put("47","支付宝");
		CHONGZHI.put("48","微信扫码支付");
		CHONGZHI.put("49","微信扫码支付");
		//现在支付宝支付
		CHONGZHI.put("70","支付宝");
		CHONGZHI.put("71","支付宝");
		CHONGZHI.put("72","支付宝");
		//威富通支付宝支付
		CHONGZHI.put("80","支付宝");
		CHONGZHI.put("81","支付宝");
		CHONGZHI.put("82","支付宝");
		//威富通微信支付
		CHONGZHI.put("83","微信支付");
		CHONGZHI.put("84","微信支付");
		CHONGZHI.put("85","微信支付");
		CHONGZHI.put("122","微信支付");
		CHONGZHI.put("123","微信支付");
		CHONGZHI.put("124","微信支付");

		//威富通微信支付
		CHONGZHI.put("86","微信支付");
		CHONGZHI.put("87","微信支付");
		CHONGZHI.put("88","微信支付");

		//贝付宝微信充值
		CHONGZHI.put("104","微信支付");
		CHONGZHI.put("105","微信支付");
		CHONGZHI.put("106","微信支付");
		CHONGZHI.put("113","微信支付");
		CHONGZHI.put("114","微信支付");
		CHONGZHI.put("115","微信支付");
		CHONGZHI.put("116","微信支付");
		CHONGZHI.put("117","微信支付");
		CHONGZHI.put("118","微信支付");
		CHONGZHI.put("119","微信支付");

		//京东支付
		CHONGZHI.put("5000","京东支付");
		CHONGZHI.put("5001","京东支付");
		CHONGZHI.put("5002","京东支付");
		CHONGZHI.put("5003","京东支付");
		CHONGZHI.put("5004","京东支付");
		CHONGZHI.put("5005","京东支付");
		CHONGZHI.put("5006","京东支付");
		CHONGZHI.put("5007","京东支付");
		CHONGZHI.put("5008","京东支付");

		////贝付宝支付宝
		CHONGZHI.put("101","支付宝");
		CHONGZHI.put("102","支付宝");
		CHONGZHI.put("103","支付宝");
		CHONGZHI.put("107","支付宝");
		CHONGZHI.put("108","支付宝");
		CHONGZHI.put("109","支付宝");
		CHONGZHI.put("110","支付宝");
		CHONGZHI.put("111","支付宝");
		CHONGZHI.put("112","支付宝");

		//各银行代理微信，支付宝
		CHONGZHI.put("120","微信支付");
		CHONGZHI.put("121","微信支付");
		CHONGZHI.put("130","微信支付");
		CHONGZHI.put("131","微信支付");
		CHONGZHI.put("132","微信支付");
		CHONGZHI.put("140","微信支付");
		CHONGZHI.put("141","微信支付");
		CHONGZHI.put("146","微信支付");
		CHONGZHI.put("147","微信支付");
		CHONGZHI.put("148","微信支付");
		CHONGZHI.put("153","微信支付");
		CHONGZHI.put("154","微信支付");
		CHONGZHI.put("156","微信支付");
		CHONGZHI.put("157","微信扫码支付");
		CHONGZHI.put("158","微信扫码支付");
		CHONGZHI.put("163","微信支付");
		CHONGZHI.put("164","微信支付");
		CHONGZHI.put("166","微信支付");
		CHONGZHI.put("167","微信支付");
		CHONGZHI.put("168","微信支付");
		CHONGZHI.put("169","微信支付");
		CHONGZHI.put("173","微信支付");
		CHONGZHI.put("174","微信支付");
		CHONGZHI.put("176","微信支付");
		CHONGZHI.put("183","微信支付");
		CHONGZHI.put("184","微信支付");
		CHONGZHI.put("186","微信支付");
		CHONGZHI.put("190","微信支付");
		CHONGZHI.put("191","微信支付");
		CHONGZHI.put("192","微信支付");
		CHONGZHI.put("193","微信支付");
		CHONGZHI.put("194","微信支付");
		CHONGZHI.put("195","微信支付");
		CHONGZHI.put("196","微信支付");
		CHONGZHI.put("197","微信支付");
		CHONGZHI.put("198","微信支付");
		CHONGZHI.put("213","微信支付");
		CHONGZHI.put("214","微信支付");
		CHONGZHI.put("133","支付宝");
		CHONGZHI.put("134","支付宝");
		CHONGZHI.put("135","支付宝");
		CHONGZHI.put("136","支付宝");
		CHONGZHI.put("142","支付宝");
		CHONGZHI.put("143","支付宝");
		CHONGZHI.put("144","支付宝");
		CHONGZHI.put("145","支付宝");
		CHONGZHI.put("150","支付宝");
		CHONGZHI.put("151","支付宝");
		CHONGZHI.put("152","支付宝");
		CHONGZHI.put("155","支付宝");
		CHONGZHI.put("160","支付宝");
		CHONGZHI.put("161","支付宝");
		CHONGZHI.put("162","支付宝");
		CHONGZHI.put("165","支付宝");
		CHONGZHI.put("170","支付宝");
		CHONGZHI.put("171","支付宝");
		CHONGZHI.put("172","支付宝");
		CHONGZHI.put("175","支付宝");
		CHONGZHI.put("180","支付宝");
		CHONGZHI.put("181","支付宝");
		CHONGZHI.put("182","支付宝");
		CHONGZHI.put("185","支付宝");
		CHONGZHI.put("200","支付宝");
		CHONGZHI.put("201","支付宝");
		CHONGZHI.put("202","支付宝");
		CHONGZHI.put("220","支付宝");
		CHONGZHI.put("221","支付宝");
		CHONGZHI.put("222","支付宝");

		CHONGZHI.put("255", "支付宝");
		CHONGZHI.put("256", "支付宝");
		CHONGZHI.put("257", "支付宝");
		CHONGZHI.put("253", "微信支付");
		CHONGZHI.put("254", "微信支付");

		CHONGZHI.put("260", "支付宝");
		CHONGZHI.put("261", "支付宝");
		CHONGZHI.put("262", "支付宝");
		CHONGZHI.put("263", "微信支付");
		CHONGZHI.put("264", "微信支付");
		CHONGZHI.put("265", "微信支付");

		CHONGZHI.put("8000","借记卡快捷(联动优势)");
		CHONGZHI.put("8001","借记卡快捷(联动优势)");
		CHONGZHI.put("8002","借记卡快捷(联动优势)");
		CHONGZHI.put("8003","信用卡快捷(联动优势)");
		CHONGZHI.put("8004","信用卡快捷(联动优势)");
		CHONGZHI.put("8005","信用卡快捷(联动优势)");

		//QQ钱包
		CHONGZHI.put("230","QQ钱包");
		CHONGZHI.put("231","QQ钱包");
		CHONGZHI.put("232","QQ钱包");
		CHONGZHI.put("240","QQ钱包");
		CHONGZHI.put("241","QQ钱包");
		CHONGZHI.put("242","QQ钱包");
		CHONGZHI.put("243","QQ钱包");
		CHONGZHI.put("244","QQ钱包");
		CHONGZHI.put("245","QQ钱包");

		//派洛贝微信支付
		CHONGZHI.put("273","微信支付");
		CHONGZHI.put("274","微信支付");
		CHONGZHI.put("275","微信支付");

		CHONGZHI.put("276","QQ钱包");
		CHONGZHI.put("277","QQ钱包");
		CHONGZHI.put("278","QQ钱包");
		//派洛贝支付宝
		CHONGZHI.put("270","支付宝");
		CHONGZHI.put("271","支付宝");
		CHONGZHI.put("272","支付宝");
		//微众支付宝
		CHONGZHI.put("300","支付宝");
		CHONGZHI.put("301","支付宝");
		CHONGZHI.put("302","支付宝");
		//温州平安支付宝
		CHONGZHI.put("280","支付宝");
		CHONGZHI.put("281","支付宝");
		CHONGZHI.put("282","支付宝");

		CHONGZHI.put("290","支付宝");
		CHONGZHI.put("291","支付宝");
		CHONGZHI.put("292","支付宝");

		CHONGZHI.put("330","支付宝");
		CHONGZHI.put("331","支付宝");
		CHONGZHI.put("332","支付宝");

		CHONGZHI.put("236","微信支付");
		CHONGZHI.put("237","微信支付");
		CHONGZHI.put("238","微信支付");

		CHONGZHI.put("333","微信支付");
		CHONGZHI.put("334","微信支付");
		CHONGZHI.put("335","微信支付");

		CHONGZHI.put("350","微信支付");
		CHONGZHI.put("351","微信支付");
		CHONGZHI.put("352","微信支付");
		CHONGZHI.put("353","微信支付");
		CHONGZHI.put("354","微信支付");
		CHONGZHI.put("355","微信支付");

		CHONGZHI.put("233","支付宝");
		CHONGZHI.put("234","支付宝");
		CHONGZHI.put("235","支付宝");

		CHONGZHI.put("5020","借记卡快捷(合利宝)");
		CHONGZHI.put("5021","借记卡快捷(合利宝)");
		CHONGZHI.put("5022","借记卡快捷(合利宝)");
		CHONGZHI.put("5023","借记卡快捷(合利宝)");
		CHONGZHI.put("5024","借记卡快捷(合利宝)");
		CHONGZHI.put("5025","借记卡快捷(合利宝)");

	}
	// 提款状态
	public static HashMap<String, String> TIKUAN = new HashMap<String, String>();
	static {
		TIKUAN.put("0", "处理中");
		TIKUAN.put("1", "提款成功");
		TIKUAN.put("2", "提款失败");
		TIKUAN.put("3", "银行处理失败");
		TIKUAN.put("4", "处理中"); //支付宝提款中
		TIKUAN.put("5", "提款成功"); //支付宝提款成功
		TIKUAN.put("6", "提款失败"); //支付宝提款失败
		TIKUAN.put("7", "处理中"); //手动批付中
		TIKUAN.put("8", "提款成功"); //手动批付成功
		TIKUAN.put("11", "处理中"); //银行卡批付中
		TIKUAN.put("12", "提款成功"); //银行卡批付成功
		TIKUAN.put("13", "提款失败"); //银行卡批付失败
	}

	public static HashMap<String, String> LOT = new HashMap<String, String>();
	static {

		LOT.put("01", "双色球");
		LOT.put("03", "福彩3D");
		LOT.put("04", "时时彩");
		LOT.put("05", "新快3");
		LOT.put("06", "快3");
		LOT.put("07", "七乐彩");
		LOT.put("08", "内蒙快3");
		LOT.put("09", "江苏快3");
		LOT.put("10", "江西快3");
		LOT.put("20", "新时时彩");

		LOT.put("50", "超级大乐透");
		LOT.put("51", "七星彩");
		LOT.put("52", "排列五");
		LOT.put("53", "排列三");
		LOT.put("54", "11选5");
		LOT.put("55", "广东11选5");
		LOT.put("56", "11运夺金");
		LOT.put("57", "上海11选5");
		LOT.put("58", "快乐扑克3");
		LOT.put("59", "新11选5");

		LOT.put("80", "胜负彩");
		LOT.put("81", "任选九");
		LOT.put("82", "进球彩");
		LOT.put("83", "半全场");

		LOT.put("84", "北单胜负过关");
		LOT.put("85", "足球单场-让球胜平负");
		LOT.put("86", "足球单场-比分");
		LOT.put("87", "足球单场-半全场");
		LOT.put("88", "足球单场-上下单双");
		LOT.put("89", "足球单场-总进球数");

		LOT.put("90", "竞彩足球-让球胜平负");
		LOT.put("91", "竞彩足球-比分");
		LOT.put("92", "竞彩足球-半全场");
		LOT.put("93", "竞彩足球-总进球数");
		LOT.put("70", "竞彩足球-混合过关");
		LOT.put("72", "竞彩足球-胜平负");

		LOT.put("94", "竞彩篮球-胜负");
		LOT.put("95", "竞彩篮球-让分胜负");
		LOT.put("96", "竞彩篮球-胜分差");
		LOT.put("97", "竞彩篮球-大小分");
		LOT.put("71", "竞彩篮球-混合过关");
		LOT.put("98", "冠军竞猜");
		LOT.put("99", "冠亚军竞猜");
	}

	public static HashMap<String, String> HUODONGJIAJIAN = new HashMap<String, String>();
	static {
		HUODONGJIAJIAN.put("80001", "胜负猜中12场活动奖金");
		HUODONGJIAJIAN.put("81001", "任九猜中8场活动奖金");
		HUODONGJIAJIAN.put("54001", "11选5擂台赛活动奖金");
		HUODONGJIAJIAN.put("56001", "十一运夺金擂台赛活动奖金");
		HUODONGJIAJIAN.put("04001", "时时彩擂台赛活动奖金");
		HUODONGJIAJIAN.put("20001", "新时时彩擂台赛活动奖金");
		HUODONGJIAJIAN.put("06001", "快3擂台赛活动奖金");
	}

	public static HashMap<String, String> CATEGORY = new HashMap<String, String>();
	static {
		CATEGORY.put("01", "双色球");
		CATEGORY.put("03", "福彩3D");
		CATEGORY.put("04", "时时彩");
		CATEGORY.put("05", "新快3");
		CATEGORY.put("06", "快3");
		CATEGORY.put("07", "七乐彩");
		CATEGORY.put("08", "内蒙快3");
		CATEGORY.put("09", "江苏快3");
		CATEGORY.put("10", "江西快3");
		CATEGORY.put("20", "新时时彩");

		CATEGORY.put("50", "超级大乐透");
		CATEGORY.put("51", "七星彩");
		CATEGORY.put("52", "排列五");
		CATEGORY.put("53", "排列三");
		CATEGORY.put("54", "11选5");
		CATEGORY.put("55", "广东11选5");
		CATEGORY.put("56", "11运夺金");
		CATEGORY.put("57", "上海11选5");
		CATEGORY.put("58", "快乐扑克3");
		CATEGORY.put("59", "新11选5");

		CATEGORY.put("80", "足彩");
		CATEGORY.put("81", "足彩");
		CATEGORY.put("82", "足彩");
		CATEGORY.put("83", "足彩");

		CATEGORY.put("84", "北京单场");
		CATEGORY.put("85", "北京单场");
		CATEGORY.put("86", "北京单场");
		CATEGORY.put("87", "北京单场");
		CATEGORY.put("88", "北京单场");
		CATEGORY.put("89", "北京单场");

		CATEGORY.put("90", "竞彩足球");
		CATEGORY.put("91", "竞彩足球");
		CATEGORY.put("92", "竞彩足球");
		CATEGORY.put("93", "竞彩足球");
		CATEGORY.put("70", "竞彩足球");
		CATEGORY.put("72", "竞彩足球");

		CATEGORY.put("94", "竞彩篮球");
		CATEGORY.put("95", "竞彩篮球");
		CATEGORY.put("96", "竞彩篮球");
		CATEGORY.put("97", "竞彩篮球");
		CATEGORY.put("71", "竞彩篮球");

		CATEGORY.put("98", "冠军竞猜");
		CATEGORY.put("99", "冠亚军竞猜");
	}

	public static final String[] YUCELOT = {"ssq", "dlt", "jczq", "jclq", "zc", "bjdc", "3d", "pl3"};
	public static final String[] YUCELOT_HTML5 = {"ssq", "dlt", "jczq", "jclq", "bjdc", "zc"};

	public static HashMap<String, String> GIDPATH = new HashMap<String, String>();
	static {
		GIDPATH.put("01", YUCELOT[0]);
		GIDPATH.put("03", YUCELOT[6]);

		GIDPATH.put("50", YUCELOT[1]);
		GIDPATH.put("53", YUCELOT[7]);

		GIDPATH.put("80", YUCELOT[4]);
		GIDPATH.put("81", YUCELOT[4]);
		GIDPATH.put("82", YUCELOT[4]);
		GIDPATH.put("83", YUCELOT[4]);

		GIDPATH.put("84", YUCELOT[5]);
		GIDPATH.put("85", YUCELOT[5]);
		GIDPATH.put("86", YUCELOT[5]);
		GIDPATH.put("87", YUCELOT[5]);
		GIDPATH.put("88", YUCELOT[5]);
		GIDPATH.put("89", YUCELOT[5]);

		GIDPATH.put("90", YUCELOT[2]);
		GIDPATH.put("91", YUCELOT[2]);
		GIDPATH.put("92", YUCELOT[2]);
		GIDPATH.put("93", YUCELOT[2]);
		GIDPATH.put("70", YUCELOT[2]);
		GIDPATH.put("72", YUCELOT[2]);

		GIDPATH.put("94", YUCELOT[3]);
		GIDPATH.put("95", YUCELOT[3]);
		GIDPATH.put("96", YUCELOT[3]);
		GIDPATH.put("97", YUCELOT[3]);
		GIDPATH.put("71", YUCELOT[3]);
	}

	public static HashMap<String, String> GIDMAP = new HashMap<String, String>();
	static {
		GIDMAP.put("01", "01");
		GIDMAP.put("03", "03");

		GIDMAP.put("50", "50");
		GIDMAP.put("53", "53");

		GIDMAP.put("80", "80");
		GIDMAP.put("81", "81");
		GIDMAP.put("82", "81");
		GIDMAP.put("83", "81");

		GIDMAP.put("84", "85");
		GIDMAP.put("85", "85");
		GIDMAP.put("86", "85");
		GIDMAP.put("87", "85");
		GIDMAP.put("88", "85");
		GIDMAP.put("89", "85");

		GIDMAP.put("90", "70");
		GIDMAP.put("91", "70");
		GIDMAP.put("92", "70");
		GIDMAP.put("93", "70");
		GIDMAP.put("70", "70");
		GIDMAP.put("72", "70");

		GIDMAP.put("94", "71");
		GIDMAP.put("95", "71");
		GIDMAP.put("96", "71");
		GIDMAP.put("97", "71");
		GIDMAP.put("71", "71");
	}

	public static HashMap<String, String> LOTTID = new HashMap<String, String>();
	static {
		LOTTID.put(YUCELOT[0], "116");
		LOTTID.put(YUCELOT[1], "117");
		LOTTID.put(YUCELOT[2], "119");
		LOTTID.put(YUCELOT[3], "120");
		LOTTID.put(YUCELOT[4], "121");
		LOTTID.put(YUCELOT[5], "122");
		LOTTID.put(YUCELOT[6], "118");
		LOTTID.put(YUCELOT[7], "123");
	}

	public static final String YUCEROOT = "/opt/export/www/cms/mnews/yuce/";
	public static final String HOTNEWSROOT= "/opt/export/www/cms/mnews/hotnews/";
	public static final String WORLDCUPNEWSROOT = "/opt/export/www/cms/mnews/worldcupnews/";

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

	public static String parseXML(Map<String, String> parameters) throws UnsupportedEncodingException {
		StringBuilder sb= new StringBuilder();
		sb.append("<xml>");
		Set<Map.Entry<String, String>> es = parameters.entrySet();
		Iterator<Map.Entry<String, String>> it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if("body".equals(k)){
				log.info("body--------------->"+v);
			}
			if (null != v && !"".equals(v) && !"appkey".equals(k)) {
				sb.append("<" + k + ">" + parameters.get(k) + "</" + k + ">\n");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * 转XMLmap
	 * @author
	 * @param xmlBytes
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public static Map<String, String> toMap(byte[] xmlBytes,String charset) throws Exception{
		SAXReader reader = new SAXReader(false);
		InputSource source = new InputSource(new ByteArrayInputStream(xmlBytes));
		source.setEncoding(charset);
		Document doc = reader.read(source);
		Map<String, String> params = XmlUtil.toMap(doc.getRootElement());
		return params;
	}

	/**
	 * 转MAP
	 * @author
	 * @param element
	 * @return
	 */
	public static Map<String, String> toMap(Element element){
		Map<String, String> rest = new HashMap<String, String>();
		List<Element> els = element.elements();
		for(Element el : els){
			rest.put(el.getName().toLowerCase(), el.getTextTrim());
		}
		return rest;
	}
	
	public static JXmlWrapper parseMapList(List<Map<String, Object>> mapList,String nodeName,String parentNode){
		if(null != mapList && mapList.size()>0){
			StringBuilder builder = new StringBuilder();
			builder.append("<"+parentNode+">");
			for(Map<String, Object> map : mapList){
				builder.append("<"+nodeName+" ");
				Set<String> keySet = map.keySet();
				for(String key : keySet){
					builder.append(JXmlUtil.createAttrXml(key.toLowerCase(), String.valueOf(map.get(key))));
				}
				builder.append("/>\n");
			}
			builder.append("</"+parentNode+">");
			return JXmlWrapper.parse(builder.toString());
		}else{
			return null;
		}
	}

	/**
	 * 读取本地xml文件
	 * @param dir 文件所在目录绝对地址
	 * @param filename 文件名
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static JXmlWrapper readLocalXml(String dir, String filename) throws FileNotFoundException, IOException {
		return readLocalXml(new File(dir, filename));
	}

	/**
	 * 读取本地xml文件
	 * @param file 文件对象
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static JXmlWrapper readLocalXml(File file) throws FileNotFoundException, IOException {
		if (file == null) {
			throw new IllegalArgumentException("readLocalXml输入参数file不能为null");
		}
		if (!file.exists()) {
			throw new FileNotFoundException("找不到文件" + file.getCanonicalPath());
		}
		return JXmlWrapper.parse(file);
	}
}

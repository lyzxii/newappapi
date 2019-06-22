package com.caiyi.lottery.tradesystem.tradecenter.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

import org.slf4j.Logger;

import com.caipiao.game.GameContains;


public class FileCastCodeUtil {

	public static void printInfo(String gid,String pid,String info,Logger logger) {
		logger.info("游戏=" + gid + " 期次=" + pid + "\t " + info);
	}
	
	public final static String getCodesFromFile(String gid, String pid, String basePath, String fileName, String play,Logger logger) throws Exception {
		int g = Integer.parseInt(gid);
		if (g >= 84 || g == 72 || g== 70 || g== 71) {
			return getCodesFromFileJQ(gid, pid, basePath, fileName, play,logger);
		} else {
			return getCodesFromFileSZ(gid, pid, basePath, fileName, play,logger);
		}
	}
	
	public final static String getCodesFromCode(String gid,String codes, int extype,String play,Logger logger) throws Exception {
		if (extype == 13 && (GameContains.isFootball(gid) || GameContains.isBasket(gid))) {// 
			return getCodesFromCodeJC(gid, codes,extype,play,logger);
		} else {
			return codes;
		}
	}
	

	// 北单和竞彩
	private final static String getCodesFromFileJQ(String gid, String pid, String basePath, String fileName, String play,Logger logger) throws Exception {
		File file = new File(basePath + File.separator + gid + File.separator + pid, fileName);
		if (file.exists() && file.isFile()) {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				temp = temp.trim();
				if (temp.length() > 0) {
					sb.append(temp).append(";");
				}
			}
			String s = new String(sb);
			if (s.length() > 0) {
				s = s.substring(0, s.length() - 1);
			}
			return s;
		} else {
			printInfo(gid, pid, "文件不存在 " + basePath + File.separator + gid + File.separator + pid + File.separator + fileName,logger);
			File newFile = new File(file.getAbsolutePath());
			printInfo(gid, pid, basePath + File.separator + gid + File.separator + pid + File.separator + fileName+" newFile.exists():"+newFile.exists()+" newFile.isFile():"+newFile.isFile(),logger);
			return null;
		}
	}

	
	
	// 篮彩和竞彩  单关复式转换
		private final static String getCodesFromCodeJC(String gid, String codes, int extype, String play,Logger logger) throws Exception {
				StringBuffer sb = new StringBuffer();
				String temp = null;
				String[] codearr = codes.split(";");
				for(int i=0;i<codearr.length;i++){
					temp = codearr[i].trim();
					if (temp.length() > 0 && temp.indexOf("_")>0) {
						String[] temparr = temp.split("_");
						for(int j=0;j<Integer.parseInt(temparr[1]);j++){
							sb.append(temparr[0]).append(";");
						}
						
					}
				}
				String s = new String(sb);
				if (s.length() > 0) {
					s = s.substring(0, s.length() - 1);
				}
				return s;
		}

	// 数字彩
	private final static String getCodesFromFileSZ(String gid, String pid, String basePath, String fileName, String play,Logger logger) throws Exception {
		File file = new File(basePath + File.separator + gid + File.separator + pid, fileName);
		if (file.exists() && file.isFile()) {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = br.readLine()) != null) {
				temp = temp.trim();
				if (temp.length() > 0) {
					if (temp.indexOf(",") >= 0) {
						if (temp.indexOf(":") < 0) {
							sb.append(temp + ":" + play + ":1;");
						} else {
							sb.append(temp).append(";");
						}
					} else {
						for (int i = 0; i < temp.length(); i++) {
							sb.append(temp.charAt(i));
							if (i != temp.length() - 1) {
								sb.append(",");
							}
						}
						sb.append(":").append(play).append(":1;");
					}
				}
			}
			String s = new String(sb);
			if (s.length() > 0) {
				s = s.substring(0, s.length() - 1);
			}
			return s;
		} else {
			printInfo(gid, pid, "文件不存在 " + basePath + File.separator + gid + File.separator + pid + File.separator + fileName,logger);
			return null;
		}
	}

	public final static void write_to_file(File file,StringBuffer sb) throws Exception {
		FileOutputStream fout = new FileOutputStream(file);
		String s = sb.toString();
		byte[] buf = s.getBytes("UTF-8");
		fout.write(buf);
		fout.close();
		fout = null ;
	}
	
	// 数字彩文件追号添加
	public final static String getCodesFromPathSZ(String gid,String filepath, String basePath, String fileName, String play,Logger logger) throws Exception {
			File file = new File(basePath + File.separator +  filepath, fileName);
			if (file.exists() && file.isFile()) {
				StringBuffer sb = new StringBuffer();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String temp = null;
				while ((temp = br.readLine()) != null) {
					temp = temp.trim();
					temp = UploadFormatUtils.transf_format(gid, temp, UploadFormatUtils.delims_b, UploadFormatUtils.delims_r);
					if (temp.length() > 0) {
						if (temp.indexOf(",") >= 0) {
							if (temp.indexOf(":") < 0) {
								sb.append(temp + ":" + play + ":1;");
							} else {
								sb.append(temp).append(";");
							}
						} else {
							for (int i = 0; i < temp.length(); i++) {
								sb.append(temp.charAt(i));
								if (i != temp.length() - 1) {
									sb.append(",");
								}
							}
							sb.append(":").append(play).append(":1;");
						}
					}
				}
				String s = new String(sb);
				if (s.length() > 0) {
					s = s.substring(0, s.length() - 1);
				}
				return s;
			} else {
				return null;
			}
		}
	
	
}

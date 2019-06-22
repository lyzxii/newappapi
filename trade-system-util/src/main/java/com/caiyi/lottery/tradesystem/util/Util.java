package com.caiyi.lottery.tradesystem.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

@Slf4j
public class Util {
	
	public static boolean SaveFile(String contents, String fdir,String fname, String encode) {
		File d = new File(fdir);
		if ( ! d.exists() ) {
			d.mkdirs();
		}
		return SaveFile(contents,fdir + File.separator + fname,encode);
	}

	public static boolean SaveFile(String contents, String filename, String encode) {
		if ("".equals(encode)) {
			encode = "UTF-8";
		}
		StringBuffer sb = new StringBuffer();
		sb.append(contents);
		FileOutputStream outSTr = null;
		BufferedWriter bw = null;
		File file = new File(filename);
		try {
			outSTr = new FileOutputStream(file);
			bw = new BufferedWriter(new OutputStreamWriter(outSTr, encode));
			bw.write(sb.toString());
			bw.flush();
			sb = null;
			return true;
		} catch (FileNotFoundException e) {
			log.error("SaveFile Exception contents:"+contents+" filename:"+filename+" encode:"+encode,e);
		} catch (UnsupportedEncodingException e) {
			log.error("SaveFile Exception contents:"+contents+" filename:"+filename+" encode:"+encode,e);
		} catch (IOException e) {
			log.error("SaveFile Exception contents:"+contents+" filename:"+filename+" encode:"+encode,e);
		} finally {
			try {
				bw.close();
				outSTr.close();
			} catch (IOException e) {
				log.error("SaveFile Exception contents:"+contents+" filename:"+filename+" encode:"+encode,e);
			}
		}
		return false;
	}
}

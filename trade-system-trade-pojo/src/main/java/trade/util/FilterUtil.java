package trade.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
public class FilterUtil {

	public static byte[] compressBytes(byte input[]) {
		int cachesize = 1024;

		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(input);
		compresser.finish();
		byte output[] = new byte[0];
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];
			int got;
			while (!compresser.finished()) {
				got = compresser.deflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				log.error("compressBytes",e);
			}
		}
		return output;
	}

	public static byte[] decompressBytes(byte input[]) {
		int cachesize = 1024;
		Inflater decompresser = new Inflater();

		byte output[] = new byte[0];
		decompresser.reset();
		decompresser.setInput(input);
		ByteArrayOutputStream o = new ByteArrayOutputStream(input.length);
		try {
			byte[] buf = new byte[cachesize];
			int got;
			while (!decompresser.finished()) {
				got = decompresser.inflate(buf);
				o.write(buf, 0, got);
			}
			output = o.toByteArray();
		} catch (Exception e) {
			log.error("decompressBytes",e);
		} finally {
			try {
				o.close();
			} catch (IOException e) {
				log.error("decompressBytes",e);
			}
		}
		return output;
	}
	public static int readInt(byte[] data, int off) {
		int ch1 = (data[off + 0] & 0xff) << 24;
		int ch2 = (data[off + 1] & 0xff) << 16;
		int ch3 = (data[off + 2] & 0xff) << 8;
		int ch4 = (data[off + 3] & 0xff) << 0;
		return ch1 + ch2 + ch3 + ch4;
	}
	public static int[] SplitterInt(String s, String delim) {
		String[] ss = split(s,delim);
		int[] ret = new int[ss.length];
		for (int i=0;i<ss.length;i++) {
			ret[i] = Integer.parseInt(ss[i]);
		}
		return ret ;
	}
	
	public static String[] split(String code, String delim) {
    	int size = CountStrNum(code.trim(), delim);
    	return splitter(code.trim(),delim,size);
    }
    
    private static int CountStrNum(String source, String delim) {
        int pos = -1;
        int begin = 0;
        int count = 1;
        while ((pos = source.indexOf(delim, begin)) >= 0) {
            count++;
            begin = pos + 1;
        }
        return count;
    }
    
    private static String[] splitter(String code, String delim, int length) {
        int pos = -1;
        int begin = 0;
        String[] s = new String[length];
        int count = 0;
        while ((pos = code.indexOf(delim, pos + 1)) != -1) {
            s[count] = code.substring(begin, pos);
            begin = pos + 1;
            count++;
        }
        s[count] = code.substring(begin, code.length());
        count++;
        return s;
    }
	
	/**
	 * 从n个数字中选择m个数字
	 * @param a
	 * @param m
	 * @return
	 * @throws Exception
	 */
	public static List<int[]> combine(int n, int m){
		if(m>n){
			return null;
		}
		List<int[]> result = new ArrayList<int[]>();
		int[] bs = new int[n];
		for(int i=0;i<n;i++){
			bs[i]=0;
		}
		//初始化
		for(int i=0;i<m;i++){
			bs[i]=1;
		}
		boolean flag = true;
		boolean tempFlag = false;
		int pos = 0;
		int sum = 0;
		//首先找到第一个10组合，然后变成01，同时将左边所有的1移动到数组的最左边
		do{
			sum = 0;
			pos = 0;
			tempFlag = true; 
			result.add(copy(bs));
			if(n==m){
				return result;
			}else if(m ==0){
				return result;
			}
			for(int i=0;i<n-1;i++){
				if(bs[i]==1 && bs[i+1]==0 ){
					bs[i]=0;
					bs[i+1]=1;
					pos = i;
					break;
				}
			}
			//将左边的1全部移动到数组的最左边
			for(int i=0;i<pos;i++){
				if(bs[i]==1){
					sum++;
				}
			}
			for(int i=0;i<pos;i++){
				if(i<sum){
					bs[i]=1;
				}else{
					bs[i]=0;
				}
			}
			//检查是否所有的1都移动到了最右边
			for(int i= n-m;i<n;i++){
				if(bs[i]==0){
					tempFlag = false;
					break;
				}
			}
			if(tempFlag==false){
				flag = true;
			}else{
				flag = false;
			}
		}while(flag);
		result.add(copy(bs));
		return result;
	}

	private static int[] copy(int[] bs){
		int[] result = new int[bs.length];
		for(int i=0;i<bs.length;i++){
			result[i] = bs[i];
		}
		return result ;
	}
	
	public static String longToString(long l) {
		String s = "";
		for (int i=0;i<64;i++) {
			if ( ((l & (1L << i)) >> i) == 1 ) {
				int m = i;
				if ( m < 10 ) {
					s += "0" + m + ",";
				} else {
					s += m + ",";
				}
			}
		}
		if ( s.length() > 0 ) {
			s = s.substring(0,s.length()-1);
		}
		return s;
	}
}
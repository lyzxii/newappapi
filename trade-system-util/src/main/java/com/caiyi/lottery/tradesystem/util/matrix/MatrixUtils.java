package com.caiyi.lottery.tradesystem.util.matrix;

import com.caiyi.lottery.tradesystem.constants.FileConstant;
import com.caiyi.lottery.tradesystem.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 旋转矩阵工具类
 * 
 * @author sjq
 */
@Slf4j
public class MatrixUtils {
	private static final String ROOT_DIR = FileConstant.MATRIX_PATH;

	/**
	 * 获取旋转矩阵投注号码,注意：投注中的蓝球号码(后区号码)并不参与旋转矩阵的运算,只有红球参与旋转矩阵的运算
	 * 
	 * @author sjq
	 * @param lotid
	 *            彩种id,与全站的彩种id保持一致,双色球为01,七乐彩为07,大乐透为50,
	 * @param tzcodes
	 *            投注号码,红球在前,蓝球在后,红球与蓝球之间用|隔开,只支持单个号码组合
	 * @param xztype
	 *            旋转类型,中用S替代,保用E替代,如：中6保5,则该值为S6E5,中6保4,则该值为S6E4,中5保5,则该值为S5E5
	 * @return codeList 通过旋转矩阵筛选后的投注列表,List<String>形式
	 */
	public static List<String> getMatrixCodes(String lotid, String tzcodes, String xztype) throws Exception {
		List<String> codeList = new ArrayList<String>();
		if (!StringUtil.isEmpty(tzcodes)) {
			String[] s = null;
			String[] hcodes = null; // 红球号码(前区号码)
			String[] lcodes = null; // 蓝球号码(后区号码)
			if (tzcodes.contains("|")) // 如果号码包含2个部分(前后区或者红蓝区),则先按规则截取
			{
				s = tzcodes.split("\\|");
				hcodes = s[0].split(",");
				lcodes = s[1].contains(":") ? s[1].substring(0, s[1].indexOf(":")).split(",") : s[1].split(",");
			} else {
				s = tzcodes.split(",");
				hcodes = s;
			}

			// 读取矩阵公式对照文件
			File file = new File(ROOT_DIR + lotid + "/C" + hcodes.length + xztype + ".db");
			BufferedReader reader = null;
			try {
				if (file.exists()) {
					reader = new BufferedReader(new FileReader(file));
					String temp = null;
					int length = 0;
					int lastindex = 0;
					if ("01".equals(lotid)) // 双色球
					{
						while ((temp = reader.readLine()) != null) {
							String code = "";
							length = temp.length();
							lastindex = length - 2;
							for (int i = 0; i < length; i += 2) {
								code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的红球号码
								if (i != lastindex) {
									code += ",";
								}
							}
							code += "|";
							for (String lcode : lcodes) // 循环组装完整的投注号码(红球号码 +
														// 蓝球号码)
							{
								codeList.add(code + lcode);
							}
						}
					} else if ("50".equals(lotid)) // 大乐透
					{
						List<String> lcodeList = doCodes(lcodes, 2);
						while ((temp = reader.readLine()) != null) {
							String code = "";
							length = temp.length();
							lastindex = length - 2;
							for (int i = 0; i < length; i += 2) {
								code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的前区号码
								if (i != lastindex) {
									code += ",";
								}
							}
							code += "|";
							for (String lcode : lcodeList) // 循环组装完整的投注号码(前区号码 +
															// 后区号码)
							{
								codeList.add(code + lcode);
							}
						}
					} else if ("07".equals(lotid)) // 七乐彩
					{
						while ((temp = reader.readLine()) != null) {
							String code = "";
							length = temp.length();
							lastindex = length - 2;
							for (int i = 0; i < length; i += 2) {
								code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码
								if (i != lastindex) {
									code += ",";
								}
							}
							codeList.add(code);
						}
					}
				}
			} catch (Exception e) {
				log.error("getMatrixCodes lotid:"+lotid+" tzcodes:"+tzcodes+" xztype:"+xztype,e);
			} finally {
				reader = null;
			}
		}
		return codeList;
	}

	/**
	 * 获取旋转矩阵投注号码,注意：投注中的蓝球号码(后区号码)并不参与旋转矩阵的运算,只有红球参与旋转矩阵的运算
	 * 
	 * @author sjq
	 * @param lotid
	 *            彩种id,与全站的彩种id保持一致,双色球为01,七乐彩为07,大乐透为50,
	 * @param tzcodes
	 *            投注号码,红球在前,蓝球在后,红球与蓝球之间用|隔开,只支持单个号码组合
	 * @param xztype
	 *            旋转类型,中用S替代,保用E替代,如：中6保5,则该值为S6E5,中6保4,则该值为S6E4,中5保5,则该值为S5E5
	 * @return codesStr 通过旋转矩阵筛选后的投注列表,String字符串形式
	 */
	public static String getMatrixCodesStr(String lotid, String tzcodes, String xztype) throws Exception {
		StringBuilder codesStr = new StringBuilder();
		if (!StringUtil.isEmpty(tzcodes)) {
			String[] s = null;
			String[] hcodes = null; // 红球号码(前区号码)
			String[] lcodes = null; // 蓝球号码(后区号码)
			if (tzcodes.contains("|")) // 如果号码包含2个部分(前后区或者红蓝区),则先按规则截取
			{
				s = tzcodes.split("\\|");
				hcodes = s[0].split(",");
				lcodes = s[1].contains(":") ? s[1].substring(0, s[1].indexOf(":")).split(",") : s[1].split(",");
			} else {
				s = tzcodes.split(",");
				hcodes = s;
			}

			// 读取矩阵公式对照文件
			BufferedReader reader = null;
			try {
				File file = new File(ROOT_DIR + lotid + "/C" + hcodes.length + xztype + ".db");
				if (file.exists()) {
					reader = new BufferedReader(new FileReader(file));
					String temp = null;
					int length = 0;
					int lastindex = 0;
					if ("01".equals(lotid)) // 双色球
					{
						while ((temp = reader.readLine()) != null) {
							String code = "";
							length = temp.length();
							lastindex = length - 2;
							for (int i = 0; i < length; i += 2) {
								code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的红球号码
								if (i != lastindex) {
									code += ",";
								}
							}
							code += "|";
							for (String lcode : lcodes) {
								codesStr.append(code + lcode + "<br/>");
							}
						}
					} else if ("50".equals(lotid)) // 大乐透
					{
						List<String> lcodeList = doCodes(lcodes, 2);
						while ((temp = reader.readLine()) != null) {
							String code = "";
							length = temp.length();
							lastindex = length - 2;
							for (int i = 0; i < length; i += 2) {
								code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的前区号码
								if (i != lastindex) {
									code += ",";
								}
							}
							code += "|";
							for (String lcode : lcodeList) {
								codesStr.append(code + lcode + "<br/>");
							}
						}
					} else if ("07".equals(lotid)) // 七乐彩
					{
						while ((temp = reader.readLine()) != null) {
							String code = "";
							length = temp.length();
							lastindex = length - 2;
							for (int i = 0; i < length; i += 2) {
								code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码
								if (i != lastindex) {
									code += ",";
								}
							}
							codesStr.append(code + "<br/>");
						}
					}
				}
			} catch (Exception e) {
				log.error("getMatrixCodesStr Exception:lotid:"+lotid+" tzcode:"+tzcodes+" xztype:"+xztype,e);
			} finally {
				reader = null;
			}
		}
		return codesStr.toString();
	}

	/**
	 * 获取旋转矩阵投注号码,注意：投注中的蓝球号码(后区号码)并不参与旋转矩阵的运算,只有红球参与旋转矩阵的运算
	 * 
	 * @author sjq
	 * @param lotid
	 *            彩种id,与全站的彩种id保持一致,双色球为01,七乐彩为07,大乐透为50,
	 * @param tzcodes
	 *            投注号码,红球在前,蓝球在后,红球与蓝球之间用|隔开,多个号码组合用";"连接,每一组号码都需要在蓝球后面加"-" +
	 *            旋转类型
	 * @return codeList 通过旋转矩阵筛选后的投注列表,List<String>形式
	 */
	public static List<String> getMatrixCodes(String lotid, String tzcodes) throws Exception {
		List<String> codeList = new ArrayList<String>();
		if (!StringUtil.isEmpty(tzcodes)) {
			BufferedReader reader = null;
			Map<String, BufferedReader> buffer = new HashMap<String, BufferedReader>();
			try {
				String[] codes = tzcodes.split(";");
				List<String> lcodeList = null; // 大乐透后区号码组合
				String xztype = ""; // 选择类型
				String[] s = null; // 用来接收单个组合的投注号码
				String[] hcodes = null; // 红球号码(前区号码)
				String[] lcodes = null; // 蓝球号码(后区号码)
				File file = null;
				for (String c : codes) {
					if (c.contains("|")) // 如果号码包含2个部分(前后区或者红蓝区),则先按规则截取
					{
						s = c.split("\\|");
						hcodes = s[0].split(",");
						lcodes = s[1].substring(0, s[1].indexOf("-")).split(",");
					} else {
						s = c.split("-");
						hcodes = s[0].split(",");
					}
					xztype = s[1].contains(":") ? s[1].substring(s[1].indexOf("-") + 1, s[1].indexOf(":"))
							: s[1].substring(s[1].indexOf("-") + 1);
					lcodeList = ("50".equals(lotid)) ? doCodes(lcodes, 2) : null;
					reader = buffer.get(xztype); // 先从变量中读取矩阵公式对照文件流,如果没读到,则读取文件中的数据
					if (reader == null) {
						file = new File(ROOT_DIR + lotid + "/C" + hcodes.length + xztype + ".db");
						if (file.exists()) {
							reader = new BufferedReader(new FileReader(file));
						}
					}
					if (reader != null) {
						String temp = null;
						int length = 0;
						int lastindex = 0;
						if ("01".equals(lotid)) // 双色球
						{
							while ((temp = reader.readLine()) != null) {
								String code = "";
								length = temp.length();
								lastindex = length - 2;
								for (int i = 0; i < length; i += 2) {
									code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的红球号码
									if (i != lastindex) {
										code += ",";
									}
								}
								code += "|";
								for (String lcode : lcodes) {
									codeList.add(code + lcode);
								}
							}
						} else if ("50".equals(lotid)) // 大乐透
						{
							while ((temp = reader.readLine()) != null) {
								String code = "";
								length = temp.length();
								lastindex = length - 2;
								for (int i = 0; i < length; i += 2) {
									code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的前区号码
									if (i != lastindex) {
										code += ",";
									}
								}
								code += "|";
								for (String lcode : lcodeList) {
									codeList.add(code + lcode);
								}
							}
						} else if ("07".equals(lotid)) // 七乐彩
						{
							while ((temp = reader.readLine()) != null) {
								String code = "";
								length = temp.length();
								lastindex = length - 2;
								for (int i = 0; i < length; i += 2) {
									code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码
									if (i != lastindex) {
										code += ",";
									}
								}
								codeList.add(code);
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("getMatrixCodes Exception:lotid:"+lotid+" tzcode:"+tzcodes,e);
			} finally {
				reader = null;
				buffer = null;
			}
		}
		return codeList;
	}

	/**
	 * 获取旋转矩阵投注号码,注意：投注中的蓝球号码(后区号码)并不参与旋转矩阵的运算,只有红球参与旋转矩阵的运算
	 * 
	 * @author sjq
	 * @param lotid
	 *            彩种id,与全站的彩种id保持一致,双色球为01,七乐彩为07,大乐透为50,
	 * @param tzcodes
	 *            投注号码,红球在前,蓝球在后,红球与蓝球之间用|隔开,多个号码组合用";"连接,每一组号码都需要在蓝球后面加"-" +
	 *            旋转类型
	 * @return codeList 通过旋转矩阵筛选后的投注列表,字符串形式
	 */
	public static String getMatrixCodesStr(String lotid, String tzcodes) {
		StringBuilder codesStr = new StringBuilder();
		if (!StringUtil.isEmpty(tzcodes)) {
			BufferedReader reader = null;
			Map<String, BufferedReader> buffer = new HashMap<String, BufferedReader>();
			try {
				String[] codes = tzcodes.split(";");
				List<String> lcodeList = null; // 大乐透后区号码组合
				String xztype = ""; // 选择类型
				String[] s = null; // 用来接收单个组合的投注号码
				String[] hcodes = null; // 红球号码(前区号码)
				String[] lcodes = null; // 蓝球号码(后区号码)
				File file = null;
				String pm = (codes != null && codes.length > 0) ? codes[0].substring(codes[0].indexOf(":")) : ":1:1";
				for (String c : codes) {
					if (c.contains("|")) // 如果号码包含2个部分(前后区或者红蓝区),则先按规则截取
					{
						s = c.split("\\|");
						hcodes = s[0].split(",");
						lcodes = s[1].substring(0, s[1].indexOf("-")).split(",");
					} else {
						s = c.split("-");
						hcodes = s[0].split(",");
					}
					xztype = s[1].contains(":") ? s[1].substring(s[1].indexOf("-") + 1, s[1].indexOf(":"))
							: s[1].substring(s[1].indexOf("-") + 1);
					lcodeList = ("50".equals(lotid)) ? doCodes(lcodes, 2) : null;
					reader = buffer.get(xztype); // 先从变量中读取矩阵公式对照文件流,如果没读到,则读取文件中的数据
					if (reader == null) {
						file = new File(ROOT_DIR + lotid + "/C" + hcodes.length + xztype + ".db");
						if (file.exists()) {
							reader = new BufferedReader(new FileReader(file));
						}
					}
					if (reader != null) {
						String temp = null;
						int length = 0;
						int lastindex = 0;
						if ("01".equals(lotid)) // 双色球
						{
							while ((temp = reader.readLine()) != null) {
								String code = "";
								length = temp.length();
								lastindex = length - 2;
								for (int i = 0; i < length; i += 2) {
									code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的红球号码
									if (i != lastindex) {
										code += ",";
									}
								}
								code += "|";
								for (String lcode : lcodes) {
									codesStr.append(";" + code + lcode + pm);
								}
							}
						} else if ("50".equals(lotid)) // 大乐透
						{
							while ((temp = reader.readLine()) != null) {
								String code = "";
								length = temp.length();
								lastindex = length - 2;
								for (int i = 0; i < length; i += 2) {
									code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码中的前区号码
									if (i != lastindex) {
										code += ",";
									}
								}
								code += "|";
								for (String lcode : lcodeList) {
									codesStr.append(";" + code + lcode + pm);
								}
							}
						} else if ("07".equals(lotid)) // 七乐彩
						{
							while ((temp = reader.readLine()) != null) {
								String code = "";
								length = temp.length();
								lastindex = length - 2;
								for (int i = 0; i < length; i += 2) {
									code += hcodes[Integer.parseInt(temp.substring(i, i + 2)) - 1]; // 取出公式文件中相对应的索引号,并在索引位置匹配投注号码
									if (i != lastindex) {
										code += ",";
									}
								}
								codesStr.append(";" + code + pm);
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("getMatrixCodesStr Exception:lotid:"+lotid+" tzcode:"+tzcodes,e);
			} finally {
				reader = null;
				buffer = null;
			}
		}
		return codesStr.toString().substring(1); // 去掉最前面的一个";",并返回结果
	}

	/**
	 * 从m中取n个数的排列组合
	 * 
	 * @author sjq
	 * @param numbers
	 *            源数据,待排列组合的号码数组
	 * @param n
	 *            组合个数(即：以多少个号码为一个组合)
	 * @return results 拆分后的号码列表(集合形式)
	 */
	private static List<String> doCodes(String[] numbers, int n) {
		if (numbers != null) {
			int length = 1; // 构造数组长度标志
			int m = numbers.length;

			// 获取排列数组的长度(参照组合公式：(m * (m -1) * .....(m - i)) / (n * (n -1) *
			// .....(n - j)),且i > (m - n),j < n,m/n/i/j都为正整数)
			for (int i = numbers.length; i > (m - n); i--) {
				length = length * i;
			}
			for (int j = n; j > 1; j--) {
				length = length / j;
			}

			List<String> codes = new ArrayList<String>(length);
			String first = ""; // 用来保存第一种排列号码
			int t = n - 1; // 用来控制循环逻辑
			for (int k = 0; k < n; k++) {
				first += numbers[k];
				if (k != t) {
					first += ",";
				}
			}
			codes.add(0, first); // 将第一个排列号号码放入号码数组中

			/* 模拟关联矩阵,构造集合(数组) */
			int[] index = new int[m]; // 构造下标数组
			boolean flag = true; // 循环开关
			int k = 1; // 返回结果数组长度(自增长)
			for (int i = 0; i < m; i++) // 初始化构造下标数组,首先将第一个下标设置为1,其他的设置为0
			{
				if (i < n) {
					index[i] = 1;
				} else {
					index[i] = 0;
				}
			}

			// 循环,从左至右,依次扫描,依次将 1 0 这种组合设置为 0 1,并将其左边的值为1的元素全部移到数组的最左端
			do {
				flag = false;
				int zerocount = 0; // 将 1 0 置为 0 1 前的值为0的元素个数
				for (int i = 0; i < m - 1; i++) {
					if (index[i] == 0) // 记录前0个数(非0即1)，可以通过这个参数进行1的前移
					{
						zerocount++;
					}
					if (index[i] == 1 && index[i + 1] == 0) // 如果满足 1 0
															// 这种条件,则置换,将 1 0 置为
															// 0 1
					{
						index[i] = 0; // 将第i个置为0
						index[i + 1] = 1; // 将第i + 1个置为1
						flag = true; // 置换完后,将flag设置为true,如果走不到这一步,则flag为false,说明1已经是最后一位了,则终止循环
						for (int j = 0; j < i; j++) // 将 1 0 置为0 1
													// 前的所有值为1的元素进行前移操作
						{
							if (j < i - zerocount) {
								index[j] = 1;
							} else {
								index[j] = 0;
							}
						}
						String code = ""; // 用于存储当前构造数组下的投注号码组合
						for (int kk = 0; kk < m; kk++) // 通过构造数组下标，得到需要的投注号码
						{
							if (index[kk] == 1) {
								code += numbers[kk] + ",";
							}
						}
						codes.add(k, code.substring(0, code.lastIndexOf(",")));
						i = m;
						k++;
					}
				}
			} while (flag == true);
			return codes; // 返回结果
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		// long s = System.currentTimeMillis();
		// String codes = "01,02,03,04,05,06,07,08|01-S6E4";
		// String codes =
		// "01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30|01,02,03,04,05,06,07,08,09,10,11,12-S5E4";
		// List<String> sss = getMatrixCodes("01",codes);
		// System.out.println("大约耗时：" + (System.currentTimeMillis() - s) +
		// ",length:" + sss.length());
		System.out.println("abc".substring(0, 3));
	}
}
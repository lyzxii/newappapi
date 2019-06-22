package com.caiyi.lottery.tradesystem.tradecenter.util;

/**
 * 新增单式上传格式转换类
 * 
 * @author kouyi
 * @version 1.0
 */
public class UploadFormatUtils {
	// 定义格式化分隔符
	public final static String[] delims_b = { "\\+", "\\|", "\\:", "\\&" }; // 后区分隔符
	public final static String[] delims_r = { "\\s+", "\\.", "\\,", "\\*" }; // 前区分隔符

	/**
	 * 用户单式上传格式（双色球和大乐透）
	 * 
	 * @param gid
	 *            彩种编号
	 * @param temp
	 * @param delims_b
	 *            蓝球区支持的截取符
	 * @param delims_r
	 *            红球区支持的截取符
	 * @return
	 */
	public static String transf_format(String gid, String temp,
			String[] delims_b, String[] delims_r) {
		// 未知彩种
		if (gid == null || "".equals(gid)) {
			return null;
		}
		// 本次只支持双色球和大乐透的格式转换
		if (!"01".equals(gid) && !"50".equals(gid)) {
			return temp; // 原字符串返回
		}

		// 允许文件中使用空行
		if (temp == null || temp.trim().length() == 0)
			return temp.trim();

		temp = temp.trim();
		StringBuffer buffer = new StringBuffer();
		String[] str_b = null;
		for (int index = 0; index < delims_b.length; index++) {
			if (temp.split(delims_b[index]).length > 1) {
				str_b = temp.split(delims_b[index]);
				buffer.append(str_format(gid, str_b[0], delims_r));
				break;
			}
		}

		// 其他数字彩种目前暂不作处理
		if (str_b == null) {
			buffer.append(temp);
			return buffer.toString();
		}

		buffer.append("|");
		// 大乐透蓝球区两个号码做同样处理
		String str_dl = str_format(gid, str_b[1], delims_r);
		buffer.append(str_dl);
		return buffer.toString();
	}

	/**
	 * 红球区标准格式转换
	 * 
	 * @param gid
	 *            彩种编号
	 * @param temp
	 * @param delims_b
	 *            蓝球区支持的截取符
	 * @param delims_r
	 *            红球区支持的截取符
	 * @return
	 */
	private static String str_format(String gid, String str, String[] delims_r) {
		StringBuffer buffer = new StringBuffer();
		String[] strs = null;
		// 格式化红球区
		for (int index = 0; index < delims_r.length; index++) {
			if (str.split(delims_r[index]).length > 1) {
				strs = str.split(delims_r[index]);
				break;
			}
		}

		if (strs == null || strs.length == 0) {
			strs = new String[] { str };
		}

		// 标准化号码
		for (int ind = 0; ind < strs.length; ind++) {
			// 判断双色球和大乐透不合法号码如：003
			if (("01".equals(gid) || "50".equals(gid)) && strs[ind].length() > 2) {
				buffer.append(ind == strs.length - 1 ? "[" + strs[ind] + "]": "[" + strs[ind] + "],");
				continue;
			}
			if (ind == strs.length - 1) {
				buffer.append(strs[ind].length() == 1 ? "0" + strs[ind]: strs[ind]);
			} else {
				buffer.append(strs[ind].length() == 1 ? "0" + strs[ind] + ",": strs[ind] + ",");
			}
		}
		return buffer.toString();
	}
}

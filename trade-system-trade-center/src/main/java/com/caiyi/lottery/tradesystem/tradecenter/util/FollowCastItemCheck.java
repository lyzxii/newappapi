package com.caiyi.lottery.tradesystem.tradecenter.util;

import com.caiyi.lottery.tradesystem.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FollowCastItemCheck {

	public static String sort(String code) {
        if (StringUtil.isEmpty(code)) {
			return null;
		}
		String gameId = code.substring(0, code.indexOf('|'));
		String castItem = code.substring(code.indexOf('|') + 1, code.lastIndexOf('|'));
		String overWay = code.substring(code.lastIndexOf('|') + 1, code.lastIndexOf('_'));
		List<String> itemList = Arrays.asList(castItem.split(","));
		Collections.sort(itemList);
		StringBuilder sb = new StringBuilder(gameId + "|");
		String str = itemList.toString().replace("[", "").replace("]", "").replace(" ", "");
		sb.append(str);
		sb.append("|");
		sb.append(overWay);
		return sb.toString();
	}

	public static boolean doCheck(List<String> tzList, List<String> matchList) {
		if (tzList.size() != matchList.size()) {
			return false;
		}
		for (String item : matchList) {
			if (!tzList.contains(item)) {
				return false;
			}
		}
		return true;
	}
	

}
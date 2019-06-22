package com.caiyi.lottery.tradesystem.integralcenter.service;

import integral.pojo.PointsDrawGoods;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 抽奖算法
 */
public class PointsDrawUtil {
    // 奖品 key name
	public static Map<Integer, PointsDrawGoods> prizeMap = new HashMap<Integer,PointsDrawGoods>();
	static {
		prizeMap.put(0, new PointsDrawGoods("1642","20减1红包",20));
		prizeMap.put(1, new PointsDrawGoods("1643","30减1红包",50));
		prizeMap.put(2, new PointsDrawGoods("1644","50减1红包",100));
		prizeMap.put(3, new PointsDrawGoods("1645","10000积分",10));
		prizeMap.put(4, new PointsDrawGoods("1646","5000积分",10));
		prizeMap.put(5, new PointsDrawGoods("1647","没有中奖",Integer.MAX_VALUE));
	}

	// 进行抽奖
	public static int draw() {
		Integer result = null;
		// 中奖率
		int arr[] = new int[] { 2, 5, 10, 1, 1, 81 };
		int sum = 100;
		for (int i = 0; i < arr.length; i++) {
			int randomNum = new Random().nextInt(sum);
			if (randomNum < arr[i]) {
				result = i;
				break;
			} else {
				sum -= arr[i];
			}
		}
		return result;
	}
}

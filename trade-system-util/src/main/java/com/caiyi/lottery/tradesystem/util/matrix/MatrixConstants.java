package com.caiyi.lottery.tradesystem.util.matrix;

import java.util.HashMap;
import java.util.Map;

public class MatrixConstants 
{
	public static Map<String,Integer[]> MatrixMaps = new HashMap<String, Integer[]>();	//旋转矩阵注数快速算法
	public static Map<String,String> MatrixTypeMaps = new HashMap<String, String>();	//旋转矩阵-旋转类型与名称的映射
	static
	{
		MatrixMaps.put("01S6E5",new Integer[]{1,4,7,14,22,38,61,98,142,224,338,484,684,850,1130,1529,2072,2773,3577,4483,5665,7461,9066,10978,13168,15688,18621});
		MatrixMaps.put("01S6E4",new Integer[]{0,3,3,3,5,6,10,14,19,25,34,42,54,66,80,102,170,205,245,289,342,404,470,551,640,739,0});
		MatrixMaps.put("01S5E5",new Integer[]{6,12,30,50,100,132,245,371,579,808,1213,1547,2175,2850,3930,4681,6162,7084,9321,11952,15210,18369,22899,27136,32365,0,0});
		MatrixMaps.put("50S5E4",new Integer[]{0,3,5,9,14,22,35,50,69,95,134,179,234,305,388,491,628});
		MatrixMaps.put("50S5E3",new Integer[]{0,0,0,0,2,5,6,9,12,13,20,21,26,32,40,47,57});
		MatrixMaps.put("50S4E4",new Integer[]{5,9,20,30,51,66,113,157,230,295,405,491,664,846,1083,1251,1573});
		MatrixMaps.put("07S8E7",new Integer[]{1,5,17,47,113,245,501,971,1760,3024,5231,8162,12446,18575,50249,74050,0,0,0,0,0,0,0});
		MatrixMaps.put("07S7E6",new Integer[]{1,4,8,19,35,61,100,181,293,444,660,938,1466,2278,3361,4760,6715,9693,12736,16549,21283,27117,34187});
		MatrixMaps.put("07S6E6",new Integer[]{1,16,45,84,176,264,501,825,1329,2048,3261,4552,6603,9284,13214,17247,0,0,0,0,0,0,0});
		MatrixTypeMaps.put("S8E7","中8保7");
		MatrixTypeMaps.put("S7E6","中7保6");
		MatrixTypeMaps.put("S6E6","中6保6");
		MatrixTypeMaps.put("S6E5","中6保5");
		MatrixTypeMaps.put("S6E4","中6保4");
		MatrixTypeMaps.put("S5E5","中5保5");
		MatrixTypeMaps.put("S5E4","中5保4");
		MatrixTypeMaps.put("S5E3","中5保3");
		MatrixTypeMaps.put("S4E4","中4保4");
	}
}
package pay.util;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 工具类
 * 
 */
public class BaseHelper {
    public static final String PARAM_EQUAL = "=";
	public static final String PARAM_AND = "&";


	/**
	 * 将bean转换成键值对列表
	 * 
	 * @param bean
	 * @return
	 */
	public static List<NameValuePair> bean2Parameters(Object bean) {
		if (bean == null) {
			return null;
		}
		List<NameValuePair> parameters = new ArrayList<>();

		// 取得bean所有public 方法
		Method[] Methods = bean.getClass().getMethods();
		for (Method method : Methods) {
			if (method != null && method.getName().startsWith("get")
					&& !method.getName().startsWith("getClass")) {
				// 得到属性的类名
				String value = "";
				// 得到属性值
				try {
					String className = method.getReturnType().getSimpleName();
					if (className.equalsIgnoreCase("int")) {
						int val = 0;
						try {
							val = (Integer) method.invoke(bean);
						} catch (InvocationTargetException e) {
							//Log.e("InvocationTargetException", e.getMessage(),e);
						}
						value = String.valueOf(val);
					} else if (className.equalsIgnoreCase("String")) {
						try {
							value = (String) method.invoke(bean);
						} catch (InvocationTargetException e) {
							//Log.e("InvocationTargetException", e.getMessage(),e);
						}
					}
					if (value != null && value != "") {
						// 添加参数
						// 将方法名称转化为id，去除get，将方法首字母改为小写
						String param = method.getName().replaceFirst("get", "");
						if (param.length() > 0) {
							String first = String.valueOf(param.charAt(0))
									.toLowerCase();
							param = first + param.substring(1);
						}
						parameters.add(new BasicNameValuePair(param, value));
					}
				} catch (IllegalArgumentException e) {
					//Log.e("IllegalArgumentException", e.getMessage(), e);
				} catch (IllegalAccessException e) {
					//Log.e("IllegalAccessException", e.getMessage(), e);
				}
			}
		}
		return parameters;
	}

	/**
	 * 对Object进行List<NameValuePair>转换后按key进行升序排序，以key=value&...形式返回
	 *
	 * @return
	 */
	public static String sortParam(Object order) {
		List<NameValuePair> list = bean2Parameters(order);
		return sortParam(list);
	}

	/**
	 * 对List<NameValuePair>按key进行升序排序，以key=value&...形式返回
	 * 
	 * @param list
	 * @return
	 */
	public static String sortParam(List<NameValuePair> list) {
		if (list == null) {
			return null;
		}
		Collections.sort(list, new Comparator<NameValuePair>() {
			@Override
			public int compare(NameValuePair lhs, NameValuePair rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		});
		StringBuffer sb = new StringBuffer();
		for (NameValuePair nameVal : list) {
			//排除不需要签名的字段
			if (null != nameVal.getValue() && !"".equals(nameVal.getValue())
					&& !nameVal.getName().equals("user_id")
					&& !nameVal.getName().equals("pay_type")
					&& !nameVal.getName().equals("bank_code")
					&& !nameVal.getName().equals("force_bank")
					&& !nameVal.getName().equals("id_type")
					&& !nameVal.getName().equals("id_no")
					&& !nameVal.getName().equals("acct_name")
					&& !nameVal.getName().equals("flag_modify")
					&& !nameVal.getName().equals("no_agree")
					&& !nameVal.getName().equals("card_no")   
					 ) {
				sb.append(nameVal.getName());
				sb.append(PARAM_EQUAL);
				sb.append(nameVal.getValue());
				sb.append(PARAM_AND);
			}
		}
		String params = sb.toString();
		if (sb.toString().endsWith(PARAM_AND)) {
			params = sb.substring(0, sb.length() - 1);
		}
		//Log.v("待签名串", params);
		return params;
	}
}
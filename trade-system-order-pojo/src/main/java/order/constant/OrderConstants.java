package order.constant;

/**
 * 订单常量类
 *
 * @author GJ
 * @create 2017-12-22 9:50
 **/
public class OrderConstants {
    public static final String client_jc_all = "900"; // 客户端设置竞彩足球查询全部标记参数
    public static final String client_lc_all = "940"; // 客户端设置竞彩篮球查询全部标记参数
    public static final String client_bd_all = "850"; // 客户端设置北单查询全部标记参数
    public static final String server_jc_all = "90"; // 后端服务器竞彩足球查询全部标记
    public static final String server_lc_all = "94"; // 后端服务器竞彩篮球查询全部标记
    public static final String server_bd_all = "85"; // 后端服务器北单查询全部标记

    public static final String cachekey1 = "jcfs"; // 存放过关统计的键值（已结束的成功方案）
    public static final String cachekey2 = "jcus"; // 存放过关统计的键值（未结束的成功方案）
    public static final String cachekey3 = "jcff"; // 存放过关统计的键值（已结束的流产方案）
    public static final String cachekey4 = "jcuf"; // 存放过关统计的键值（未结束的流产方案）

    public final static int bigCacheLimit = 4000; //大缓存页面大小
}

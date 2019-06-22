package pay.pojo;


import lombok.Data;

@Data
public class RechargeWayPojo { 
	private String channel;//路由充值的渠道
	private String product;//渠道对应的产品
	private String key;//唯一标识符
	private String minlimit;//单笔最低限额
	private String maxlimit;//单笔最高限额
	private String daylimit;//单日限额
	private String openflag;//开关标识  0-关闭  1-打开
	private String bindIdcard;//是否需要绑定身份证标识 0-不要求实名 1-前端针对实名用户显示,后端未限制 2-后端必须限制
	private String category;//种类
	private int oredr;//顺序  越低优先级越高
	private String name;//渠道名称
}

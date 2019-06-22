package pay.dto;

import lombok.Data;

@Data
public class OrderStatusDto {
	private String applyStatus = "";         //订单状态 0-失败 1-成功
	private String rechargeName = "";        //充值渠道名称
	private String money = "";               //充值金额
}

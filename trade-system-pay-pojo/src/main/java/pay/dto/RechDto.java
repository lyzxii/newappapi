package pay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import pay.pojo.PayParam;

//充值返回传输对象
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RechDto {
	private String applyid = "";    //订单号
	private double addmoney = 0.0;   //充值金额
	private PayParam payParam=new PayParam(); //支付参数
}

package pay.dto;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class RechRouteDto {
	private String realName = "";//真实姓名
	private String idcard = "";//证件号
	private List<JSONObject> rechargeWay = new ArrayList<>();
}

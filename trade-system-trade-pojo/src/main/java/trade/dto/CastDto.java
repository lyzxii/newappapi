package trade.dto;

import lombok.Data;

@Data
public class CastDto {
	private String nextPid = ""; //下一期期次
	private String projid = ""; //方案编号
	private String balance = ""; //余额
}

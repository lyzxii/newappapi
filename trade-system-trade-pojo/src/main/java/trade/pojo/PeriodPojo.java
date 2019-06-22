package trade.pojo;

import lombok.Data;

//对应tb_period表
@Data
public class PeriodPojo {
	private String endtime;   //投注截止时间
	private String fendtime;  //文件投注截止时间
	private int salestate;    //销售状态
}

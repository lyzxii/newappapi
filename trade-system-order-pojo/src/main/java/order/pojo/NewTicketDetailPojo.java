package order.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author GJ
 * @create 2018-03-30 11:52
 **/
@Data
public class NewTicketDetailPojo implements Serializable {
//    private String projid;
//    private String periodid;
//    private String istate;
//    private String applyid;
    private String nums="";//注数
    private String mulity="";
    private String codes="";
    private String lscode="";
    private String lsmoney="";
    private String lswininfo="";
//    private String omoney;//每份金额(2 元 大乐透追加为3）
}

package order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 进度
 *
 * @author GJ
 * @create 2018-01-08 20:48
 **/
@Data
public class ProcessDTO implements Serializable {
    private String  node="";//显示节点
    private String  percent="";//百分比
    private String  paint="";//方案描述
    private String  kjtime="";//开奖时间
    private String  pjtime="";//派奖时间
    private String  isflag="";//实际方案状态码
    private String  at;//增加快频开奖时间

    public ProcessDTO(){
        super();
    }

    public ProcessDTO(String node, String percent, String paint ,String kjtime,String pjtime,String isflag) {
        super();
        this.node = node;
        this.percent = percent;
        this.paint = paint;
        this.kjtime=kjtime;
        this.pjtime=pjtime;
        this.isflag = isflag;
    }
}

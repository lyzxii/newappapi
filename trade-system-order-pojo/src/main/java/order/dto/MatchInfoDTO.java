package order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 比赛对阵
 * 例如:奖金优化对阵
 *
 * @author GJ
 * @create 2018-01-09 17:09
 **/
@Data
public class MatchInfoDTO  implements Serializable {

    /**
     * 奖金优化类型 ，只有奖金优化方案详情有
     */
    private String fs;

    private List<MatchDTO> matchs;
    /**
     * 一场制胜
     */
    private String type;

    /**
     * 一场制胜才有的节点
     */
    private List<MatchDTO> othermatchs;

    private List<YCZSMatchInfoDTO> yczsmatchs;

}

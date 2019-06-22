package order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 奖金优化sp
 *
 * @author GJ
 * @create 2018-01-09 13:38
 **/
@Data
public class SpValueDTO  implements Serializable {
    private String count;
    /**
     * key值id和value
     *
     */
    private List<Map<String,String>> spList;
}

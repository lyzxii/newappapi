package order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 过关方式
 *
 * @author GJ
 * @create 2018-01-09 17:18
 **/
@Data
public class PassInfoDTO  implements Serializable {
    /**
     * 过关方式
     */
    private String gg;
    /**
     * missmatch
     */
    private String missmatch;
    /**
     * 过关方案
     */
    private List<PassDTO> passinfo;
}

package order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 过关方式
 *
 * @author GJ
 * @create 2018-01-09 17:14
 **/
@Data
public class PassDTO  implements Serializable {
    /**
     * 过关方案翻译串
     */
    private String str;

    /**
     * 倍数
     */
    private String bs;
}

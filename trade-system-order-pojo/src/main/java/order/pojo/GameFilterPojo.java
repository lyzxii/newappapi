package order.pojo;

import lombok.Data;

import java.util.Date;

/**
 * game_filter
 *
 * @author GJ
 * @create 2018-01-08 18:27
 **/
@Data
public class GameFilterPojo {
    /**
     * id
     */
    private Integer id;

    /**
     * 彩种
     */
    private String GID;

    /**
     * 期次
     */
    private String PID;

    /**
     * 方案编号
     */
    private String HID;
    /**
     * 原始信息
     */
    private String OI;
    /**
     * 过滤条件
     */
    private String FC;
    /**
     * 过滤信息
     */
    private String NI;

    /**
     * 添加时间
     */
    private Date ADDDATE;

}
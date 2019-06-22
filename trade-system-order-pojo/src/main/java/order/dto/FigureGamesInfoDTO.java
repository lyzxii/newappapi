package order.dto;

import lombok.Data;
import order.pojo.QueryProjAppPojo;

/**
 * @author GJ
 * @create 2018-01-12 14:38
 **/
@Data
public class FigureGamesInfoDTO extends QueryProjAppPojo {

    private String wininfostr;

    private String avg;

    /**
     * 开奖号码
     */
    private String acode;

    /**
     * 旋转矩阵号码 旋转矩阵才有 source=9
     */
    private String matrixCodes;

    /**
     * 彩种logo
     */
    private String logo;
}

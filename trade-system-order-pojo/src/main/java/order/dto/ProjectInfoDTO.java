package order.dto;

import lombok.Data;
import order.pojo.QueryProjAppPojo;

/**
 * 方案详情
 *
 * @author GJ
 * @create 2018-01-05 20:06
 **/
@Data
public class ProjectInfoDTO extends ProjectDTO {

    /**
     * 奖金优化起投金额显示
     */
    private String yhMoney;

    /**
     * 奖金优化标志 getJJYHDuizhen、getJJYHDuizhen2xuan1
     */
    private String jjyh;
    /**
     * 中奖注数
     * wininfo.split("\\|").length >= 3,wininfo.split("\\|")[0]
     */
    private String winzs="";
    /**
     * getWinDuizhen source=7才有
     */
    private String rs1;
    /**
     * getWinDuizhen source=7才有
     */
    private String rs2;

    /**
     * 串关方式
     * getNewDanDuizhen、getDanDuizhenByCodes
     */
    private String gg="";


    //数字彩
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
    /**
     * 中奖金额是否包含乐善彩 0不含，1含
     */
    private String iscontainls="0";

    /**
     * 是否是加奖的单子0不是，1是
     */
    private String isaddreward = "0";

}

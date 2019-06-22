package order.pojo;

import lombok.Data;

/**
 * @author GJ
 * @create 2018-01-15 9:35
 **/
@Data
public class MatchPojo {


    /**
     * 主站id
     */
    private String mid="";
    /**
     * 主队名
     */
    private String hn="";
    /**
     * 客队名
     */
    private String gn="";
    /**
     * 比赛开始时间
     */
    private String bt="";
    /**
     * 主队进球数
     */
    private String ms="";
    /**
     * 客队进球数
     */
    private String ss="";
    /**
     * 彩果
     */
    private String rs="";

    private String isdan = "0";

    private String code = "";


    /**
     * 竞彩编号
     */
    private String itemid;


    /**
     * 投注截止时间
     */
    private String et;
    /**
     * 比赛时间
     */
    private String mt;
    /**
     * 主胜赔率
     */
    private String b3;
    /**
     * 平局赔率
     */
    private String b1;
    /**
     * 主负赔率
     */
    private String b0;

    /**
     * 胜平负赔率
     */
    private String spf;
    /**
     * 猜比分赔率
     */
    private String cbf;
    /**
     * 进球数赔率
     */
    private String jqs;
    /**
     * 浮动胜平负
     */
    private String fspf;
    private String fcbf;
    private String fjqs;
    private String fbqc;

    private String sxp;
    /**
     * 让球数
     */
    private String close;
    /**
     * 竞彩名字
     */
    private String name;

    /**
     * 期次
     */
    private String expect;
    /**
     * 联赛名
     */
    private String mname;
    /**
     * 联赛颜色
     */
    private String cl;

    /**
     * 销售状态
     */
    private String isale;
}

package order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 比赛对阵
 *
 * @author GJ
 * @create 2018-01-09 16:45
 **/
@Data
public class MatchDTO  implements Serializable {

    /**
     * 对阵场次号
     */
    private String id="";
    /**
     * 对阵竞彩编号，例如：周一001
     */
    private String name="";
    /**
     * 主队名
     */
    private String hn="";
    /**
     * 客队名
     */
    private String gn="";
    /**
     * 让球数 篮球让分数
     */
    private String close="";
    /**
     * 主队全场比分
     */
    private String hs="";
    /**
     * 客队全场比分
     */
    private String gs="";
    /**
     * 主队半场比分
     */
    private String hhs;
    /**
     * 客队半场比分
     */
    private String hgs;
    /**
     * 即时比分
     */
    private String jsbf;

    /**
     * 是否可跳转到资料库 0不可以，1可以
     */
    private String isForward;
    /**
     * 期次 isForward=1存在
     */
    private String qc;
    /**
     * 场次编号  isForward=1存在
     */
    private String sort;
    /**
     * 竞彩场次编号  isForward=1存在
     */
    private String roundItemId;
    /**
     * 资料库id isForward=1存在
     */
    private String rid;
    /**
     * 球探id isForward=1存在
     */
    private String sid;
    /**
     * 投注方案代码
     */
    private String ccodes;
    /**
     * 加密方式
     */
    private String isEncrypt;
    /**
     * 是否是胆 getNewDanDuizhen、getDanDuizhenByCodes、getHhDanDuizhenByCodes
     */
    private String isdan="0";


    /**
     * 篮球大小分
     */
    private String zclose;

    /**
     * 联赛名
     */
    private String ln;

    /**
     * 北单对阵计奖sp
     */
    private String spvalue;

    /**
     * 平均瓯指 负
     */
    private String bet0;

    /**
     * 平均瓯指 平
     */
    private String bet1;

    /**
     * 平均瓯指 胜
     */
    private String bet3;

}

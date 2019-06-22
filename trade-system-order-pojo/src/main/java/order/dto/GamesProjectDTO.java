package order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 竞技彩方案详情
 *
 * @author GJ
 * @create 2018-01-08 20:56
 **/
@Data
public class GamesProjectDTO {
    /**
     * 方案节点
     */
    private ProjectInfoDTO projectInfo;
    /**
     * 方案对阵节点
     */
    private MatchInfoDTO matchInfo;
    /**
     * 进度节点
     */
    private ProcessDTO processInfo;
    /**
     * sp赔率节点
     * 奖金优化对阵且神单排行查看对阵
     */
    private SpValueDTO spvalueInfo;

    /**
     * 过关相关信息
     * 奖金优化对阵、
     */
    private PassInfoDTO passInfo;

    /**
     * 猜冠军
     */
    private GuessChampionDTO guessChampion;

    /**
     * 猜冠军
     * Map中的key
     * id
     * name
     * spvalue
     */
    private List<Map<String,String>> items;

}

package order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author GJ
 * @create 2018-01-11 19:44
 **/
@Data
public class FigureGamesDTO  implements Serializable {
    /**
     * 方案节点
     */
    private ProjectInfoDTO projectInfo;

    /**
     * 进度节点
     */
    private ProcessDTO processInfo;




}

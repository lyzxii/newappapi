package order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author GJ
 * @create 2018-01-12 19:31
 **/
@Data
public class ZucaiMatchProDTO  implements Serializable {

    /**
     * 方案节点
     */
    private ProjectInfoDTO projectInfo;

    /**
     * 进度节点
     */
    private ProcessDTO processInfo;

    /**
     * 对阵
     */
    private ZucaiMatchInfoDTO matchInfo;
}

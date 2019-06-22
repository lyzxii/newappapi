package order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author GJ
 * @create 2018-01-25 15:23
 **/
@Data
public class ZucaiMatchInfoDTO implements Serializable {
    private List<ZucaiMatchDTO> matchs;
}

package order.dto;

import lombok.Data;

import java.util.List;

/**
 * @author GJ
 * @create 2018-01-17 18:30
 **/
@Data
public class ZuCaiMatchVSDTO {

    private String pid = "";
    private String et = "";
    private String fet = "";
    private String sale = "";

    private String pids = "";
    private List<ZuCaiMatchVsInfoDTO> matchs;
}

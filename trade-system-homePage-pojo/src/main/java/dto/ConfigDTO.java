package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-22   16:04
 **/
@Data
public class ConfigDTO {
    private BaseConfigDTO app;
    private List<BaseConfigDTO> version;
    private List<BaseConfigDTO> biztype;
    private List<BaseConfigDTO> addmoneytype;
    private List<BaseConfigDTO> bankid;
    private List<BaseConfigDTO> huodongjiajian;
    private List<BaseConfigDTO> game;
    private HotlineDTO telphone;
    private List<BaseConfigDTO> launchimage;
    private BaseConfigDTO xrlb;

}

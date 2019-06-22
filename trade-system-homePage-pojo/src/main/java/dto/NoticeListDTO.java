package dto;

import lombok.Data;

import java.util.List;

/**
 * @author wxy
 * @create 2018-01-09 20:14
 **/
@Data
public class NoticeListDTO extends BaseHomePageDTO{
    private String auto;
    private List<NoticeDTO> notice;
}

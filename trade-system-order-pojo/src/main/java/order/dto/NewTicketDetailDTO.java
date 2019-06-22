package order.dto;

import lombok.Data;
import order.pojo.NewTicketDetailPojo;

import java.io.Serializable;
import java.util.List;

/**
 * 出票详情
 * @author GJ
 * @create 2018-03-30 13:48
 **/
@Data
public class NewTicketDetailDTO implements Serializable {

    /**
     * 乐善总奖金
     */
    private String totalLsBous="";


    private List<NewTicketDetailPojo> tickets;

}

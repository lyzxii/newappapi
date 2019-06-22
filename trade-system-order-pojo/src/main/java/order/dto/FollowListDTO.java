package order.dto;

import lombok.Data;

@Data
public class FollowListDTO {

    private Integer pageSize = 1;
    private Integer pageNumber = 1;
    private Integer totalPages = 1;
    private Integer totalRecords = 1;
    private String tMoney = "";//方案购买总金额
    private Integer followNum = 0;//跟买金额
    private Integer finish = 0;
    private Object datas;
}

package order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author GJ
 * @create 2018-01-12 18:22
 **/
@Data
public class ZucaiMatchDTO  implements Serializable {
    private String mid="";
    private String bt="";
    private String hn="";
    private String gn="";
    private String ms="";
    private String ss="";
    private String rs="";
    private String code="";
    private String isdan="";
}

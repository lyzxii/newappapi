package dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FloatImgDTO {
    private String jumpUrl;//跳转链接
    private String imgUrl;//图片链接
    private String ymId;//友盟id
    private String id;
    private String title;
    private String width;
    private String height;
}

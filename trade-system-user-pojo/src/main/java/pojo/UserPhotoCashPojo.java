package pojo;

import lombok.Data;

import java.util.Date;

/**
 * 用户头像Pojo
 *
 * @author GJ
 * @create 2017-12-04 13:45
 **/
@Data
public class UserPhotoCashPojo {

    private String nickid;
    private String userImg;
    private Date addDate;
    private String status;//审核状态（0-上传成功待审核，1-审核成功，2-审核不通过）
    private String rebackFlag;//用户确认审核失败标记
    private Date aduitDate;//审核完成时间
    private String opertor;//操作人
    private Date opertorDate;//操作时间
    private String opertorDesc;//操作描述



}

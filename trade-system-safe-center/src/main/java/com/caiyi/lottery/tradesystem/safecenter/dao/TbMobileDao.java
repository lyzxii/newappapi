package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.*;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_mobile 表
 */
@Mapper
public interface TbMobileDao {

    @Select("select mid as mobileId,mobileno from tb_mobile where mobileno = #{mobileno} ")
    List<SafeBean> queryMobileByNo(@Param("mobileno") String mobileno);

    @Select("select mid as mobileId,mobileno from tb_mobile where mid = #{mid} ")
    List<SafeBean> queryMobileByMid(@Param("mid") String mid);

    @Modifying
    @Update("update tb_mobile set mobileno = #{mobileno} where mid = #{mid}")
    int updateMobile(@Param("mobileno") String mobileno, @Param("mid") String mid);

    // seq_mobile_mid.nextval
    @Modifying
    @Insert("insert into tb_mobile(mid,mobileno) values(#{md5mobileno}, #{mobileno}) ")
    Integer addMobileByNo(@Param("md5mobileno") String md5mobileno, @Param("mobileno") String mobileno);


}

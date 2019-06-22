package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_id_card 表
 */
@Mapper
public interface TbIdCardDao {

    @Select("select cid as idCardId,idcard from tb_id_card where idcard = #{idcard} ")
    List<SafeBean> queryIdCardByNo(@Param("idcard") String idcard);

    @Select("select cid as idCardId,idcard from tb_id_card where cid = #{cid} ")
    List<SafeBean> queryIdCardByCid(@Param("cid") String cid);

    // seq_idcard_cid.nextval
    @Modifying
    @Insert("insert into tb_id_card(cid,idcard) values(#{md5idcard}, #{idcard}) ")
    Integer addIdCardByNo(@Param("md5idcard") String md5idcard, @Param("idcard") String idcard);
}

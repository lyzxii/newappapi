package com.caiyi.lottery.tradesystem.safecenter.dao;

import bean.SafeBean;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * tb_bank_card 表
 */
@Mapper
public interface TbBankCardDao {

    @Select("select bid as bankcardId,bankcard from tb_bank_card where bankcard = #{bankcard} ")
    List<SafeBean> queryBankCardByNo(@Param("bankcard") String bankcard);

    @Select("select bid as bankcardId,bankcard from tb_bank_card where bid = #{bid} ")
    List<SafeBean> queryBankCardByBid(@Param("bid") String bid);

    @Modifying
    @Insert("insert into tb_bank_card(bid,bankcard) values(#{md5bankcard}, #{bankcard}) ")
    Integer addBankCardByNo(@Param("md5bankcard") String md5bankcard, @Param("bankcard") String bankcard);
}

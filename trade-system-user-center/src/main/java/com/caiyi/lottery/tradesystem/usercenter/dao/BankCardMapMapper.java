package com.caiyi.lottery.tradesystem.usercenter.dao;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


/**
 * tb_bank_card_map
 * @author A-0205
 *
 */
@Mapper
public interface BankCardMapMapper {

	/**
	 * 根据提款银行卡编码查询真实银行卡Code
	 * @param drawBankCode
	 * @return
	 */
	@Select("select distinct cbankcode from tb_bank_card_map where cbcode = #{drawBankCode}")
	String getBankCodeByDrawCode(@Param("drawBankCode") String drawBankCode);
}

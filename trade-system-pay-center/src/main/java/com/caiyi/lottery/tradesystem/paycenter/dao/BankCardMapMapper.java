package com.caiyi.lottery.tradesystem.paycenter.dao;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pay.pojo.BankCardMapPojo;

import java.util.List;


/**
 * tb_bank_card_map
 * @author XQH
 *
 */
@Mapper
public interface BankCardMapMapper {

	/**
	 * 根据cbinno查询银行卡信息进行充值银行卡卡bin校验
	 * @param cbinno
	 * @return
	 */
	@Select("select t.ccauthenticationflag ,t.cbankcode ,t.cbankname,t.ccardtypename,t.cbankno from tb_bank_card_map t where t.cbinno=substr(#{cbinno},0,t.ibinlen)")
	public List<BankCardMapPojo> getBankCardInfoByBinno(@Param( "cbinno" ) String cbinno);


	/**
	 * 根据cbinno查询银行卡信息进行提款银行卡检测
	 * @param cbinno
	 * @return
	 */
	@Select("select t.cbcode,t.cbankcode,t.cbankname,t.ccardtypename,t.cbankno from tb_bank_card_map t where t.cbinno=substr(#{cbinno},0,t.ibinlen)")
	public List<BankCardMapPojo> drawBankCardInfoByBinno(@Param( "cbinno" ) String cbinno);
}

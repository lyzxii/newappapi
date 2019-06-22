package com.caiyi.lottery.tradesystem.paycenter.dao;



import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pay.bean.PayBean;
import pay.pojo.BankBranchPojo;
import pay.pojo.BankCardMapPojo;

import java.util.List;


/**
 * tb_bank_branch
 * @author XQH
 *
 */
@Mapper
public interface BankBranchMapper {

	/**
	 * 根据银行对应的自定义编码，省市查询银行支行
	 * @param bankBranchPojo
	 * @return
	 */
	@Select("select bankbranch from tb_bank_branch where bcode = #{bcode,jdbcType=VARCHAR} and pro = #{pro,jdbcType=VARCHAR} and city = #{city,jdbcType=VARCHAR}")
	public List<PayBean> getBankBranchByBcodeProCity(PayBean bankBranchPojo);


}

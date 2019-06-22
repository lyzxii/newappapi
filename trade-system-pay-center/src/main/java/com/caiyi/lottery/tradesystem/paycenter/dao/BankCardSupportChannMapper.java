package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pay.pojo.BankCardSupportChannel;

/**
 * Created by XQH on 2017/12/29.
 */
@Mapper
public interface BankCardSupportChannMapper {
    @Select("select max(to_number(cmaxlimit)) cmaxlimit,max(to_number(cdaylimit)) cdaylimit from tb_bankcard_support_channel where cbankcode = #{cbankcode} and ccardtype = #{ccardtype}")
    BankCardSupportChannel findLimitLinesByBankcodeType(@Param("cbankcode") String cbankcode,@Param("ccardtype") String ccardtype);


    @Select("select max(to_number(cmaxlimit)) cmaxlimit,max(to_number(cdaylimit)) cdaylimit from tb_bankcard_support_channel where cbankcode = #{cbankcode} and ccardtype = #{ccardtype} and cchannel = #{cchannel} and cproduct = #{cproduct}")
    BankCardSupportChannel findLimitLinesByBankcodeTypeCP(@Param("cbankcode") String cbankcode,@Param("ccardtype") String ccardtype,@Param("cchannel") String cchannel,@Param("cproduct") String cproduct);
}

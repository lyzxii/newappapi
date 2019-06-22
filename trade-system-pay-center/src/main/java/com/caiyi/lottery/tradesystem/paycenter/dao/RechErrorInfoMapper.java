package com.caiyi.lottery.tradesystem.paycenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import pay.bean.PayBean;

/**
 * 对应tb_user_pay_error
 */
@Mapper
public interface RechErrorInfoMapper {

    //插入错误信息
    @Insert("insert into tb_user_pay_error(csatday,cchannelcode,cproducttype,cnickid,cbankcode,ccardno,ccardtype,cmodifydate,capplyids,cremark,csafekey) " +
            "values(#{applydate},#{channel},#{product},#{uid},#{bankCode},#{cardNo},#{cardtype},sysdate,#{applyid},#{remark},#{safeKey})")
    public int insertErrorInfo(PayBean bean);
}

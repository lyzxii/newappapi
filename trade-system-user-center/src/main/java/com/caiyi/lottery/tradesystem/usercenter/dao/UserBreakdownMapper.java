package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * TB_USER_BREAKDOWN
 */
@Mapper
public interface UserBreakdownMapper {

    @Insert("INSERT INTO TB_USER_BREAKDOWN (ID, CNICKID, CERRORTIME, CAPPNAME, CAPPPLATFORM,CAPPVERSION,CSOURCE,COSVERSION,CMOBILETYPE,CERRORDESC) "
            + "VALUES(seq_user_neterror.NEXTVAL,#{cnickid},to_date(#{cerrortime},'yyyy-MM-dd hh24:mi:ss'),#{cappname},#{cappplatform},#{cappversion},#{csource},#{cosversion},#{cmobiletype},#{cerrordesc})")
    int addUserBreakdownInfo(@Param("cnickid") String cnickid, @Param("cerrortime") String cerrortime, @Param("cappname") String cappname, @Param("cappplatform") String cappplatform, @Param("cappversion") String cappversion,
                             @Param("csource") String csource, @Param("cosversion") String cosversion, @Param("cmobiletype") String cmobiletype, @Param("cerrordesc") String cerrordesc);
}

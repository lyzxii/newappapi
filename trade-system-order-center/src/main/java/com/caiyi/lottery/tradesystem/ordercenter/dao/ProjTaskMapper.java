package com.caiyi.lottery.tradesystem.ordercenter.dao;

import org.apache.ibatis.annotations.*;

/**
 *
 * tb_proj_task表
 */
@Mapper
public interface ProjTaskMapper {

    @Insert("insert into tb_proj_task(cprojid,cgameid,itype,iflag,cadddate,cperiodid) values(#{hid},#{gid},'6','0',sysdate,#{periodid})")
    int insertProjTask(@Param( "hid" ) String hid, @Param( "gid" ) String gid, @Param( "periodid" ) String periodid);

}

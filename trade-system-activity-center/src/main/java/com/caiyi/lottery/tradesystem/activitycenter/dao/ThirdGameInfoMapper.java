package com.caiyi.lottery.tradesystem.activitycenter.dao;

import activity.pojo.ThirdGameInfoPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用于表tb_third_ginfo
 */
@Mapper
public interface ThirdGameInfoMapper {
    /**
     * 查询游戏信息
     * @param gameId
     * @return
     */
    @Select("select CGAMEID as gameId, CGAMENAME as gameName, CSUPPLIER as supplier, CSTARTTIME as startTime, " +
                    "CENDTIME as endTime, CDESCRIBE as describe, CURL as photoUrl, CDOWNLOADURL as downloadUrl, " +
                    "CTHRIDURL as thirdUrl, ISTATE as state, IPRIO as prio, CDEMO as memo from tb_third_ginfo t where t.cgameid = #{gameId}")
    ThirdGameInfoPojo queryGameInfoByGameId(@Param("gameId") String gameId);
}

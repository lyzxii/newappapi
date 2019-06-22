package com.caiyi.lottery.tradesystem.activitycenter.dao;

import activity.pojo.ProjQvodPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用于tb_proj_qvod表
 */
@Mapper
public interface ProjQvodMapper {
    /**
     * 查询天天分钱活动方案
     * @param activiDate
     * @return
     */
    @Select("select cprojid as projId,cgameid as gameId,iaward as award,activitydate as activityDate,cendtime as endTime,itmoney as money,ibonus as bonus \n" +
                    "\t\tfrom tb_proj_qvod \n" +
                    "\t\twhere activitydate < to_date(#{activiDate}, 'yyyymmddhh24miss') \n" +
                    "\t\torder by activitydate desc")
    List<ProjQvodPojo> queryQvodOrders(@Param("activiDate") String activiDate);

    /**
     * 查询天天分钱活动累计总奖金
     * @return
     */
    @Select("select sum(ibonus) totalBonus from tb_proj_qvod")
    Double queryTotalBonus();

    /**
     * 查询方案详情
     * @param projId 方案编号
     * @return
     */
    @Select("select cperiodid as periodId,icast as cast,cadddate as addDate,itmoney as money,iaward as award,ireturn as prizes," +
                    "cwininfo as winInfo,imulity as mulity,ccodes as codes,ibonus as bonus,cendtime activityDate \n" +
                    "\t\tfrom tb_proj_qvod \n" +
                    "\t\twhere cprojid=#{projId}")
    ProjQvodPojo queryDetail(@Param("projId") String projId);
}

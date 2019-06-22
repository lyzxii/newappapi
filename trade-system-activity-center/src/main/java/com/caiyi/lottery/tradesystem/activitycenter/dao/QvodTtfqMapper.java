package com.caiyi.lottery.tradesystem.activitycenter.dao;

import activity.pojo.QvodTtfqPojo;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * 用于表tb_qvod_ttfq
 */
@Mapper
public interface QvodTtfqMapper {
    /**
     * 查询天天分钱活动我的参与记录
     * @param nickid 用户昵称
     * @return
     */
    @Select("select cprojid as projId,cgameid as gameId,imoney as money,istatus as status \n" +
                    "\t\tfrom tb_qvod_ttfq \n" +
                    "\t\twhere cnickid=#{nickid}")
    List<QvodTtfqPojo> queryMyJoin(@Param("nickid") String nickid);

    /**
     * 查询天天分钱活动某一方案参与人数
     * @param projId 方案编号
     * @return
     */
    @Select("select count(1) from tb_qvod_ttfq where cprojid=#{projId}")
    Integer queryJoinCounts(@Param("projId") String projId);

    /**
     * 查询天天分钱活动方案开始时间
     * @param projId 方案编号
     * @return
     */
    @Select("select cendtime activitydate from tb_proj_qvod where cprojid = #{projId}")
    Date queryEndTimeByProjId(@Param("projId") String projId);

    /**
     * 查询是否已经参与某一个天天分钱活动方案
     * @param nickId 用户昵称
     * @param projId 方案编号
     * @return
     */
    @Select("select count(1) from tb_qvod_ttfq where cnickid=#{nickId} and cprojid=#{projId}")
    Integer queryByNickidAndProjId(@Param("nickId") String nickId, @Param("projId") String projId);

    /**
     * 保存用户参与活动数据
     * @param projId 方案编号
     * @param gameId 彩种编号
     * @param nickId 用户昵称
     * @return
     */
    @Insert("insert into tb_qvod_ttfq (cprojid,cgameid,cnickid) values (#{projId},#{gameId},#{nickId})")
    int insertJoin(@Param("projId") String projId, @Param("gameId") String gameId, @Param("nickId") String nickId);
}

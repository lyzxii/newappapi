package com.caiyi.lottery.tradesystem.activitycenter.dao;

import org.apache.ibatis.annotations.*;

/**
 * 用于表tb_third_game
 */
@Mapper
public interface ThirdGameMapper {
    /**
     * 查询用户该游戏是否登录过
     * @param nickId 用户昵称
     * @param gameId 游戏编号
     * @return 查询到的件数，即大于0，存在登录记录
     */
    @Select("select count(1) as num from tb_third_game where cusername = #{nickId} and cgameid = #{gameId} ")
    int countByUserAndGame(@Param("nickId") String nickId, @Param("gameId") String gameId);

    /**
     * 更新游戏记录表用户登陆信息
     * @param nickId 用户昵称
     * @param gameId 游戏编号
     * @return 操作成功件数
     */
    @Update("update tb_third_game t set t.clogintime = sysdate where t.cusername = #{nickId} and t.cgameid = #{gameId} ")
    int updateLoginTime(@Param("nickId") String nickId, @Param("gameId") String gameId);

    /**
     * 插入游戏记录表用户首次登陆信息
     * @param nickId 用户昵称
     * @param gameId 游戏编号
     * @param gameName 游戏名称
     * @param supplier 供应商
     * @return 插入成功件数
     */
    @Insert("insert into tb_third_game(cusername,cgameid,cgamename,csupplier,cfirsttime,clogintime) " +
                    "values (#{nickId},#{gameId},#{gameName},#{supplier},sysdate,sysdate)")
    int insert(@Param("nickId") String nickId, @Param("gameId") String gameId, @Param("gameName") String gameName, @Param("supplier") String supplier);
}

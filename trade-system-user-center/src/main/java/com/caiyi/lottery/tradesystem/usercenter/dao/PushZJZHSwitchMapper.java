package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import pojo.PushPojo;

/**
 * 用于 tb_push_zjzh_switch 表
 */
@Mapper
public interface PushZJZHSwitchMapper {
    /**
     * 当前用户存在数（用户是否存在）
     * @param nickid 用户昵称
     * @return 存在数（1：存在，0：不存在）
     */
    @Select("select count(1) from TB_PUSH_ZJZH_SWITCH where cnickid=#{nickid}")
    int countByNicdid (@Param("nickid") String nickid);

    /**
     * 插入数据
     * @param nickid 用户昵称
     * @param winSwtich 中奖开关 1：开，0：关
     * @param chaseSwitch 追号开关 1：开，0：关
     * @return
     */
    @Insert("insert into tb_push_zjzh_switch(cnickid,zj_switch,zh_switch,add_date,update_date)values(#{nickid},#{winSwitch},#{chaseSwitch},sysdate,sysdate)")
    int insert(@Param("nickid") String nickid, @Param("winSwitch") Integer winSwtich, @Param("chaseSwitch") Integer chaseSwitch);

    /**
     * 更新数据
     * @param winSwitch 中奖开关 1：开，0：关
     * @param chaseSwitch 追号开关 1：开，0：关
     * @param nickid 用户昵称
     * @return 操作成功件数
     */
    @Update("update TB_PUSH_ZJZH_SWITCH set zj_switch=#{winSwitch},zh_switch=#{chaseSwitch},update_date=sysdate where cnickid=#{nickid}")
    int updateByNickid (@Param("winSwitch") Integer winSwitch, @Param("chaseSwitch") Integer chaseSwitch, @Param("nickid") String nickid);


    @Select("select zj_switch zj,zh_switch zh from TB_PUSH_ZJZH_SWITCH where cnickid = #{uid}")
    @Results(value = {
            @Result(id = true, property = "zjSwitch", column = "zj", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "zhSwitch", column = "zh", javaType = String.class, jdbcType = JdbcType.VARCHAR)}
            )
    PushPojo findSwitch(@Param("uid") String uid);

    @Insert("insert into TB_PUSH_ZJZH_SWITCH values(#{uid},#{zj},#{zh},sysdate,sysdate)")
    int insertDefaultOpenKey(@Param("uid") String uid, @Param("zj") String zj, @Param("zh") String zh);


}

package com.caiyi.lottery.tradesystem.usercenter.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * 回滚
 *
 * @author GJ
 * @create 2017-12-29 18:24
 **/
@Mapper
public interface RollBackMapper {

    @Delete(" delete from tb_ally where nickid=#{nickid} and allyid=#{allyid} and #{sysdate} < 1/60/24 + ADDTIME ")
    void deleteAlly(@Param("nickid") String nickid, @Param("allyid") String allyid, @Param("sysdate") Date sysdate);

    @Delete(" delete from tb_user where cnickid=#{nickid}  and #{sysdate} < 1/60/24 + CADDDATE ")
    void deleteUser(@Param("nickid") String nickid, @Param("sysdate") Date sysdate);

    @Delete(" delete from tb_user_acct where cnickid=#{nickid}  ")
    void deleteUserAcct(@Param("nickid") String nickid, @Param("sysdate") Date sysdate);

    @Delete(" delete from tb_ally_log where cnickid=#{nickid}  and #{sysdate} < 1/60/24 + ADDTIME ")
    void deleteAllyLog(@Param("nickid") String nickid, @Param("sysdate") Date sysdate);

    @Delete(" delete from tb_cellphone_imei where cnickid=#{nickid}  and #{sysdate} < 1/60/24 + CADDDATE ")
    void deleteCellphoneImei(@Param("nickid") String nickid, @Param("sysdate") Date sysdate);

}

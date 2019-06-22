package com.caiyi.lottery.tradesystem.activitycenter.dao;

import org.apache.ibatis.annotations.*;

/**
 * 用于表tb_qvod_ttfq_acct
 */
@Mapper
public interface QvodTtfqAcctMapper {
    /**
     * 通过用户id查询当前已领总金额
     * @param nickid
     * @return
     */
    @Select("select imoney from tb_qvod_ttfq_acct where cnickid=#{nickid}")
    Double queryByNickid(@Param("nickid") String nickid);

    /**
     * 更新当前用户总金额
     * @param money
     * @param nickid
     * @return
     */
    @Update("update tb_qvod_ttfq_acct set imoney=#{money} where cnickid=#{nickid}")
    int updateMoneyByNickid(@Param("money") Double money, @Param("nickid") String nickid);

    /**
     * 插入一条新记录
     * @param nickid
     * @param money
     * @return
     */
    @Insert("insert into tb_qvod_ttfq_acct (cnickid, imoney) values(#{nickid}, #{imoney})")
    int add(@Param("nickid") String nickid, @Param("money") Double money);

    /**
     * 查询当前用户是否存在账户
     * @param nickid
     * @return
     */
    @Select("select count(1) as num from tb_qvod_ttfq_acct where cnickid=#{nickid}")
    int queryCountByNickid(@Param("nickid") String nickid);
}

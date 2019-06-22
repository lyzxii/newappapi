package com.caiyi.lottery.tradesystem.integralcenter.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * tb_cj_record表
 */
@Mapper
public interface CJRecordMapper {

    @Select("select count(1) as cnt from tb_cj_record where cnickid=#{uid} and to_char(cj_date,'yyyy-MM-dd')=#{cj_date}")
    int getUserCJCntDay(@Param("uid") String uid, @Param("cj_date")String cj_date);


    @Select("select count(1) as cnt from tb_cj_record")
    int getTotalCnt();

    @Insert("insert into tb_cj_record(CJ_ID,CNICKID,CJ_RESULT,CJ_DATE)values(seq_cj_record.nextval,#{uid},#{cj_result},sysdate)")
    int insertCjRecord(@Param("uid") String uid,@Param("cj_result") int cj_result);

}

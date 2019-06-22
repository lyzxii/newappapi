package com.caiyi.lottery.tradesystem.integralcenter.dao;

import integral.pojo.PointsDrawResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * tb_cj_constraint
 */
@Mapper
public interface CJConstraintMapper {

    @Select("select cnt,per_cnt from tb_cj_constraint where type=#{type} for update")
    PointsDrawResult getLeftCnt(String type);

    @Update("update tb_cj_constraint set per_cnt=#{cnt1},cnt=#{cnt2} where type=#{type}")
    int updatePerCnt(int cnt1,int cnt2,String type);

    @Update("update tb_cj_constraint set cnt=cnt-1 where type=#{type}")
    int updateCnt(String type);
}

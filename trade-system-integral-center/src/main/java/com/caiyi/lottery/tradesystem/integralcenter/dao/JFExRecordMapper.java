package com.caiyi.lottery.tradesystem.integralcenter.dao;

import integral.bean.PointsMallBean;
import integral.pojo.PointsExchangeRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * tb_jf_exrecord tb_jf_records关联查询
 */
@Mapper
public interface JFExRecordMapper {

    @Select("select t2.ex_goods_name,t2.require_point,t1.ex_time,decode(t1.ex_status, 1, '兑换成功', 0, '兑换失败') as ex_status\n" +
            "from tb_jf_exrecord t1, tb_jf_goods t2 where t1.ex_goods_id = t2.ex_goods_id and cnickid = #{cnickid}")
    List<PointsExchangeRecord> queryExchangeRecord(String cnickid);


    @Insert("insert into tb_jf_exrecord(ex_record_id,cnickid,ex_goods_id,ex_time,ex_status,ex_point)values(SEQ_JF_EXRECORD.NEXTVAL,#{uid},#{ex_goods_id},sysdate,1,#{require_point})")
    int insertJFExchangeRecord(PointsMallBean bean);

}

package com.caiyi.lottery.tradesystem.integralcenter.dao;

import integral.pojo.PointsMallGood;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * tb_jf_goods 表
 */
@Mapper
public interface JFGoodsMapper {

    @Select("select ex_goods_name,ex_goods_id,require_point,ex_goods_desc,type from tb_jf_goods")
    List<PointsMallGood> getJFMallGoods();

    @Select("select ex_goods_name,ex_goods_cnt,ex_goods_id,require_point,ex_goods_desc,type from tb_jf_goods t where t.ex_goods_id=#{ex_goods_id}")
    PointsMallGood getJFGoodDetail(String ex_goods_id);


}

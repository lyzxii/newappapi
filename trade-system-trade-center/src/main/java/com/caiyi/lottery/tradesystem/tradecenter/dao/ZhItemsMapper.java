package com.caiyi.lottery.tradesystem.tradecenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import trade.pojo.ZhRecordPojo;

import java.util.List;

@Mapper
public interface ZhItemsMapper {

    List<ZhRecordPojo> queryZhByZhId(@Param("gid") String gid, @Param("tid") String tid);
}

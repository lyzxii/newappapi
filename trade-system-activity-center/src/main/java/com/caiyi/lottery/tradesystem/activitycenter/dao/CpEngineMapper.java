package com.caiyi.lottery.tradesystem.activitycenter.dao;

import activity.pojo.CpenginePojo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用于cpengine包下的存储过程
 */
@Mapper
public interface CpEngineMapper {
    void qvodttfqReturn(CpenginePojo pojo);
}
